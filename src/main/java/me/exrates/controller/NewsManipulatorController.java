package me.exrates.controller;

import lombok.extern.log4j.Log4j2;
import me.exrates.exception.NewsBriefNotSetException;
import me.exrates.exception.NewsTitleNotSetException;
import me.exrates.model.ErrorInfoDto;
import me.exrates.model.NewsVariantDeleteDto;
import me.exrates.model.dto.NewsEditorCreationFormDto;
import me.exrates.model.enums.NewsTypeEnum;
import me.exrates.model.error.ErrorInfo;
import me.exrates.service.NewsExtService;
import me.exrates.service.NewsVariantExtService;
import me.exrates.service.impl.NewsContentManipulator;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.LocaleResolver;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;

@RestController
@Log4j2
public class NewsManipulatorController {

    @Autowired
    MessageSource messageSource;
    @Autowired
    private LocaleResolver localeResolver;

    @Autowired
    private NewsExtService newsExtService;

    @Autowired
    private NewsVariantExtService newsVariantExtService;

    @Autowired
    NewsContentManipulator newsContentManipulator;

    @RequestMapping(value = "/news/upload/image", method = RequestMethod.POST)
    public Map<String, String> uploadNewsImage(@RequestParam(required = true) MultipartFile file,
                                               @RequestParam(required = true) Integer newsId,
                                               @RequestParam(required = true) String newsType) throws IOException {
        String uploadedFileName = newsExtService.uploadImageForNews(file);  //   temp_img_upload/xxx.png
        String resourcePathToUploadedFile = newsContentManipulator.getResourcePathToUploadedFile(
                uploadedFileName,
                NewsTypeEnum.convert(newsType));  //  /newstopic/temp_img_upload/xxx.png
        return Collections.singletonMap("location", resourcePathToUploadedFile);
    }

    @RequestMapping(value = "/news/upload/file", method = RequestMethod.POST)
    public Map<String, String> uploadNewsFile(@RequestParam(required = true) MultipartFile file,
                                              @RequestParam(required = true) Integer newsId,
                                              @RequestParam(required = true) String newsType) throws IOException {
        String uploadedFileName = newsExtService.uploadFileForNews(file); //   temp_file_upload/xxx.png
        String resourcePathToUploadedFile = newsContentManipulator.getResourcePathToUploadedFile(
                uploadedFileName,
                NewsTypeEnum.convert(newsType));  //  /newstopic/temp_file_upload/xxx.png
        return Collections.singletonMap("location", resourcePathToUploadedFile);
    }

    @RequestMapping(value = "/news/addNews", method = RequestMethod.POST, consumes = "application/json;charset=utf-8")
    @ResponseBody
    public NewsEditorCreationFormDto createNewsWithEditor(
            @RequestBody NewsEditorCreationFormDto newsEditorCreationFormDto,
            HttpServletRequest request) throws IOException, InterruptedException {
        NewsEditorCreationFormDto result = newsExtService.uploadNews(newsEditorCreationFormDto);
        log.info(result.getNewsType());
        if (result.getNotifySubscribers()) {
            result.setBaseUrl(
                    request.getScheme()
                            .concat("://")
                            .concat(request.getServerName())
                            .concat(":")
                            .concat(String.valueOf(request.getServerPort())));
        }
        result.setCallbackMessage(messageSource.getMessage("news.successload", null, localeResolver.resolveLocale(request)));
        return result;
    }

    @RequestMapping(value = "/news/newstopic/delete", method = RequestMethod.POST, consumes = "application/json;charset=utf-8")
    @ResponseBody
    public NewsVariantDeleteDto delete(
            @RequestBody NewsVariantDeleteDto newsVariantDeleteDto,
            HttpServletRequest request) throws IOException {
        newsVariantExtService.deleteNewsVariant(newsVariantDeleteDto.getId());
        newsVariantDeleteDto.setCallbackMessage(messageSource.getMessage("news.successdelete", null, localeResolver.resolveLocale(request)));
        return newsVariantDeleteDto;
    }

    @ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
    @ExceptionHandler({NewsTitleNotSetException.class, NewsBriefNotSetException.class})
    @ResponseBody
    public ErrorInfoDto NewsManipulationExceptionHandler(HttpServletRequest req, Exception exception) {
        log.error("\n\t" + ExceptionUtils.getStackTrace(exception));
        if (exception.getLocalizedMessage() == null || exception.getLocalizedMessage().isEmpty()) {
            return new ErrorInfoDto(exception.getClass().getSimpleName());
        } else {
            return new ErrorInfoDto(exception.getClass().getSimpleName(), exception.getLocalizedMessage());
        }
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ErrorInfo OtherErrorsHandler(HttpServletRequest req, Exception exception) {
        log.error(exception);
        exception.printStackTrace();
        return new ErrorInfo(req.getRequestURL(), exception);
    }

}