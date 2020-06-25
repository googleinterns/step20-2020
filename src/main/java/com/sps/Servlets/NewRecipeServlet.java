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

@WebServlet("/new-recipe")
public class NewRecipeServlet extends HttpServlet {

  private DatastoreService datastore;
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

  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String name = request.getParameter("name");
    String description = request.getParameter("description");
    EmbeddedEntity ingredients = getIngredients(request);
    EmbeddedEntity steps = getSteps(request);

    Entity recipe = new Entity("Recipe");
    recipe.setProperty("name", name);
    recipe.setProperty("description", description);
    recipe.setProperty("ingredients", ingredients);
    recipe.setProperty("steps", steps);
    datastore.put(recipe);

    response.sendRedirect("/edit-recipe.html");
  }

  private EmbeddedEntity getIngredients(HttpServletRequest request) {
    EmbeddedEntity ingredients = new EmbeddedEntity();
    int ingredientNum = 0;

    String parameterName = INGREDIENT + ingredientNum;
    String ingredient = request.getParameter(INGREDIENT + ingredientNum);
    while (ingredient != null) {
      ingredients.setProperty(parameterName, ingredient);
      parameterName = INGREDIENT + (++ingredientNum);
      ingredient = request.getParameter(parameterName);
    }
    return ingredients;
  }

  private EmbeddedEntity getSteps(HttpServletRequest request) {
    EmbeddedEntity steps = new EmbeddedEntity();
    int stepNum = 0;

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
