package me.exrates.service.impl;

import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;
import me.exrates.exception.NewsContentNotSetException;
import me.exrates.exception.NewsTitleImageNotSetException;
import me.exrates.exception.UnrecognisedUrlPathForNewsTypeException;
import me.exrates.model.enums.NewsTypeEnum;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
@Log4j2
public class NewsContentManipulator {

    @Value("${news.ext.locationDir}")
    private String newsLocationDir;

    @Value("${news.tempImageFolderUnderLocationDir}")
    private String tempImageUploadFolder;

    @Value("${news.tempFileFolderUnderLocationDir}")
    private String tempFileUploadFolder;

    @Value("${news.newstopic.urlPath}")
    private String newsUrlPath;

    @Value("${news.materialsView.urlPath}")
    private String materialsViewUrlPath;

    @Value("${news.webinar.urlPath}")
    private String webinarUrlPath;

    @Value("${news.event.urlPath}")
    private String eventUrlPath;

    @Value("${news.feastDay.urlPath}")
    private String feastDayUrlPath;

    @Value("${news.page.urlPath}")
    private String pageUrlPath;

    @Value("${news.flattenResourceLocationDir}")
    private Boolean flattenResourceLocationDir;

    @Value("${news.titleImgDir}")
    private String titleImgDir;

    @Value("${news.titleImgFileName}")
    private String titleImgFileName;

    private Map<String, String> replaceConformityMap;

    @PostConstruct
    private void init() {
        replaceConformityMap = new HashMap<String, String>() {{
            put("img/", tempImageUploadFolder);
            put("file/", tempFileUploadFolder);
        }};
    }


    private String getUrlPath(NewsTypeEnum newsTypeEnum) {
        switch (newsTypeEnum) {
            case NEWS:
                return newsUrlPath;
            case MATERIALS:
                return materialsViewUrlPath;
            case WEBINAR:
                return webinarUrlPath;
            case EVENT:
                return eventUrlPath;
            case FEASTDAY:
                return feastDayUrlPath;
            case PAGE:
                return pageUrlPath;
        }
        throw new UnrecognisedUrlPathForNewsTypeException(newsTypeEnum.name());
    }

    public String getResourcePathToUploadedFile(String uploadedFileName, NewsTypeEnum newsTypeEnum) {
        String urlPath = getUrlPath(newsTypeEnum);
        return urlPath
                .concat("/")
                .concat(uploadedFileName); //    newstopic/2016/AUGUST/10/img/xxx.jpg
    }

    public String getFolderForUploadImage() {
        return newsLocationDir.concat(tempImageUploadFolder); //  c:/DEVELOPING/NEWS/temp_img_upload/
    }

    public String getFolderForUploadFile() {
        return newsLocationDir.concat(tempFileUploadFolder); //  c:/DEVELOPING/NEWS/temp_file_upload/
    }

    public String replaceAbsoluteResourcesPathInHtmlToReferences(String html) {
        if (StringUtils.isEmpty(html)) {
            return html;
        }
        Map<String, String> replacementMap = getResourcesToRefPathReplacementMap(
                getReferenses(html));
        for (Map.Entry<String, String> pair : replacementMap.entrySet()) {
            html = html.replaceAll("=\\s*[']\\s*{1}" + pair.getKey(), "='".concat(pair.getValue()));
            html = html.replaceAll("=\\s*[\"]\\s*{1}" + pair.getKey(), "=\"".concat(pair.getValue()));
        }
        return html;
    }

    public String convertFileLinkToDownloadLink(String html) {
        if (StringUtils.isEmpty(html)) {
            return html;
        }
        Matcher matcher = Pattern.compile("(<a\\s+href\\s*=\\s*[\"']([\\d\\D&&[^\"']]*)[\"']+[\\d\\D&&[^>]]*>)").matcher(html);
        while (matcher.find()) {
            String aTag = matcher.group(1);
            String href = matcher.group(2);
            if (!href.matches("^[\"']*http.*") && !aTag.contains("download")) {
                String newATag = aTag.replaceAll(">", " download target='_self'>");
                html = html.replaceAll(aTag, newATag);
            }
        }
        return html;
    }

    public String createAndGetNewsRootFolder(String resourcePath, Integer newsId) throws IOException {
        String newsRoot = getNewsRootFolder(resourcePath, newsId);
        Path rootPath = Paths.get(newsRoot);
        if (!rootPath.toFile().exists()) {
            Files.createDirectories(rootPath);
        }
        return newsRoot;
    }


