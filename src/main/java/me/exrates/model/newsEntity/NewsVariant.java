package me.exrates.model.newsEntity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class NewsVariant {

    private Integer id;

    private String title;

    private String language;

    private String brief;

    private String content;

    private LocalDateTime addedDate;

    private Boolean active;

    private Integer visitCount;

    private String tags;

    private LocalDateTime updatedDate;

    private LocalDateTime syncToWalletDate;

    private News news;

}
