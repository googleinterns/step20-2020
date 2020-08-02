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
import com.google.appengine.api.datastore.EmbeddedEntity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.Gson;
import java.util.Collection;
import java.util.List;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.ArrayList;
import java.util.LinkedList;
import shef.data.Recipe;
import shef.data.Step;

@WebServlet("/new-recipe")
public class NewRecipeServlet extends HttpServlet {

  private DatastoreService datastore;
  private UserService userService;
  private final String TAG = "tag";
  private final String INGREDIENT = "ingredient";
  private final String STEP = "step";

  @Override
  public void init() {
    datastore = DatastoreServiceFactory.getDatastoreService();
    userService = UserServiceFactory.getUserService();
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
    response.getWriter().println(convertToJsonUsingGson(original));  
  }

  /** Posts a new recipe to the servlet. */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Collection<String> searchStrings = new HashSet<>();
    String name = request.getParameter("name");
    searchStrings.add(name.toUpperCase());
    String description = request.getParameter("description");
    Collection<EmbeddedEntity> tags = getParameters(request, TAG, searchStrings);
    Collection<EmbeddedEntity> ingredients = getParameters(request, INGREDIENT, searchStrings);
    Collection<EmbeddedEntity> steps = getParameters(request, STEP, null);
    int likes;
    try {
      likes = Integer.parseInt(request.getParameter("likes"));
    } catch (NumberFormatException e) {
      likes = 0;
    }
    long timestamp = System.currentTimeMillis();

    Entity recipe = new Entity("Recipe");
    recipe.setProperty("name", name);
    recipe.setProperty("description", description);
    recipe.setProperty("tags", tags);
    recipe.setProperty("ingredients", ingredients);
    recipe.setProperty("steps", steps);
    recipe.setProperty("search-strings", new ArrayList<String>(searchStrings));
    recipe.setProperty("has-live-stream", false); // A newly created recipe does not have an associated live stream.
    recipe.setProperty("timestamp", timestamp);
    recipe.setProperty("likes", likes);

    // Add property for the key string of the current user.
    if (userService != null && userService.getCurrentUser() != null) {
      String id = userService.getCurrentUser().getUserId();
      Key userKey = KeyFactory.createKey("User", id);
      String userKeyString = KeyFactory.keyToString(userKey);
      recipe.setProperty("user", userKeyString);
    }
    datastore.put(recipe);

    response.sendRedirect("/recipe.html");
  }

  /**
   * Gets the parameters for fields that have different numbers of parameters from recipe to recipe.
   * This method ensures that all tags, ingredients, and steps are recorded, no matter how many of each a recipe has.
   * @return A Collection of EmbeddedEntities, where each EmbeddedEntity holds one parameter.
   */
  private Collection<EmbeddedEntity> getParameters(HttpServletRequest request, String field, Collection<String> searchStrings) {
    Collection<EmbeddedEntity> parameters = new LinkedList<>();
    int parameterNum = 0;
    String parameterName = field + parameterNum;
    String parameter = request.getParameter(parameterName);

    // In the HTML form, parameters are named as [field name][index], ie step0.
    // This loop increments the index of the parameter's name, exiting once it reaches an index for which there is no parameter.
    while (parameter != null) {
      addToSearchStrings(searchStrings, parameter);
      EmbeddedEntity parameterEntity = new EmbeddedEntity();
      parameterEntity.setProperty(field, parameter);
      parameters.add(parameterEntity);

      parameterName = field + (++parameterNum);
      parameter = request.getParameter(parameterName);
    }
    return parameters;
  }

  /** Adds a formatted search string to the set of search strings. */
  private void addToSearchStrings(Collection<String> searchStrings, String stringToAdd) {
    if (searchStrings == null) {
      return;
    }
    searchStrings.add(stringToAdd.toUpperCase());
  }

  private String convertToJsonUsingGson(Recipe recipe) {
    Gson gson = new Gson();
    return gson.toJson(recipe);
  }
}
