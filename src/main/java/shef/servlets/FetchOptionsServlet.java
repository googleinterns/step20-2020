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
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import shef.data.TestRecipe;
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
 
/** Servlet responsible for fetching all possible search options,
    based on existing recipes in Datastore (i.e. recipes on Shef). */
@WebServlet("/fetch-options")
public class FetchOptionsServlet extends HttpServlet {
 
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Query query = new Query("Recipe");
 
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);
 
    Set<String> allOptions = new HashSet<>();
    for (Entity entity : results.asIterable()) {
      ArrayList<String> options = (ArrayList<String>) entity.getProperty("search-strings");
      allOptions.addAll(options);
    }
    allOptions = titleCaseItems(allOptions);
 
    Gson gson = new Gson();
    response.setContentType("application/json;");
    response.getWriter().println(gson.toJson(allOptions));
  }

  /** Title cases all strings in the given array, returning a new array
      with the modified strings. */
  public static Set<String> titleCaseItems(Set<String> set) {
    return set.stream().map(str -> str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase())
                        .collect(Collectors.toSet()); 
  }
}
