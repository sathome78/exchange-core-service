package me.exrates.controller;

import me.exrates.exception.FileLoadingException;
import me.exrates.exception.NewsCreationException;
import me.exrates.exception.NewsRemovingException;
import me.exrates.exception.NoFileForLoadingException;
import me.exrates.model.dto.NewsEditorCreationForm;
import me.exrates.model.dto.NewsSummaryDto;
import me.exrates.model.error.ErrorInfo;
import me.exrates.model.main.News;
import me.exrates.service.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.LocaleResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@RestController
@PropertySource(value = {"classpath:/news.properties"})
public class NewsControllerRest {
    private final int DEAFAULT_PAGE_SIZE = 20;
    private final String TITLE_DESCRIPTION_FILE_NAME = "title.md";
    private final String BRIEF_DESCRIPTION_FILE_NAME = "brief.md";

    @Autowired
    MessageSource messageSource;
    private
    @Value("${news.locationDir}")
    String newsLocationDir;
    @Value("${news.tempImageDir}")
    String tempImageDir;
    @Value("${news.urlPath}")
    String urlPath;
    @Autowired
    private LocaleResolver localeResolver;
    @Autowired
    private NewsService newsService;

    @RequestMapping(value = "/news/addNewsVariant", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    public String uploadNewsVariant(HttpServletRequest request, HttpServletResponse response,
                                    @RequestParam(value = "file", required = false) MultipartFile[] multipartFiles,
                                    @RequestParam(value = "id", required = false) Integer newsId,
                                    @RequestParam(value = "date", required = false) String date,
                                    @RequestParam(value = "resource", required = false) String resource) {
        MultipartFile multipartFile = multipartFiles[0];
        if (multipartFile.isEmpty() ||
                Stream.of(multipartFiles)
                        .filter(file -> file.getOriginalFilename().substring(multipartFile.getOriginalFilename().lastIndexOf(".") + 1).toUpperCase().equals("ZIP"))
                        .collect(Collectors.toList()).isEmpty()) {
            throw new NoFileForLoadingException(messageSource.getMessage("news.nofilesforloading", null, localeResolver.resolveLocale(request)));
        } else {
            News news = new News();
            news.setId(newsId);
            if (newsId == null) {
                /*new News. Requires date.*/
                try {
                    news.setDate(LocalDate.parse(date));
                } catch (Exception e) {
                    throw new NewsCreationException(messageSource.getMessage("news.dateerror", null, localeResolver.resolveLocale(request)));
                }
                resource = String.valueOf(news.getDate().getYear()) + "/" + String.valueOf(news.getDate().getMonth()) + "/" + String.valueOf(news.getDate().getDayOfMonth()) + "/";
            }
            news.setResource(resource);
            /*populate title, brief and collect locale from archive file*/
            Map<String, News> variants = new HashMap<>();
            try {
                ZipInputStream zis = new ZipInputStream(multipartFile.getInputStream(), Charset.forName("CP866"));
                ZipEntry ze;
                while ((ze = zis.getNextEntry()) != null) {
                    if (ze.getName().contains("/" + TITLE_DESCRIPTION_FILE_NAME)) {
                        byte[] buff = new byte[(int) ze.getSize()];
                        zis.read(buff);
                        news.setTitle(new String(buff, "UTF-8"));
                        /**/
                        String locale = ze.getName().split("\\/")[ze.getName().split("\\/").length - 2];
                        News n = variants.get(locale);
                        if (n == null) {
                            try {
                                news.setNewsVariant(locale);
                                variants.put(locale, (News) news.clone());
                            } catch (CloneNotSupportedException e) {
                                e.printStackTrace();
                            }
                        } else {
                            n.setTitle(news.getTitle());
                        }
                    }
                    if (ze.getName().contains("/" + BRIEF_DESCRIPTION_FILE_NAME)) {
                        byte[] buff = new byte[(int) ze.getSize()];
                        zis.read(buff);
                        news.setBrief(new String(buff, "UTF-8"));
                        /**/
                        String locale = ze.getName().split("\\/")[ze.getName().split("\\/").length - 2];
                        News n = variants.get(locale);
                        if (n == null) {
                            try {
                                news.setNewsVariant(locale);
                                variants.put(locale, (News) news.clone());
                            } catch (CloneNotSupportedException e) {
                                e.printStackTrace();
                            }
                        } else {
                            n.setTitle(news.getBrief());
                        }
                    }
                }
                /**/
                if (variants.isEmpty()) {
                    throw new FileLoadingException("");
                }
                newsService.uploadNews(variants.values(), multipartFile, newsLocationDir);
                /**/
            } catch (NewsCreationException e) {
                throw new NewsCreationException(messageSource.getMessage("news.errorcreate", null, localeResolver.resolveLocale(request)));
            } catch (FileLoadingException | IOException e) {
                throw new FileLoadingException(String.format("%s </br> %s", messageSource.getMessage("news.errorload", null, localeResolver.resolveLocale(request)), e.getLocalizedMessage()));
            }
            return "{\"result\":\"" + messageSource.getMessage("news.successload", null, localeResolver.resolveLocale(request)) + "\"}";
        }
    }

    @RequestMapping(value = "/news/addNewsFromEditor", method = RequestMethod.POST, consumes = "application/json;charset=utf-8")
    public News createNewsWithEditor(@RequestBody NewsEditorCreationForm form, HttpServletRequest request) {
        String html = form.getContent();
        me.exrates.model.main.News news = new me.exrates.model.main.News();
        news.setId(form.getId());
        news.setDate(form.getDate() == null || form.getDate().isEmpty() ? LocalDate.now() : LocalDate.parse(form.getDate(), DateTimeFormatter.ISO_DATE));
        news.setContent(html);
        news.setNewsVariant(form.getNewsVariant().toLowerCase());
        if (news.getNewsVariant() == null || news.getNewsVariant().isEmpty()) {
            news.setNewsVariant(localeResolver.resolveLocale(request).getLanguage());
        }
        news.setTitle(form.getTitle());
        news.setBrief(form.getBrief());
        String resource = form.getResource() == null || form.getResource().isEmpty() ? String.valueOf(news.getDate().getYear()) + "/" +
                String.valueOf(news.getDate().getMonth()) + "/" + String.valueOf(news.getDate().getDayOfMonth()) + "/" : form.getResource();
        news.setResource(resource);
        return newsService.createNewsVariant(news, newsLocationDir, tempImageDir, urlPath);
    }


    @RequestMapping(value = "/news/getNewsVariant", method = RequestMethod.GET)
    public News getNewsVariant(@RequestParam Integer newsId, @RequestParam String language, HttpServletRequest request) {
        Locale locale = language == null || language.isEmpty() ? localeResolver.resolveLocale(request) : new Locale(language);
        News newsWithContent = newsService.getNewsWithContent(newsId, locale, newsLocationDir);
        return newsWithContent;
    }

    @RequestMapping(value = "/news/uploadImage", method = RequestMethod.POST)
    public Map<String, String> uploadNewsImage(@RequestParam MultipartFile file,
                                               @RequestParam Integer newsId,
                                               HttpServletRequest request) throws IOException {
        String resourcePath = "";
        String imageDir = tempImageDir;
        if (newsId != null) {
            News news = newsService.getNews(newsId, localeResolver.resolveLocale(request));
            resourcePath = "/" + news.getResource() + newsId;
            imageDir = newsLocationDir + resourcePath + "/img/";
        }
        return Collections.singletonMap("location", newsService.uploadImageForNews(file, imageDir, urlPath + resourcePath + "/img/"));

    }


    @RequestMapping(value = "/news/deleteNews", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    public String uploadNewsVariant(HttpServletRequest request, HttpServletResponse response,
                                    @RequestParam(value = "id", required = false) Integer newsId,
                                    @RequestParam(value = "removingType", required = false) String removingType,
                                    @RequestParam(value = "variant", required = false) String variant,
                                    @RequestParam(value = "resource", required = false) String resource) {
        try {
            News news = new News();
            news.setId(newsId);
            int result = 0;
            if ("news".equals(removingType)) {
                result = newsService.deleteNews(news);
            } else if ("variant".equals(removingType)) {
                news.setNewsVariant(variant);
                result = newsService.deleteNewsVariant(news);
            }
            if (result <= 0) {
                throw new NewsRemovingException("");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new NewsRemovingException(messageSource.getMessage("news.errordelete", null, localeResolver.resolveLocale(request)));
        }
        return "{\"result\":\"" + messageSource.getMessage("news.successdelete", null, localeResolver.resolveLocale(request)) + "\"}";
    }

    @RequestMapping(value = "/news/findAllNewsVariants", method = RequestMethod.GET)
    public List<NewsSummaryDto> findAllNewsVariants() {
        return newsService.findAllNewsVariants();

    }

    @ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
    @ExceptionHandler(NoFileForLoadingException.class)
    @ResponseBody
    public ErrorInfo NoFileForLoadingExceptionHandler(HttpServletRequest req, Exception exception) {
        return new ErrorInfo(req.getRequestURL(), exception);
    }

    @ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
    @ExceptionHandler(FileLoadingException.class)
    @ResponseBody
    public ErrorInfo FileLoadingExceptionHandler(HttpServletRequest req, Exception exception) {
        return new ErrorInfo(req.getRequestURL(), exception);
    }

    @ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
    @ExceptionHandler(NewsCreationException.class)
    @ResponseBody
    public ErrorInfo NewsCreationExceptionHandler(HttpServletRequest req, Exception exception) {
        return new ErrorInfo(req.getRequestURL(), exception);
    }

    @ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
    @ExceptionHandler(NewsRemovingException.class)
    @ResponseBody
    public ErrorInfo NewsRemovingException(HttpServletRequest req, Exception exception) {
        return new ErrorInfo(req.getRequestURL(), exception);
    }

}