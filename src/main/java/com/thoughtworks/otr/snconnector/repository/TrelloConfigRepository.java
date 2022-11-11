package com.thoughtworks.otr.snconnector.repository;

import com.thoughtworks.otr.snconnector.entity.TrelloConfig;
import com.thoughtworks.otr.snconnector.enums.Squad;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TrelloConfigRepository extends JpaRepository<TrelloConfig, Long> {

    Optional<TrelloConfig> findBySquad(Squad squad);
}
