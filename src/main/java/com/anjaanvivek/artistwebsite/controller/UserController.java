package com.anjaanvivek.artistwebsite.controller;

import com.anjaanvivek.artistwebsite.model.OtpType;
import com.anjaanvivek.artistwebsite.model.User;
import com.anjaanvivek.artistwebsite.service.OtpService;
import com.anjaanvivek.artistwebsite.service.UserService;
import lombok.Data;   // Lombok
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class UserController {

    @Autowired
    private UserService userService;
    

    @Autowired
    private OtpService otpService;

    @PostMapping("/signup")
    public ResponseEntity<User> signup(@RequestBody UserSignupRequest request) {
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setMobile(request.getMobile());
        user.setCountry(request.getCountry());
        User savedUser = userService.signup(user, request.getPassword());
        return ResponseEntity.ok(savedUser);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
    	System.out.print("reached hereeeee");
    	String token = userService.login(request.getEmail(), request.getPassword());
        return ResponseEntity.ok(new AuthResponse(token));
    }
    
    @PostMapping("/send-otp")
    public ResponseEntity<?> sendOtp(@RequestBody SendOtpRequest req) {
    	System.out.print("Reached to send Otp");
        try {
            OtpType type = OtpType.valueOf(req.getType().toUpperCase());
            otpService.createAndSendOtp(req.getTarget(), type);
            return ResponseEntity.ok("OTP sent");
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body("Invalid OTP type");
        } catch (Exception ex) {
        	System.out.print(ex);
            return ResponseEntity.status(500).body("Failed to send OTP");
        }
    }

    // Verify OTP:
    // body: { "target": "...", "type":"EMAIL", "code":"123456" }
    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody VerifyOtpRequest req) {
        try {
            OtpType type = OtpType.valueOf(req.getType().toUpperCase());
            boolean ok = otpService.verifyOtp(req.getTarget(), type, req.getCode());
            if (ok) return ResponseEntity.ok("Verified");
            else return ResponseEntity.badRequest().body("Invalid or expired OTP");
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body("Invalid OTP type");
        }
    }
    
    @GetMapping("/profile")
    public ResponseEntity<User> getProfile(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).build();
        }
        String email = principal.getName(); // JWT Filter sets this
        User user = userService.getUserProfile(email);
        
        // Security: Don't send the password back to frontend
        user.setPassword(null); 
        
        return ResponseEntity.ok(user);
    }
    
    
}

// --- DTOs ---

@Data
@NoArgsConstructor
@AllArgsConstructor
class UserSignupRequest {
    private String name;
    private String email;
    private String mobile;
    private String country;
    private String password;
    
	public String getName() {
		return name;
	}

	public String getPassword() {
		// TODO Auto-generated method stub
		return password;
	}

	public String getCountry() {
		// TODO Auto-generated method stub
		return country;
	}

	public String getMobile() {
		// TODO Auto-generated method stub
		return mobile;
	}

	public String getEmail() {
		// TODO Auto-generated method stub
		return email;
	}
	
}

@Data
class LoginRequest {
    private String email;
    private String password;
	public String getEmail() {
		return email;
	}
	public String getPassword() {
		return password;
	}
}

class AuthResponse {
    private String token;
    public AuthResponse(String token) { this.token = token; }
    public String getToken() { return token; }
}


@Data
@NoArgsConstructor
class SendOtpRequest {
    private String target;
    private String type; // EMAIL or MOBILE
	public String getTarget() {
		return target;
	}
	public void setTarget(String target) {
		this.target = target;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
}

@Data
@NoArgsConstructor
class VerifyOtpRequest {
    private String target;
    private String type; // EMAIL or MOBILE
    private String code;
	public String getTarget() {
		return target;
	}
	public void setTarget(String target) {
		this.target = target;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
}