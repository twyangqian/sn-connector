package com.thoughtworks.otr.snconnector.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "service_now_sync_file")
public class ServiceNowSyncFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String ticket;
    private String name;
    private String urlLink;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_now_sync_data_id")
    @JsonIgnore
    private ServiceNowSyncData serviceNowSyncData;
}
