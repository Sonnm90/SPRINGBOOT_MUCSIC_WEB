package com.example.demo.service.band;

import com.example.demo.model.Band;
import com.example.demo.model.Category;
import com.example.demo.model.Singer;
import com.example.demo.model.Song;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface IBandService {
    List<Band> findAll();
    Optional<Band> findById(Long id);
    List<Band> findByName(String name);
    Page<Band> findAll(Pageable pageable);
    Boolean existsByName(String name);
    Band save (Band band);
    Band updateBand(Band band);
    void deleteById(Long id);
//    List<Song> findSongsByBandId(Long id);
}
