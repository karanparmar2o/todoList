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
import org.springframework.http.RequestEntity;
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
    public List<User> getAllUsers(){
        return urepo.findAll();
    }

    @PostMapping("/users")
    public ResponseEntity<User> createUser(@RequestBody User user){

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        User savedUser = urepo.save(user);
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<User> getUserByName(@PathVariable int id){
        User user = urepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        return ResponseEntity.ok(user);
    }
    @PutMapping("/user/{id}")
    public ResponseEntity<User> UpdateUser(@PathVariable int id, @RequestBody User userDetail){
        User user = urepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        user.setUname(userDetail.getUname());
        user.setEmail(userDetail.getEmail());
        user.setPassword(userDetail.getPassword());
        return ResponseEntity.ok(user);
    }
    @DeleteMapping("/user/{id}")
    public ResponseEntity<User> DeleteUser(@PathVariable int id){
        User user=urepo.findById(id).orElseThrow(()->new ResourceNotFoundException("User not Found"));
        urepo.delete(user);
        return ResponseEntity.ok(user);
    }
    @PostMapping("/users/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        try {
            User user = urepo.findByEmail(loginRequest.getEmail());
            System.out.println("inside login");

            // Add explicit null-check and print
            if (user == null) {
                System.out.println("user is null");
            } else {
                System.out.println("user is not null, password: " + user.getPassword());
            }

            if (user == null /*|| !passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())*/) {
                System.out.println("password is wrong");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Invalid email or password");
            }
            System.out.println("login success 1");
            try {
                String token = jwtUtil.generateToken(user.getEmail());
                System.out.println("login success 2");

                Cookie cookie = new Cookie("jwt", token);
                cookie.setHttpOnly(true);
                cookie.setPath("/");
                response.addCookie(cookie);

                System.out.println("login success");
                return ResponseEntity.ok("Login Successful.");
            } catch (Exception e) {
                System.out.println("JWT Generation Error: " + e.getMessage());
                e.printStackTrace();  // 👈 make sure stack trace shows full error
                return ResponseEntity.status(500).body("JWT Exception: " + e.getMessage());
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Internal server error: " + e.getMessage());
        }
    }

    @GetMapping("/user/me")
    public ResponseEntity<User> getLoggedInUser(@CookieValue("jwt") String token) {
        String email = jwtUtil.validateTokenAndGetEmail(token);
        User user = urepo.findByEmail(email);
        user.setPassword(null);
        return ResponseEntity.ok(user);
    }



}
