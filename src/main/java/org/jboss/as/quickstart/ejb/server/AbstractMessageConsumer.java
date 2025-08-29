/*
 * Copyright 2025 Red Hat, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jboss.as.quickstart.ejb.server;

import jakarta.jms.Message;
import jakarta.jms.MessageListener;
import jakarta.jms.TextMessage;

/**
 *
 */
public class AbstractMessageConsumer extends AbstractUtilBase implements MessageListener {

    @Override
    public void onMessage(Message message) {

        String msg = null;

        try {
            if (message instanceof TextMessage) {
                msg = String.format("MessageID: %s - %s", message.getJMSMessageID(), ((TextMessage) message).getText());
            } else {
                msg = String.format("MessageID: %s - %s", message.getJMSMessageID());
            }

        } catch (Throwable t) {
            msg = message == null ? "null" : message.toString();
        }

        // sleep to simulate some work being done
        sleep(1);

        log.info("onMessage processed: " + msg);
    }
}