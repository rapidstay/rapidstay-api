package com.rapidstay.xap.api.service;

import com.rapidstay.xap.api.entity.User;
import com.rapidstay.xap.api.repository.UserRepository;
import com.rapidstay.xap.api.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public Map<String, Object> register(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "✅ Registration successful");
        response.put("username", user.getUsername());
        return response;
    }

    public Map<String, Object> login(String email, String password) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        Map<String, Object> response = new HashMap<>();

        if (userOpt.isPresent() && passwordEncoder.matches(password, userOpt.get().getPassword())) {
            String token = jwtTokenProvider.createToken(userOpt.get().getUsername());
            response.put("token", token);
            response.put("username", userOpt.get().getUsername());
            return response;
        } else {
            response.put("error", "❌ Invalid credentials");
            return response;
        }
    }
}
