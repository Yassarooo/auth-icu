package com.jazara.icu.auth.controller;

import com.jazara.icu.auth.domain.Person;
import com.jazara.icu.auth.payload.UploadFileResponse;
import com.jazara.icu.auth.service.FileStorageService;
import com.jazara.icu.auth.service.PersonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequestMapping("/api/files")
@RestController
public class FileController {

    private static final Logger logger = LoggerFactory.getLogger(FileController.class);

    @Autowired
    private FileStorageService fileStorageService;
    @Autowired
    private PersonService personService;

    @PostMapping("/uploadFile")
    public UploadFileResponse uploadFile(@RequestPart("file") MultipartFile file) {
        String fileName = fileStorageService.storeFile(file);

        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/downloadFile/")
                .path(fileName)
                .toUriString();

        return new UploadFileResponse(fileName, fileDownloadUri,
                file.getContentType(), file.getSize());
    }

    @PostMapping("/uploadMultipleFiles")
    public List<UploadFileResponse> uploadMultipleFiles(@RequestParam("files") MultipartFile[] files) {
        return Arrays.asList(files)
                .stream()
                .map(file -> uploadFile(file))
                .collect(Collectors.toList());
    }

    @PostMapping("/uploadPersonimage")
    public UploadFileResponse uploadPersonImage(@RequestPart("image") MultipartFile file, @RequestHeader Long id, @RequestHeader List<Double> face) {
        String fileName = fileStorageService.storeFile(file);

        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/downloadFile/")
                .path(fileName)
                .toUriString();

        Optional<Person> person = personService.getPersonById(id);
        Person p = person.get();
        p.setImageLink(fileDownloadUri);
        p.setFaceFeatures(face);
        try {
            personService.editPerson(p);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new UploadFileResponse(fileName, fileDownloadUri,
                file.getContentType(), file.getSize());

    }

/*
    @PostMapping("/uploadPersonimages")
    public List<UploadFileResponse> uploadPersonImages(@RequestParam("images") MultipartFile[] files, @RequestHeader Long id, @RequestHeader List<>) {
        return Arrays.asList(files)
                .stream()
                .map(file -> uploadPersonImage(file, id))
                .collect(Collectors.toList());
    }
*/

    @GetMapping("/downloadFile/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, HttpServletRequest request) {
        // Load file as Resource
        Resource resource = fileStorageService.loadFileAsResource(fileName);

        // Try to determine file's content type
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            logger.info("Could not determine file type.");
        }

        // Fallback to the default content type if type could not be determined
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}