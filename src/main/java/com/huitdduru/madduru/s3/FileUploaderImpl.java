package com.huitdduru.madduru.s3;

import com.amazonaws.services.s3.AmazonS3;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class FileUploaderImpl implements FileUploader {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Override
    public String uploadFile(MultipartFile file) throws IOException {
        if (file==null) return null;

        File uploadFile = change(file);
        String imagePath = UUID.randomUUID().toString();

        return upload(uploadFile, imagePath);
    }

    private String upload(File uploadFile, String fileName) {
        return putObject(uploadFile, fileName);
    }

    @Override
    public void removeFile(String imageName) {
        removeObject(imageName);
    }

    @Override
    public File getFile(String imageName) throws IOException {
        return getObject(imageName);
    }

    @Override
    public String getUrl(String imageName) {
        if (imageName==null) return null;

        return amazonS3.getUrl(bucket, imageName).toString();
    }

    private String putObject(File uploadFile, String fileName) {
        amazonS3.putObject(bucket, fileName, uploadFile);
        return amazonS3.getUrl(bucket, fileName).toString();
    }

    private void removeObject(String fileName) {
        amazonS3.deleteObject(bucket, fileName);
    }

    private File getObject(String fileName) throws IOException {
        File file = new File(fileName);
        InputStream inputStream = amazonS3.getObject(bucket, fileName).getObjectContent();
        OutputStream outputStream = new FileOutputStream(file);
        byte[] buf = new byte[1024];
        int count;
        while ((count = inputStream.read(buf)) != -1) {
            outputStream.write(buf, 0, count);
        }
        outputStream.close();
        inputStream.close();
        return file;
    }

    private File change(MultipartFile multipartFile) throws IOException {
        File file = Files.createTempFile("InTechs", "").toFile();
        multipartFile.transferTo(file);

        return file;
    }

    private Optional<File> convert(MultipartFile file) throws IOException {
        File convertFile = new File(file.getOriginalFilename());
        if(convertFile.createNewFile()) {
            try (FileOutputStream fos = new FileOutputStream(convertFile)) {
                fos.write(file.getBytes());
            }
            return Optional.of(convertFile);
        }

        return Optional.empty();
    }
}
