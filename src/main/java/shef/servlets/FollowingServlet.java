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

/** Servlet that updates and retrieves following information. */
@WebServlet("/following")
public class FollowingServlet extends HttpServlet {

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

    List<String> following;
    try {
      Entity userEntity = datastore.get(userKey);
      following = (List<String>) userEntity.getProperty("following");
    } catch(EntityNotFoundException e) {
      // User doesn't exist.
      e.printStackTrace();
      return;      
    }

    LinkedList<User> usersFollowed = new LinkedList<User>();

    if(following != null) {
      for(String keyString : following) {
        try {
          Key key = KeyFactory.stringToKey(keyString);
          Entity userEntity = datastore.get(key);
          String name = (String) userEntity.getProperty("username");
          String profilePicKey = (String) userEntity.getProperty("profile-pic");

          User user = new User(keyString, name, profilePicKey);
          usersFollowed.add(user);
        } catch(EntityNotFoundException e) {
          e.printStackTrace();
          return;
        }
      }
    }

    // Convert to JSON and send it as the response.
    Gson gson = new Gson();

    response.setContentType("application/json");
    response.getWriter().println(gson.toJson(usersFollowed));   
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    UserService userService = UserServiceFactory.getUserService();
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    // Retrieve the key of the user to follow.
    String userToFollowKeyString = request.getParameter("user");
    Key userToFollowKey = KeyFactory.stringToKey(userToFollowKeyString);
    Entity userToFollow;

    // Get the current user's key.
    Key currentUserKey = KeyFactory.createKey("User", userService.getCurrentUser().getUserId());
    String currentUserKeyString = KeyFactory.keyToString(currentUserKey);
    Entity currentUser;

    boolean unfollow = Boolean.parseBoolean(request.getParameter("unfollow"));
  
    try {
      // Retrieve the current user from Datastore and add to their following list.
      currentUser = datastore.get(currentUserKey);
      ArrayList<String> following = (ArrayList<String>) currentUser.getProperty("following");
      // Retrieve the user to follow, and add the current user as a follow
      userToFollow = datastore.get(userToFollowKey);
      ArrayList<String> followers = (ArrayList<String>) userToFollow.getProperty("followers");

      if(unfollow) {
        if(following != null) {
          following.remove(userToFollowKeyString);
        }
        if(followers != null) {
          followers.remove(currentUserKeyString);
        }
      } else {
        if(following == null) {
          following = new ArrayList<String>();
        }
        if(followers == null) {
          followers = new ArrayList<String>();
        }
        following.add(userToFollowKeyString);
        followers.add(currentUserKeyString);
        currentUser.setProperty("following", following);
        userToFollow.setProperty("followers", followers);
      }

    } catch(EntityNotFoundException e) {
      /** This means the current user, for whom we're trying to set following for, doesn't exist.
        * This should never happen. If it does, multiple things have gone seriously wrong.
        */
      e.printStackTrace();
      return;
    }

    // Store the user in Datastore.
    datastore.put(currentUser);
    datastore.put(userToFollow);
    response.sendRedirect("/profile-page.html?key=" + userToFollowKeyString);
  }
}
