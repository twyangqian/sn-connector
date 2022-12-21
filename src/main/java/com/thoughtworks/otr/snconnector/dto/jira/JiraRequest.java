package com.thoughtworks.otr.snconnector.dto.jira;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JiraRequest {

    @NotBlank
    private String username;

    @NotBlank
    private String password;

    @NotBlank
    private String summary;

    @NotBlank
    private String ticketNumber;
}
