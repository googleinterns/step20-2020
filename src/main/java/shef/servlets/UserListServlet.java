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
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.gson.Gson;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.EntityNotFoundException;

/** Servlet that returns links to every user's profile (FOR TESTING PURPOSES). */
@WebServlet("/user-list")
public class UserListServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    
    // Query all users from Datastore.
    Query query = new Query("User");

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);

    // Fill userInfo arraylist with data from Datastore.
    ArrayList<User> userInfo = new ArrayList<User>();
    for (Entity entity : results.asIterable()) {
      Key userKey = entity.getKey();
      String username = (String) entity.getProperty("username");
      String link = "/profile-page-test.html?key=" + KeyFactory.keyToString(userKey);
      User user = new User(username, link);
      userInfo.add(user);
    }

    // Convert to JSON and send it as the response.
    Gson gson = new Gson();

    response.setContentType("application/json");
    response.getWriter().println(gson.toJson(userInfo));
    }
  }
