package org.example.backend.global.infra.s3.uploader;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.example.backend.global.common.constants.BaseResponseStatus;
import org.example.backend.global.exception.InvalidCustomException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ImageUploader {
	private final AmazonS3 s3;
	@Value("${cloud.aws.s3.bucket}")
	private String bucket;

	public List<String> uploadImages(MultipartFile[] files) {
		return Arrays.stream(files)
			.map(this::uploadImage)
			.collect(Collectors.toList());
	}

	public String uploadImage(MultipartFile file) {
		String folderPath = getFolderPath();
		String fileName = getFileName(folderPath, file.getOriginalFilename());

		ObjectMetadata objectMetadata = getS3ObjectMetadata(file.getContentType(), file.getSize());
		uploadToS3(fileName, file, objectMetadata);

		return s3.getUrl(bucket, fileName).toString();
	}

	private String getFolderPath() {
		return LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd")) + "/";
	}

	private String getFileName(String folderPath, String fileName) {
		return folderPath + UUID.randomUUID().toString().substring(0, 8) + "_" + fileName;
	}

	private ObjectMetadata getS3ObjectMetadata(String contentType, long size) {
		ObjectMetadata objectMetadata = new ObjectMetadata();
		objectMetadata.setContentType(contentType);
		objectMetadata.setContentLength(size);
		return objectMetadata;
	}

	private void uploadToS3(String fileName, MultipartFile file, ObjectMetadata objectMetadata) {
		try {
			s3.putObject(bucket, fileName, file.getInputStream(), objectMetadata);
		} catch (IOException e) {
			throw new InvalidCustomException(BaseResponseStatus.PRODUCT_BOARD_REGISTER_FAIL_UPLOAD_IMAGE);
		}
	}
}
