package me.exrates.dao;

import me.exrates.model.dto.NewsSummaryDto;
import me.exrates.model.main.News;
import me.exrates.model.onlineTableDto.NewsDto;

import java.util.List;
import java.util.Locale;

public interface NewsDao {
    int deleteNews(News news);

    int deleteNewsVariant(News news);

    List<NewsSummaryDto> findAllNewsVariants();

    News getNews(Integer newsId, Locale locale);

    int addNews(News news);

    int addNewsVariant(News news);

    List<NewsDto> getNewsBriefList(Integer offset, Integer limit, Locale locale);

}
