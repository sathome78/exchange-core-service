package me.exrates.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.exrates.model.enums.NewsTypeEnum;
import me.exrates.model.newsEntity.NewsVariant;

import java.time.format.DateTimeFormatter;

@Getter
@Setter
@NoArgsConstructor
public class NewsTopicDto extends NewsDto {
    private String content;
    private String visitCount;
    private String newsVariantDate;

    public NewsTopicDto(NewsVariant news) {
        if (news.getNews() != null) {
            this.id = news.getNews().getId();
            this.resource = news.getNews().getResource();
            this.newsType = NewsTypeEnum.convert(news.getNews().getNewsType().getId());
            this.calendarDate = news.getNews().getCalendarDate() == null ? null : news.getNews().getCalendarDate().toString();
            this.noTitleImg = news.getNews().getNoTitleImg();
        }
        this.newsVariantId = news.getId();
        this.newsVariantDate = news.getAddedDate() == null ? null : news.getAddedDate().toLocalDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        this.title = news.getTitle();
        this.brief = news.getBrief();
        this.content = news.getContent();
        this.language = news.getLanguage();
    }
}
