package com.karan.kingsairline.Repository;

import com.karan.kingsairline.modules.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepo extends JpaRepository<Category,Integer> {
    List<Category> findByUserId(int userId);
}
