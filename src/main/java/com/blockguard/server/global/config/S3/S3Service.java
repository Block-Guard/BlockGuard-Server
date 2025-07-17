package com.blockguard.server.global.config.S3;

import com.blockguard.server.global.common.codes.ErrorCode;
import com.blockguard.server.global.exception.BusinessExceptionHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {
    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucket-name}")
    private String bucket;

    public String upload(MultipartFile file, String dir) {
        String contentType = file.getContentType();
        if (contentType == null ||
                !List.of("image/jpeg", "image/png", "image/webp").contains(contentType)) {
            throw new BusinessExceptionHandler(ErrorCode.INVALID_PROFILE_IMAGE);
        }

        String original = file.getOriginalFilename();
        if (original == null) {
            throw new BusinessExceptionHandler(ErrorCode.FILE_NAME_NOT_FOUND);
        }

        String ext = StringUtils.getFilenameExtension(original);
        if (ext == null ||
                !List.of("jpg", "jpeg", "png", "webp").contains(ext.toLowerCase())) {
            throw new BusinessExceptionHandler(ErrorCode.INVALID_PROFILE_IMAGE);
        }

        if (dir.contains("..") || dir.contains("/") || dir.contains("\\")) {
            throw new BusinessExceptionHandler(ErrorCode.INVALID_DIRECTORY_ROUTE);
        }

        String key = dir + "/" + UUID.randomUUID() + "." + ext;

        try (InputStream is = file.getInputStream()) {
            PutObjectRequest req = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(req, RequestBody.fromInputStream(is, file.getSize()));
        } catch (IOException e) {
            throw new RuntimeException("S3 업로드 실패", e);
        }

        return key;
    }

    public void delete(String key) {
        DeleteObjectRequest req = DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();
        s3Client.deleteObject(req);
    }

    public String getPublicUrl(String key) {
        return String.format("https://%s.s3.%s.amazonaws.com/%s",
                bucket, s3Client.serviceClientConfiguration().region(), key);
    }
}
