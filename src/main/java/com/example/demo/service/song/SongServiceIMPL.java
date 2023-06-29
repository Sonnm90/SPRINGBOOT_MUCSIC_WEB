package com.example.demo.service.song;

import com.example.demo.model.Song;
import com.example.demo.model.User;
import com.example.demo.repository.ISongRepository;
import com.example.demo.security.userprincal.UserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


import java.util.*;

@Service
public class SongServiceIMPL implements ISongService {
    @Autowired
    private ISongRepository songRepository;
    @Autowired
    private UserDetailService userDetailService;

    @Override
    public List<Song> findAll() {
        return songRepository.findAll();
    }

    @Override
    public Boolean existsByName(String name) {
        return songRepository.existsByName(name);
    }

    @Override
    public List<Song> findByName(String name) {
        return songRepository.findByNameContaining(name);
    }

    @Override
    public Optional<Song> findById(Long id) {
        return songRepository.findById(id);
    }

    @Override
    public Page<Song> findAll(Pageable pageable) {
        return songRepository.findAll(pageable);
    }

    @Override
    public Song save(Song song) {
        User user = userDetailService.getCurrentUser();
        song.setUser(user);
        return songRepository.save(song);
    }

    @Override
    public Song updateSong(Song song) {
        return songRepository.save(song);
    }

    @Override
    public void deleteById(Long id) {
        songRepository.deleteById(id);
    }

    @Override
    public List<Song> findSongsByUserIsLike(Long id) {
        return songRepository.findSongsByUserIsLike(id);
    }

    @Override
    public List<Song> findTopViewOfSong() {
        return songRepository.findTop10ByOrderByNumberOfViewDesc();
    }

    @Override
    public Set<Song> getList5Random() {
        List<Song> songs = songRepository.findAll();
        Set<Song> randomSongs = new HashSet<>();
        int number;
            for (int i = 0; i < 10; i++) {
                number = (int) Math.floor(songs.size() * Math.random());
                randomSongs.add(songs.get(number));
                if (randomSongs.size()==5){
                    break;
                }
            }
        return randomSongs;
    }


    @Override
    public List<Song> findSongsByCategoryId(Long id) {
        return songRepository.findByCategory_Id(id);
    }


//    @Override
//    public List<Song> findByPlaylistId(Long id) {
//     return songRepository.findSongsByPlaylistId(id);
//    }

    @Override
    public List<Song> findSongsByBandId(Long id) {
        return songRepository.findSongsByBandId(id);
    }

    @Override
    public List<Song> findSongsBySingerId(Long id) {
        return songRepository.findSongsBySingerId(id);
    }

    @Override
    public List<Song> findTop10ByOrderByLikeUserDesc() {
        return songRepository.findTop10ByOrderByLikeUserDesc();
    }
}
