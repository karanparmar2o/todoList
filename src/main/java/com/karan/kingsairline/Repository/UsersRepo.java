package com.karan.kingsairline.Repository;

import com.karan.kingsairline.modules.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsersRepo extends JpaRepository<User,Integer> {
    User findByEmail(String email);
    User findByUname(String uname);
}
