package ru.mvideo.just.model;


import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public class Book {

    @Id
    private Long id;
    private String title;
    private String author;
}