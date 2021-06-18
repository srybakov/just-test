package ru.mvideo.just.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UpdateBookDto {

    private String title;
    private String author;

}
