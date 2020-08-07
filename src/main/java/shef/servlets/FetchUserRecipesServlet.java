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

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.Gson;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
 
/** Servlet responsible for fetching recipes the user has created. */
@WebServlet("/fetch-user-recipes")
public class FetchUserRecipesServlet extends HttpServlet {
 
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    UserService userService = UserServiceFactory.getUserService();
    Key currentUserKey = KeyFactory.createKey("User", userService.getCurrentUser().getUserId());
    String userKeyString = KeyFactory.keyToString(currentUserKey);
    Query query = new Query("Recipe");
    Filter userRecipesFilter = new FilterPredicate("user", FilterOperator.EQUAL, userKeyString);
    query.setFilter(userRecipesFilter);
 
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);
 
    List<String> recipeKeys = new LinkedList<>();
    for (Entity entity : results.asIterable()) {
      String recipeKey = KeyFactory.keyToString(entity.getKey());
      recipeKeys.add(recipeKey);
    }
 
    Gson gson = new Gson();
    response.setContentType("application/json;");
    response.getWriter().println(gson.toJson(recipeKeys));
  }
}
