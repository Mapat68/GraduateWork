package ru.netology.graduate.work.service;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.netology.graduate.work.dto.response.FileResponse;
import ru.netology.graduate.work.exeption.InputDataException;
import ru.netology.graduate.work.exeption.UnauthorizedException;
import ru.netology.graduate.work.model.StorageFile;
import ru.netology.graduate.work.model.User;
import ru.netology.graduate.work.repository.AuthenticationRepo;
import ru.netology.graduate.work.repository.StorageFileRepo;
import ru.netology.graduate.work.repository.UserRepo;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class StorageFileService {

    private StorageFileRepo storageFileRepo;
    private AuthenticationRepo authenticationRepo;
    private UserRepo userRepo;


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
