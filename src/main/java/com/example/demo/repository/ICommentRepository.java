package com.example.demo.repository;

import com.example.demo.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface ICommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllBySong_Id(Long id);
    List<Comment> findAllByUser_Id(Long id);
    List<Comment>findCommentsBySongId(Long id);
}
