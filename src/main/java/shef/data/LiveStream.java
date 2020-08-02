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

import java.util.Date;
 
/** A YouTube live stream.*/
public class LiveStream {
  String userId;
  String recipeKey;
  String link;
  Date startTime;
  Date endTime;
  long duration;

 /**
  * @param userId Unique id of the associated user (poster).
  * @param recipeKey Unique key of the associated recipe.
  * @param link Link to the live stream.
  */
  public LiveStream(String userId, String recipeKey, String link) {
    this.userId = userId;
    this.recipeKey = recipeKey;
    this.link = link;
  }

  public void setStartTime(Date startTime) {
    this.startTime = startTime;
  }

  public void setEndTime(Date endTime) {
    this.endTime = endTime;
  }

  public void setDuration(long duration) {
    this.duration = duration;
  }
}   
