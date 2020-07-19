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

/** Servlet responsible for creating new LiveStream entities
    and storing them in Datastore. */
@WebServlet("/new-live-stream")
public class NewLiveStreamServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Entity liveStreamEntity = new Entity("LiveStream");
    if (request.getParameterMap().get("duration")[0].equals("P0D")) {
      throw new IllegalArgumentException("Live stream start or end time is missing or invalid.");
    }
    for (String param : request.getParameterMap().keySet()) {
      liveStreamEntity.setProperty(param, (String) request.getParameterMap().get(param)[0]);
    }
    
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(liveStreamEntity);

    response.sendRedirect("/create-live-stream.html");
  }
}
