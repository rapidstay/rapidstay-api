package com.rapidstay.xap.api.service;

import com.rapidstay.xap.api.client.ExpediaClient;
import com.rapidstay.xap.api.dto.HotelDetailResponse;
import com.rapidstay.xap.api.dto.HotelResponse;
import com.rapidstay.xap.api.dto.HotelSearchRequest;
import com.rapidstay.xap.api.dto.PagedResult;
import com.rapidstay.xap.api.common.dto.CityDTO;
import com.rapidstay.xap.api.common.repository.SupplierHotelRepository;
import com.rapidstay.xap.api.common.repository.MasterHotelRepository;
import com.rapidstay.xap.api.common.repository.MasterCityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * RapidStay Hotel Service
 * - 기존: 도시명 기반 단일 스레드 검색
 * - 개선: cityId + cityType 기반 병렬(공급사별) 검색
 */
@Service
@RequiredArgsConstructor
public class HotelService {

    private final ExpediaClient expediaClient;
    private final CityService cityService;

    // ✅ 신규 DB Repository (공급사/매핑 조회용)
    private final MasterCityRepository masterCityRepository;
    private final MasterHotelRepository masterHotelRepository;
    private final SupplierHotelRepository supplierHotelRepository;

    @Value("${rapidstay.mock.enabled:true}")
    private boolean useMock;

    /**
     * ✅ 도시명 → 좌표 변환 후 호텔 목록 조회 + 페이징 (기존 유지)
     */
    public PagedResult<HotelResponse> searchHotels(HotelSearchRequest req) {
        CityDTO city = cityService.getCityInfo(req.getCity());
        if (city == null)
            throw new RuntimeException("City not found: " + req.getCity());

        List<HotelResponse> allHotels = expediaClient.searchHotelsByRegion(
                city.getCityName(),
                req.getCheckIn(),
                req.getCheckOut(),
                req.getRooms()
        );

        return buildPagedResult(allHotels, req.getPage(), req.getPageSize());
    }

    /**
     * ✅ cityId + cityType 기반 병렬 검색 (신규 추가)
     */
    public PagedResult<HotelResponse> searchHotelsByCityId(Long cityId, String cityType, HotelSearchRequest req) {
        System.out.println("🚀 [HotelService] cityId 기반 병렬 검색 시작: cityId=" + cityId + ", cityType=" + cityType);

        // 1️⃣ cityType 에 따라 검색 기준 테이블 결정
        List<Long> masterHotelIds = switch (cityType == null ? "city" : cityType.toLowerCase()) {
            case "city" -> masterHotelRepository.findIdsByCityId(cityId);
            case "hotel" -> List.of(cityId); // 단일 호텔 직접 지정
            default -> new ArrayList<>();
        };

        if (masterHotelIds.isEmpty()) {
            System.out.println("⚠️ [HotelService] No hotels found for cityId=" + cityId);
            return new PagedResult<>(1, req.getPageSize(), 0, List.of());
        }

        // 2️⃣ 매핑 테이블 통해 supplier_hotel ID 목록 확보
        List<Long> supplierHotelIds = supplierHotelRepository.findIdsByMasterHotelIds(masterHotelIds);
        if (supplierHotelIds.isEmpty()) {
            System.out.println("⚠️ [HotelService] No supplier hotels mapped for master_hotel_ids=" + masterHotelIds.size());
            return new PagedResult<>(1, req.getPageSize(), 0, List.of());
        }

        // 3️⃣ 병렬 호출 준비
        int batchSize = 100;
        ExecutorService executor = Executors.newFixedThreadPool(Math.min(10, (supplierHotelIds.size() / batchSize) + 1));
        List<CompletableFuture<List<HotelResponse>>> futures = new ArrayList<>();

        for (int i = 0; i < supplierHotelIds.size(); i += batchSize) {
            int start = i;
            int end = Math.min(i + batchSize, supplierHotelIds.size());
            List<Long> batch = supplierHotelIds.subList(start, end);

            CompletableFuture<List<HotelResponse>> future = CompletableFuture.supplyAsync(() -> {
                try {
                    return expediaClient.searchHotelsByIds(batch, req.getCheckIn(), req.getCheckOut(), req.getRooms());
                } catch (Exception e) {
                    System.err.println("❌ [Expedia] 병렬 호출 실패 (" + start + "~" + end + "): " + e.getMessage());
                    return List.of();
                }
            }, executor);
            futures.add(future);
        }

        // 4️⃣ 결과 병합
        List<HotelResponse> allResults = futures.stream()
                .map(CompletableFuture::join)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        executor.shutdown();

        System.out.println("✅ [HotelService] Expedia 호출 완료: 총 " + allResults.size() + "건");

        return buildPagedResult(allResults, req.getPage(), req.getPageSize());
    }

