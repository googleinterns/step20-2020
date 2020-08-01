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

import java.util.ArrayList;

/** Class representing a user. */
public class User {

    // Websafe unique identifier of the User.
    private String key;
    private String email;
    private String username;
    private String location;
    private String profilePicKey;
    private String bio;
    private String profilePageUrl;
    private boolean isCurrentUser;
    private boolean isFollowedByCurrentUser;
    private long followingCount;
    private long followersCount;


    public User(String key, String email, String username, String location, String profilePicKey, String bio, boolean isCurrentUser, boolean isFollowedByCurrentUser, long followingCount, long followersCount) {
      this.key = key;
      this.email = email;
      this.username = username;
      this.location = location;
      this.profilePicKey = profilePicKey;
      this.bio = bio;
      this.isCurrentUser = isCurrentUser;
      this.isFollowedByCurrentUser = isFollowedByCurrentUser;
      this.followingCount = followingCount;
      this.followersCount = followersCount;
    }

    public User(String key, String username, String profilePicKey) {
      this.key = key;
      this.username = username;
      this.profilePicKey = profilePicKey;
    }
}
