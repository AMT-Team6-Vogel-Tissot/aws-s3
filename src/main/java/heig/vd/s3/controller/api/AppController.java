package heig.vd.s3.controller.api;

import heig.vd.s3.AwsCloudClient;
import heig.vd.s3.controller.request.AppRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URL;


@RestController
public class AppController {

    @GetMapping("/creationBucket")
    public boolean createBucket(@RequestParam String name) {
        return true;
    }
    @GetMapping("/demandeObjet")
    public URL urlObject(@RequestParam String name) {
        return (AwsCloudClient.getInstance().getDataObject()).publish(name);
    }
    @PostMapping("/ajoutObjet")
    public boolean createObject(@RequestBody AppRequest request) {
        AwsCloudClient.getInstance().getDataObject().create(request.getObjectName(),request.getContentFile());
        return true;
    }

}