    public String createAndGetNewsLanguageFolder(String lang, String newsRoot) throws IOException {
        String newsLang = newsRoot
                .concat(lang)
                .concat("/");
        Path langPath = Paths.get(newsLang);
        if (!langPath.toFile().exists()) {
            Files.createDirectories(langPath);
        }
        return newsLang;
    }

    public void storeNewsTitleIntoFile(String title, String newsLang) throws IOException {
        if (title == null) {
            title = "";
//      throw new NewsTitleNotSetException("Title must be set");
        }
        String newsTitle = newsLang
                .concat("/title.md");
        Path titlePath = Paths.get(newsTitle);
        if (titlePath.toFile().exists()) {
            titlePath.toFile().delete();
        }
        Files.write(titlePath, title.getBytes("UTF-8"));
    }

    public void storeNewsBriefIntoFile(String brief, String newsLang) throws IOException {
        if (brief == null) {
            brief = "";
//      throw new NewsBriefNotSetException("Brief must be set");
        }
        String newsBrief = newsLang
                .concat("/brief.md");
        Path briefPath = Paths.get(newsBrief);
        if (briefPath.toFile().exists()) {
            briefPath.toFile().delete();
        }
        Files.write(briefPath, brief.getBytes("UTF-8"));
    }

    public void storeNewsContentIntoFile(String convertedContent, String newsLang) throws IOException {
        if (convertedContent == null) {
            throw new NewsContentNotSetException("Content must be set");
        }
        String newstopic = convertedContent;
        String newsNewstopic = newsLang
                .concat("/newstopic.html");
        Path newstopicPath = Paths.get(newsNewstopic);
        if (newstopicPath.toFile().exists()) {
            newstopicPath.toFile().delete();
        }
        Files.write(newstopicPath, newstopic.getBytes("UTF-8"));
    }

    public void storeNewsTitleImageIntoFile(Integer newsId, String htmlWithTitleImg, String newsRoot, String newsType) throws IOException {
        NewsTypeEnum newsTypeEnum = NewsTypeEnum.convert((newsType));
        List<String> insertedTitleImgFiles = getNewsInsertedFiles(
                htmlWithTitleImg,
                "/".concat(tempImageUploadFolder));
        if (insertedTitleImgFiles.size() == 0 && !htmlWithTitleImg.matches("^.*<img\\s+src\\s*=.*$")) {
            throw new NewsTitleImageNotSetException("Title image must be set !");
        }
        if (insertedTitleImgFiles.size() == 0) {
            return;
        }
        TitleImgParams titleImgParams = getTitleImgParams(newsId, newsRoot);
        deleteExistingTitleFileNames(titleImgParams);
        /**/
        String fullPathToTitleImage = newsLocationDir.concat(tempImageUploadFolder);
        String titleImgFile = insertedTitleImgFiles.get(0);
        String ext = titleImgFile.split("\\.")[1];
        String newsTitleImgFile = titleImgParams.newsTitleImgFolder
                .concat(titleImgParams.newsTitleImgFileName)
                .concat(".")
                .concat(ext);
        Path titleImgFilePath = Paths.get(newsTitleImgFile);
        /**/
        Path tempFilePath = Paths.get(fullPathToTitleImage.concat(titleImgFile));
        Files.move(tempFilePath, titleImgFilePath);
    }

    public void moveNewInsertedFileFromTempFolderToNewsFolder(String originalContent, String newsRoot) throws IOException {
        for (Map.Entry<String, String> pair : replaceConformityMap.entrySet()) {
            String newsResourceFolder = newsRoot.concat(pair.getKey()); //   c:/DEVELOPING/NEWS/2016_DECEMBER_21_230/img/
            String tempResourceFolder = pair.getValue(); //  "temp_img_upload/"
            Path resourcePath = Paths.get(newsResourceFolder);
            if (!resourcePath.toFile().exists()) {
                Files.createDirectories(resourcePath);
            }
            List<String> insertedFiles = getNewsInsertedFiles(
                    originalContent,
                    "/".concat(tempResourceFolder));
            String fullPathToTempImage = newsLocationDir
                    .concat(tempResourceFolder); // C:/DEVELOPING/NEWS/temp_img_upload
            for (String resourceFile : insertedFiles) {
                String newsResourceFile = newsResourceFolder
                        .concat(resourceFile);
                Path resourceFilePath = Paths.get(newsResourceFile);
                if (resourceFilePath.toFile().exists()) {
                    resourceFilePath.toFile().delete();
                }
                Path tempFilePath = Paths.get(fullPathToTempImage.concat(resourceFile));
                Files.move(tempFilePath, resourceFilePath);
            }
        }
    }

