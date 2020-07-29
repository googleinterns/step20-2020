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
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import shef.data.Recipe;
import shef.data.Step;
import shef.servlets.NewRecipeServlet;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EmbeddedEntity;
import com.google.appengine.api.datastore.KeyFactory;
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
public class DisplayRecipesServlet extends HttpServlet {
 
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Query query = new Query("Recipe").addSort("timestamp", SortDirection.DESCENDING);
 
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);
 
    List<Recipe> recipes = new ArrayList<>();
    for (Entity entity : results.asIterable()) {
      recipes.add(entityToRecipe(entity));
    }
 
    Gson gson = new Gson();
 
    response.setContentType("application/json;");
    response.getWriter().println(gson.toJson(recipes));
  }

   /** Converts a Datastore entity into a Recipe. */
  public Recipe entityToRecipe(Entity recipeEntity) {
    String key = KeyFactory.keyToString(recipeEntity.getKey());
    String user = (String) recipeEntity.getProperty("user");
    String name = (String) recipeEntity.getProperty("name");
    String description = (String) recipeEntity.getProperty("description");
    LinkedHashSet<String> tags = new LinkedHashSet<>((LinkedList<String>) (LinkedList<?>) getDataAsList(recipeEntity.getProperty("tags"), "tag"));
    LinkedHashSet<String> ingredients = new LinkedHashSet<>((LinkedList<String>) (LinkedList<?>) getDataAsList(recipeEntity.getProperty("ingredients"), "ingredient"));
    LinkedList<Step> steps = (LinkedList<Step>) (LinkedList<?>) getDataAsList(recipeEntity.getProperty("steps"), "step");
    long timestamp = (long) recipeEntity.getProperty("timestamp");
    return new Recipe(key, name, user, description, tags, ingredients, steps, timestamp);
  }

  /** Gets a list of Recipe parameters from a Datastore property. */
  public Collection<Object> getDataAsList(Object propertiesObject, String field) {
    Collection<EmbeddedEntity> properties = (Collection<EmbeddedEntity>) propertiesObject;
    Collection<Object> dataAsList = new LinkedList<>();
    for (EmbeddedEntity property : properties) {
      dataAsList.add(property.getProperty(field));
    }
    return dataAsList;
  }
}
