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

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.EntityNotFoundException;
import java.util.ArrayList;

/**
 * A wrapper class for Groupchat entities in Datastore.
 * Groupchat objects hold references to their corresponding Datastore entities and provide methods that
 *     update both the Groupchat object and entity.
 * This ensures that Groupchat data is consistent between servlets and Datastore.
 */
public class Groupchat {

  private ArrayList<String> messages;
  private DatastoreService datastore;
  private Entity entity;

  public Groupchat(String key) {
    this.datastore = DatastoreServiceFactory.getDatastoreService();
    this.entity = null;
    try {
      this.entity = datastore.get(KeyFactory.stringToKey(key));
    } catch (EntityNotFoundException e) {
      e.printStackTrace();
      return;
    }

    Object messageObject = this.entity.getProperty("messages");
    this.messages = messageObject != null ? (ArrayList<String>) messageObject : new ArrayList<>();
  }

  public ArrayList<String> getMessages() {
    return messages;
  }

  public void addMessage(String message) {
    messages.add(message);
    entity.setProperty("messages", messages);
  }

  public void update() {
    datastore.put(entity);
  }
}
