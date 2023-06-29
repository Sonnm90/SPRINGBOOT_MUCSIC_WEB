package com.example.demo.controller;

import com.example.demo.config.Message;
import com.example.demo.dto.request.SongDTO;
import com.example.demo.dto.response.ResponMessage;
import com.example.demo.model.Comment;
import com.example.demo.model.Playlist;
import com.example.demo.model.Song;
import com.example.demo.model.User;
import com.example.demo.security.userprincal.UserDetailService;
import com.example.demo.service.comment.ICommentService;
import com.example.demo.service.playlist.IPlaylistService;
import com.example.demo.service.song.ISongService;
import com.example.demo.service.user.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

@RestController
@RequestMapping("/songs")
@CrossOrigin(origins = "*")
public class SongController {
    @Autowired
    private ISongService songService;
    @Autowired
    private IUserService userService;
    @Autowired
    private UserDetailService userDetailService;
    @Autowired
    private IPlaylistService playlistService;
    @Autowired
    private ICommentService commentService;

    @GetMapping
    public ResponseEntity<?> showListSong() {
        return new ResponseEntity<>(songService.findAll(), HttpStatus.OK);
    }

    @GetMapping("/rdSongs")
    public ResponseEntity<?> getList5RandomSong() {
        return new ResponseEntity<>(songService.getList5Random(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> createSong(@Valid @RequestBody SongDTO songDTO) {
        Song song = new Song(songDTO.getName(), songDTO.getAvatar(), songDTO.getCategory(), songDTO.getLyrics(), songDTO.getSrc(), songDTO.getBandList(), songDTO.getSingerList());
        if (song.getSingerList()==null && song.getBandList()==null) {
            return new ResponseEntity<>(new ResponMessage(Message.ERROR), HttpStatus.OK);
        }
        songService.save(song);
        return new ResponseEntity<>(new ResponMessage(Message.CREATE_SUCCESS), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> playSong(@PathVariable Long id) {
        Optional<Song> song = songService.findById(id);
        if (!song.isPresent()) {
            return new ResponseEntity<>(new ResponMessage("song " + Message.NOT_FOUND), HttpStatus.OK);
        }
        song.get().setNumberOfView(song.get().getNumberOfView() + 1);
        songService.updateSong(song.get());
        return new ResponseEntity<>(songService.findById(id), HttpStatus.OK);
    }

    @GetMapping("/song/{id}")
    public ResponseEntity<?> detailSong(@PathVariable Long id) {
        Optional<Song> song = songService.findById(id);
        if (!song.isPresent()) {
            return new ResponseEntity<>(new ResponMessage("song " + Message.NOT_FOUND), HttpStatus.OK);
        }

        return new ResponseEntity<>(songService.findById(id), HttpStatus.OK);
    }

    @GetMapping("/page")
    public ResponseEntity<?> pageSong(@PageableDefault(size = 5) Pageable pageable) {
        return new ResponseEntity<>(songService.findAll(pageable), HttpStatus.OK);
    }

    @GetMapping("/topView")
    public ResponseEntity<?> showTopSong() {
        return new ResponseEntity<>(songService.findTopViewOfSong(), HttpStatus.OK);
    }

    @GetMapping("/topLike")
    public ResponseEntity<?> showTopLike() {
        return new ResponseEntity<>(songService.findTop10ByOrderByLikeUserDesc(), HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<?> getListSearchSongByName(@RequestParam String name) {
        List<Song> songs = songService.findByName(name);
        if (songs.isEmpty()) {
            return new ResponseEntity<>(new ResponMessage(Message.SONG_NOT_FOUND), HttpStatus.OK);
        }
        return new ResponseEntity<>(songs, HttpStatus.OK);
    }

    @GetMapping("/like-user/{id}")
    public ResponseEntity<?> showListSongOfUserLike(@PathVariable Long id) {
        Optional<User> user = userService.findByUserId(id);
        User user1 = userDetailService.getCurrentUser();
        if (!user.isPresent()) {
            return new ResponseEntity<>(new ResponMessage("user " + Message.NOT_FOUND), HttpStatus.OK);
        }
        if (userService.getUserRole(user1).equals("USER") && !userService.checkUser(user.get())) {
            return new ResponseEntity<>(new ResponMessage(Message.ACCESS_DENIED), HttpStatus.OK);
        }
        List<Song> songs = songService.findSongsByUserIsLike(id);
        return new ResponseEntity<>(songs, HttpStatus.OK);
    }

    @GetMapping("/like-song/{id}")
    public ResponseEntity<?> showListUsersLikeOfSong(@PathVariable Long id) {
        User user = userDetailService.getCurrentUser();
        Optional<Song> song = songService.findById(id);
        if (!song.isPresent()) {
            return new ResponseEntity<>(new ResponMessage(Message.SONG_NOT_FOUND), HttpStatus.OK);
        }
        if (userService.getUserRole(user).equals("USER") && !userService.checkUser(song.get().getUser())) {
            return new ResponseEntity<>(new ResponMessage(Message.ACCESS_DENIED), HttpStatus.OK);
        }
        List<User> userList = song.get().getLikeUser();
        return new ResponseEntity<>(userList, HttpStatus.OK);
    }

    @GetMapping("/comments/{id}")
    public ResponseEntity<?> getListCommentsOfSong(@PathVariable Long id) {
        List<Comment> comments = commentService.findCommentsBySongId(id);
        Optional<Song> song = songService.findById(id);
        if (!song.isPresent()) {
            return new ResponseEntity<>(new ResponMessage(Message.SONG_NOT_FOUND), HttpStatus.OK);
        }
        if (comments.isEmpty()) {
            return new ResponseEntity<>(new ResponMessage("comment " + Message.NOT_FOUND), HttpStatus.OK);
        }
        return new ResponseEntity<>(comments, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSong(@PathVariable Long id) {
        Optional<Song> song = songService.findById(id);
        User user = userDetailService.getCurrentUser();

        if (!song.isPresent()) {
            return new ResponseEntity<>(new ResponMessage(Message.SONG_NOT_FOUND), HttpStatus.OK);
        }
        if (!userService.checkUser(song.get().getUser()) && (userService.getUserRole(user).equals("USER"))) {
            return new ResponseEntity<>(new ResponMessage(Message.ACCESS_DENIED), HttpStatus.OK);
        }
        Optional<Playlist> playlist = playlistService.findPlaylistBySongId(id);
        if (playlist.isPresent()) {
            playlist.get().getSongs().remove(song.get());
            playlistService.update(playlist.get());
        }
        commentService.findCommentsBySongId(id).forEach(comment -> {
            commentService.deleteById(comment.getId());
        });
        songService.deleteById(id);
        return new ResponseEntity<>(new ResponMessage(Message.DELETE_SUCCESS), HttpStatus.OK);
    }

    @PutMapping("/user/{id}")
    public ResponseEntity<?> likeSong(@PathVariable Long id) {
        Optional<Song> song = songService.findById(id);
        User user = userDetailService.getCurrentUser();
        AtomicBoolean check = new AtomicBoolean(false);
        if (!song.isPresent()) {
            return new ResponseEntity<>(new ResponMessage(Message.SONG_NOT_FOUND), HttpStatus.OK);
        }
        List<User> userList = song.get().getLikeUser();
        userList.forEach(user1 -> {
            if (user1.getId().equals(user.getId())) {
                check.set(true);
            }
        });
        if (check.get()) {
            userList.remove(user);
        } else {
            userList.add(user);
        }
        song.get().setLikeUser(userList);
        songService.updateSong(song.get());
        return new ResponseEntity<>(new ResponMessage(Message.UPDATE_SUCCESS), HttpStatus.OK);
    }
}
