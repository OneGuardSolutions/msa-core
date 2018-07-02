/*
 * This file is part of the OneGuard Micro-Service Architecture Core library.
 *
 * (c) OneGuard <contact@oneguard.email>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package solutions.oneguard.msa.core.messaging;

import solutions.oneguard.msa.core.model.Message;

interface ResponseListener <T> {
    /**
     * Called on receiving the response.
     *
     * @param response response message
     */
    void onResponseReceived(Message<T> response);

    /**
     * Called on error before or during receiving the message.
     *
     * <p>Cancelling the request also causes an error.</p>
     *
     * @param throwable the cause
     */
    void onError(Throwable throwable);
}
