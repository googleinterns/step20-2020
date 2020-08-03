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
import com.google.appengine.api.datastore.Query.CompositeFilter;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.PreparedQuery;
import java.util.List;

/** 
 * Returns recipes that are currently popular.
 * The recipes returned are the same for all users.
 */
public class Trending implements RecipeFilter {

  private DatastoreService datastore;
  private static final int MIN_LIKES = 10;

  public Trending() {
    datastore = DatastoreServiceFactory.getDatastoreService();
  }

  /** Returns a query with the most popular recipes. */
  public PreparedQuery getResults(Query query) {
    query.setFilter(new FilterPredicate("likes", FilterOperator.GREATER_THAN_OR_EQUAL, MIN_LIKES));
    return datastore.prepare(query);
  }

  /** Retrieves additional data from Datastore to be used in the filter. */
  public List<String> getUserData() {
    throw new UnsupportedOperationException();
  }  
}