    /** ✅ 페이징 공통 처리 */
    private PagedResult<HotelResponse> buildPagedResult(List<HotelResponse> all, int page, int size) {
        int total = all.size();
        int pageNo = Math.max(1, page);
        int pageSize = Math.max(1, size);
        int start = (pageNo - 1) * pageSize;
        int end = Math.min(start + pageSize, total);
        List<HotelResponse> list = total > 0 ? all.subList(start, end) : List.of();
        return new PagedResult<>(pageNo, pageSize, total, list);
    }

    /** ✅ 호텔 목록만 필요할 때 */
    public List<HotelResponse> searchHotelsWithRooms(HotelSearchRequest request) {
        return searchHotels(request).getHotels();
    }

    /** ✅ 상세 페이지용 — 특정 호텔 ID 기반 조회 */
    public HotelDetailResponse getHotelDetail(String hotelId,
                                              String city,
                                              String checkIn,
                                              String checkOut,
                                              List<HotelSearchRequest.RoomInfo> rooms) {

        CityDTO cityInfo = cityService.getCityInfo(city);
        if (cityInfo == null)
            throw new RuntimeException("City not found: " + city);

        HotelSearchRequest req = new HotelSearchRequest();
        req.setCity(city);
        req.setCheckIn(checkIn);
        req.setCheckOut(checkOut);
        req.setRooms(rooms);
        req.setPage(1);
        req.setPageSize(100);

        List<HotelResponse> results = searchHotelsWithRooms(req);
        HotelResponse base = results.stream()
                .filter(h -> String.valueOf(h.getId()).equals(hotelId))
                .findFirst()
                .orElse(null);

        if (base == null) return null;

        return HotelDetailResponse.builder()
                .id(base.getId())
                .name(base.getName())
                .address(base.getAddress())
                .city(base.getCity())
                .rating(base.getRating())
                .latitude(Double.isNaN(base.getLatitude()) ? cityInfo.getLat() : base.getLatitude())
                .longitude(Double.isNaN(base.getLongitude()) ? cityInfo.getLon() : base.getLongitude())
                .description("이 호텔은 Mock 데이터 기반이며 Expedia 연동 시 실제 데이터로 교체됩니다.")
                .images(List.of(
                        "https://picsum.photos/seed/" + base.getName() + "/800/400",
                        "https://picsum.photos/seed/" + base.getName() + "2/800/400",
                        "https://picsum.photos/seed/" + base.getName() + "3/800/400"
                ))
                .amenities(List.of("무료 Wi-Fi", "레스토랑", "피트니스 센터", "수영장"))
                .rooms(buildMockRooms(base.getName()))
                .build();
    }

    /** ✅ 테스트용 Mock Room 데이터 생성 */
    private List<HotelDetailResponse.RoomDetail> buildMockRooms(String hotelName) {
        List<HotelDetailResponse.RoomDetail> list = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            list.add(HotelDetailResponse.RoomDetail.builder()
                    .roomName("디럭스룸 " + i)
                    .bedType("킹베드")
                    .amenities(List.of("무료 Wi-Fi", "TV", "냉장고"))
                    .description("편안한 객실로 가족 및 출장객 모두에게 적합합니다.")
                    .images(List.of("https://picsum.photos/seed/" + hotelName + "room" + i + "/400/250"))
                    .originalPrice(220000.0 + (i * 10000.0))
                    .finalPrice(190000.0 + (i * 10000.0))
                    .cancellationPolicy("체크인 2일 전까지 무료 취소")
                    .build());
        }
        return list;
    }
}
