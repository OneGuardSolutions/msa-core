/*
 * This file is part of the OneGuard Micro-Service Architecture Core library.
 *
 * (c) OneGuard <contact@oneguard.email>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package solutions.oneguard.msa.core.util;

import org.junit.Test;

import solutions.oneguard.msa.core.model.Instance;

import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class UtilsTest {
    @Test
    public void instanceTopicWithInstance() {
        UUID uuid = UUID.randomUUID();

        assertEquals("service.test." + uuid.toString(), Utils.instanceTopic(new Instance("test", uuid)));
    }

    @Test
    public void instanceTopicWithServiceNameAndInstanceId() {
        UUID uuid = UUID.randomUUID();

        assertEquals("service.test." + uuid.toString(), Utils.instanceTopic("test", uuid.toString()));
    }

    @Test
    public void serviceTopicWithInstance() {
        assertEquals("service.test", Utils.serviceTopic(new Instance("test", UUID.randomUUID())));
    }

    @Test
    public void serviceTopicWithServiceName() {
        assertEquals("service.test", Utils.serviceTopic("test"));
    }
}
