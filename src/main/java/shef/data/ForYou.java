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

package shef.data;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import java.util.ArrayList;
import java.util.List;

/** Defines the For You algorithm, which shows users recipes unique to their preferences. */
public class ForYou implements RecipeFilter {

  private DatastoreService datastore;
  private UserService userService;
  private List<String> preferences;

  public ForYou() {
    datastore = DatastoreServiceFactory.getDatastoreService();
    userService = UserServiceFactory.getUserService();
  }

  /** 
   * Returns recipes that match the responses to the user's preference quiz. 
   * For now, returns recipes that match SPICY, CHICKEN, and CHOCOLATE, or that have more than 50 likes.
   * This is just a hard-coded example, and will change. */
  public PreparedQuery getResults(Query query) {
    preferences = getUserData();
    if (preferences == null) {
      // If the user cannot be found, return null.
      return null;
    }
    query.setFilter(new FilterPredicate("search-strings", FilterOperator.IN, preferences));
    return datastore.prepare(query);
  }

  /** Retrieves additional data from Datastore to be used in the filter. */
  public List<String> getUserData() {
    Entity user;
    try {
      user = datastore.get(KeyFactory.createKey("User", userService.getCurrentUser().getUserId()));
    } catch(Exception e) {
      // User could not be found. In this case, return null, which will cause the servlet to return trending recipes.
      e.printStackTrace();
      return null;
    }
    List<String> userPreferences = (List<String>) user.getProperty("preferences");
    return userPreferences;
  }  
}
