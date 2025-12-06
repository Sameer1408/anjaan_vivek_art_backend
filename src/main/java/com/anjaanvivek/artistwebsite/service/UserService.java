package com.anjaanvivek.artistwebsite.service;

import com.anjaanvivek.artistwebsite.model.OtpType;
import com.anjaanvivek.artistwebsite.model.User;
import com.anjaanvivek.artistwebsite.repository.UserRepository;
import com.anjaanvivek.artistwebsite.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private OtpService otpService;
    
    @Autowired
    private JwtUtil jwtUtil;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public User signup(User user, String rawPassword) {
       
    	 boolean emailVerified = otpService.isVerified(user.getEmail(), OtpType.EMAIL);
         boolean mobileVerified = otpService.isVerified(user.getMobile(), OtpType.MOBILE);
         
         if (!emailVerified && !mobileVerified) {
             throw new RuntimeException("Please verify both email and mobile before signup.");
         }

    	
    	user.setUserId("USR-" + System.currentTimeMillis());
        user.setEmail(user.getEmail());
        user.setMobile(user.getMobile());
        user.setName(user.getName());
        user.setCountry(user.getCountry());
        // hash password
        user.setPassword(passwordEncoder.encode(rawPassword));
        
        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("BUYER");
        }
        
        return userRepository.save(user);
    }

    public String login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        return jwtUtil.generateToken(email); // return JWT token
    }

    public User getUserProfile(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
    
//    public String login(String email, String rawPassword) {
//        Optional<User> optionalUser = userRepository.findByEmail(email);
//        if (optionalUser.isPresent()) {
//            User user = optionalUser.get();
//            if (passwordEncoder.matches(rawPassword, user.getPassword())) {
//                return jwtUtil.generateToken(email);
//            }
//        }
//        throw new RuntimeException("Invalid email or password");
//    }
}
