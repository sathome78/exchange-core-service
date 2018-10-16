package me.exrates.model.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.exrates.model.newsEntity.News;
import me.exrates.model.newsEntity.NewsVariant;
import me.exrates.util.LocalDateDeserializer;

import java.time.LocalDate;


@Getter
@Setter
@NoArgsConstructor
public class NewsEditorCreationFormDto {
    private Integer id;
    private String date;
    private String resource;
    private String title;
    private String brief;
    private String content;
    private Integer newsVariantId;
    private String language;
    private String titleImgHtml;
    private Boolean newCreatedNewsVariant;
    private String category;
    private Boolean notifySubscribers;
    private String baseUrl;
    private String newsTopicUrl;
    private String newsType;
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate calendarDate;
    private Boolean noTitleImg;
    /**/
    private String callbackMessage;

    /**/
    public NewsEditorCreationFormDto(News news, NewsVariant newsVariant, NewsEditorCreationFormDto newsEditorCreationFormDto) {
        this.id = news.getId();
        this.date = news.getDate().toString();
        this.resource = news.getResource();
        this.title = newsVariant.getTitle();
        this.brief = newsVariant.getBrief();
        this.newsVariantId = newsVariant.getId();
        this.language = newsVariant.getLanguage();
        this.newsType = news.getNewsType().getName();
        this.calendarDate = news.getCalendarDate();
        this.noTitleImg = news.getNoTitleImg();
        /**/
        this.content = newsEditorCreationFormDto.getContent();
        this.titleImgHtml = newsEditorCreationFormDto.getTitleImgHtml();
        this.newCreatedNewsVariant = newsEditorCreationFormDto.getNewCreatedNewsVariant();
        this.category = newsEditorCreationFormDto.getCategory();
        this.notifySubscribers = newsEditorCreationFormDto.getNotifySubscribers();
        this.baseUrl = newsEditorCreationFormDto.getBaseUrl();
        this.newsTopicUrl = newsEditorCreationFormDto.newsTopicUrl;
    }
}
