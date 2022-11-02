package org.bala;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.stream.StreamSupport;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import io.minio.ListObjectsArgs;
import io.minio.RemoveBucketArgs;
import io.minio.RemoveObjectArgs;
import io.minio.Result;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.ServerException;
import io.minio.errors.XmlParserException;
import io.minio.messages.Item;

@ApplicationScoped
@Path("/bucket")
public class BucketManagementService {

    @DELETE
    @Path("/delete/{bucketName}")
    @Produces(MediaType.TEXT_PLAIN)
    public void deleteBucket(@PathParam("bucketName") String bucketName) {
        cleanup(bucketName, true);
    }

    @DELETE
    @Path("/cleanup/{bucketName}")
    @Produces(MediaType.TEXT_PLAIN)
    public void cleanupBucket(@PathParam("bucketName") String bucketName) {
        cleanup(bucketName, false);
    }

    @GET
    @Path("/size/{bucketName}")
    @Produces(MediaType.TEXT_PLAIN)
    public int sizeOfBucket(@PathParam("bucketName") String bucketName) {
        return Long.valueOf(count(bucketName)).intValue();
    }

    long count(String bucketName) {
        Iterable<Result<Item>> results = ImageRetriever.minioClient.listObjects(
                ListObjectsArgs.builder().bucket(bucketName).build());

        return StreamSupport.stream(results.spliterator(), true).count();
    }

    void cleanup(String bucketName, boolean deleteBucket) {
        Iterable<Result<Item>> results = ImageRetriever.minioClient.listObjects(
                ListObjectsArgs.builder().bucket(bucketName).build());

        try {
            for (Result<Item> image : results) {
                ImageRetriever.minioClient.removeObject(
                        RemoveObjectArgs.builder().bucket(bucketName).object(image.get().objectName()).build());
            }

            if (deleteBucket) {
                ImageRetriever.minioClient.removeBucket(RemoveBucketArgs.builder().bucket(bucketName).build());
            }
        } catch (InvalidKeyException | ErrorResponseException | InsufficientDataException | InternalException
                | InvalidResponseException | NoSuchAlgorithmException | ServerException | XmlParserException
                | IllegalArgumentException | IOException e) {
            e.printStackTrace();
        }
    }
}
