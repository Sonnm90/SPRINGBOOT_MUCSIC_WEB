package com.example.demo.repository;

import com.example.demo.model.Band;
import com.example.demo.model.Singer;
import com.example.demo.model.Song;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IBandRepository extends JpaRepository<Band, Long> {
    List<Band> findByNameContaining(String name);

    Boolean existsByName(String name);
//    @Query("select b from Band b join b.songList s where b.id = :id")
//    List<Song> findSongsByBandId(@Param("id") Long id);

}
