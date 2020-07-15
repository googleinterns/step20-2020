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

import java.util.Observable;

/** Handles incoming messages and distributes them to waiting clients. */
public class MessageUpdate extends Observable {

  private String message;

  public MessageUpdate() {
    this.message = null;
  }

  /** 
   * Sends the message to waiting observers via notifyObservers().
   * The MessageUpdate is also marked as changed, and is unable to accept new messages until the change is cleared. 
   */
  public void sendMessage() {
    setChanged();
    notifyObservers(message);
  }

  /**
   * If the MessageUpdate has not changed, set the message.
   * This is to ensure that all messages are sent, even if they're sent at the same time.
   */
  public boolean setMessage(String message) {
    if (hasChanged()) {
      return false;
    }
    this.message = message;
    return true;
  }

}