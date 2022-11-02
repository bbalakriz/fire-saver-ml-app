package org.bala;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.ServerException;
import io.minio.errors.XmlParserException;
import io.quarkus.runtime.Startup;
import io.smallrye.reactive.messaging.kafka.Record;
import io.vertx.core.json.JsonObject;

@ApplicationScoped
public class ImageRetriever {
    final static String FIRE_BUCKET = "fire";
    final static String SMOKE_BUCKET = "smoke";
    final static String NORMAL_BUCKET = "normal";

    @ConfigProperty(name = "minio.endpoint")
    public String endpoint;

    @ConfigProperty(name = "minio.accesskey")
    public String accessKey;

    @ConfigProperty(name = "minio.secretkey")
    public String secretKey;

    public static MinioClient minioClient;
    String bucketName;
    String b64EncodedImage;

    // initialize the minIO client at the startup of the service
    @Startup
    public void init() {
        minioClient = MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
    }

    @Incoming("images")
    public void receive(Record<Long, String> record) {
        System.out.println("Obtained image: " + record.key());

        try {

            b64EncodedImage = record.value();

            // feed the base64 encoded image binary data to prediction ML service and
            // determine the classification
            String imageType = classifyImage(b64EncodedImage);

            // set bucket name based on the result
            if (imageType.equals("fire")) {
                bucketName = FIRE_BUCKET;
            } else if (imageType.equals("smoke")) {
                bucketName = SMOKE_BUCKET;
            } else {
                bucketName = NORMAL_BUCKET;
            }

            System.out.println("Prediction: " + imageType);
            System.out.println("Chosen bucket: " + bucketName);

            // upload the image to the suitable bucket in MinIO
            ByteArrayInputStream bis = new ByteArrayInputStream(Base64.getDecoder().decode(b64EncodedImage));
            minIOClientUpload(bucketName, bis, record.key() + ".jpg");
            bis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Inject
    @RestClient
    PredictionService predeictionService;

    // get the prediction from ML model
    // sample format of response data is {"success": "fire"}
    private String classifyImage(String data) {
        return new JsonObject(predeictionService.predict(data)).getString("success");
    }

    private void minIOClientUpload(String bucketName, InputStream imgInputStream, String objectName) {
        Map<String, String> userMetadata = new HashMap<>(1);
        userMetadata.put("project-code", "fire-alarm");

        try {
            minioClient.putObject(
                    PutObjectArgs
                            .builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .stream(imgInputStream, imgInputStream.available(), -1)
                            .userMetadata(userMetadata)
                            .build());
            imgInputStream.close();
        } catch (InvalidKeyException | ErrorResponseException | InsufficientDataException | InternalException
                | InvalidResponseException | NoSuchAlgorithmException | ServerException | XmlParserException
                | IllegalArgumentException | IOException e) {
            e.printStackTrace();
        }
        System.out.println("Uploaded image: " + objectName);
    }

}