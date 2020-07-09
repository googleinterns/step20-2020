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

// Set the link for the sign-in page.
function getSignInLink() {
  fetch('/sign-in').then(response => response.json()).then(info => {
    const linkEl = document.getElementById('link');
    if(info.status) {
      linkEl.innerHTML = '<a href=\"' + info.url + '\">Sign out</a>.';
    }
    else {
      linkEl.innerHTML = '<a href=\"' + info.url + '\">Sign in</a>.';
    }
  });
}

// Set the sign-up link for the sign-up page.
function getSignUpLink() {
  fetch('/sign-up').then(response => response.text()).then(link => {
    const linkEl = document.getElementById('sign-up-link');
    linkEl.innerHTML = '<a href=\"' + link + '\">Sign up</a>.';
  });
}

// Get a specific user's data to populate their profile page.
function getUserData() {
  var url = window.location.href;
  var key = url.split('?')[1];

  fetch('/user?' + key).then(response => response.json()).then(userInfo => {
    document.getElementById('info').innerHTML = 'Email: ' + userInfo.email + '<br> Name: ' + userInfo.username + '<br> Location: ' + userInfo.location + '<br> Bio: ' + userInfo.bio;
    document.getElementById('profile-pic').src = userInfo.profilePicUrl;

    document.getElementById('username-input').innerHTML = userInfo.username;
    document.getElementById('location-input').innerHTML = userInfo.location;
    document.getElementById('bio-input').innerHTML = userInfo.bio;

    const buttonEl = document.getElementById('edit-button');
    if(!userInfo.isCurrentUser) {
      buttonEl.classList.add('hidden');
    }
  });
}

// Sets the image upload URL in the account creation and profile pages.
function fetchBlobstoreUrl() {
  fetch('/profile-pic-upload-url').then(response => response.text()).then(imageUploadUrl => {
    const signupForm = document.getElementById('user-form');
    signupForm.action = imageUploadUrl;
  });
}

// Enables or disables the editable form in the profile page.
function toggleProfileEditMode() {
  const staticEl = document.getElementById('static');
  const editableEl = document.getElementById('editable');
  const buttonEl = document.getElementById('edit-button');

  if(staticEl.classList.contains('hidden')) {
    staticEl.classList.remove('hidden');
    editableEl.classList.add('hidden');
    buttonEl.innerHTML = 'Edit Profile';
  }
  else {
    staticEl.classList.add('hidden');
    editableEl.classList.remove('hidden');
    buttonEl.innerHTML = 'Back';
  }
}

// Creates the list of users on the user-list-test page.
function getUserProfileLinks() {
  fetch('/user-list').then(response => response.json()).then(infoList => {
    const userList = document.getElementById('user-list');
    infoList.forEach(item => {
      userList.appendChild(createListElement(item));
    });
  });
}

// Creates an <li> element containing a link to a user's profile page.
function createListElement(item) {
  const liElement = document.createElement('li');
  liElement.innerHTML = '<a href=\"' + item.profilePageUrl + '\">' + item.username + '</a>';
  return liElement;
}

// Show fail message if appropriate.
function signInFailMessage() {
  var url = window.location.href;
  var status = url.split('=')[1];

  if(status === 'fail') {
    const messageEl = document.getElementById('fail-message');
    messageEl.classList.remove('hidden');
  }
}
