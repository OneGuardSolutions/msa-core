/*
 * This file is part of the OneGuard Micro-Service Architecture Core library.
 *
 * (c) OneGuard <contact@oneguard.email>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package solutions.oneguard.msa.core.util;

import solutions.oneguard.msa.core.model.Instance;

public class Utils {
    private Utils() {}

    /**
     * Creates topic name for service instance.
     *
     * @param instance service instance representation
     * @return instance topic name
     */
    public static String instanceTopic(Instance instance) {
        return instanceTopic(instance.getService(), instance.getId().toString());
    }

    /**
     * Creates topic name for service instance.
     *
     * @param serviceName service name
     * @param instanceId instance ID
     * @return instance topic name
     */
    public static String instanceTopic(String serviceName, String instanceId) {
        return String.format("service.%s.%s", serviceName, instanceId);
    }

    /**
     * Creates topic name for service.
     *
     * @param instance service instance representation
     * @return service topic name
     */
    public static String serviceTopic(Instance instance) {
        return serviceTopic(instance.getService());
    }

    /**
     * Creates topic name for service.
     *
     * @param serviceName service name
     * @return service topic name
     */
    public static String serviceTopic(String serviceName) {
        return "service." + serviceName;
    }
}
