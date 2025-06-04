package com.karan.kingsairline.Repository;

import com.karan.kingsairline.modules.Tasks;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepo extends JpaRepository<Tasks,Integer> {

}
