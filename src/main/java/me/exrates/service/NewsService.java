package me.exrates.service;

import me.exrates.model.dto.NewsSummaryDto;
import me.exrates.model.main.CacheData;
import me.exrates.model.main.News;
import me.exrates.model.onlineTableDto.NewsDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

public interface NewsService {
    List<NewsDto> getNewsBriefList(CacheData cacheData, final Integer offset, final Integer limit, Locale locale);

    News getNews(final Integer newsId, Locale locale);

    News getNewsWithContent(Integer newsId, Locale locale, String locationDir);

    boolean uploadNews(Collection<News> variants, MultipartFile multipartFile, String newsLocationDir);

    News createNewsVariant(News news, String newsLocationDir, String tempImageDir, String logicalPath);

    String uploadImageForNews(MultipartFile file, String location, String logicalPath) throws IOException;

    int deleteNewsVariant(News news);

    int deleteNews(News news);

    List<NewsSummaryDto> findAllNewsVariants();

    List<me.exrates.model.onlineTableDto.NewsDto> getTwitterNews(Integer amount);

}
