package com.thoughtworks.otr.snconnector.repository;

import com.thoughtworks.otr.snconnector.entity.ServiceNowSyncFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceNowSyncFileRepository extends JpaRepository<ServiceNowSyncFile, Long> {
}
