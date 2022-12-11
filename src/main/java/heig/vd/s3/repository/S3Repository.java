package heig.vd.s3.repository;

import heig.vd.s3.utils.GetEnvVal;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
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
import java.util.Objects;

@Repository
public class S3Repository {

    private final S3Presigner presigner;
    private final S3Client cloudClient;

    private final String bucket;

    public S3Repository() {

        cloudClient = S3Client
                .builder()
                .credentialsProvider(getCredentials())
                .region(Region.of(Objects.requireNonNull(GetEnvVal.getEnvVal("REGION"))))
                .build();

        presigner = S3Presigner
                .builder()
                .credentialsProvider(getCredentials())
                .region(Region.of(Objects.requireNonNull(GetEnvVal.getEnvVal("REGION"))))
                .build();

        bucket = Objects.requireNonNull(GetEnvVal.getEnvVal("BUCKET"));
    }

    public String getBucket() {
        return bucket;
    }

    private static AwsCredentialsProvider getCredentials(){
        String accessKeyID = Objects.requireNonNull(GetEnvVal.getEnvVal("AWS_ACCESS_KEY_ID"));
        String secretAccessKey = Objects.requireNonNull(GetEnvVal.getEnvVal("AWS_SECRET_ACCESS_KEY"));

        return StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKeyID, secretAccessKey));
    }

    public URL getURLObject(GetObjectPresignRequest goPreReq){
        PresignedGetObjectRequest presignedRequest = presigner.presignGetObject(goPreReq);

        return presignedRequest.url();
    }

    public void exist(HeadBucketRequest hbReq) {
        cloudClient.headBucket(hbReq);
    }

    public void exist(GetObjectRequest goReq) {
        cloudClient.getObject(goReq);
    }

    public ListBucketsResponse list(ListBucketsRequest listBucketsRequest) {
        return cloudClient.listBuckets(listBucketsRequest);
    }

    public ListObjectsResponse list(ListObjectsRequest listObjects) {
        return cloudClient.listObjects(listObjects);
    }

    public void create(PutObjectRequest poReq, byte[] contentFile) {
        cloudClient.putObject(poReq, RequestBody.fromBytes(contentFile));
    }

    public void delete(DeleteObjectRequest deleteObjectRequest) {
        cloudClient.deleteObject(deleteObjectRequest);
    }

    public ResponseBytes<GetObjectResponse> getObjectAsBytes(GetObjectRequest getObjectRequest) {
        return cloudClient.getObjectAsBytes(getObjectRequest);
    }
}
