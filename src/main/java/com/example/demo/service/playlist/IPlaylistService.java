package com.example.demo.service.playlist;

import com.example.demo.model.Band;
import com.example.demo.model.Playlist;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface IPlaylistService {
    List<Playlist> findAll();
    Boolean existsByName(String name);
    List<Playlist> findByName(String name);
    Optional<Playlist> findById(Long id);

    Playlist save (Playlist playlist);
    Playlist update (Playlist playlist);
    void deleteById(Long id);
    List<Playlist> findPlaylistByUserId( Long id);
    Optional<Playlist> findPlaylistBySongId( Long id);

}
