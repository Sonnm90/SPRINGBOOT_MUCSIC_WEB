package com.example.demo.repository;

import com.example.demo.model.Singer;
import com.example.demo.model.Song;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface ISingerRepository extends JpaRepository<Singer, Long> {
    List<Singer> findByNameContaining(String name);

}
