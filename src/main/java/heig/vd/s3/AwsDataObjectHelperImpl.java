package HEIG.vd;

import HEIG.vd.interfaces.IDataObjectHelper;
import HEIG.vd.utils.GetEnvVal;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.net.URL;
import java.time.Duration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class AwsDataObjectHelperImpl implements IDataObjectHelper {
    private final S3Presigner presigner;
    private final S3Client cloudClient;

    private final String nameBucket;

    public AwsDataObjectHelperImpl(StaticCredentialsProvider credentialsProvider, Region region) {

        cloudClient = S3Client
                .builder()
                .credentialsProvider(credentialsProvider)
                .region(region)
                .build();

        presigner = S3Presigner
                .builder()
                .credentialsProvider(credentialsProvider)
                .region(region)
                .build();

        nameBucket = GetEnvVal.getEnvVal("BUCKET");
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

    @Override
    public void create(String objectName, byte[] contentFile) {
        createObject(objectName, contentFile);
    }

    @Override
    public byte[] get(String objectName) {
        return getObject(objectName);
    }

    @Override
    public void delete(String objectName) {
        removeObject(objectName);
    }

    @Override
    public URL publish(String objectName) {
        return publishURL(objectName);
    }

    @Override
    public void update(String objectName, byte[] contentFile) {
        updateObject(objectName, contentFile);
    }

    @Override
    public void update(String objectName, byte[] contentFile, String newImageName) {
        updateObject(objectName, contentFile, newImageName);
    }

    private boolean existBucket() {
        HeadBucketRequest hbReq = HeadBucketRequest
                .builder()
                .bucket(nameBucket)
                .build();
        try{
            cloudClient.headBucket(hbReq);
            return true;
        }catch (S3Exception e) {
            Logger.getLogger(AwsDataObjectHelperImpl.class.getName()).log(Level.WARNING, e.getMessage());
            return false;
        }
    }

    private boolean existObject(String nameObject) {
        GetObjectRequest goReq = GetObjectRequest
                .builder()
                .bucket(nameBucket)
                .key(nameObject)
                .build();

        try {
            cloudClient.getObject(goReq);
            return true;
        } catch (S3Exception e) {
            Logger.getLogger(AwsDataObjectHelperImpl.class.getName()).log(Level.WARNING, e.getMessage());
            return false;
        }
    }

    private String listBuckets() {
        StringBuilder str = new StringBuilder();

        ListBucketsRequest listBucketsRequest = ListBucketsRequest.builder().build();
        ListBucketsResponse listBucketsResponse = cloudClient.listBuckets(listBucketsRequest);

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
                    .bucket(nameBucket)
                    .build();

            ListObjectsResponse res = cloudClient.listObjects(listObjects);

            List<S3Object> objects = res.contents();
            for (S3Object myValue : objects) {
                str.append(myValue.key()).append("\n");
            }
        } catch (S3Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }

        return str.toString();
    }

    private void createObject(String objectName, byte[] contentFile) {
        if(exist() && !exist(objectName)) {

            try{

                PutObjectRequest poReq = PutObjectRequest
                        .builder()
                        .bucket(nameBucket)
                        .key(objectName)
                        .build();

                cloudClient.putObject(poReq, RequestBody.fromBytes(contentFile));

            } catch (S3Exception e) {
                System.err.println(e.awsErrorDetails().errorMessage());
                System.exit(1);
            }

        }

    }

    private URL publishURL(String objectName) {

        GetObjectRequest goReq = GetObjectRequest
                .builder()
                .bucket(nameBucket)
                .key(objectName)
                .build();

        GetObjectPresignRequest goPreReq = GetObjectPresignRequest
                .builder()
                .signatureDuration(Duration.ofMinutes(Long.parseLong(GetEnvVal.getEnvVal("URL_DURATION"))))
                .getObjectRequest(goReq)
                .build();

        PresignedGetObjectRequest presignedRequest = presigner.presignGetObject(goPreReq);

        return presignedRequest.url();
    }

    private void removeObject(String objectName) {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(nameBucket)
                .key(objectName)
                .build();

        cloudClient.deleteObject(deleteObjectRequest);
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
                    .bucket(nameBucket)
                    .key(objectName)
                    .build();

            ResponseBytes<GetObjectResponse> objectBytes = cloudClient.getObjectAsBytes(getObjectRequest);

            data = objectBytes.asByteArray();
        }

        return data;
    }
}
