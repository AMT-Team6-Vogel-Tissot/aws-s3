package HEIG.vd;

import HEIG.vd.interfaces.ICloudClient;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;

import java.util.Objects;

import HEIG.vd.utils.GetEnvVal;

public class AwsCloudClient implements ICloudClient {

    private final AwsDataObjectHelperImpl dataObject;
    private final AwsLabelDetectorHelperImpl labelDetector;

    private static AwsCloudClient INSTANCIED = null;

    private final String bucketUrl;

    private AwsCloudClient(){

        Region region = Region.of(Objects.requireNonNull(GetEnvVal.getEnvVal("REGION")));

        StaticCredentialsProvider credentialsProvider = StaticCredentialsProvider
                .create(AwsBasicCredentials.create(GetEnvVal.getEnvVal("AWS_ACCESS_KEY_ID"),
                        GetEnvVal.getEnvVal("AWS_SECRET_ACCESS_KEY")));

        bucketUrl = GetEnvVal.getEnvVal("BUCKET");
        dataObject = new AwsDataObjectHelperImpl(credentialsProvider, region);
        labelDetector = new AwsLabelDetectorHelperImpl(credentialsProvider, region);
    }

    public static AwsCloudClient getInstance() {
        if(INSTANCIED == null){
            INSTANCIED = new AwsCloudClient();
        }
        return INSTANCIED;
    }

    public AwsDataObjectHelperImpl getDataObject() {
        return dataObject;
    }

    public AwsLabelDetectorHelperImpl getLabelDetector() {
        return labelDetector;
    }

    public String getBucketUrl(){
        return bucketUrl;
    }
}
