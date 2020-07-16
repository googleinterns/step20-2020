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

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.gson.Gson;
import shef.data.Recipe;
import java.util.List;
import java.util.LinkedList;

@WebServlet("/get-browsing-recipes")
public class BrowseRecipesServlet extends HttpServlet  {

  private DatastoreService datastore;

  @Override
  public void init() {
    datastore = DatastoreServiceFactory.getDatastoreService();
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String algorithm = request.getParameter("algorithm");
    PreparedQuery recipeEntities = datastore.prepare(new Query("Recipe"));
    List<Recipe> recipes = new LinkedList<>();

    for (Entity recipeEntity : recipeEntities.asIterable()) {
      recipes.add(new Recipe(recipeEntity));
    }

    response.setContentType("application/json;");
    response.getWriter().println(new Gson().toJson(recipes));
  }
}
