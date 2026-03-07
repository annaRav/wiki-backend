package com.axis.media.mapper;

import com.axis.media.domain.MediaFile;
import com.axis.media.dto.MediaFileResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "jakarta")
public interface MediaFileMapper {

    MediaFileResponse toResponse(MediaFile mediaFile);
}
