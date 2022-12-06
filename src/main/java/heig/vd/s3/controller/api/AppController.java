package heig.vd.s3.controller.api;

import heig.vd.s3.controller.request.AppRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class AppController {

    @GetMapping("/creationBucket")
    public void createBucket(@RequestParam String name) {

    }
    @GetMapping("/demandeObjet")
    public void urlObject(@RequestParam String name) {

    }
    @PostMapping("/ajoutObjet")
    public void createObject(@RequestBody AppRequest request) {

    }

}