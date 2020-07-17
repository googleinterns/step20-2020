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

public class MessageUpdate extends Observable {

/**
 * Handles incoming messages and distributes them to waiting MessagePromises. 
 * The methods are synchronized to allow only one thread to modify and send a message at a time.
 * This prevents race conditions where two users post a new message simultaneously, which could allow
 *     a message to be changed and lost before it is sent.
 */
public class MessageUpdate extends Observable {

  private String message;

  public MessageUpdate() {
    this.message = null;
  }

  /** Sends the message to waiting observers via notifyObservers(). */
  public synchronized void sendMessage() {
    notifyObservers(message);
  }

  /** Set the message and mark the MessageUpdate as changed. */
  public synchronized void setMessage(String message) {
    this.message = message;
    setChanged();
  }
}
