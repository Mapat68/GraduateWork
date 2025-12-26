package ru.netology.graduate.work.controller;


import lombok.AllArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.graduate.work.dto.request.EditFileNameRequest;
import ru.netology.graduate.work.dto.response.FileResponse;
import ru.netology.graduate.work.model.User;
import ru.netology.graduate.work.repository.AuthenticationRepo;
import ru.netology.graduate.work.repository.UserRepo;
import ru.netology.graduate.work.service.StorageFileService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/")
@AllArgsConstructor
public class StorageFileController {
    private AuthenticationRepo authenticationRepo;
    private UserRepo userRepo;
    private StorageFileService cloudStorageService;

    @PostMapping("/file")
    public ResponseEntity<?> uploadFile(@RequestHeader("auth-token") String authToken, @RequestParam("filename") String filename, MultipartFile file) {
        final User user = getUserByAuthToken(authToken);
        cloudStorageService.uploadFile(user, filename, file);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @DeleteMapping("/file")
    public ResponseEntity<?> deleteFile(@RequestHeader("auth-token") String authToken, @RequestParam("filename") String filename) {
        final User user = getUserByAuthToken(authToken);
        cloudStorageService.deleteFile(user, filename);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @GetMapping("/file")
    public ResponseEntity<Resource> downloadFile(@RequestHeader("auth-token") String authToken, @RequestParam("filename") String filename) {
        final User user = getUserByAuthToken(authToken);
        byte[] file = cloudStorageService.downloadFile(user, filename);
        return ResponseEntity.ok().body(new ByteArrayResource(file));
    }

    @PutMapping(value = "/file")
    public ResponseEntity<?> editFileName(@RequestHeader("auth-token") String authToken, @RequestParam("filename") String filename, @RequestBody EditFileNameRequest editFileNameRQ) {
        final User user = getUserByAuthToken(authToken);
        cloudStorageService.editFileName(user, filename, editFileNameRQ);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @GetMapping("/list")
    public List<FileResponse> getAllFiles(@RequestHeader("auth-token") String authToken, @RequestParam("limit") Integer limit) {
        final User user = getUserByAuthToken(authToken);
        return cloudStorageService.getAllFiles(user, limit).stream()
                .map(o -> new FileResponse(o.getFilename(), o.getSize()))
                .collect(Collectors.toList());
    }

    private User getUserByAuthToken(@RequestHeader("auth-token") String authToken) {
        if (authToken.startsWith("Bearer ")) {
            final String authTokenWithoutBearer = authToken.split(" ")[1];
            final String username = authenticationRepo.getUsernameByToken(authTokenWithoutBearer);
            return userRepo.findByUsername(username);
        }
        return null;
    }
}
