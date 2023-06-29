package com.example.demo.dto.request;

import com.example.demo.model.Song;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class CommentDTO {
    @NotNull
    @NotBlank
    private String content;

    @NotNull
    @NotBlank
    private Song song;

}
