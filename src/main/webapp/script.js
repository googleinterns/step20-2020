// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the 'License");
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

/** Fetches tasks from the server and adds them to the DOM. */
function loadComments() {
  fetch('/display-comments').then(response => response.json()).then((comments) => {
    const commentListElement = document.getElementById('comment-list');
    comments.forEach((comment) => {
      commentListElement.appendChild(createCommentElement(comment));
    })
  });
}

/** Creates an element that represents a comment. */
function createCommentElement(comment) {
  const commentElement = document.createElement('div');
  commentElement.className = 'small-sep';

  const userComment = document.createElement('span');
  var userInfoDisplayed = comment.username + " • " + comment.location + " • " + comment.MMDDYYYY;
  userComment.innerHTML += addParagraph(userInfoDisplayed) + addParagraph(comment.comment);

  commentElement.appendChild(userComment);
  return commentElement;
}

function addParagraph(content) {
  return "<p>" + content + "</p>";
}

function newGroupchat() {
  var request = new Request("/load-groupchat", {method: 'POST'});
  fetch(request).then(response => response.text()).then((key) => {
    redirectToGroupchat(key);
  });
}

function loadGroupchat() {
  const urlParams = new URLSearchParams(window.location.search);
  const key = urlParams.get('key');
  fetch('/load-groupchat?key=' + key)
    .then(response => response.json(), error => {
      alert('Error: Groupchat does not exist ' + error);
      window.location.href = 'index.html';
    }).then((messages) => {
      document.getElementById('messages').innerHTML = '';
      document.getElementById('groupchat-key').value = key;
      for (var i = 0; i < messages.length; i++) {
        addMessage(messages[i]);
      }
      getNextMessage();
  });
}

function postNextMessage() {
  const message = document.getElementById('message-input').value;
  const groupKey = document.getElementById('groupchat-key').value;
  var request = new Request('/new-message?message=' + message + '&groupchat-key=' + groupKey, {method: 'POST'});
  fetch(request)
    .then(v => console.log('POST success'), v => console.log('POST failure'));
}

function getNextMessage() {
  console.log('getting a new message');
  var request = new Request("/new-message", {method: 'GET'})
  fetch(request)
    .then(response => {
      console.log("In response => response.text() block");
      return response.text();
    }, error => {
      console.log('SERVER ERROR: ' + error);
      //getNextMessage();
    })
    .then(message => {
      console.log('got message "' + message + '"');
      addMessage(message);
      getNextMessage();
    }, err => alert('text() error ' + err));
}

function redirectToGroupchat(keyParameter) {
  const key = keyParameter ? keyParameter : document.getElementById('groupchat-key').value;
  window.location.href = "/groupchat.html?key=" + key;
}

function addMessage(message) {
  var messagesContainer = document.getElementById('messages');
  const messageElement = document.createElement('p');
  messageElement.innerText = message;
  messagesContainer.appendChild(messageElement);
}
