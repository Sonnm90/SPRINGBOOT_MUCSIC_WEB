package com.example.demo.model;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "songs")
@NoArgsConstructor(force = true)
@AllArgsConstructor
@Data
public class Song {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @NonNull
    @Size(min = 2, max = 50)
    private String name;


    @ManyToMany
    @JoinTable(name = "like_Of_User",
            joinColumns = @JoinColumn(name = "song_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
    private List<User> likeUser;
    @ManyToMany
    @JoinTable(name = "song_Of_Singer",
            joinColumns = @JoinColumn(name = "song_id"), inverseJoinColumns = @JoinColumn(name = "singer_id"))
    private List<Singer> singerList = new ArrayList<>();

    @ManyToOne
    private Category category;

    @ManyToMany
    @JoinTable(name = "song_of_band",
            joinColumns = @JoinColumn(name = "song_id"), inverseJoinColumns = @JoinColumn(name = "band_id"))
    private List<Band> bandList = new ArrayList<>();

    @Transient
    private List<Playlist> playlists = new ArrayList<>();

   @Transient
    private List<Comment> commentList;

    @ManyToOne
    private User user;

    private Long numberOfView = 0L;
    @Lob
    @NotBlank
    @NotNull
    private String avatar;
    @NotBlank
    @NotNull
    private String lyrics;

    @Lob
    @NotBlank
    @NonNull
    private String src;

    public Song(String name, String avatar, String lyrics, Category category, String src) {
        this.name = name;
        this.avatar = avatar;
        this.lyrics = lyrics;
        this.category = category;
        this.src = src;
    }


    public Song(String name, String avatar, Category category, String lyrics, String src, List<Band> bandList, List<Singer> singerList) {
        this.name = name;
        this.avatar = avatar;
        this.category = category;
        this.src = src;
        this.lyrics =lyrics;
        this.bandList =bandList;
        this.singerList =singerList;
    }
}
