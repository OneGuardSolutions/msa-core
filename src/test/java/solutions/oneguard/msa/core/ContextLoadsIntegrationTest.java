/*
 * This file is part of the OneGuard Micro-Service Architecture Core library.
 *
 * (c) OneGuard <contact@oneguard.email>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package solutions.oneguard.msa.core;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import solutions.oneguard.msa.core.model.Instance;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest({
    "oneguard.msa.service.name=test",
    "spring.main.banner-mode=off"
})
public class ContextLoadsIntegrationTest {
    @Autowired
    private Instance instance;

    @Test
    public void contextLoads() {
        assertEquals("test", instance.getService());
        assertNotNull(instance.getId());
    }

    @SpringBootApplication
    public static class TestConfiguration {
    }
}
