package com.example.demo.repository;

import com.example.demo.model.Playlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IPlaylistRepository extends JpaRepository<Playlist, Long> {

    List<Playlist> findByNameContaining(String name);
    Boolean existsByName(String name);
    @Query("select p from Playlist p join p.user u where u.id = :id")
    List<Playlist> findPlaylistByUserId(@Param("id") Long id);
    @Query("select p from Playlist p join p.songs s where s.id = :id")
    Optional<Playlist> findPlaylistBySongId(@Param("id") Long id);
}
