package com.abl.lmd.configuration;

import lombok.Data;
import lombok.Generated;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Data
@Component
@Generated
@Profile("local")
@ConfigurationProperties(prefix = "mongo")
public class MongoConfigurationProperties {

    private String databaseName;
    private String connectionString;
}
