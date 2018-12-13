package com.mine.boot.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

@Configuration
public class JacksonConfig {
    @Bean
    @Primary
    @ConditionalOnMissingBean(ObjectMapper.class)
    public ObjectMapper jacksonObjectMapper(Jackson2ObjectMapperBuilder builder) {
        ObjectMapper om = builder.createXmlMapper(false).build();
        om.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:dd:ss"));
        om.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        om.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        return om;
    }

}
