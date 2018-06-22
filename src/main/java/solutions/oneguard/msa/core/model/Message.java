package solutions.oneguard.msa.core.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Message {
    private String type;

    private Object principal;

    private Instance issuer;

    private Object payload;

    @Builder.Default
    private Date occurredAt = new Date();

    private Object reference;

    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private boolean respondToIssuer = false;
}
