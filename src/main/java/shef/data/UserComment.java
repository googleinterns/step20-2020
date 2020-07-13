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
  long id;
  String comment;
  long timestamp;

 /**
  * @param id The entity's id.
  * @param comment The user's comment.
  * @param timestamp The time at which the comment was submitted.
  */
  public UserComment(long id, String comment, long timestamp) {
      this.id = id;
      this.comment = comment;
      this.timestamp = timestamp;
  }
}   