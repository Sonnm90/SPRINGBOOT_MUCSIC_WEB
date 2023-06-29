package com.example.demo.dto.request;

import com.example.demo.model.*;

import java.util.ArrayList;
import java.util.List;

public class ResultSearchDTO {
    List<Band> bands = new ArrayList<>();
    List<Singer> singers = new ArrayList<>();
    List<Playlist> playlists = new ArrayList<>();
    List<Song> songs = new ArrayList<>();
    List<Category> categories = new ArrayList<>();

    public ResultSearchDTO(List<Band> bands, List<Singer> singers, List<Playlist> playlists, List<Song> songs, List<Category> categories) {
        this.bands = bands;
        this.singers = singers;
        this.playlists = playlists;
        this.songs = songs;
        this.categories = categories;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public ResultSearchDTO() {

    }

    public List<Band> getBands() {
        return bands;
    }

    public void setBands(List<Band> bands) {
        this.bands = bands;
    }

    public List<Singer> getSingers() {
        return singers;
    }

    public void setSingers(List<Singer> singers) {
        this.singers = singers;
    }

    public List<Playlist> getPlaylists() {
        return playlists;
    }

    public void setPlaylists(List<Playlist> playlists) {
        this.playlists = playlists;
    }

    public List<Song> getSongs() {
        return songs;
    }

    public void setSongs(List<Song> songs) {
        this.songs = songs;
    }

    public ResultSearchDTO(List<Band> bands, List<Singer> singers, List<Playlist> playlists, List<Song> songs) {
        this.bands = bands;
        this.singers = singers;
        this.playlists = playlists;
        this.songs = songs;
    }
}
