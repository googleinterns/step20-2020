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
 
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import shef.data.Recipe;
import shef.data.Step;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.gson.Gson;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
 
/** Servlet responsible for displaying comments. */
@WebServlet("/display-recipes")
public class TestDisplayRecipesServlet extends HttpServlet {
 
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Query query = new Query("Recipe").addSort("timestamp", SortDirection.DESCENDING);
 
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);
 
    List<Recipe> recipes = new ArrayList<>();
    for (Entity entity : results.asIterable()) {
      String name = (String) entity.getProperty("name");
      String description = (String) entity.getProperty("description");
      Set<String> tags = (HashSet<String>) entity.getProperty("tags");
      Set<String> ingredients = (HashSet<String>) entity.getProperty("ingredients");
      List<Step> steps = (ArrayList<Step>) entity.getProperty("steps");
      long timestamp = (long) entity.getProperty("timestamp");
 
      Recipe recipe = new Recipe(name, description, tags, ingredients, steps, timestamp);
      recipes.add(recipe);
    }
 
    Gson gson = new Gson();
 
    response.setContentType("application/json;");
    response.getWriter().println(gson.toJson(recipes));
  }
}
