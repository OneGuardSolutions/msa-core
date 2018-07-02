/*
 * This file is part of the OneGuard Micro-Service Architecture Core library.
 *
 * (c) OneGuard <contact@oneguard.email>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package solutions.oneguard.msa.core.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Message <T> {
    /** Unique message identifier. */
    private UUID id;

    /** Message type. */
    @NonNull
    private String type;

    /** Security principal identifier in whose authority the message is published. */
    private String principal;

    /** Descriptor of service instance that issued the message. */
    private Instance issuer;

    /** Actual message content. */
    @NonNull
    private T payload;

    /** Context in which the client issued the message. To be sent back unmodified on any response. */
    private Map<String, Object> context;

    /** Original message ID, this message was created as a result of, if applicable. */
    private UUID responseTo;

    /** Time of event occurrence that led to publishing of this message. */
    @NonNull
    @Builder.Default
    private Date occurredAt = new Date();

    /**
     * Indicates that any response is to be sent to the specific instance that issued this message,
     * otherwise it is to be sent to the service.
     */
    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private boolean respondToInstance = false;
}
