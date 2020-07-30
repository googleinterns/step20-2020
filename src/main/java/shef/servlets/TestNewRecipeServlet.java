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
import java.util.List;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
 
/** Servlet responsible for creating new comments. */
@WebServlet("/new-recipe")
public class TestNewRecipeServlet extends HttpServlet {
 
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    List<String> searchStrings = new ArrayList<String>();
    String name = request.getParameter("name-input").toUpperCase();
    String ingred1 = request.getParameter("ingred-input-1").toUpperCase();
    String ingred2 = request.getParameter("ingred-input-2").toUpperCase();
    String tag1 = request.getParameter("tag-input-1").toUpperCase();
    String tag2 = request.getParameter("tag-input-2").toUpperCase();
    searchStrings.add(name);
    searchStrings.add(ingred1);
    searchStrings.add(ingred2);
    searchStrings.add(tag1);
    searchStrings.add(tag2);
 
    long timestamp = System.currentTimeMillis();
 
    Entity recipeEntity = new Entity("Recipe");
    recipeEntity.setProperty("search-strings", searchStrings);
    recipeEntity.setProperty("timestamp", timestamp);
 
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(recipeEntity);
 
    response.sendRedirect("/recipe-test.html");
  }
}
