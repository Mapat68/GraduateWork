package ru.netology.graduate.work.controller;


import lombok.AllArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.netology.graduate.work.dto.response.FileResponse;
import ru.netology.graduate.work.service.StorageFileService;

import java.util.List;

@RestController
@RequestMapping("/")
@AllArgsConstructor
public class StorageFileController {

    private StorageFileService cloudStorageService;


    @GetMapping("/file")
    public ResponseEntity<Resource> downloadFile(@RequestHeader("auth-token") String authToken, @RequestParam("filename") String filename) {
        byte[] file = cloudStorageService.downloadFile(authToken, filename);
        return ResponseEntity.ok().body(new ByteArrayResource(file));
    }


    @GetMapping("/list")
    public List<FileResponse> getAllFiles(@RequestHeader("auth-token") String authToken, @RequestParam("limit") Integer limit) {
        return cloudStorageService.getAllFiles(authToken, limit);
    }
}
