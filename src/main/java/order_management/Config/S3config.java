package order_management.example.File_Handling.Config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class S3config {


    @Value("${cloud.aws.credentials.access-key}")
    private String accessKey;

    @Value("${cloud.aws.credentials.secret-key}")
    private String secreteKey;

    @Value("${cloud.aws.region.static}")
    private String region;

    @Bean
    public S3Client client(){

        AwsBasicCredentials awsBasicCredentials =
                AwsBasicCredentials.create(accessKey, secreteKey);
      //  AwsBasicCredentials.create(secreteKey,accessKey);


      return S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(awsBasicCredentials))
                .build();

    }


}
