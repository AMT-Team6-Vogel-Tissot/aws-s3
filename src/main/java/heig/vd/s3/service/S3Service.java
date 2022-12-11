package heig.vd.s3.service;

import heig.vd.s3.repository.S3Repository;
import heig.vd.s3.utils.GetEnvVal;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.FileNotFoundException;
import java.net.URL;
import java.time.Duration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class S3Service {

    private final S3Repository repository;

    public S3Service() {
        repository = new S3Repository();
    }

    public boolean exist() {
        return existBucket();
    }

    public boolean exist(String objectName) {
        return existObject(objectName);
    }

    public String list() {
        return listObjects();
    }

    public void create(String objectName, byte[] contentFile) {
        createObject(objectName, contentFile);
    }

    public byte[] get(String objectName) {
        return getObject(objectName);
    }

    public void delete(String objectName) {
        removeObject(objectName);
    }

    public URL publish(String objectName) throws FileNotFoundException {
        return publishURL(objectName);
    }

    public void update(String objectName, byte[] contentFile) {
        updateObject(objectName, contentFile);
    }

    public void update(String objectName, byte[] contentFile, String newImageName) {
        updateObject(objectName, contentFile, newImageName);
    }

    private boolean existBucket() {
        HeadBucketRequest hbReq = HeadBucketRequest
                .builder()
                .bucket(repository.getBucket())
                .build();

        try{
            repository.exist(hbReq);
            return true;
        }catch (S3Exception e) {
            Logger.getLogger(S3Service.class.getName()).log(Level.WARNING, e.getMessage());
            return false;
        }
    }

    private boolean existObject(String nameObject) {
        GetObjectRequest goReq = GetObjectRequest
                .builder()
                .bucket(repository.getBucket())
                .key(nameObject)
                .build();

        try {
            repository.exist(goReq);
            return true;
        } catch (S3Exception e) {
            Logger.getLogger(S3Service.class.getName()).log(Level.WARNING, e.getMessage());
            return false;
        }
    }

    private String listBuckets() {
        StringBuilder str = new StringBuilder();

        ListBucketsRequest listBucketsRequest = ListBucketsRequest.builder().build();
        ListBucketsResponse listBucketsResponse = repository.list(listBucketsRequest);

        for (Bucket b : listBucketsResponse.buckets()) {
            str.append(b.name()).append("\n");
        }

        return str.toString();
    }

    private String listObjects() {

        StringBuilder str = new StringBuilder();

        try {
            ListObjectsRequest listObjects = ListObjectsRequest
                    .builder()
                    .bucket(repository.getBucket())
                    .build();

            ListObjectsResponse res = repository.list(listObjects);

            List<S3Object> objects = res.contents();
            for (S3Object myValue : objects) {
                str.append(myValue.key()).append("\n");
            }
        } catch (S3Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
        }

        return str.toString();
    }

    private void createObject(String objectName, byte[] contentFile) {
        if(exist() && !exist(objectName)) {

            try{

                PutObjectRequest poReq = PutObjectRequest
                        .builder()
                        .bucket(repository.getBucket())
                        .key(objectName)
                        .build();

                repository.create(poReq, contentFile);

            } catch (S3Exception e) {
                System.err.println(e.awsErrorDetails().errorMessage());
            }

        }

    }

    private URL publishURL(String name) throws FileNotFoundException {
        if(exist(name)){
            GetObjectRequest goReq = GetObjectRequest
                    .builder()
                    .bucket(repository.getBucket())
                    .key(name)
                    .build();

            GetObjectPresignRequest goPreReq = GetObjectPresignRequest
                    .builder()
                    .signatureDuration(Duration.ofMinutes(Long.parseLong(GetEnvVal.getEnvVal("URL_DURATION"))))
                    .getObjectRequest(goReq)
                    .build();

            return repository.getURLObject(goPreReq);
        }else {
            throw new FileNotFoundException();
        }
    }

    private void removeObject(String objectName) {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(repository.getBucket())
                .key(objectName)
                .build();

        repository.delete(deleteObjectRequest);
    }

    private void updateObject(String objectName, byte[] contentFile) {

        if(exist(objectName)) {
            removeObject(objectName);
            createObject(objectName, contentFile);
        }
    }

    private void updateObject(String objectName, byte[] contentFile, String newObjectName) {

        if(exist(objectName) && !exist(newObjectName)) {
            removeObject(objectName);
            createObject(newObjectName, contentFile);
        }

    }

    private byte[] getObject(String objectName) {
        byte[] data = null;

        if(exist() && existObject(objectName)) {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(repository.getBucket())
                    .key(objectName)
                    .build();

            ResponseBytes<GetObjectResponse> objectBytes = repository.getObjectAsBytes(getObjectRequest);

            data = objectBytes.asByteArray();
        }

        return data;
    }
}
