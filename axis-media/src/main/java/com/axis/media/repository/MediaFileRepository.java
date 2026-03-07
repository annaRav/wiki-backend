package com.axis.media.repository;

import com.axis.media.domain.MediaFile;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class MediaFileRepository implements PanacheRepositoryBase<MediaFile, UUID> {

    public List<MediaFile> findByOwnerId(UUID ownerId, Page page) {
        return find("ownerId", Sort.by("createdAt", Sort.Direction.Descending), ownerId)
            .page(page)
            .list();
    }

    public long countByOwnerId(UUID ownerId) {
        return count("ownerId", ownerId);
    }

    public Optional<MediaFile> findByIdAndOwnerId(UUID id, UUID ownerId) {
        return find("id = ?1 and ownerId = ?2", id, ownerId).firstResultOptional();
    }

    public long deleteByIdAndOwnerId(UUID id, UUID ownerId) {
        return delete("id = ?1 and ownerId = ?2", id, ownerId);
    }
}
