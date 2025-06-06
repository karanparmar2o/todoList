package com.karan.kingsairline.modules;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    @Column(unique = true)
    String uname;
    @Column(unique = true)
    String email;
    String phno;
    String password;
}
