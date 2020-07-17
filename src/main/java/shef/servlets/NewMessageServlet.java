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

package shef.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import shef.data.MessageUpdate;
import shef.data.MessagePromise;
import shef.data.Groupchat;

@WebServlet("/new-message")
public class NewMessageServlet extends HttpServlet {

  private MessageUpdate messageUpdate;
  private DatastoreService datastore;

  @Override
  public void init() {
    messageUpdate = new MessageUpdate();
    datastore = DatastoreServiceFactory.getDatastoreService();
  }
  
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String message = request.getParameter("message");
    if (message.equals("")) {
      return;
    }

    // Upload new message to Datastore.
    String keyString = request.getParameter("groupchat-key");
    Groupchat group = new Groupchat(keyString);
    group.addMessage(message);
    group.update();

    // Send message to waiting MessagePromises.
    messageUpdate.setMessage(message);
    messageUpdate.sendMessage();

    response.setStatus(response.SC_NO_CONTENT);
  }

}
