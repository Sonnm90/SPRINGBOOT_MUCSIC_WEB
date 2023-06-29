package com.example.demo.repository;

import com.example.demo.model.Song;
import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ISongRepository extends JpaRepository<Song, Long> {
    List<Song> findByNameContaining(String name);

    Boolean existsByName(String name);

    @Query("select s from Song s join s.bandList b where b.id = :id")
    List<Song> findSongsByBandId(@Param("id") Long id);

    @Query("select s from Song s join s.singerList b where b.id = :id")
    List<Song> findSongsBySingerId(@Param("id") Long id);

    @Query("select s from Song s join s.likeUser u where u.id = :id")
    List<Song> findSongsByUserIsLike(@Param("id") Long id);


    List<Song> findTop10ByOrderByLikeUserDesc();

    List<Song> findTop10ByOrderByNumberOfViewDesc();


    List<Song> findByCategory_Id(Long id);

}
