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
    public boolean createBucket(@RequestParam String name) {
        return true;
    }
    @GetMapping("/demandeObjet")
    public String urlObject(@RequestParam String name) {
        return "";
    }
    //TODO retourner url si object créé
    @PostMapping("/ajoutObjet")
    public String createObject(@RequestBody AppRequest request) {
        return "";
    }

}