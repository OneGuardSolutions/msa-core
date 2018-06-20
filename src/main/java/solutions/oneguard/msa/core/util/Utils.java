package solutions.oneguard.msa.core.util;

import solutions.oneguard.msa.core.model.Instance;

public class Utils {
    private Utils() {}

    public static String instanceTopic(Instance instance) {
        return instanceTopic(instance.getService(), instance.getId().toString());
    }

    public static String instanceTopic(String serviceName, String instanceId) {
        return String.format("service-%s-%s", serviceName, instanceId);
    }

    public static String serviceTopic(Instance instance) {
        return serviceTopic(instance.getService());
    }

    public static String serviceTopic(String serviceName) {
        return "service-" + serviceName;
    }
}
