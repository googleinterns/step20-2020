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
 
/** A YouTube live stream. */
public class LiveStream {
  String userKey;
  String recipeKey;
  String liveStreamKey;
  String link;
  String startTime;
  String endTime;
  String duration;

 /**
  * @param userKey Unique key of the user creating the live stream.
  * @param recipeKey Unique key of the associated recipe.
  * @param liveStreamKey Unique key of the live stream.
  * @param link Link to the live stream.
  */
  public LiveStream(String userKey, String recipeKey, String liveStreamKey, String recipeKey, String link, String startTime, String endTime, String duration) {
    this.userKey = userKey;
    this.recipeKey = recipeKey;
    this.liveStreamKey = liveStreamKey;
    this.link = link;
    this.startTime = startTime;
    this.endTime = endTime;
    this.duration = duration;
  }
}   
