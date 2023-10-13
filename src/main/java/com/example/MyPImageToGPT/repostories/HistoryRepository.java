package com.example.MyPImageToGPT.repostories;

import com.example.MyPImageToGPT.Entities.History;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import java.util.List;

public interface HistoryRepository extends JpaRepository<History, Long> {
    // Additional query methods if required...
    List<History> findByUserId(Integer userId);

}
