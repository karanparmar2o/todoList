package com.karan.kingsairline.Controller;

import com.karan.kingsairline.Exception.ResourceNotFoundException;
import com.karan.kingsairline.Repository.CategoryRepo;
import com.karan.kingsairline.Repository.TaskRepo;
import com.karan.kingsairline.Repository.UsersRepo;
import com.karan.kingsairline.modules.Category;
import com.karan.kingsairline.modules.Tasks;
import com.karan.kingsairline.modules.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/")
public class TaskController {
    @Autowired
    TaskRepo taskRepo;
    @Autowired
    UsersRepo usersRepo;
    @Autowired
    CategoryRepo categoryRepo;
    @GetMapping("/tasks")
    public ResponseEntity<Page<Tasks>> getAllTasks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        Pageable pageable=  PageRequest.of(page,size);
        Page<Tasks> tasks = taskRepo.findAll(pageable);
        return ResponseEntity.ok(tasks);
    }
     @PostMapping("/tasks")
    public ResponseEntity<Tasks> addTask(@RequestBody TaskReq taskReq){
         User user=usersRepo.findById(taskReq.getUId()).orElseThrow(()->new ResourceNotFoundException("user ID not Found"));
         Category category=categoryRepo.findById(taskReq.getCId()).orElseThrow(()->new ResourceNotFoundException("Category Id not found"));
         Tasks task=new Tasks();
         task.setUser(user);
         task.setCategory(category);
         task.setTname(taskReq.getTname());
         task.setStatus(taskReq.getStatus());
         Tasks saveTask=taskRepo.save(task);
         return ResponseEntity.ok(saveTask);
     }
     @GetMapping("/task/{id}")
    public ResponseEntity<Tasks> getTaskById(@PathVariable int id){
            Tasks task= taskRepo.findById(id).orElseThrow(() ->new ResourceNotFoundException("ID was not found"));
            return ResponseEntity.ok(task);
     }
     @PutMapping("/task/{id}")
    public ResponseEntity<Tasks> upddateTask(@PathVariable int id, @RequestBody Tasks taskDetail){
            Tasks task=taskRepo.findById(id).orElseThrow(()->new ResourceNotFoundException("ID is not found."));
            task.setTname(taskDetail.getTname());
            task.setStatus(taskDetail.getStatus());
            task.setCategory(taskDetail.getCategory());
           // task.setUser(taskDetail.getUser());
            Tasks updatedTask=taskRepo.save(task);
            return ResponseEntity.ok(updatedTask);

     }
     @DeleteMapping("/task/{id}")
    public ResponseEntity<String> deleteTask(@PathVariable int id){
        Tasks task=taskRepo.findById(id).orElseThrow(()->new ResourceNotFoundException("ID is Not Found"));
        taskRepo.delete(task);
        return ResponseEntity.ok(id+" Deleted Sucessfully");
     }

}
