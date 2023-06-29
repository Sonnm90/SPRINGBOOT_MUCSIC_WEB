package com.example.demo.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotNull;
@Data
@AllArgsConstructor
public class PlaylistDTO {
    @NotNull
    private String name;
    @NotNull
    private String avatar;
}
