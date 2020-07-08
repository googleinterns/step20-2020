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

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that returns a sign-in or sign-out URL. */
@WebServlet("/sign-in")
public class SignInServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

    UserService userService = UserServiceFactory.getUserService();
    String json;

    if (userService.isUserLoggedIn()) {
      String signOutUrl = userService.createLogoutURL("/sign-in-test.html");
      json = "{ \"status\": true, \"url\": \"" + signOutUrl + "\" }";
    } else {
      String signInUrl = userService.createLoginURL("/sign-in-validity-check");
      json = "{ \"status\": false, \"url\": \"" + signInUrl + "\" }";
    }

    response.setContentType("application/json");
    response.getWriter().println(json);
  }
}
