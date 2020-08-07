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
import java.util.Map;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet responsible for creating new LiveStream entities
    and storing them in Datastore. */
@WebServlet("/new-live-stream")
public class NewLiveStreamServlet extends HttpServlet {
  // If a live stream has a length of P0D, it is invalid.
  // The YouTube API returns P0D for live streams with no start
  // and/or end time or live streams with invalid chronological
  // start and end times.
  public static final String INVALID_DURATION = "P0D";

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Entity liveStreamEntity = new Entity("LiveStream");
    Map<String, String[]> paramMap = request.getParameterMap();
    if (paramMap.get("duration")[0].equals(INVALID_DURATION)) {
      throw new IllegalArgumentException("Live stream start or end time is missing or invalid.");
    }
    for (String param : paramMap.keySet()) {
      liveStreamEntity.setProperty(param, (String) paramMap.get(param)[0]);
    }

    long timestamp = System.currentTimeMillis();
    liveStreamEntity.setProperty("timestamp", timestamp);
    
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(liveStreamEntity);

    /* Get the recipe that the user wishes to associate the live stream
      with. If the recipe already has an associated live stream, throw an error.
      Else, set the recipe's hasLiveStream field to true. hasLiveStream is used
      to determine whether and how the recipe shows on the recipe and live stream feeds. */
    Query query = new Query("Recipe");
    Key recipeKey = KeyFactory.stringToKey(request.getParameter("recipe-key"));
    Filter recipeKeyFilter = new FilterPredicate(Entity.KEY_RESERVED_PROPERTY, FilterOperator.EQUAL, recipeKey);
    query.setFilter(recipeKeyFilter);
    PreparedQuery results = datastore.prepare(query);
    for (Entity entity : results.asIterable()) {
      boolean hasLiveStream = (boolean) entity.getProperty("has-live-stream");
      if (hasLiveStream) {
        System.err.println("This event already has a live stream associated with it. Each recipe can only be associated with one live stream.");
      } else {
        entity.setProperty("has-live-stream", true);
        datastore.put(entity);
      }
    }

    response.sendRedirect("/create-live-stream.html");
  }
}
