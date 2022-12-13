package heig.vd.s3.controller.api;

import heig.vd.s3.controller.request.AppRequest;
import heig.vd.s3.controller.request.UpdateAppRequest;
import heig.vd.s3.exception.BucketDoesntExistException;
import heig.vd.s3.exception.ObjectAlreadyExistException;
import heig.vd.s3.exception.ObjectNotFoundException;
import heig.vd.s3.service.S3Service;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.net.URL;

@RestController
public class S3AppController {

    private final S3Service service;

    public S3AppController() {
        service = new S3Service();
    }

    @GetMapping(value = "/objet/publie", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> urlObject(@RequestParam @NotBlank String name) {

        if(!service.exist()) {
            throw new BucketDoesntExistException(service.getBucketName());
        }

        if(!service.exist(name)) {
            throw new ObjectNotFoundException(name);
        }

        URL url = service.publish(name);

        return new ResponseEntity<>(url, HttpStatus.OK);
    }

    @GetMapping(value = "/objet/existe", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> doesObjectExist(@RequestParam @NotBlank String name) {

        if(!service.exist()) {
            throw new BucketDoesntExistException(service.getBucketName());
        }

        boolean response = service.exist(name);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/objets", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> listObject() {

        if(!service.exist()) {
            throw new BucketDoesntExistException(service.getBucketName());
        }

        String response = service.list();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/objet/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getObject(@NotBlank @PathVariable String name) {

        if(!service.exist()) {
            throw new BucketDoesntExistException(service.getBucketName());
        }

        if(!service.exist(name)) {
            throw new ObjectNotFoundException(name);
        }

        byte[] response = service.get(name);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping(value = "/objet/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> deleteObject(@NotBlank @PathVariable String name) {

        if(!service.exist()) {
            throw new BucketDoesntExistException(service.getBucketName());
        }

        if(!service.exist(name)) {
            throw new ObjectNotFoundException(name);
        }

        service.delete(name);

        return new ResponseEntity<>("Success", HttpStatus.OK);
    }

    @PutMapping(value = "/objet", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> updateObject(@Valid @RequestBody UpdateAppRequest updateAppRequest) {

        if(!service.exist()) {
            throw new BucketDoesntExistException(service.getBucketName());
        }

        if(!service.exist(updateAppRequest.getObjectName())) {
            throw new ObjectNotFoundException(updateAppRequest.getObjectName());
        }

        if(!service.exist(updateAppRequest.getNewObjectName())){
            throw new ObjectAlreadyExistException(updateAppRequest.getNewObjectName());
        }

        if(updateAppRequest.getNewObjectName().isBlank()){
            service.update(updateAppRequest.getObjectName(), updateAppRequest.getContentFile().getBytes());
        } else {
            service.update(updateAppRequest.getObjectName(), updateAppRequest.getContentFile().getBytes(), updateAppRequest.getNewObjectName());
        }

        return new ResponseEntity<>(updateAppRequest, HttpStatus.OK);
    }

    //TODO Demander au prof si on doit faire seulement les appels API du diagramme
    //TODO Voir comment passer une image (encodée en base64 ?)
    //TODO Demander comment faire une requête json avec un AppRequest/updateAppRequest
    //TODO Voir comment renvoyer un objet en json (ResponseEntity en json, pas obligé mais mieux)
    //TODO Demander si les throws qui sont fait dans le controller ne devraient pas être fait uniquement dans le service ou alors dans les deux endroits
    //TODO Faire tests d'intégration ?
    //TODO Faire la création/destruction du bucket
    @PostMapping(value = "/objet", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> createObject(@Valid @RequestBody AppRequest request) {

        if(!service.exist()) {
            throw new BucketDoesntExistException(service.getBucketName());
        }

        if(service.exist(request.getObjectName())) {
            throw new ObjectNotFoundException(request.getObjectName());
        }

        service.create(request.getObjectName(), request.getContentFile().getBytes());

        return new ResponseEntity<>(request, HttpStatus.CREATED);
    }



}