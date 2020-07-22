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
import javax.servlet.http.HttpServletRequest;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.CompositeFilter;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.PreparedQuery;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;

/** Defines the For You algorithm, which shows users recipes unique to their preferences. */
public class ForYou implements RecipeFilter {

  private DatastoreService datastore;
  private static final List<String> TEMP_PREFERENCES = new ArrayList<>(Arrays.asList("SPICY", "CHICKEN", "CHOCOLATE"));
  private Set<Filter> filters;

  public ForYou() {
    datastore = DatastoreServiceFactory.getDatastoreService();
    filters = new HashSet<>();
  }

  /** 
   * Returns recipes that match the responses to the user's preference quiz. 
   * For now, returns recipes that match SPICY, CHICKEN, and CHOCOLATE, or that have more than 50 likes.
   * This is just a hard-coded example, and will change. */
  public PreparedQuery getResults(Query query) {
    filters.add(new FilterPredicate("search-strings", FilterOperator.IN, TEMP_PREFERENCES));
    filters.add(new FilterPredicate("likes", FilterOperator.GREATER_THAN_OR_EQUAL, 50));
    query.setFilter(new CompositeFilter(CompositeFilterOperator.OR, filters));
    return datastore.prepare(query);
  }

  /** Helper method that adds a filter to the composite filter. */
  public Filter addFilter(CompositeFilter filters) {
    throw new UnsupportedOperationException();
  }

  /** Retrieves additional data from Datastore to be used in the filter. */
  public PreparedQuery getData(Query query) {
    throw new UnsupportedOperationException();
  }  
}
