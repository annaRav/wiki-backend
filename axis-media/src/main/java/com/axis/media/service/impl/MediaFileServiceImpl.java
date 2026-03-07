package com.axis.media.service.impl;

import com.axis.common.exception.ResourceNotFoundException;
import com.axis.media.domain.MediaFile;
import com.axis.media.dto.MediaFileResponse;
import com.axis.media.mapper.MediaFileMapper;
import com.axis.media.repository.MediaFileRepository;
import com.axis.media.service.MediaFileService;
import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Slf4j
@ApplicationScoped
public class MediaFileServiceImpl implements MediaFileService {

    @Inject
    MediaFileRepository mediaFileRepository;

    @Inject
    MediaFileMapper mediaFileMapper;

    @ConfigProperty(name = "media.storage.path", defaultValue = "/var/media")
    String storagePath;

    @Override
    @Transactional
    public MediaFileResponse upload(InputStream data, String originalFilename, String mimeType, UUID ownerId) {
        String extension = extractExtension(originalFilename);
        UUID fileId = UUID.randomUUID();
        String storedFilename = fileId + (extension.isEmpty() ? "" : "." + extension);

        Path ownerDir = Paths.get(storagePath, ownerId.toString());
        Path filePath = ownerDir.resolve(storedFilename);

        try {
            Files.createDirectories(ownerDir);
            long sizeBytes = Files.copy(data, filePath, StandardCopyOption.REPLACE_EXISTING);

            MediaFile mediaFile = MediaFile.builder()
                .ownerId(ownerId)
                .originalFilename(originalFilename)
                .storedFilename(storedFilename)
                .mimeType(mimeType)
                .sizeBytes(sizeBytes)
                .storagePath(filePath.toString())
                .build();

            mediaFileRepository.persist(mediaFile);
            log.info("Uploaded file: {} for owner: {}", storedFilename, ownerId);
            return mediaFileMapper.toResponse(mediaFile);
        } catch (IOException e) {
            log.error("Failed to store file: {}", originalFilename, e);
            throw new RuntimeException("Failed to store file: " + originalFilename, e);
        }
    }

    @Override
    public File download(UUID id, UUID ownerId) {
        MediaFile mediaFile = mediaFileRepository.findByIdAndOwnerId(id, ownerId)
            .orElseThrow(() -> new ResourceNotFoundException("MediaFile", id));
        File file = new File(mediaFile.getStoragePath());
        if (!file.exists()) {
            throw new ResourceNotFoundException("MediaFile", id);
        }
        return file;
    }

    @Override
    public MediaFileResponse getMetadata(UUID id, UUID ownerId) {
        MediaFile mediaFile = mediaFileRepository.findByIdAndOwnerId(id, ownerId)
            .orElseThrow(() -> new ResourceNotFoundException("MediaFile", id));
        return mediaFileMapper.toResponse(mediaFile);
    }

    @Override
    public List<MediaFileResponse> listByOwner(UUID ownerId, int page, int size) {
        return mediaFileRepository.findByOwnerId(ownerId, Page.of(page, size))
            .stream()
            .map(mediaFileMapper::toResponse)
            .toList();
    }

    @Override
    public long countByOwner(UUID ownerId) {
        return mediaFileRepository.countByOwnerId(ownerId);
    }

    @Override
    @Transactional
    public void delete(UUID id, UUID ownerId) {
        MediaFile mediaFile = mediaFileRepository.findByIdAndOwnerId(id, ownerId)
            .orElseThrow(() -> new ResourceNotFoundException("MediaFile", id));

        Path filePath = Paths.get(mediaFile.getStoragePath());
        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            log.warn("Could not delete file from disk: {}", filePath, e);
        }

        mediaFileRepository.deleteByIdAndOwnerId(id, ownerId);
        log.info("Deleted file: {} for owner: {}", id, ownerId);
    }

    private String extractExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf('.') + 1);
    }
}
