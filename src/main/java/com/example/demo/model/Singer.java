package com.example.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import java.util.List;

@Entity
@Table(name = "singers")
@NoArgsConstructor(force = true)
@AllArgsConstructor
@Data
public class Singer {
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
    private List<Long> songs;

}
