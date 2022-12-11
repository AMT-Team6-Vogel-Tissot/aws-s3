package heig.vd.s3.controller.api;

import heig.vd.s3.controller.request.AppRequest;
import heig.vd.s3.service.S3Service;
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
    public ResponseEntity<Object> urlObject(@RequestParam String name) {

        try{
            URL url = service.publish(name);
            return new ResponseEntity<>(url, HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>("Image non existante : " + e.getMessage(), HttpStatus.NOT_FOUND);
        }

    }

    @PostMapping(value = "/objet", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AppRequest> createObject(@Valid @RequestBody AppRequest request) {
        //AwsCloudClient.getInstance().getDataObject().create(request.getObjectName(),request.getContentFile());

        return new ResponseEntity<>(request, HttpStatus.CREATED);
    }

}