package solutions.oneguard.msa.core.configuration;

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
@SpringBootTest("oneguard.msa.service.name=test")
public class InstanceConfigurationTest {
    @Autowired
    private Instance instance;

    @Test
    public void contextLoads() {
        assertEquals("test", instance.getService());
        assertNotNull(instance.getId());
    }

    @SpringBootApplication
    static class TestConfiguration {}
}