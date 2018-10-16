package me.exrates.model.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class NewsEditorCreationForm {
    private Integer id;
    private String title;
    private String brief;
    private String content;
    private String date;
    private String resource;
    private String newsVariant;

}
