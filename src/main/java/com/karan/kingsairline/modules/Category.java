package com.karan.kingsairline.modules;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    String category;
    String discription;

    @ManyToOne
    private User user;
}
