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
    EmbeddedEntity tags = getTags(request, searchStrings);
    EmbeddedEntity ingredients = getIngredients(request, searchStrings);
    EmbeddedEntity steps = getSteps(request);

    Entity recipe = new Entity("Recipe");
    recipe.setProperty("name", name);
    recipe.setProperty("description", description);
    recipe.setProperty("tags", tags);
    recipe.setProperty("ingredients", ingredients);
    recipe.setProperty("steps", steps);
    recipe.setProperty("search-strings", searchStrings);
    datastore.put(recipe);

    response.sendRedirect("/edit-recipe.html");
  }

  private EmbeddedEntity getTags(HttpServletRequest request, Set<String> searchStrings) {
    EmbeddedEntity tags = new EmbeddedEntity();
    int tagNum = 1;

    String parameterName = TAG + tagNum;
    String tag = request.getParameter(TAG + tagNum);
    while (tag != null) {
      tags.setProperty(parameterName, tag);
      String hashTag = "#" + tag.toUpperCase();
      searchStrings.add(hashTag);
      parameterName = TAG + (++tagNum);
      tag = request.getParameter(parameterName);
    }
    return tags;
  }

  private EmbeddedEntity getIngredients(HttpServletRequest request, Set<String> searchStrings) {
    EmbeddedEntity ingredients = new EmbeddedEntity();
    int ingredientNum = 1;

    String parameterName = INGREDIENT + ingredientNum;
    String ingredient = request.getParameter(INGREDIENT + ingredientNum);
    while (ingredient != null) {
      ingredients.setProperty(parameterName, ingredient);
      searchStrings.add(ingredient.toUpperCase());
      parameterName = INGREDIENT + (++ingredientNum);
      ingredient = request.getParameter(parameterName);
    }
    return ingredients;
  }

  private EmbeddedEntity getSteps(HttpServletRequest request) {
    EmbeddedEntity steps = new EmbeddedEntity();
    int stepNum = 1;

    String parameterName = STEP + stepNum;
    String step = request.getParameter(parameterName);
    while (step != null) {
      steps.setProperty(parameterName, step);
      parameterName = STEP + (++stepNum);
      step = request.getParameter(parameterName);
    }
    return steps;
  }

}
