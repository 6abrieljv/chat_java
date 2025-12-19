package com.chat.chat_java.controller;



import com.chat.chat_java.dto.AuthRequest;
import com.chat.chat_java.dto.AuthResponse;
import com.chat.chat_java.dto.RegisterRequest;
import com.chat.chat_java.model.User;
import com.chat.chat_java.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin("*")
public class AuthController {
    
    private final UserService userService;
    
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setAvatar(request.getAvatar() != null ? request.getAvatar() : 
                      request.getUsername().substring(0, 1).toUpperCase());
        
        userService.register(user);
        String token = userService.login(request.getUsername(), request.getPassword());
        
        return ResponseEntity.ok(new AuthResponse(
                token, 
                user.getUsername(),
                user.getName(),
                user.getAvatar()
        ));
    }
    
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        String token = userService.login(request.getUsername(), request.getPassword());
        User user = userService.getUserByUsername(request.getUsername());
        
        return ResponseEntity.ok(new AuthResponse(
                token,
                user.getUsername(),
                user.getName(),
                user.getAvatar()
        ));
    }
    
    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser(@RequestHeader("Authorization") String token) {
        String username = token.substring(7); // Remove "Bearer "
        User user = userService.getUserByUsername(username);
        return ResponseEntity.ok(user);
    }
}