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
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.gson.Gson;
import java.util.Map;
import shef.data.Recipe;

@WebServlet("/new-recipe")
public class NewRecipeServlet extends HttpServlet {

  private DatastoreService datastore;

  @Override
  public void init() {
    datastore = DatastoreServiceFactory.getDatastoreService();
  }

  /** When a spin-off is created, this GET request gets the original recipe's data. */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String keyString = request.getParameter("key");
    Entity recipeEntity = null;
    try {
      recipeEntity = datastore.get(KeyFactory.stringToKey(keyString));
    } catch (Exception e) {
      e.printStackTrace();
      return;
    }
    Recipe original = new Recipe(recipeEntity);
    response.setContentType("application/json;");
    response.getWriter().println(new Gson().toJson(original));  
  }

  /** Posts a new recipe to the servlet. */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Map<String, String[]> parameterMap = request.getParameterMap();
    Entity recipeEntity = new Recipe(parameterMap).getEntity();
    recipeEntity.setProperty("imageKey", BlobServlet.getUploadedFileBlobKey(request, "image"));
    datastore.put(recipeEntity);
    response.sendRedirect("/recipe.html?key=" + KeyFactory.keyToString(recipeEntity.getKey()));
  }
}
