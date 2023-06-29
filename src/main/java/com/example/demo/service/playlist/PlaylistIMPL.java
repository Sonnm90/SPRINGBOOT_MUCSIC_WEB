package com.example.demo.service.playlist;

import com.example.demo.model.Playlist;
import com.example.demo.model.User;
import com.example.demo.repository.IPlaylistRepository;
import com.example.demo.security.userprincal.UserDetailService;
import com.example.demo.service.song.ISongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PlaylistIMPL implements IPlaylistService {
    @Autowired
    private IPlaylistRepository playlistRepository;
    @Autowired
    private UserDetailService userDetailService;
    @Autowired
    private ISongService songService;

    @Override
    public List<Playlist> findAll() {
        return playlistRepository.findAll();
    }

    @Override
    public Boolean existsByName(String name) {
        return playlistRepository.existsByName(name);
    }

    @Override
    public List<Playlist> findByName(String name) {
        return playlistRepository.findByNameContaining(name);
    }

    @Override
    public Optional<Playlist> findById(Long id) {
        return playlistRepository.findById(id);
    }

    @Override
    public Playlist save(Playlist playlist) {
        User user = userDetailService.getCurrentUser();
        playlist.setUser(user);
        return playlistRepository.save(playlist);
    }

    @Override
    public Playlist update(Playlist playlist) {
        return playlistRepository.save(playlist);
    }

    @Override
    public void deleteById(Long id) {
        playlistRepository.deleteById(id);
    }

    @Override
    public List<Playlist> findPlaylistByUserId(Long id) {
        return playlistRepository.findPlaylistByUserId(id);
    }

    @Override
    public Optional<Playlist> findPlaylistBySongId(Long id) {
        return playlistRepository.findPlaylistBySongId(id);
    }


}
