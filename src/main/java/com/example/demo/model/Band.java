package com.example.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "bands")
@NoArgsConstructor(force = true)
@AllArgsConstructor
@Data
public class Band {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    @NotNull
    @Size(min = 2, max = 50)
    private String name;
    @Lob
    @NotNull
    @NotBlank
    private String avatar;
    @NotNull
    @NotBlank
    private String profile;

    @ManyToOne(fetch = FetchType.EAGER)
    User user;

        @Transient
//    @ManyToMany(fetch = FetchType.EAGER)
//    @JoinTable(name = "song_of_band",
//            joinColumns = @JoinColumn(name = "song_id"), inverseJoinColumns = @JoinColumn(name = "band_id"))
    private List<Long> songList = new ArrayList<>();
}
