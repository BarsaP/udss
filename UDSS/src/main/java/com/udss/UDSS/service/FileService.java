package com.udss.UDSS.service;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Object;

@Service
public class FileService {
  
	private final S3Client s3Client;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    public FileService() {
        this.s3Client = S3Client.builder()
        		.credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create("YOUR_ACCESS_KEY", "YOUR_ACCESS_SECRET")))
        		.region(Region.AP_SOUTH_1)  
                .build();
    }
 // Search files by userName and searchTerm
    public List<String> searchFiles(String userName, String searchTerm) {
        String prefix = userName + "/";
        ListObjectsV2Request request = ListObjectsV2Request.builder()
                .bucket(bucketName)
                .prefix(prefix)
                .build();

        ListObjectsV2Response result = s3Client.listObjectsV2(request);
        return result.contents().stream()
                .map(S3Object::key)
                .filter(key -> key.contains(searchTerm))
                .collect(Collectors.toList());
    }

    // Download file logic here
    public byte[] downloadFile(String userName, String fileName) throws IOException {
        String key = userName + "/" + fileName; // Build S3 object key using userName and fileName

        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        ByteBuffer fileContent = s3Client.getObjectAsBytes(request).asByteBuffer();
        return fileContent.array(); // Return as byte array
    }
    //Upload file method
    public String uploadFile(String userName, MultipartFile file) {
        String fileName = file.getOriginalFilename();
        String key = userName + "/" + fileName; // Build the key with userName and fileName
        // Prepare the PutObjectRequest to upload the file to S3
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(file.getContentType())
                .build();

        try {
            // Upload file to S3 bucket
            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));
            // Return the URL/key of the uploaded file
            return "File uploaded successfully. S3 key: " + key;
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload file", e);
        }
    }

    }
