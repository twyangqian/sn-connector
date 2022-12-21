package com.thoughtworks.otr.snconnector.utils;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;


import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class FreeTemplateUtils {

    public static <K, V> String renderQueryWithTemplate(Map<K, V> dataModel, Configuration cfg, String path) {
        try {
            StringWriter sw = new StringWriter();
            Template template = cfg.getTemplate(path);
            template.process(dataModel, sw);
            return sw.toString();
        } catch (IOException | TemplateException e) {
            log.error(e.toString());
        }
        return StringUtils.EMPTY;
    }
}
