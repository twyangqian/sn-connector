package com.thoughtworks.otr.snconnector.entity;

import com.thoughtworks.otr.snconnector.enums.Squad;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "service_now_sync_data")
public class ServiceNowSyncData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String ticket;
    private String shortDescription;
    private String description;
    private String serviceNowLink;
    private String contact;
    private Instant ticketOpenDate;
    @Enumerated(EnumType.STRING)
    private Squad squad;
    private String trelloCardId;

}
