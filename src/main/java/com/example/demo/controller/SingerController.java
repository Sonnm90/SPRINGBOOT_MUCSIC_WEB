package com.example.demo.controller;

import com.example.demo.config.Message;
import com.example.demo.dto.response.ResponMessage;
import com.example.demo.model.Singer;
import com.example.demo.model.Song;
import com.example.demo.model.User;
import com.example.demo.security.userprincal.UserDetailService;
import com.example.demo.service.singer.ISingerService;

import com.example.demo.service.song.ISongService;
import com.example.demo.service.user.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/singers")
@CrossOrigin(origins = "*")
public class SingerController {
    @Autowired
    private ISingerService singerService;
    @Autowired
    private ISongService songService;
    @Autowired
    private IUserService userService;
    @Autowired
    private UserDetailService userDetailService;

    @GetMapping
    public ResponseEntity<?> showListSinger() {
        return new ResponseEntity<>(singerService.findAll(), HttpStatus.OK);
    }
    @GetMapping("/page")
    public ResponseEntity<?> pageSinger(Pageable pageable) {
        return new ResponseEntity<>(singerService.findAll(pageable), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> createSinger(@RequestBody Singer singer) {
        User user = userDetailService.getCurrentUser();
        if (!user.isStatus()) {
            return new ResponseEntity<>(new ResponMessage(Message.ACCESS_DENIED), HttpStatus.OK);
        }
        singerService.save(singer);
        return new ResponseEntity<>(new ResponMessage(Message.CREATE_SUCCESS), HttpStatus.OK);
    }
    @GetMapping("/search")
    public ResponseEntity<?> getListSearchSingerByName(@RequestParam String name){
        List<Singer> singers = singerService.findByName(name);
        if (singers.isEmpty()){
            return new ResponseEntity<>(new ResponMessage("singer "+Message.NOT_FOUND),HttpStatus.OK);
        }
        return new ResponseEntity<>(singers,HttpStatus.OK);
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> detailSinger(@PathVariable Long id) {
        Optional<Singer> singer = singerService.findById(id);

        if (!singer.isPresent()){
            return new ResponseEntity<>(new ResponMessage("singer "+Message.NOT_FOUND),HttpStatus.OK);
        } else {
            List<Song> songList = songService.findSongsBySingerId(id);
            List<Long> longList = new ArrayList<>();
            songList.forEach(song ->
                longList.add(song.getId())
            );
            singer.get().setSongs(longList);
            return new ResponseEntity<>(singer,HttpStatus.OK);
        }
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> updateSinger(@PathVariable Long id, @RequestBody Singer singer) {
        Optional<Singer> singer1 = singerService.findById(id);
        User user = userDetailService.getCurrentUser();
        if (!singer1.isPresent()) {
            return new ResponseEntity<>(new ResponMessage("singer "+Message.NOT_FOUND), HttpStatus.OK);
        } else {
            if (userService.getUserRole(user).equals("USER")) {
                return new ResponseEntity<>(new ResponMessage(Message.ACCESS_DENIED), HttpStatus.OK);
            }
            if (!singer.getAvatar().equals(singer1.get().getAvatar())) {
                singer.setId(singer1.get().getId());
            }
            if (singer.getAvatar().equals(singer1.get().getAvatar()) && (singer.getName().equals(singer1.get().getName()))) {
                return new ResponseEntity<>(new ResponMessage(Message.NO_CHANGE), HttpStatus.OK);
            }
            singer.setId(singer1.get().getId());
            singerService.save(singer);
            return new ResponseEntity<>(new ResponMessage(Message.UPDATE_SUCCESS), HttpStatus.OK);
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSinger(@PathVariable Long id) {
        Optional<Singer> singer = singerService.findById(id);
        User user = userDetailService.getCurrentUser();
        if (!singer.isPresent()) {
            return new ResponseEntity<>(new ResponMessage("singer "+Message.NOT_FOUND), HttpStatus.OK);
        }
        if (!userService.getUserRole(user).equals("ADMIN")) {
            return new ResponseEntity<>(new ResponMessage(Message.ACCESS_DENIED), HttpStatus.OK);
        }
        List<Song> songs = songService.findSongsBySingerId(id);
        if (!songs.isEmpty()) {
            songs.forEach(song -> {
                song.getSingerList().remove(singer.get());
                songService.updateSong(song);
            });

        }
        singerService.deleteById(id);
        return new ResponseEntity<>(new ResponMessage(Message.DELETE_SUCCESS), HttpStatus.OK);
    }
}
