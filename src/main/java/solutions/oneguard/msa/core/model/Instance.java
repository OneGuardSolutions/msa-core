package solutions.oneguard.msa.core.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class Instance {
    private final String service;
    private final UUID id;
}
