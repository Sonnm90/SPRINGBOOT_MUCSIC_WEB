package com.example.demo.service.comment;

import com.example.demo.model.Comment;

import java.util.List;
import java.util.Optional;

public interface ICommentService {
    Optional<Comment> findById(Long id);
    List<Comment> findAll();
    List<Comment> findAllBySong_Id(Long id);
    List<Comment> findAllByUser_Id(Long id);
    Comment save(Comment comment);
    Comment update(Comment comment);
    void  deleteById(Long id);
    List<Comment>findCommentsBySongId(Long id);
}
