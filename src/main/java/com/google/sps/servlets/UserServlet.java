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

package com.google.sps.servlets;

import data.User;
import java.io.IOException;
import com.google.gson.Gson;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/** Servlet to create and update user information. */
@WebServlet("/user")
public final class UserServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Get the id from the query string.
    long id = request.getParameter("id");

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Entity userEntity = datastore.get(id);
    String email = userEntity.getProperty("email");
    String username = userEntity.getProperty("username");
    User user = new User(id, email, username);

    // Convert to JSON and send it as the response.
    Gson gson = new Gson();

    response.setContentType("application/json");
    response.getWriter().println(gson.toJson(user));
  }

  @Override
  public void doPost(HttpServletRequeest request, HttpServletResponse response) throws IOException {   
    long id = request.getParameter("id");
    String email = request.getParameter("email");
    String username = request.getParameter("username");

    // Create a new User entity with data from the request.
    Entity userEntity = new Entity("User");
    userEntity.setProperty("id", id);
    userEntity.setProperty("email", email);
    userEntity.setProperty("username", username);

    // Store the User entity in Datastore.
    DatastoreService datastore = new DataStoreServiceFactory.getDatastoreService();
    datastore.put(userEntity);
  }
}
