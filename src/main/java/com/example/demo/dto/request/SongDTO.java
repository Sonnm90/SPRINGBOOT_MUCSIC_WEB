package com.example.demo.dto.request;

import com.example.demo.model.Band;
import com.example.demo.model.Category;
import com.example.demo.model.Comment;
import com.example.demo.model.Singer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class SongDTO {
    @NotNull
    private String name;
    @NotNull
    private String avatar;
    @NotNull
    private String lyrics;
    @NotNull
    private String src;
    @NotNull
    private Category category;

    private List<Band> bandList =new ArrayList<>();
    private List<Singer> singerList = new ArrayList<>();
}
