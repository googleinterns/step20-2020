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
 
/** A YouTube live stream.
    TODO: Add field for associated user. */
public class LiveStream {
  String liveStreamKey;
  String recipeKey;
  String link;
  Date startTime;
  Date endTime;
  long duration;

 /**
  * @param liveStreamKey Unique key of the livestream.
  * @param recipeKey Unique key of the associated recipe.
  * @param link Link to the live stream.
  */
  public LiveStream(String liveStreamKey, String recipeKey, String link, String startTime, String endTime) {
    this.liveStreamKey = liveStreamKey;
    this.recipeKey = recipeKey;
    this.link = link;
    this.startTime = strToDate(startTime);
    this.endTime = strToDate(endTime);
    this.duration = durationFromDates(this.startTime, this.endTime);
  }

  public Date strToDate(String dateStr) {
  }

  public long durationFromDates(Date startTime, Date endTime) {
  }
}   
