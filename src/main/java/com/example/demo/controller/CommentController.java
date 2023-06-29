package com.example.demo.controller;

import com.example.demo.config.Message;
import com.example.demo.dto.request.CommentDTO;
import com.example.demo.dto.response.ResponMessage;
import com.example.demo.model.Comment;
import com.example.demo.model.Song;
import com.example.demo.model.User;
import com.example.demo.security.userprincal.UserDetailService;
import com.example.demo.service.comment.ICommentService;
import com.example.demo.service.song.ISongService;
import com.example.demo.service.user.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/comments")
@CrossOrigin(origins = "*")
public class CommentController {
    @Autowired
    private ICommentService commentService;
    @Autowired
    private IUserService userService;
    @Autowired
    private UserDetailService userDetailService;
    @Autowired
    private ISongService songService;

    @GetMapping
    public ResponseEntity<?> showListComments(){
        return new ResponseEntity<>(commentService.findAll(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> creatComment(@RequestBody CommentDTO commentDTO){
        User user = userDetailService.getCurrentUser();
        Comment comment = new Comment();
        if (!user.isStatus()) {
            return new ResponseEntity<>(new ResponMessage(Message.ACCESS_DENIED), HttpStatus.OK);
        }
        comment.setUser(user);
        comment.setSong(commentDTO.getSong());
        comment.setContent(commentDTO.getContent());
        commentService.save(comment);
        return new ResponseEntity<>(new ResponMessage(Message.CREATE_SUCCESS),HttpStatus.OK);
    }
    @GetMapping("/song/{id}")
    public ResponseEntity<?> getListCommentsOfSong(@PathVariable Long id){
        Optional<Song> song = songService.findById(id);
        User user = userDetailService.getCurrentUser();
        if (!user.isStatus()) {
            return new ResponseEntity<>(new ResponMessage(Message.ACCESS_DENIED), HttpStatus.OK);
        }
        if (!song.isPresent()){
            return new ResponseEntity<>(new ResponMessage(Message.SONG_NOT_FOUND),HttpStatus.OK);
        }
        List<Comment> comments = commentService.findAllBySong_Id(id);

        return new ResponseEntity<>(comments,HttpStatus.OK);
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<?> getListCommentsOfUser(@PathVariable Long id){
        Optional<User> user = userService.findByUserId(id);
        User user1 = userDetailService.getCurrentUser();
        if (!user1.isStatus()) {
            return new ResponseEntity<>(new ResponMessage(Message.ACCESS_DENIED), HttpStatus.OK);
        }
        if (!user.isPresent()){
            return new ResponseEntity<>(new ResponMessage(Message.SONG_NOT_FOUND),HttpStatus.OK);
        }
        if (userService.getUserRole(user1).equals("USER") && !userService.checkUser(user.get())){
            return new ResponseEntity<>(new ResponMessage(Message.ACCESS_DENIED),HttpStatus.OK);
        }
        return new ResponseEntity<>(commentService.findAllByUser_Id(id),HttpStatus.OK);
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> detailComment(@PathVariable Long id){
        Optional<Comment> comment = commentService.findById(id);
        if (!comment.isPresent()){
            return new ResponseEntity<>(new ResponMessage(Message.NOT_FOUND),HttpStatus.OK);
        }
        return new ResponseEntity<>(comment,HttpStatus.OK);
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> updateComment(@RequestBody CommentDTO commentDTO,@PathVariable Long id){
        Optional<Comment> comment1 = commentService.findById(id);
        User user = userDetailService.getCurrentUser();
        if (!user.isStatus()) {
            return new ResponseEntity<>(new ResponMessage(Message.ACCESS_DENIED), HttpStatus.OK);
        }
        if (!comment1.isPresent()){
            return new ResponseEntity<>(new ResponMessage(Message.NOT_FOUND),HttpStatus.OK);
        }
        if (!userService.getUserRole(user).equals("ADMIN") && !userService.checkUser(comment1.get().getUser())){
            return new ResponseEntity<>(new ResponMessage(Message.ACCESS_DENIED),HttpStatus.OK);
        }
        comment1.get().setContent(commentDTO.getContent());
        commentService.save(comment1.get());
        return new ResponseEntity<>(comment1.get(),HttpStatus.OK);
    }
    @DeleteMapping ("/{id}")
    public ResponseEntity<?> deleteComment(@PathVariable Long id){
        Optional<Comment> comment1 = commentService.findById(id);
        User user = userDetailService.getCurrentUser();
        if (!user.isStatus()) {
            return new ResponseEntity<>(new ResponMessage(Message.ACCESS_DENIED), HttpStatus.OK);
        }
        if (!comment1.isPresent()){
            return new ResponseEntity<>(new ResponMessage(Message.NOT_FOUND),HttpStatus.OK);
        }
        if (!userService.getUserRole(user).equals("ADMIN") && !userService.checkUser(comment1.get().getUser())){
            return new ResponseEntity<>(new ResponMessage(Message.ACCESS_DENIED),HttpStatus.OK);
        }
        commentService.deleteById(id);
        return new ResponseEntity<>(new ResponMessage(Message.DELETE_SUCCESS),HttpStatus.OK);
    }
}
