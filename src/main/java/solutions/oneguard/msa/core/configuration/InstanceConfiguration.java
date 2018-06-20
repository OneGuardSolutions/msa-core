package solutions.oneguard.msa.core.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import solutions.oneguard.msa.core.model.Instance;

import java.util.UUID;

@Configuration
@EnableConfigurationProperties(ServiceProperties.class)
public class InstanceConfiguration {
    @Value("${service.name}")
    private String serviceName;

    @Bean
    public Instance currentInstance(ServiceProperties serviceProperties) {
        return new Instance(serviceProperties.getName(), UUID.randomUUID());
    }
}
