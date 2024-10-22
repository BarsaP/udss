package com.udss.UDSS.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.udss.UDSS.service.FileService;

@RestController
@RequestMapping("/api/files")
public class FileController {
	private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @GetMapping("/search")
    public ResponseEntity<List<String>> searchFiles(
            @RequestParam String userName,
            @RequestParam String searchTerm) {
        List<String> files = fileService.searchFiles(userName, searchTerm);
        return ResponseEntity.ok(files);
    }

    // Optional: Download file endpoint
    @GetMapping("/download")
    public ResponseEntity<byte[]> downloadFile(
            @RequestParam String userName,
            @RequestParam String fileName) throws IOException {

        byte[] fileContent = fileService.downloadFile(userName, fileName);
        
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .body(fileContent);
    }
    // Optional: Upload file endpoint
    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(
            @RequestParam("userName") String userName,
            @RequestParam("file") MultipartFile file) {

        String fileUrl = fileService.uploadFile(userName, file);
        return ResponseEntity.ok("File uploaded successfully. File URL: " + fileUrl);
    }
}
