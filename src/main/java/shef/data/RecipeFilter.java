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

import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.CompositeFilter;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.PreparedQuery;

public interface RecipeFilter {

  /** Returns a PreparedQuery of filtered Recipe entities. */
  public PreparedQuery getResults(Query query);

  /** Helper method that adds a filter to the composite filter. */
  public Filter addFilter(CompositeFilter filters);

  /** Retrieves additional data from Datastore to be used in the filter. */
  public PreparedQuery getData(Query query);
  
}
