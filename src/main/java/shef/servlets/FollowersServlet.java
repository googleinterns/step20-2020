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

import shef.data.User;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.LinkedList;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.gson.Gson;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.EntityNotFoundException;

/** Servlet that retrieves follower information. */
@WebServlet("/followers")
public class FollowersServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    UserService userService = UserServiceFactory.getUserService();
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    // Get the key from the query string.
    String userKeyString = request.getParameter("key");
    Key userKey;
    if(userKeyString == null && userService.isUserLoggedIn()) {
      userKey = KeyFactory.createKey("User", userService.getCurrentUser().getUserId());
    } else {
      userKey = KeyFactory.stringToKey(userKeyString);
    }

    List<String> followers;
    try {
      Entity userEntity = datastore.get(userKey);
      followers = (List<String>) userEntity.getProperty("followers");
    } catch(EntityNotFoundException e) {
      // User doesn't exist.
      e.printStackTrace();
      return;      
    }


    LinkedList<User> followerUsers = new LinkedList<User>();

    if(followers != null) {
      for(String keyString : followers) {
        try {
          Key key = KeyFactory.stringToKey(keyString);
          Entity userEntity = datastore.get(key);
          String name = (String) userEntity.getProperty("username");
          String profilePicKey = (String) userEntity.getProperty("profile-pic");

          User user = new User(keyString, name, profilePicKey);
          followerUsers.add(user);
        } catch(EntityNotFoundException e) {
          e.printStackTrace();
          return;
        }
      }
    }

    // Convert to JSON and send it as the response.
    Gson gson = new Gson();

    response.setContentType("application/json");
    response.getWriter().println(gson.toJson(followerUsers));   
  }
}