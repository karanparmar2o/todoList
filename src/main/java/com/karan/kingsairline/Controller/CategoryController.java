package com.karan.kingsairline.Controller;

import com.karan.kingsairline.Repository.CategoryRepo;
import com.karan.kingsairline.Repository.UsersRepo;
import com.karan.kingsairline.modules.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/categories")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class CategoryController {

    @Autowired
    private CategoryRepo categoryRepo;

    @Autowired
    private UsersRepo userRepo;

    // ✅ Get categories for a specific user
    @GetMapping("/user/{userId}")
    public List<Category> getCategoriesByUser(@PathVariable int userId) {
        return categoryRepo.findByUserId(userId);
    }

    // ✅ Add a new category
    @PostMapping
    public Category addCategory(@RequestBody Category category) {
        // optionally check if user exists
        return categoryRepo.save(category);
    }

    // ✅ Update a category
    @PutMapping("/{id}")
    public Category updateCategory(@PathVariable int id, @RequestBody Category updatedCategory) {
        Optional<Category> optionalCategory = categoryRepo.findById(id);
        if (optionalCategory.isPresent()) {
            Category category = optionalCategory.get();
            category.setCategory(updatedCategory.getCategory());
            category.setDiscription(updatedCategory.getDiscription());
            return categoryRepo.save(category);
        } else {
            throw new RuntimeException("Category not found");
        }
    }

    // ✅ Delete a category
    @DeleteMapping("/{id}")
    public String deleteCategory(@PathVariable int id) {
        categoryRepo.deleteById(id);
        return "Category deleted successfully";
    }
}
