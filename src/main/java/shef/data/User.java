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

    // String representation of the id from Datastore.
    private String id;
    private String email;
    private String username;
    private String location;
    private String profilePicUrl;
    private String bio;
    private String profilePageUrl;
    private boolean isCurrentUser;

    public User(String id, String email, String username, String location, String profilePicUrl, String bio, boolean isCurrentUser) {
      this.id = id;
      this.email = email;
      this.username = username;
      this.location = location;
      this.profilePicUrl = profilePicUrl;
      this.bio = bio;
      this.isCurrentUser = isCurrentUser;
    }

    public User(String username, String profilePageUrl) {
      this.username = username;
      this.profilePageUrl = profilePageUrl;
    }
}
