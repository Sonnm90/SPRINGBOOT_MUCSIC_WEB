package com.example.demo.service.singer;
import com.example.demo.model.Category;
import com.example.demo.model.Singer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ISingerService {
    List<Singer> findAll();
    List<Singer> findByName(String name);
    Page<Singer> findAll(Pageable pageable);
    Singer save(Singer singer);
    Singer updateSinger(Singer singer);
    Optional<Singer> findById(Long id);
    void deleteById(Long id);
}
