package heig.vd.s3.controller.api;

import heig.vd.s3.controller.request.UpdateAppRequest;
import heig.vd.s3.exception.FileException;
import heig.vd.s3.service.S3Service;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
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
    public ResponseEntity<Object> urlObject(@RequestParam @NotBlank String name) {

        URL url = service.publish(name);

        return new ResponseEntity<>(url, HttpStatus.OK);
    }

    @GetMapping(value = "/objet/existe", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> doesObjectExist(@RequestParam @NotBlank String name) {

        boolean response = service.exist(name);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/objets", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> listObject() {

        String response = service.list();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/objet/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getObject(@NotBlank @PathVariable String name) {

        byte[] response = service.get(name);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping(value = "/objet/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> deleteObject(@NotBlank @PathVariable String name) {

        service.delete(name);

        return new ResponseEntity<>("Success", HttpStatus.OK);
    }

    @PutMapping(value = "/objet", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> updateObject(@Valid @RequestBody UpdateAppRequest updateAppRequest) {

        if(updateAppRequest.getNewObjectName().isBlank()){
            service.update(updateAppRequest.getObjectName(), updateAppRequest.getContentFile().getBytes());
        } else {
            service.update(updateAppRequest.getObjectName(), updateAppRequest.getContentFile().getBytes(), updateAppRequest.getNewObjectName());
        }

        return new ResponseEntity<>(updateAppRequest, HttpStatus.OK);
    }

    //TODO Voir comment renvoyer un objet en json (ResponseEntity en json, pas obligé mais mieux)
    //TODO Faire la création/destruction du bucket
    @PostMapping(value = "/objet", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> createObject(@NotBlank @RequestParam String objectName, @NotBlank @RequestParam MultipartFile file) {
        byte[] content;
        try{
           content = file.getBytes();
        } catch (IOException e){
            throw new FileException(objectName);
        }
        service.create(objectName, content);

        return new ResponseEntity<>(objectName, HttpStatus.CREATED);
    }



}