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

/*
 * The following source informed the architecture of this servlet and its functional components:
 * https://docstore.mik.ua/orelly/java-ent/servlet/ch10_03.htm
 */

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

/**
 * This servlet handles new messages, allowing for clients to see them as they're sent in real time.
 * A client sends in new messages with doPost(), and doGet() distributes them to all other active clients.
 */
@WebServlet("/new-message")
public class NewMessageServlet extends HttpServlet {

  private MessageUpdate messageUpdate;
  private DatastoreService datastore;

  @Override
  public void init() {
    messageUpdate = new MessageUpdate();
    datastore = DatastoreServiceFactory.getDatastoreService();
  }
  
  /**
   * Waits for incoming messages using MessagePromises.
   * Each GET request instantiates a MessagePromise, which blocks until a new message is received.
   * The MessagePromise then unblocks and allows the GET request to respond with the new message.
   */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    System.out.println(Thread.currentThread().getName() + ": GET request made for next mesasge");
    MessagePromise newMessagePromise = new MessagePromise(messageUpdate);

    // Blocks until the next message is received.
    String newMessage = newMessagePromise.getNextMessage();

    response.setContentType("text/html");
    response.getWriter().println(newMessage);
  }

  /**
   * Posts new messages to Datastore and distributes them to active clients using MessageUpdates.
   * The message is first added to the groupchat in Datastore, and then distributed to the waiting MessagePromises.
   * Empty messages are ignored.
   */
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

    // Send new message to waiting MessagePromises.
    messageUpdate.setMessage(message);
    messageUpdate.sendMessage();

    response.setStatus(response.SC_NO_CONTENT);
  }

  @Override
  public void service(HttpServletRequest request, HttpServletResponse response) throws IOException {
    System.out.println("NEW REQUEST: " + request.getMethod() + " " + request.getRequestURL());
    if (request.getMethod().equals("ET")) {
      System.out.println("FOUND ET");
      doGet(request, response);
    } else if (request.getMethod().equals("OST")) {
      System.out.println("FOUND OST");
      response.sendError(response.SC_BAD_REQUEST);
    } else {
      try {
        super.service(request, response);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }
}
