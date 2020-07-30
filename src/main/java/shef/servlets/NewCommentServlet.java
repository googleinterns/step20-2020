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

import java.util.Date;
import java.text.SimpleDateFormat;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet responsible for creating new comments. */
@WebServlet("/new-comment")
public class NewCommentServlet extends HttpServlet {

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String comment = request.getParameter("comment-input");
    long timestamp = System.currentTimeMillis();
    String MMDDYYYY = timestampToMMDDYYYY(timestamp);

    Entity userCommentEntity = new Entity("UserComment");
    userCommentEntity.setProperty("username", "mcardenas");
    userCommentEntity.setProperty("location", "Seattle, WA");
    userCommentEntity.setProperty("comment", comment);
    userCommentEntity.setProperty("timestamp", timestamp);
    userCommentEntity.setProperty("MMDDYYYY", MMDDYYYY);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(userCommentEntity);

    response.sendRedirect("/recipe.html");
  }

 /**
  * @param timestamp Time in milliseconds.
  * @return Input timestamp in MM/DD/YYYY format.
  */
  private String timestampToMMDDYYYY(long timestamp) {
    return new SimpleDateFormat("MM/dd/yyyy").format(new Date(timestamp));
  }
}
