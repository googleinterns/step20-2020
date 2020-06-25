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
import java.util.Set;
import java.util.HashSet;

@WebServlet("/new-recipe")
public class NewRecipeServlet extends HttpServlet {

  private DatastoreService datastore;
  private final String TAG = "tag";
  private final String INGREDIENT = "ingredient";
  private final String STEP = "step";

  @Override
  public void init() {
    datastore = DatastoreServiceFactory.getDatastoreService();
  }

  /*
   * When a spin-off is created, this GET method gets the original recipe's data.
   */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String keyString = request.getParameter("key");
    Entity originalRecipe = null;
    try {
      originalRecipe = datastore.get(KeyFactory.stringToKey(keyString));
    } catch (EntityNotFoundException e) {
      e.printStackTrace();
      return;
    }
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Set<String> searchStrings = new HashSet<>();
    String name = request.getParameter("name");
    searchStrings.add(name.toUpperCase());
    String description = request.getParameter("description");
    EmbeddedEntity tags = getParameters(request, TAG, searchStrings);
    EmbeddedEntity ingredients = getParameters(request, INGREDIENT, searchStrings);
    EmbeddedEntity steps = getParameters(request, STEP, null);

    Entity recipe = new Entity("Recipe");
    recipe.setProperty("name", name);
    recipe.setProperty("description", description);
    recipe.setIndexedProperty("tags", tags);
    recipe.setIndexedProperty("ingredients", ingredients);
    recipe.setIndexedProperty("steps", steps);
    recipe.setProperty("search-strings", searchStrings);
    datastore.put(recipe);

    response.sendRedirect("/edit-recipe.html");
  }

  /**
   * Gets the parameters for fields that have different numbers of parameters from recipe to recipe.
   * For example, one recipe may have 2 ingredients, while another may have 20.
   * This method ensures that all tags, ingredients, and steps are recorded, no matter how many of each a recipe has.
   */
  private EmbeddedEntity getParameters(HttpServletRequest request, String type, Set<String> searchStrings) {
    EmbeddedEntity parameters = new EmbeddedEntity();
    int parameterNum = 1;

    String parameterName = type + parameterNum;
    String parameter = request.getParameter(parameterName);
    while (parameter != null) {
      parameters.setProperty(parameterName, parameter);
      addToSearchStrings(searchStrings, parameter, type);
      parameterName = type + (++parameterNum);
      parameter = request.getParameter(parameterName);
    }
    return parameters;
  }

  /**
   * Adds a formatted search string to the set of search strings.
   */
  private void addToSearchStrings(Set<String> searchStrings, String stringToAdd, String type) {
    if (searchStrings == null) {
      return;
    }
    if (type.equals("tag")) {
      stringToAdd = "#" + stringToAdd;
    }
    searchStrings.add(stringToAdd.toUpperCase());
  }
}
