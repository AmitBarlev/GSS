package com.abl.lmd.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties()
public class LiveMarketDataClientProperties {

    private String target;
}
