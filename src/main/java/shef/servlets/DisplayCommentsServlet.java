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

import shef.data.UserComment;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet responsible for displaying comments. */
@WebServlet("/display-comments")
public class DisplayCommentsServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Query query = new Query("UserComment").addSort("timestamp", SortDirection.DESCENDING);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);

    String userKeyString;
    String username;
    String location;
    List<UserComment> userComments = new LinkedList<>();
    for (Entity entity : results.asIterable()) {
      String recipeKeyString = (String) entity.getProperty("recipe-key-string");
      String comment = (String) entity.getProperty("comment");
      String MMDDYYYY = (String) entity.getProperty("MMDDYYYY");
      userKeyString = (String) entity.getProperty("user-key-string"); // Key corresponding to the user, as a string.
      if (userKeyString == null) { // If the user is not logged in, the key-string entry is blank.
        username = "Anonymous";
        location = "Unknown";
      } else {
        try {
          // Get info from user corresponding to the key.
          Key userKey = KeyFactory.stringToKey(userKeyString);
          Entity user = datastore.get(userKey);
          username = (String) user.getProperty("username");
          location = (String) user.getProperty("location");
        } catch (EntityNotFoundException e) {
          throw new IOException("Entity not found.");
        }
      }
      UserComment userComment = new UserComment(recipeKeyString, userKeyString, username, location, secureReformat(comment), MMDDYYYY);
      userComments.add(userComment);
    }

    Gson gson = new Gson();
    response.setContentType("application/json;");
    response.getWriter().println(gson.toJson(userComments));
  }

 /**
  * Reformat comments to prevent HTML and script injections.
  * @param input Comment to reformat.
  * @return Comment with HTML tags replaced.
  */
  private String secureReformat(String input) {
    return input.replace("<", "&lt;").replace(">", "&gt;");
  }
}
