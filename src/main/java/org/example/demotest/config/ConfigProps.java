package org.example.demotest.config;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@NoArgsConstructor
@Component
@ConfigurationProperties(prefix = "custom.props")
public class ConfigProps {
    private String url;
    private String name;
    private Props properties;
    @Data
    @NoArgsConstructor
    private static class Props{
        private Integer value;
        private String region;
    }
}
