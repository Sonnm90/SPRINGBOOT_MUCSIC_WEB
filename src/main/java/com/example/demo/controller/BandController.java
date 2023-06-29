package com.example.demo.controller;

import com.example.demo.config.Message;
import com.example.demo.dto.response.ResponMessage;
import com.example.demo.model.Band;
import com.example.demo.model.Song;
import com.example.demo.model.User;
import com.example.demo.security.userprincal.UserDetailService;
import com.example.demo.service.band.IBandService;
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
@RequestMapping("/bands")
@CrossOrigin(origins = "*")
public class BandController {
    @Autowired
    private IBandService bandService;
    @Autowired
    private ISongService songService;
    @Autowired
    private IUserService userService;
    @Autowired
    private UserDetailService userDetailService;

    @GetMapping
    public ResponseEntity<?> showListBand() {
        return new ResponseEntity<>(bandService.findAll(), HttpStatus.OK);
    }

    @GetMapping("/page")
    public ResponseEntity<?> pageBand(Pageable pageable) {
        return new ResponseEntity<>(bandService.findAll(pageable), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> createBand(@RequestBody Band band) {
        User user = userDetailService.getCurrentUser();
        if (user.getId()==null){
            return new ResponseEntity<>(new ResponMessage(Message.NO_USER), HttpStatus.OK);
        }
        if (!user.isStatus()) {
            return new ResponseEntity<>(new ResponMessage(Message.ACCESS_DENIED), HttpStatus.OK);
        }

        bandService.save(band);
        return new ResponseEntity<>(new ResponMessage(Message.CREATE_SUCCESS), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> detailBand(@PathVariable Long id) {
        Optional<Band> band = bandService.findById(id);
        if (!band.isPresent()){
            return new ResponseEntity<>(new ResponMessage("band"+Message.NOT_FOUND),HttpStatus.OK);
        } else {
            List<Song> songList = songService.findSongsByBandId(id);
            List<Long> longList = new ArrayList<>();
            songList.forEach(song ->
                longList.add(song.getId())
            );
            band.get().setSongList(longList);
            bandService.updateBand(band.get());
            return new ResponseEntity<>(band,HttpStatus.OK);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateBand(@PathVariable Long id, @RequestBody Band band) {
        Optional<Band> band1 = bandService.findById(id);
        User user = userDetailService.getCurrentUser();
        if (!band1.isPresent()) {
            return new ResponseEntity<>(new ResponMessage("band"+Message.NOT_FOUND), HttpStatus.OK);
        } else {
            if (!user.isStatus()) {
                return new ResponseEntity<>(new ResponMessage(Message.ACCESS_DENIED), HttpStatus.OK);
            }
            if (userService.getUserRole(user).equals("USER")) {
                return new ResponseEntity<>(new ResponMessage(Message.ACCESS_DENIED), HttpStatus.OK);
            }
            if (!band.getAvatar().equals(band1.get().getAvatar())) {
                band.setId(band1.get().getId());
            }
            if (band.getAvatar().equals(band1.get().getAvatar())&&band.getProfile().equals(band1.get().getProfile()) && (band.getName().equals(band1.get().getName()))) {
                return new ResponseEntity<>(new ResponMessage(Message.NO_CHANGE), HttpStatus.OK);
            }
            band.setId(band1.get().getId());
            bandService.save(band);
            return new ResponseEntity<>(new ResponMessage(Message.UPDATE_SUCCESS), HttpStatus.OK);
        }
    }
    @GetMapping("/search")
    public ResponseEntity<?> getListSearchBandByName(@RequestParam String name){
        List<Band> band = bandService.findByName(name);
        if (band.isEmpty()){
            return new ResponseEntity<>(new ResponMessage("band "+Message.NOT_FOUND),HttpStatus.OK);
        }
        return new ResponseEntity<>(band,HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBand(@PathVariable Long id) {
        Optional<Band> band = bandService.findById(id);
        User user = userDetailService.getCurrentUser();
        if (!user.isStatus()) {
            return new ResponseEntity<>(new ResponMessage(Message.ACCESS_DENIED), HttpStatus.OK);
        }
        if (!band.isPresent()) {
            return new ResponseEntity<>(new ResponMessage("band"+Message.NOT_FOUND), HttpStatus.OK);
        } else {
            if (!userService.getUserRole(userDetailService.getCurrentUser()).equals("ADMIN")) {
                return new ResponseEntity<>(new ResponMessage(Message.ACCESS_DENIED), HttpStatus.OK);
            }
        }
        List<Song> songs = songService.findSongsByBandId(id);
        if (!songs.isEmpty()) {
           songs.forEach(song -> {
               song.getBandList().remove(band.get());
               songService.updateSong(song);
           });

        }
        bandService.deleteById(id);
        return new ResponseEntity<>(new ResponMessage(Message.DELETE_SUCCESS), HttpStatus.OK);
    }

}
