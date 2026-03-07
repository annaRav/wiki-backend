package com.axis.media.service;

import com.axis.media.dto.MediaFileResponse;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

public interface MediaFileService {

    MediaFileResponse upload(InputStream data, String originalFilename, String mimeType, UUID ownerId);

    File download(UUID id, UUID ownerId);

    MediaFileResponse getMetadata(UUID id, UUID ownerId);

    List<MediaFileResponse> listByOwner(UUID ownerId, int page, int size);

    long countByOwner(UUID ownerId);

    void delete(UUID id, UUID ownerId);
}
