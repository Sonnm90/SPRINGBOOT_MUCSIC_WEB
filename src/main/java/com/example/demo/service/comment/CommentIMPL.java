package com.example.demo.service.comment;

import com.example.demo.model.Comment;
import com.example.demo.model.User;
import com.example.demo.repository.ICommentRepository;
import com.example.demo.security.userprincal.UserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CommentIMPL implements ICommentService {
    @Autowired
    private ICommentRepository commentRepository;
    @Autowired
    private UserDetailService userDetailService;

    @Override
    public Optional<Comment> findById(Long id) {
        return commentRepository.findById(id);
    }

    @Override
    public List<Comment> findAll() {
        return commentRepository.findAll();
    }

    @Override
    public List<Comment> findAllBySong_Id(Long id) {
        return commentRepository.findAllBySong_Id(id);
    }

    @Override
    public List<Comment> findAllByUser_Id(Long id) {
        return commentRepository.findAllByUser_Id(id);
    }

    @Override
    public Comment save(Comment comment) {
        User user = userDetailService.getCurrentUser();
        comment.setUser(user);
        return commentRepository.save(comment);
    }

    @Override
    public Comment update(Comment comment) {
        return commentRepository.save(comment);
    }

    @Override
    public void deleteById(Long id) {
        commentRepository.deleteById(id);
    }

    @Override
    public List<Comment> findCommentsBySongId(Long id) {
        return commentRepository.findCommentsBySongId(id);
    }
}