    private List<String> getNewsInsertedFiles(String html, String maskForNewInsertedReference) {
        List<String> allReferences = getReferenses(html);
        return allReferences.stream()
                .filter(e -> e.contains(maskForNewInsertedReference))
                .map(e -> e.split("/")[e.split("/").length - 1])
                .collect(Collectors.toList());
    }

    private List<String> getReferenses(String html) {
        List<String> result = new ArrayList<>();
        if (StringUtils.isEmpty(html)) {
            return result;
        }
        Matcher matcher = Pattern.compile("(href\\s*=\\s*'([\\w[^']]*)')").matcher(html);
        while (matcher.find()) {
            result.add(matcher.group(2));
        }
        matcher = Pattern.compile("(href\\s*=\\s*\"([\\w[^\"]]*)\")").matcher(html);
        while (matcher.find()) {
            result.add(matcher.group(2));
        }
        matcher = Pattern.compile("(src\\s*=\\s*'([\\w[^']]*)')").matcher(html);
        while (matcher.find()) {
            result.add(matcher.group(2));
        }
        matcher = Pattern.compile("(src\\s*=\\s*\"([\\w[^\"]]*)\")").matcher(html);
        while (matcher.find()) {
            result.add(matcher.group(2));
        }
        return result;
    }

    private Map<String, String> getResourcesToRefPathReplacementMap(
            List<String> refList) {
        Map<String, String> result = new HashMap<>();
        for (Map.Entry<String, String> pair : replaceConformityMap.entrySet()) {
            String newReferencePathForNewInsertedReference = "../".concat(pair.getKey()); //   "../img/"
            String maskForReplacingNewInsertedReference = "/".concat(pair.getValue()); //      "/temp_img_upload/"
            for (String ref : refList) {
                if (ref.contains(maskForReplacingNewInsertedReference)) {
                    String fileName = ref.split("/")[ref.split("/").length - 1];
                    result.put(ref, newReferencePathForNewInsertedReference.concat(fileName));
                }
            }
        }
        return result;
    }

    public String getNewsRootFolder(String resourcePath, Integer newsId) throws IOException {
        resourcePath = resourcePath
                .concat(newsId.toString());
        if (flattenResourceLocationDir) {
            resourcePath = resourcePath.replaceAll("/", "_");
        }
        String newsRoot = newsLocationDir
                .concat(resourcePath)
                .concat("/");
        return newsRoot;
    }

    private void deleteExistingTitleFileNames(TitleImgParams titleImgParams) {
        String folderForSearchTitleFile = titleImgParams.newsTitleImgFolder;
        String titleFileName = titleImgParams.newsTitleImgFileName;
        List<File> files = getTitleFileNameList(titleImgParams);
        for (File file : files) {
            file.delete();
        }
    }

    public static class TitleImgParams {
        private String newsTitleImgFolder;
        private String newsTitleImgFileName;
    }

    public TitleImgParams getTitleImgParams(Integer newsId, String newsRoot) {
        TitleImgParams result = new TitleImgParams();
        if (StringUtils.isEmpty(titleImgDir)) {
            result.newsTitleImgFolder = newsRoot;
            result.newsTitleImgFileName = titleImgFileName;
        } else {
            result.newsTitleImgFolder = newsLocationDir.concat(titleImgDir);
            result.newsTitleImgFileName = newsId.toString();
        }
        return result;
    }

    private List<File> getTitleFileNameList(TitleImgParams titleImgParams) {
        String folderForSearchTitleFile = titleImgParams.newsTitleImgFolder;
        String titleFileName = titleImgParams.newsTitleImgFileName;
        File folder = new File(folderForSearchTitleFile);
        File[] files = folder.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(final File dir,
                                  final String name) {
                return name.matches(titleFileName + "\\..*");
            }
        });
        if (files == null) return new ArrayList<>();
        return Arrays.asList((File[]) files);
    }


}
