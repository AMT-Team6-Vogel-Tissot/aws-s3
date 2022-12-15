package heig.vd.s3.controller.api;

import heig.vd.s3.exception.FileUploadException;
import heig.vd.s3.service.S3Service;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;

@RestController
public class S3AppController {

    private final S3Service service;

    public S3AppController() {
        service = new S3Service();
    }

    @GetMapping(value = "/objet/publie", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> urlObject(@RequestParam @NotBlank String nom) {

        URL url = service.publish(nom);

        return new ResponseEntity<>(url, HttpStatus.OK);
    }

    @GetMapping(value = "/objet/existe", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> doesObjectExist(@RequestParam @NotBlank String nom) {

        boolean response = service.exist(nom);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/objets", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> listObject() {

        String response = service.list();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/objet/{nom}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getObject(@NotBlank @PathVariable String nom) {

        byte[] response = service.get(nom);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping(value = "/objet/{nom}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> deleteObject(@NotBlank @PathVariable String nom) {

        service.delete(nom);

        return new ResponseEntity<>("Success", HttpStatus.OK);
    }

    @PatchMapping(value = "/objet", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> updateObject(@NotBlank @RequestParam String objectName, @NotBlank @RequestParam MultipartFile file, @RequestParam String objectNewName) {

        byte[] content;
        try{
            content = file.getBytes();
        } catch (IOException e) {
            throw new FileUploadException(objectName);
        }

        if(objectNewName.isBlank()){
            service.update(objectName, content);
        } else {
            service.update(objectName, content, objectNewName);
        }

        return new ResponseEntity<>("Success", HttpStatus.OK);
    }

    @PostMapping(value = "/objet", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> createObject(@NotBlank @RequestParam String objectName, @NotBlank @RequestParam MultipartFile file) {
        byte[] content;
        try{
           content = file.getBytes();
        } catch (IOException e) {
            throw new FileUploadException(objectName);
        }
        service.create(objectName, content);

        return new ResponseEntity<>(objectName, HttpStatus.CREATED);
    }



}