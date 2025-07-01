package com.karan.kingsairline.Controller;

import com.karan.kingsairline.Exception.ResourceNotFoundException;
import com.karan.kingsairline.Repository.UsersRepo;
import com.karan.kingsairline.Utility.JwtUtil;
import com.karan.kingsairline.modules.LoginRequest;
import com.karan.kingsairline.modules.User;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.*;

@RestController
@RequestMapping("/api/v1/")
public class UserController {
    @Autowired
    UsersRepo urepo;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public UserController() {
        System.out.println("UserController loaded");
    }

    @GetMapping("/users")
    public List<User> getAllUsers() {
        List<User> users = urepo.findAll();
        // Remove passwords before returning
        users.forEach(user -> user.setPassword(null));
        return users;
    }

    @PostMapping("/users")
    public ResponseEntity<?> createUser(@RequestBody User user) {
        if (urepo.findByEmail(user.getEmail()) != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "Email already registered"));
        }
        if (urepo.findByUname(user.getUname()) != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "Username already registered"));
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = urepo.save(user);
        savedUser.setPassword(null);
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<?> getUserById(@PathVariable int id) {
        User user = urepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        user.setPassword(null);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/user/{id}")
    public ResponseEntity<?> updateUser(@PathVariable int id, @RequestBody User userDetail) {
        User user = urepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        user.setUname(userDetail.getUname());
        user.setEmail(userDetail.getEmail());
        if (userDetail.getPassword() != null && !userDetail.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDetail.getPassword()));
        }
        User updatedUser = urepo.save(user);
        updatedUser.setPassword(null);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/user/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable int id) {
        User user = urepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not Found"));
        urepo.delete(user);
        return ResponseEntity.ok(Map.of("message", "User deleted successfully"));
    }

    @PostMapping("/users/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        try {
            User user = urepo.findByEmail(loginRequest.getEmail());
            if (user == null || !passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid email or password"));
            }
            String token = jwtUtil.generateToken(user.getEmail());

            Cookie cookie = new Cookie("jwt", token);
            cookie.setHttpOnly(true);
            cookie.setSecure(true); // important if SameSite=None
            cookie.setPath("/");
            response.addCookie(cookie);// Important if frontend and backend are on different origins
            response.addHeader("Set-Cookie",
                    "jwt=" + token + "; Path=/; HttpOnly; Secure; SameSite=None");

            user.setPassword(null);
            // Return user info as JSON
            return ResponseEntity.ok(Map.of("message", "Login Successful", "user", user));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Internal server error: " + e.getMessage()));
        }
    }

    @GetMapping("/user/me")
    public ResponseEntity<?> getLoggedInUser(@CookieValue(value = "jwt", required = false) String token) {
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "JWT cookie missing"));
        }
        String email;
        try {
            email = jwtUtil.validateTokenAndGetEmail(token);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid or expired JWT"));
        }
        User user = urepo.findByEmail(email);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "User not found"));
        }
        user.setPassword(null);
        return ResponseEntity.ok(user);
    }
}