package org.smarteye.backend.repository;

import org.smarteye.backend.domain.PointcloudFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PointcloudFileRepository extends JpaRepository<PointcloudFile, Long> {}
