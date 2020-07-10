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

public class ForYou implements RecipeFilter {

  private DatastoreService datastore;
  private static final List<String> TEMP_PREFERENCES = new ArrayList<>(Arrays.asList("SPICY", "CHICKEN", "CHOCOLATE"));
  private Set<Filter> filters;

  public ForYou(HttpServletRequest request) {
    datastore = DatastoreServiceFactory.getDatastoreService();
    filters = new HashSet<>();
  }

  public PreparedQuery getResults(Query query) {
    filters.add(new FilterPredicate("search-strings", FilterOperator.IN, TEMP_PREFERENCES));
    filters.add(new FilterPredicate("likes", FilterOperator.GREATER_THAN_OR_EQUAL, 50));
    query.setFilter(new CompositeFilter(CompositeFilterOperator.OR, filters));
    return datastore.prepare(query);
  }

  public Filter addFilter(Filter filters) {
    return null;
  }

  public PreparedQuery getData(Query query) {
    return null;
  }
}
