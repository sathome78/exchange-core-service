package me.exrates.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface UserFilesService {

    List<MultipartFile> reduceInvalidFiles(MultipartFile[] files);

    boolean checkFileValidity(MultipartFile file);

    void createUserFiles(int userId, List<MultipartFile> files) throws IOException;
}
