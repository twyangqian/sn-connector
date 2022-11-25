package com.thoughtworks.otr.snconnector.repository;

import com.thoughtworks.otr.snconnector.entity.ServiceNowSyncData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ServiceNowSyncDataRepository extends JpaRepository<ServiceNowSyncData, Long> {

    Optional<ServiceNowSyncData> findByTrelloCardId(String trelloCardId);
    Optional<ServiceNowSyncData> findByTicket(String ticket);
}
