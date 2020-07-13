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

import shef.data.User;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.net.MalformedURLException;
import java.net.URL;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.gson.Gson;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobInfoFactory;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.ServingUrlOptions;


/** Servlet to store and get retrieve user information. */
@WebServlet("/user")
public final class UserServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    UserService userService = UserServiceFactory.getUserService();
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    // Get the key from the query string.
    String keyString = request.getParameter("key");
    Key userKey;
    if(keyString == null && userService.isUserLoggedIn()) {
      userKey = KeyFactory.createKey("User", userService.getCurrentUser().getUserId());
    } else {
      userKey = KeyFactory.stringToKey(keyString);
    }

    try {
      Entity userEntity = datastore.get(userKey);
      // Since we chose to store the id as a string in Datastore, it is referred to as "name".
      String id = (String) userKey.getName();
      String email = (String) userEntity.getProperty("email");
      String username = (String) userEntity.getProperty("username");
      String location = (String) userEntity.getProperty("location");
      String imageUrl = (String) userEntity.getProperty("profile-picture-url");
      String bio = (String) userEntity.getProperty("bio");
      boolean isCurrentUser;

      if (userService.isUserLoggedIn()) {
        // ID is referred to as "Name" in Datastore.
        String id = (String) userKey.getName();
        isCurrentUser = id.equals(userService.getCurrentUser().getUserId());
      } else {
        isCurrentUser = false;
      }

      User user = new User(keyString, email, username, location, imageUrl, bio, isCurrentUser);

      // Convert to JSON and send it as the response.
      Gson gson = new Gson();

      response.setContentType("application/json");
      response.getWriter().println(gson.toJson(user));
    } catch (EntityNotFoundException e) {
      throw new IOException("Entity not found.");
    }
  }

  // Create or update a user.
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    UserService userService = UserServiceFactory.getUserService();
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    String id = userService.getCurrentUser().getUserId();
    String email = userService.getCurrentUser().getEmail();
    String username = request.getParameter("username-input");
    String location = request.getParameter("location-input");
    String profilePictureUrl = getUploadedFileUrl(request, "profile-pic-upload");
    String bio = request.getParameter("bio-input");

    Key userKey = KeyFactory.createKey("User", id);
    String keyString = KeyFactory.keyToString(userKey);

    try {
      // If updating an existing user, just update the changed fields.
      Entity user = datastore.get(userKey);
      if(email != null) {
        user.setProperty("email", email);
      }
      if(username != null) {
        user.setProperty("username", username);
      }
      if(location != null) {
        user.setProperty("location", location);
      }
      if(profilePictureUrl != null) {
        user.setProperty("profile-picture-url", profilePictureUrl);
      }
      if(bio != null) {
        user.setProperty("bio", bio);
      }

      // Store the User entity in Datastore.
      datastore.put(user);
      response.sendRedirect("/profile-page.html?key=" + keyString);
    } catch (EntityNotFoundException e) {
      // Create a new User entity with data from the request.
      Entity userEntity = new Entity(userKey);
      userEntity.setProperty("email", email);
      userEntity.setProperty("username", username);
      userEntity.setProperty("location", location);
      userEntity.setProperty("profile-picture-url", profilePictureUrl);
      userEntity.setProperty("bio", bio);

      // Store the User entity in Datastore.
      datastore.put(userEntity);
      response.sendRedirect("/account-creation-finish.html?key=" + keyString); 
    }
  }

  // Returns a URL that points to the uploaded file, or null if the user didn't upload a file.
  private String getUploadedFileUrl(HttpServletRequest request, String formInputElementName) {
    BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
    Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(request);
    List<BlobKey> blobKeys = blobs.get(formInputElementName);

    // User submitted form without selecting a file, so we can't get a URL. (dev server)
    if (blobKeys == null || blobKeys.isEmpty()) {
      return null;
    }

    // Our form only contains a single file input, so get the first index.
    BlobKey blobKey = blobKeys.get(0);

    // User submitted form without selecting a file, so we can't get a URL. (live server)
    BlobInfo blobInfo = new BlobInfoFactory().loadBlobInfo(blobKey);
    if (blobInfo.getSize() == 0) {
      blobstoreService.delete(blobKey);
      return null;
    }

    // Use ImagesService to get a URL that points to the uploaded file.
    ImagesService imagesService = ImagesServiceFactory.getImagesService();
    ServingUrlOptions options = ServingUrlOptions.Builder.withBlobKey(blobKey);

    // To support running in Google Cloud Shell with AppEngine's devserver, we must use the relative
    // path to the image, rather than the path returned by imagesService which contains a host.
    try {
      URL url = new URL(imagesService.getServingUrl(options));
      return url.getPath();
    } catch (MalformedURLException e) {
      return imagesService.getServingUrl(options);
    }
  }
}
