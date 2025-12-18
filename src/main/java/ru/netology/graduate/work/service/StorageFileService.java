package ru.netology.graduate.work.service;


import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.graduate.work.dto.request.EditFileNameRequest;
import ru.netology.graduate.work.dto.response.FileResponse;
import ru.netology.graduate.work.exception.InputDataException;
import ru.netology.graduate.work.exception.UnauthorizedException;
import ru.netology.graduate.work.model.StorageFile;
import ru.netology.graduate.work.model.User;
import ru.netology.graduate.work.repository.AuthenticationRepo;
import ru.netology.graduate.work.repository.StorageFileRepo;
import ru.netology.graduate.work.repository.UserRepo;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class StorageFileService {

    private StorageFileRepo storageFileRepo;
    private AuthenticationRepo authenticationRepo;
    private UserRepo userRepo;

    public boolean uploadFile(String authToken, String filename, MultipartFile file) {
        final User user = getUserByAuthToken(authToken);
        if (user == null) {
            log.error("Upload file: Unauthorized");
            throw new UnauthorizedException("Upload file: Unauthorized");
        }

        try {
            storageFileRepo.save(new StorageFile(filename, LocalDateTime.now(), file.getSize(), file.getBytes(), user));
            log.info("Success upload file. User {}", user.getUsername());
            return true;
        } catch (IOException e) {
            log.error("Upload file: Input data exception");
            throw new InputDataException("Upload file: Input data exception");
        }
    }

    @Transactional
    public void deleteFile(String authToken, String filename) {
        final User user = getUserByAuthToken(authToken);
        if (user == null) {
            log.error("Delete file: Unauthorized");
            throw new UnauthorizedException("Delete file: Unauthorized");
        }

        storageFileRepo.deleteByUserAndFilename(user, filename);

        final StorageFile tryingToGetDeletedFile = storageFileRepo.findByUserAndFilename(user, filename);
        if (tryingToGetDeletedFile != null) {
            log.error("Delete file: Input data exception");
            throw new InputDataException("Delete file: Input data exception");
        }
        log.info("Success delete file. User {}", user.getUsername());
    }

    public byte[] downloadFile(String authToken, String filename) {
        final User user = getUserByAuthToken(authToken);
        if (user == null) {
            log.error("Download file: Unauthorized");
            throw new UnauthorizedException("Download file: Unauthorized");
        }

        final StorageFile file = storageFileRepo.findByUserAndFilename(user, filename);
        if (file == null) {
            log.error("Download file: Input data exception");
            throw new InputDataException("Download file: Input data exception");
        }
        log.info("Success download file. User {}", user.getUsername());
        return file.getFileContent();
    }

    @Transactional
    public void editFileName(String authToken, String filename, EditFileNameRequest editFileNameRequest) {
        final User user = getUserByAuthToken(authToken);
        if (user == null) {
            log.error("Edit file name: Unauthorized");
            throw new UnauthorizedException("Edit file name: Unauthorized");
        }

        storageFileRepo.editFileNameByUser(user, filename, editFileNameRequest.getFilename());

        final StorageFile fileWithOldName = storageFileRepo.findByUserAndFilename(user, filename);
        if (fileWithOldName != null) {
            log.error("Edit file name: Input data exception");
            throw new InputDataException("Edit file name: Input data exception");
        }
        log.info("Success edit file name. User {}", user.getUsername());
    }

    public List<FileResponse> getAllFiles(String authToken, Integer limit) {
        final User user = getUserByAuthToken(authToken);
        if (user == null) {
            log.error("Get all files: Unauthorized");
            throw new UnauthorizedException("Get all files: Unauthorized");
        }
        log.info("Success get all files. User {}", user.getUsername());
        return storageFileRepo.findAllByUser(user).stream()
                .map(o -> new FileResponse(o.getFilename(), o.getSize()))
                .collect(Collectors.toList());
    }

    private User getUserByAuthToken(String authToken) {
        if (authToken.startsWith("Bearer ")) {
            final String authTokenWithoutBearer = authToken.split(" ")[1];
            final String username = authenticationRepo.getUsernameByToken(authTokenWithoutBearer);
            return userRepo.findByUsername(username);
        }
        return null;
    }
}
