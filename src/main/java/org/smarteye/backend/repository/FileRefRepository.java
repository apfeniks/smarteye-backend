package org.smarteye.backend.repository;

import org.smarteye.backend.domain.FileRef;
import org.smarteye.backend.domain.enums.StorageType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FileRefRepository extends JpaRepository<FileRef, Long> {

    Optional<FileRef> findByObjectKey(String objectKey);

    boolean existsByObjectKey(String objectKey);

    List<FileRef> findAllByStorage(StorageType storage);
}
