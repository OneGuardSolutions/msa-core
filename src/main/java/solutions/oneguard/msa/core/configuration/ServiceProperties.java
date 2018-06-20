package solutions.oneguard.msa.core.configuration;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "oneguard.msa.service")
@Data
@NoArgsConstructor
public class ServiceProperties {
    private String name;
}
