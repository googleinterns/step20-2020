// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package shef.data;

import java.util.Observer;
import java.util.Observable;
import shef.data.MessageUpdate;

/** 
 * Returns new messages to GET requests, blocking until they are received from the MessageUpdate. 
 * Each MessagePromise is used to get one message. 
 */
public class MessagePromise implements Observer {

  private String message;
  private boolean updated;
  private MessageUpdate messageUpdate;

  public MessagePromise(MessageUpdate messageUpdate) {
    this.message = null;
    this.updated = false;
    this.messageUpdate = messageUpdate;
    this.messageUpdate.addObserver(this);
  }

  /**
   * Gets the next message from the MessageUpdate.
   * This method waits until it receives a new message from MessageUpdate.
   * It then unblocks, and returns the message to the servlet.
   */
  synchronized public String getNextMessage() {
    // Ensure that the thread waits until an update is detected.
    while (!updated) {
      try {
        wait();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    messageUpdate.deleteObserver(this);
    return message;
  }

  /** Receives a new message and then wakes the waiting thread with notify(). */
  @Override
  synchronized public void update(Observable messageUpdate, Object message) {
    this.message = (String) message;
    this.updated = true;
    notify();
  }
}
