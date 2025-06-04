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
        User user = urepo.findByEmail(loginRequest.getEmail());
        System.out.println("inside login");

        if (user == null || !passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            System.out.println("password is wrong");

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid email or password");
        }
        String token = jwtUtil.generateToken(user.getEmail());

        // Set HTTP-only cookie
        Cookie cookie = new Cookie("jwt", token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        response.addCookie(cookie);

        return ResponseEntity.ok("Login Successful.");
    }

    @GetMapping("/user/me")
    public ResponseEntity<User> getLoggedInUser(@CookieValue("jwt") String token) {
        String email = jwtUtil.validateTokenAndGetEmail(token);
        User user = urepo.findByEmail(email);
        user.setPassword(null);
        return ResponseEntity.ok(user);
    }



}
