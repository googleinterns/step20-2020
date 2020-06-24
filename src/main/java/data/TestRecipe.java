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

package com.google.sps.data;
 
/** A user's comment, with corresponding user info. */
public class TestRecipe {
  long id;
  String name;
  String ingred1;
  String ingred2;
  String tag1;
  String tag2;
  long timestamp;

 /**
  * @param id The entity's id.
  */
  public TestRecipe(long id, String name, String ingred1, String ingred2, String tag1, String tag2, long timestamp) {
      this.id = id;
      this.name = name;
      this.ingred1 = ingred1;
      this.ingred2 = ingred2;
      this.tag1 = tag1;
      this.tag2 = tag2;
      this.timestamp = timestamp;
    }
}   