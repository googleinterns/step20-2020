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

import java.util.Arrays;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import shef.data.Recipe;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.CompositeFilter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.gson.Gson;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet responsible for returning search results. */
@WebServlet("/results")
public class ResultsServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String userQuery = request.getParameter("user-query");
    Query query = new Query("Recipe").addSort("timestamp", SortDirection.DESCENDING);
    query.setFilter(generateFiltersFromQuery(formatQueryAsList(userQuery)));

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);

    List<TestRecipe> testRecipes = new LinkedList<>();
    for (Entity entity : results.asIterable()) {
      long id = entity.getKey().getId();
      ArrayList<String> searchStrings = (ArrayList<String>) entity.getProperty("search-strings");
      long timestamp = (long) entity.getProperty("timestamp");

      TestRecipe testRecipe = new TestRecipe(id, searchStrings, timestamp);
      testRecipes.add(testRecipe);
    }
    
    Gson gson = new Gson();
    
    // Max-age: Keep the response cached for 10 minutes.
    response.setHeader("Cache-Control", "max-age=600"); // HTTP 1.1.
    response.setContentType("application/json;");
    response.getWriter().println(gson.toJson(testRecipes));
  }

  public static String[] formatQueryAsList(String query) {
    // Replace commas (user inputted separators) with whitespace.
    // This way, we can split on whitespace instead of on commas,
    // The latter of which requires queries to be trimmed.
    // The search-strings we are querying are in all caps - 
    // in order to match results, query must also be in all upper case.
    query = query.replace(",", " ").toUpperCase();
    return (String[]) query.split("\\s+");
  }

  public static Filter generateFiltersFromQuery(String[] queryList) {
    // Note: Nothing shows up if nothing is put into the search box.
    // Also, the search value must be a Collection.
    // CompositeFilter fails if there are less than 2 filter items.
    if (queryList.length < 2) {
      return new FilterPredicate("search-strings", FilterOperator.IN, Arrays.asList(queryList));
    }
    List<Filter> filters = new ArrayList<Filter>();
    for (String query:queryList) {
      List<String> queryAsList = new ArrayList<String>();
      queryAsList.add(query);
      filters.add(new FilterPredicate("search-strings", FilterOperator.IN, queryAsList));
    }
    // Results will contain ALL keywords searched for, as opposed to one or a subset of them.
    return new CompositeFilter(CompositeFilterOperator.AND, filters);
  }
}
