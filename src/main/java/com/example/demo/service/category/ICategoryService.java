package com.example.demo.service.category;

import com.example.demo.model.Band;
import com.example.demo.model.Category;
import com.example.demo.model.Song;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ICategoryService {
    List<Category> findAll();
    Optional<Category> findById(Long id);
    List<Category> findByName(String name);
    Page<Category> findAll(Pageable pageable);
    Boolean existsByName(String name);
    Category save(Category category);
    Category update(Category category);
    void  deleteById(Long id);
}
