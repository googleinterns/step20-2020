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

import java.util.Date;
import java.text.SimpleDateFormat;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet responsible for creating new comments. */
@WebServlet("/new-comment")
public class NewCommentServlet extends HttpServlet {

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Entity userCommentEntity = new Entity("UserComment");

    String recipeKeyString = request.getParameter("recipe-key-string");
    userCommentEntity.setProperty("recipe-key-string", recipeKeyString);

    // Check if the user is logged in.
    // If so, they must have an account. So, get the user's key,
    // as a string. If not, leave the key-string property blank.
    UserService userService = UserServiceFactory.getUserService();
    if (userService.isUserLoggedIn()) {
      String id = userService.getCurrentUser().getUserId();
      Key userKey = KeyFactory.createKey("User", id);
      String userKeyString = KeyFactory.keyToString(userKey);
      userCommentEntity.setProperty("user-key-string", userKeyString);
    }

    String comment = request.getParameter("comment-input");
    userCommentEntity.setProperty("comment", comment);

    long timestamp = System.currentTimeMillis();
    String MMDDYYYY = timestampToMMDDYYYY(timestamp);
    userCommentEntity.setProperty("timestamp", timestamp);
    userCommentEntity.setProperty("MMDDYYYY", MMDDYYYY);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(userCommentEntity);

    response.sendRedirect("/recipe.html?key=" + recipeKeyString);
  }

 /**
  * @param timestamp Time in milliseconds.
  * @return Input timestamp in MM/DD/YYYY format.
  */
  public static String timestampToMMDDYYYY(long timestamp) {
    return new SimpleDateFormat("MM/dd/yyyy").format(new Date(timestamp));
  }
}
