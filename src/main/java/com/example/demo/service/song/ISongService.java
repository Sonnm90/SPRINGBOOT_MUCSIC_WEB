package com.example.demo.service.song;


import com.example.demo.model.Song;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ISongService {
    List<Song> findAll();

    Boolean existsByName(String name);

    List<Song> findByName(String name);

    Optional<Song> findById(Long id);

    Page<Song> findAll(Pageable pageable);

    Song save(Song song);

    Song updateSong(Song song);

    void deleteById(Long id);

    List<Song> findSongsByUserIsLike(Long id);

    List<Song> findTopViewOfSong();

    Set<Song> getList5Random();

    List<Song> findSongsByCategoryId(Long id);

//    List<Song> findByPlaylistId(Long id);

    List<Song> findSongsByBandId(Long id);

    List<Song> findSongsBySingerId(Long id);
    List<Song> findTop10ByOrderByLikeUserDesc();

}
