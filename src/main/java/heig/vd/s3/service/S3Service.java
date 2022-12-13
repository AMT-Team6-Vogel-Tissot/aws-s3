package heig.vd.s3.service;

import heig.vd.s3.exception.ObjectNotFoundException;
import heig.vd.s3.repository.S3Repository;
import heig.vd.s3.utils.GetEnvVal;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.net.URL;
import java.time.Duration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class S3Service {

    private final S3Repository repository;
    private final String bucketName;

    public S3Service() {
        repository = new S3Repository();
        bucketName = repository.getBucket();
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

    public void create(String objectName, byte[] contentFile){
        createObject(objectName, contentFile);
    }

    public byte[] get(String objectName) {
        return getObject(objectName);
    }

    public void delete(String objectName) {
        removeObject(objectName);
    }

    public URL publish(String objectName) {
        return publishURL(objectName);
    }

    public void update(String objectName, byte[] contentFile){
        updateObject(objectName, contentFile);
    }

    public void update(String objectName, byte[] contentFile, String newImageName){
        updateObject(objectName, contentFile, newImageName);
    }

    public String getBucketName() {
        return bucketName;
    }

    private boolean existBucket() {
        HeadBucketRequest hbReq = HeadBucketRequest
                .builder()
                .bucket(bucketName)
                .build();

        try{
            repository.exist(hbReq);
            return true;
        } catch (NoSuchBucketException e) {
            Logger.getLogger(S3Service.class.getName()).log(Level.WARNING, e.getMessage());
            return false;
        }
    }

    private boolean existObject(String nameObject) {
        GetObjectRequest goReq = GetObjectRequest
                .builder()
                .bucket(bucketName)
                .key(nameObject)
                .build();

        try {
            repository.exist(goReq);
            return true;
        } catch (NoSuchKeyException e) {
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
                    .bucket(bucketName)
                    .build();

            ListObjectsResponse res = repository.list(listObjects);

            List<S3Object> objects = res.contents();
            for (S3Object myValue : objects) {
                str.append(myValue.key()).append("\n");
            }
        } catch (S3Exception e) {
            throw new RuntimeException("S3 à refusé de traiter la requête : " + e.getMessage());
        }

        return str.toString();
    }

    private void createObject(String objectName, byte[] contentFile) {

        try{

            PutObjectRequest poReq = PutObjectRequest
                    .builder()
                    .bucket(bucketName)
                    .key(objectName)
                    .build();

            repository.create(poReq, contentFile);

        } catch (S3Exception e) {
            throw new RuntimeException("S3 à refusé de traiter la requête : " + e.getMessage());
        }

    }

    private URL publishURL(String name) {

        URL url;
        try {
            GetObjectRequest goReq = GetObjectRequest
                    .builder()
                    .bucket(bucketName)
                    .key(name)
                    .build();

            GetObjectPresignRequest goPreReq = GetObjectPresignRequest
                    .builder()
                    .signatureDuration(Duration.ofMinutes(Long.parseLong(GetEnvVal.getEnvVal("URL_DURATION"))))
                    .getObjectRequest(goReq)
                    .build();

            url = repository.getURLObject(goPreReq);
        } catch (S3Exception e) {
            throw new RuntimeException("S3 à refusé de traiter la requête : " + e.getMessage());
        }


        return url;
    }

    private void removeObject(String objectName) {
        try{
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectName)
                    .build();

            repository.delete(deleteObjectRequest);
        } catch (S3Exception e) {
            throw new RuntimeException("S3 à refusé de traiter la requête : " + e.getMessage());
        }
    }

    private void updateObject(String objectName, byte[] contentFile){
        removeObject(objectName);
        createObject(objectName, contentFile);
    }

    private void updateObject(String objectName, byte[] contentFile, String newObjectName){
        removeObject(objectName);
        createObject(newObjectName, contentFile);
    }

    private byte[] getObject(String objectName) {
        byte[] data;

        try{
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectName)
                    .build();

            ResponseBytes<GetObjectResponse> objectBytes = repository.getObjectAsBytes(getObjectRequest);

            data = objectBytes.asByteArray();
        } catch (S3Exception e) {
            throw new ObjectNotFoundException("L'objet spécifié n'existe pas : " + objectName);
        }

        return data;
    }
}
