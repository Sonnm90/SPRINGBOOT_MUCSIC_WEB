package com.example.demo.controller;

import com.example.demo.config.Message;
import com.example.demo.dto.response.ResponMessage;
import com.example.demo.model.Category;
import com.example.demo.model.Song;
import com.example.demo.model.User;
import com.example.demo.security.userprincal.UserDetailService;
import com.example.demo.service.category.ICategoryService;
import com.example.demo.service.song.ISongService;
import com.example.demo.service.user.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/categories")
@CrossOrigin(origins = "*")
public class CategoryController {
    @Autowired
    private ICategoryService categoryService;
    @Autowired
    private ISongService songService;
    @Autowired
    private UserDetailService userDetailService;
    @Autowired
    private IUserService userService;

    @GetMapping
    public ResponseEntity<?> showListCategory() {
        return new ResponseEntity<>(categoryService.findAll(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> createCategory(@RequestBody Category category) {
        User user = userDetailService.getCurrentUser();
        if (!user.isStatus()) {
            return new ResponseEntity<>(new ResponMessage(Message.ACCESS_DENIED), HttpStatus.OK);
        }
        if (categoryService.existsByName(category.getName())) {
            return new ResponseEntity<>(new ResponMessage(Message.NAME_EXISTED), HttpStatus.OK);
        }
        categoryService.save(category);
        return new ResponseEntity<>(new ResponMessage(Message.CREATE_SUCCESS), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> detailCategory(@PathVariable Long id) {
        User user = userDetailService.getCurrentUser();
        if (!user.isStatus()) {
            return new ResponseEntity<>(new ResponMessage(Message.ACCESS_DENIED), HttpStatus.OK);
        }
        if (!categoryService.findById(id).isPresent()) {
            return new ResponseEntity<>(new ResponMessage("category "+Message.NOT_FOUND), HttpStatus.OK);
        } else {
            List<Song> songs = songService.findSongsByCategoryId(id);
            Category category = categoryService.findById(id).get();
            songs.forEach(song ->
                category.getSongs().add(song.getId())
            );
            return new ResponseEntity<>(category, HttpStatus.OK);
        }
    }

    @GetMapping("/page")
    public ResponseEntity<?> pageCategory(@PageableDefault(size = 3) Pageable pageable) {
        return new ResponseEntity<>(categoryService.findAll(pageable), HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<?> getListSearchCategoryByName(@RequestParam String name){
        List<Category> category = categoryService.findByName(name);
        if (category.isEmpty()){
            return new ResponseEntity<>(new ResponMessage("category "+Message.NOT_FOUND),HttpStatus.OK);
        }
        return new ResponseEntity<>(category,HttpStatus.OK);
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCategory(@PathVariable Long id, @RequestBody Category category) {
        Optional<Category> category1 = categoryService.findById(id);
        User user = userDetailService.getCurrentUser();
        if (!user.isStatus()) {
            return new ResponseEntity<>(new ResponMessage(Message.ACCESS_DENIED), HttpStatus.OK);
        }
        if (!category1.isPresent()) {
            return new ResponseEntity<>(new ResponMessage("category "+Message.NOT_FOUND), HttpStatus.OK);
        } else {
            if (!userService.getUserRole(user).equals("ADMIN")){
                return new ResponseEntity<>(new ResponMessage(Message.ACCESS_DENIED), HttpStatus.OK);
            }
            if (!category.getAvatar().equals(category1.get().getAvatar())) {
                category.setId(category1.get().getId());
            }
            if (!category.getName().equals(category1.get().getName())) {
                if (categoryService.existsByName(category.getName())) {
                    return new ResponseEntity<>(new ResponMessage(Message.NAME_EXISTED), HttpStatus.OK);
                }
            }
            if (category.getAvatar().equals(category1.get().getAvatar()) && (category.getName().equals(category1.get().getName()))) {
                return new ResponseEntity<>(new ResponMessage(Message.NO_CHANGE), HttpStatus.OK);
            }
            category.setId(category1.get().getId());
            categoryService.save(category);
            return new ResponseEntity<>(new ResponMessage(Message.UPDATE_SUCCESS), HttpStatus.OK);
        }

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable Long id) {
        Optional<Category> category = categoryService.findById(id);
        User user = userDetailService.getCurrentUser();
        if (!user.isStatus()) {
            return new ResponseEntity<>(new ResponMessage(Message.ACCESS_DENIED), HttpStatus.OK);
        }
        if (!category.isPresent()) {
            return new ResponseEntity<>(new ResponMessage("category "+Message.NOT_FOUND), HttpStatus.OK);
        } else {
            if (!userService.getUserRole(user).equals("ADMIN")){
                return new ResponseEntity<>(new ResponMessage(Message.NOT_FOUND), HttpStatus.OK);
            }
        }
        List<Song> songs = songService.findSongsByCategoryId(id);
        if (!categoryService.findById(5L).isPresent()){
            return new ResponseEntity<>(new ResponMessage(Message.ERROR), HttpStatus.OK);
        }
        Category category1 = categoryService.findById(5L).get();
        songs.forEach(song -> {
            song.setCategory(category1);
            songService.save(song);
        });
        categoryService.deleteById(id);
        return new ResponseEntity<>(new ResponMessage(Message.DELETE_SUCCESS), HttpStatus.OK);
    }
}
