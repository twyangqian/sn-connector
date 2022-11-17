package com.thoughtworks.otr.snconnector.utils.mapper;

import com.thoughtworks.otr.snconnector.dto.report.PartsTicketReportDTO;
import com.thoughtworks.otr.snconnector.entity.ServiceNowSyncData;
import com.thoughtworks.otr.snconnector.utils.DateUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.time.Instant;

@Mapper(
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ServiceNowSyncDataMapper {

    ServiceNowSyncDataMapper SERVICE_NOW_SYNC_DATA_MAPPER = Mappers.getMapper(ServiceNowSyncDataMapper.class);

    @Mapping(target = "ticketOpenDate", expression = "java(mapTicketOpenData(serviceNowSyncData.getTicketOpenDate()))")
    PartsTicketReportDTO toPartTicketReportDTO(ServiceNowSyncData serviceNowSyncData);

    default String mapTicketOpenData(Instant ticketOpenDate) {
        return DateUtils.instantToString(ticketOpenDate);
    }
}
