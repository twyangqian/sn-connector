package com.thoughtworks.otr.snconnector.constans;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ServiceNowConstant {
    public static final String SERVICE_NOW_LINK_TEMPLATE =
            "https://digitalservices.mercedes-benz.com/nav_to.do?uri=sn_customerservice_case.do?sys_id=%s";

    public static final String SERVICE_NOW_SLA_TABLE_TEMPLATE = "```\n" +
            "| time left     | elapsed time      |elapsed percentage   |\n" +
            "| ------------- |:-----------------:| -------------------:|\n" +
            "| %s| %s| %s |\n" +
            "```";

    public static final String SLA_TIME_LEFT_REGEX = "^\\s*(\\d{1,2})\\s*Hours\\s*(\\d{1,2})\\s*Minutes$";
}
