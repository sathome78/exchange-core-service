package me.exrates.service;

import me.exrates.model.dto.NewsEditorCreationFormDto;
import me.exrates.model.enums.NewsTypeEnum;
import me.exrates.model.newsEntity.News;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface NewsExtService {
  String uploadImageForNews(MultipartFile multipartFile) throws IOException;

  String uploadFileForNews(MultipartFile multipartFile) throws IOException;

  NewsEditorCreationFormDto uploadNews(NewsEditorCreationFormDto newsEditorCreationFormDto) throws IOException;

  News getByNewsTypeAndResource(NewsTypeEnum page, String toUpperCase);

}
