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

import shef.data.LiveStream;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet responsible for displaying comments. */
@WebServlet("/display-livestreams")
public class DisplayLiveStreamsServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Query query = new Query("LiveStream").addSort("timestamp", SortDirection.DESCENDING);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);

    List<LiveStream> liveStreams = new LinkedList<>();
    for (Entity entity : results.asIterable()) {
      String recipeKey = (String) entity.getProperty("recipe-key");
      String keyString = (String) KeyFactory.keyToString(entity.getKey());
      String link = (String) entity.getProperty("live-stream-link");
      String schedStartTime = (String) entity.getProperty("sched-start-time");
      String schedEndTime = (String) entity.getProperty("sched-end-time");
      String duration = (String) entity.getProperty("duration");

      LiveStream liveStream = new LiveStream(recipeKey, keyString, link, schedStartTime, schedEndTime, duration);
      liveStreams.add(liveStream);
    }

    Gson gson = new Gson();

    response.setContentType("application/json;");
    response.getWriter().println(gson.toJson(liveStreams));
  }
}
