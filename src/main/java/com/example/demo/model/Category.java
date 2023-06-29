package com.example.demo.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "categories", uniqueConstraints = @UniqueConstraint(columnNames = "name"))
@NoArgsConstructor(force = true)
@AllArgsConstructor
@Data
public class  Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @NonNull
    @Column(unique = true)
    @Size(min = 3, max = 50)
    private String name;


//    @OneToMany
//    @JoinColumn(name = "category_id")

    @Lob
    private String avatar;
    @ManyToOne
    User user;

    @Transient
    List<Long> songs = new ArrayList<>();
}
