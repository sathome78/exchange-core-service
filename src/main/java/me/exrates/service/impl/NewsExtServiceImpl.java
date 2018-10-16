package me.exrates.service.impl;


import lombok.extern.log4j.Log4j2;
import me.exrates.dao.NewsExtDao;
import me.exrates.dao.NewsVariantExtDao;
import me.exrates.model.dto.NewsEditorCreationFormDto;
import me.exrates.model.enums.NewsTypeEnum;
import me.exrates.model.newsEntity.News;
import me.exrates.model.newsEntity.NewsType;
import me.exrates.model.newsEntity.NewsVariant;
import me.exrates.service.NewsExtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by ValkSam
 * SYNC version for project: exrates <-> edinarCoin
 * excluded repository/doa
 */
@Service
@Log4j2
@PropertySource(value = {"classpath:/news.properties"})
public class NewsExtServiceImpl implements NewsExtService {
  @Value("${news.ext.locationDir}")
  private String newsLocationDir;

  @Autowired
  private NewsExtDao newsExtDao;

  @Autowired
  private NewsVariantExtDao newsVariantExtDao;

  @Autowired
  NewsContentManipulator newsContentManipulator;

  @Override
  @Transactional
  public String uploadImageForNews(MultipartFile multipartFile) throws IOException {
    String locationForUploadFile = newsContentManipulator.getFolderForUploadImage(); //  c:/DEVELOPING/NEWS/temp_img_upload/
    return uploadFileForNews(multipartFile, locationForUploadFile);
  }

  @Override
  @Transactional
  public String uploadFileForNews(MultipartFile multipartFile) throws IOException {
    String locationForUploadFile = newsContentManipulator.getFolderForUploadFile(); //  c:/DEVELOPING/NEWS/temp_file_upload/
    return uploadFileForNews(multipartFile, locationForUploadFile);
  }

  private String uploadFileForNews(MultipartFile multipartFile, String locationForUploadFile) throws IOException {
    final Path path = Paths.get(locationForUploadFile);
    if (!Files.exists(path)) {
      Files.createDirectories(path);
    }
    final String name = generateFileNameToUpload(multipartFile);
    final Path target = Paths.get(path.toString(), name);
    Files.write(target, multipartFile.getBytes());
    String relativePath = locationForUploadFile.replaceAll("^".concat(newsLocationDir), "");   //  c:/DEVELOPING/NEWS/temp_img_upload/  => temp_img_upload/
    return relativePath.concat(name);
  }

  private String generateFileNameToUpload(MultipartFile file) {
    String fileName = file.getOriginalFilename();
    String fileExt = fileName.split("\\.")[fileName.split("\\.").length - 1];
    return UUID.randomUUID().toString() + "." + fileExt;
  }


  @Override
  @Transactional
  public NewsEditorCreationFormDto uploadNews(NewsEditorCreationFormDto newsEditorCreationFormDto) throws IOException {
    String htmlWithTitleImg = newsEditorCreationFormDto.getTitleImgHtml();
    String content = newsEditorCreationFormDto.getContent();
    News news;
    NewsVariant newsVariant = null;
    Integer newsId = newsEditorCreationFormDto.getId();
    Boolean newCreatedNewsVariant = false;
    if (newsId == null) {
      news = new News();
      news.setDate(LocalDateTime.now());
      news.setNewsType(new NewsType(NewsTypeEnum.convert(newsEditorCreationFormDto.getNewsType())));
      String resource = String.valueOf(news.getDate().getYear()) + "/" + String.valueOf(news.getDate().getMonth()) + "/" + String.valueOf(news.getDate().getDayOfMonth()) + "/";
      news.setResource(news.getNewsType().getId().equals(NewsTypeEnum.VIDEO.getCode()) ? newsEditorCreationFormDto.getResource() : resource);
    } else {
      news = newsExtDao.findOne(newsId);
      newsVariant = newsVariantExtDao.findByNewsVariantLanguage(newsId, newsEditorCreationFormDto.getLanguage());
    }
    news.setCalendarDate(newsEditorCreationFormDto.getCalendarDate());
    if (newsVariant == null) {
      newCreatedNewsVariant = true;
      newsVariant = new NewsVariant();
      newsVariant.setNews(news);
      newsVariant.setLanguage(newsEditorCreationFormDto.getLanguage());
      newsVariant.setAddedDate(LocalDateTime.now());
    }
    newsEditorCreationFormDto.setNewCreatedNewsVariant(newCreatedNewsVariant);
    newsVariant.setTitle(newsEditorCreationFormDto.getTitle());
    newsVariant.setBrief(newsEditorCreationFormDto.getBrief());

    String convertedContent = null;
    convertedContent = newsContentManipulator.replaceAbsoluteResourcesPathInHtmlToReferences(content);
    convertedContent = newsContentManipulator.convertFileLinkToDownloadLink(convertedContent);
    newsVariant.setContent(convertedContent);
    newsVariant.setUpdatedDate(LocalDateTime.now());
    newsVariant.setActive(true);
    List<NewsVariant> newsVariants = new ArrayList<>();
    newsVariants.add(newsVariant);
    news.setNewsVariant(newsVariants);
    news = newsExtDao.save(news);
    NewsEditorCreationFormDto result = new NewsEditorCreationFormDto(news, newsVariant, newsEditorCreationFormDto);
    /**/
    if ((!news.getNewsType().getId().equals(NewsTypeEnum.VIDEO.getCode()))) {
      saveResourcesToDisk(result, content, convertedContent, htmlWithTitleImg);
    }
    return result;
  }

  @Override
  public News getByNewsTypeAndResource(NewsTypeEnum newsTypeEnum, String resource) {
    resource = resource.endsWith("/") ? resource : resource.concat("/");
    return newsExtDao.findByNewsTypeAndResource(newsTypeEnum.getCode(), resource);
  }

  private void saveResourcesToDisk(
      NewsEditorCreationFormDto newsEditorCreationFormDto,
      String originalContent,
      String convertedContent,
      String htmlWithTitleImg) throws IOException  {
    String newsRoot = newsContentManipulator.createAndGetNewsRootFolder(
        newsEditorCreationFormDto.getResource(),
        newsEditorCreationFormDto.getId());
    String newsLang = newsContentManipulator.createAndGetNewsLanguageFolder(
        newsEditorCreationFormDto.getLanguage(),
        newsRoot);
    newsContentManipulator.storeNewsTitleIntoFile(
        newsEditorCreationFormDto.getTitle(),
        newsLang);
    newsContentManipulator.storeNewsBriefIntoFile(
        newsEditorCreationFormDto.getBrief(),
        newsLang);
    newsContentManipulator.storeNewsContentIntoFile(
        convertedContent,
        newsLang);
    if (!newsEditorCreationFormDto.getNoTitleImg()) {
      newsContentManipulator.storeNewsTitleImageIntoFile(
          newsEditorCreationFormDto.getId(),
          htmlWithTitleImg,
          newsRoot, newsEditorCreationFormDto.getNewsType());
    }
    newsContentManipulator.moveNewInsertedFileFromTempFolderToNewsFolder(
        originalContent,
        newsRoot);
  }

}
