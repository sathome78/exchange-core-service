package me.exrates.model.newsEntity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class News {
    private Integer id;

    private LocalDateTime date;

    private String resource;

    private String description;

    private LocalDate calendarDate;

    private List<NewsVariant> newsVariant;

    private NewsType newsType;

    private Boolean noTitleImg;

}
