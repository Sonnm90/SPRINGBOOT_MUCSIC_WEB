package com.example.demo.controller;

import com.example.demo.config.Message;
import com.example.demo.dto.request.PlaylistDTO;
import com.example.demo.dto.response.ResponMessage;
import com.example.demo.model.Playlist;
import com.example.demo.model.Song;
import com.example.demo.model.User;
import com.example.demo.security.userprincal.UserDetailService;
import com.example.demo.service.playlist.IPlaylistService;
import com.example.demo.service.song.ISongService;
import com.example.demo.service.user.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

@RestController
@RequestMapping("/playlists")
@CrossOrigin(origins = "*")
public class PlaylistController {
    @Autowired
    private IPlaylistService playlistService;
    @Autowired
    private ISongService songService;
    @Autowired
    private IUserService userService;
    @Autowired
    private UserDetailService userDetailService;

    @GetMapping
    public ResponseEntity<?> showListPlaylist() {
        return new ResponseEntity<>(playlistService.findAll(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> createPlaylist(@RequestBody PlaylistDTO playlistDTO) {
        User user = userDetailService.getCurrentUser();
        if (!user.isStatus()) {
            return new ResponseEntity<>(new ResponMessage(Message.ACCESS_DENIED), HttpStatus.OK);
        }
        Playlist playlist = new Playlist(playlistDTO.getName(), playlistDTO.getAvatar());
        playlistService.save(playlist);
        return new ResponseEntity<>(new ResponMessage(Message.CREATE_SUCCESS), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePlaylist(@PathVariable Long id, @RequestBody PlaylistDTO playlistDTO) {
        Optional<Playlist> playlist = playlistService.findById(id);
        User user = userDetailService.getCurrentUser();
        if (!user.isStatus()) {
            return new ResponseEntity<>(new ResponMessage(Message.ACCESS_DENIED), HttpStatus.OK);
        }
        if (!playlist.isPresent()) {
            return new ResponseEntity<>(new ResponMessage("playlist " + Message.NOT_FOUND), HttpStatus.OK);
        } else {
            if (!userService.checkUser(playlist.get().getUser()) && (userService.getUserRole(user).equals("USER"))) {
                return new ResponseEntity<>(new ResponMessage(Message.ACCESS_DENIED), HttpStatus.OK);
            }
            Playlist updatePl = playlist.get();
            if (!playlistDTO.getAvatar().equals(playlist.get().getAvatar())) {
                updatePl.setAvatar(playlistDTO.getAvatar());
            }
            if (!playlistDTO.getName().equals(playlist.get().getName())) {
                if (playlistService.existsByName(playlistDTO.getName())) {
                    return new ResponseEntity<>(new ResponMessage(Message.NAME_EXISTED), HttpStatus.OK);
                }
            }
            if (playlistDTO.getAvatar().equals(playlist.get().getAvatar()) && (playlistDTO.getName().equals(playlist.get().getName()))) {
                return new ResponseEntity<>(new ResponMessage(Message.NO_CHANGE), HttpStatus.OK);
            }
            updatePl.setName(playlistDTO.getName());
            playlistService.save(updatePl);
            return new ResponseEntity<>(new ResponMessage(Message.UPDATE_SUCCESS), HttpStatus.OK);
        }

    }

    @GetMapping("/{id}")
    public ResponseEntity<?> detailPlaylist(@PathVariable Long id) {
        Optional<Playlist> playlist = playlistService.findById(id);
        if (!playlist.isPresent()) {
            return new ResponseEntity<>(new ResponMessage("playlist " + Message.NOT_FOUND), HttpStatus.OK);
        }
        return new ResponseEntity<>(playlist.get(), HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<?> getListSearchPlaylistByName(@RequestParam String name) {
        List<Playlist> playlist = playlistService.findByName(name);
        if (playlist.isEmpty()) {
            return new ResponseEntity<>(new ResponMessage("playlist " + Message.NOT_FOUND), HttpStatus.OK);
        }
        return new ResponseEntity<>(playlist, HttpStatus.OK);
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<?> showAllPlaylistOfUser(@PathVariable Long id) {
        User user = userDetailService.getCurrentUser();
        if (!userService.findByUserId(id).isPresent()) {
            return new ResponseEntity<>(new ResponMessage("user " + Message.NOT_FOUND), HttpStatus.OK);
        }
        User user1 = userService.findByUserId(id).get();
        if (!user.isStatus()) {
            return new ResponseEntity<>(new ResponMessage(Message.ACCESS_DENIED), HttpStatus.OK);
        }
        if (userService.getUserRole(user).equals("USER") && !userService.checkUser(user1)) {
            return new ResponseEntity<>(new ResponMessage(Message.ACCESS_DENIED), HttpStatus.OK);
        }
        List<Playlist> playlists = playlistService.findPlaylistByUserId(id);
        return new ResponseEntity<>(playlists, HttpStatus.OK);
    }


    @PutMapping("/{plId}/{songId}")
    public ResponseEntity<?> addSongToPlaylist(@PathVariable(value = "plId") Long plId,
                                               @PathVariable(value = "songId") Long songId) {
        User user = userDetailService.getCurrentUser();
        Optional<Playlist> playlist = playlistService.findById(plId);
        Optional<Song> song = songService.findById(songId);
        AtomicBoolean check = new AtomicBoolean(false);
        if (!user.isStatus()) {
            return new ResponseEntity<>(new ResponMessage(Message.ACCESS_DENIED), HttpStatus.OK);
        }
        List<Song> listSong ;
        if (!playlist.isPresent()) {
            return new ResponseEntity<>(new ResponMessage("playlist " + Message.NOT_FOUND), HttpStatus.OK);
        } else {
            if (!userService.checkUser(playlist.get().getUser()) && (userService.getUserRole(user).equals("USER"))) {
                return new ResponseEntity<>(new ResponMessage(Message.ACCESS_DENIED), HttpStatus.OK);
            }

            Playlist playlist1 = playlist.get();
            listSong = playlist1.getSongs();
            if (!song.isPresent()) {
                return new ResponseEntity<>(new ResponMessage(Message.SONG_NOT_FOUND), HttpStatus.OK);
            } else {
                listSong.forEach(song1 -> {
                    if (song1.getId().equals(song.get().getId())) {
                        check.set(true);
                    }
                });
                if (check.get()) {
                    return new ResponseEntity<>(new ResponMessage(Message.SONG_EXISTED_ON_PLAYLIST), HttpStatus.OK);
                }
                listSong.add(song.get());
                playlist1.setSongs(listSong);
                playlistService.save(playlist1);
                return new ResponseEntity<>(new ResponMessage(Message.ADD_SUCCESS), HttpStatus.OK);
            }
        }
    }

    @DeleteMapping("/{plId}/{songId}")
    public ResponseEntity<?> removeSongToPlaylist(@PathVariable(value = "plId") Long plId,
                                                  @PathVariable(value = "songId") Long songId) {
        User user = userDetailService.getCurrentUser();
        Optional<Playlist> playlist = playlistService.findById(plId);
        AtomicBoolean check = new AtomicBoolean(false);
        if (!user.isStatus()) {
            return new ResponseEntity<>(new ResponMessage(Message.ACCESS_DENIED), HttpStatus.OK);
        }
        Optional<Song> song = songService.findById(songId);
        if (!playlist.isPresent()) {
            return new ResponseEntity<>(new ResponMessage("playlist " + Message.NOT_FOUND), HttpStatus.OK);
        } else {
            if (!userService.checkUser(playlist.get().getUser()) && (userService.getUserRole(user).equals("USER"))) {
                return new ResponseEntity<>(new ResponMessage(Message.ACCESS_DENIED), HttpStatus.OK);
            }
            Playlist playlist1 = playlist.get();
            if (!song.isPresent()) {
                return new ResponseEntity<>(new ResponMessage(Message.SONG_NOT_FOUND), HttpStatus.OK);
            } else {
                playlist1.getSongs().forEach(song1 -> {
                    if (song1.getId().equals(song.get().getId())) {
                        check.set(true);
                    }
                });
                if (!check.get()) {
                    return new ResponseEntity<>(new ResponMessage(Message.SONG_NOT_EXISTED_ON_PLAYLIST), HttpStatus.OK);
                }
                playlist1.getSongs().remove(song.get());
                playlistService.update(playlist1);
                return new ResponseEntity<>(new ResponMessage(Message.REMOVE_SUCCESS), HttpStatus.OK);
            }
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePlaylist(@PathVariable Long id) {
        Optional<Playlist> playlist = playlistService.findById(id);
        User user = userDetailService.getCurrentUser();
        if (!user.isStatus()) {
            return new ResponseEntity<>(new ResponMessage(Message.ACCESS_DENIED), HttpStatus.OK);
        }
        if (!playlist.isPresent()) {
            return new ResponseEntity<>(new ResponMessage("playlist " + Message.NOT_FOUND), HttpStatus.OK);
        }
        if (!userService.checkUser(playlist.get().getUser()) && (userService.getUserRole(user).equals("USER"))) {
            return new ResponseEntity<>(new ResponMessage(Message.ACCESS_DENIED), HttpStatus.OK);
        }
        playlistService.deleteById(id);
        return new ResponseEntity<>(new ResponMessage(Message.DELETE_SUCCESS), HttpStatus.OK);
    }

}
