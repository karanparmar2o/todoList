package com.karan.kingsairline.modules;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Tasks {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    String tname;
    @Enumerated(EnumType.STRING)
    private Status status;
    @ManyToOne
    User user;
    @ManyToOne
    Category category;

}
