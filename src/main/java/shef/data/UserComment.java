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
 
/** A user's comment, with corresponding user info. */
public class UserComment {
  String recipeKeyString;
  String userKeyString;
  String username;
  String location;
  String comment;
  String MMDDYYYY;

 /**
  * @param recipeKeyString The recipe's key, as a string.
  * @param userKeyString The user's key, as a string.
  * @param username The user's username.
  * @param location The user's location.
  * @param comment The user's comment.
  * @param MMDDYYYY The time in MM/DD/YYYY at which the comment was submitted.
  */
  public UserComment(String recipeKeyString, String userKeyString, String username, String location, String comment, String MMDDYYYY) {
    this.recipeKeyString = recipeKeyString;
    this.userKeyString = userKeyString;
    this.username = username;
    this.location = location;
    this.comment = comment;
    this.MMDDYYYY = MMDDYYYY;
  }
}   
