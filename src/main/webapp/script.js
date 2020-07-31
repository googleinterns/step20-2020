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

/** Creates a new groupchat and redirects the client to the chatting page. */
function newGroupchat() {
  var request = new Request("/load-groupchat", {method: 'POST'});
  fetch(request).then(response => response.text()).then((key) => {
    redirectToGroupchat(key);
  });
}

/** Loads an existing groupchat. */
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

/** Posts a message to a groupchat. */
function postNextMessage() {
  const message = document.getElementById('message-input').value;
  const groupKey = document.getElementById('groupchat-key').value;
  var request = new Request('/new-message?message=' + message + '&groupchat-key=' + groupKey, {method: 'POST'});
  fetch(request).then(
    document.getElementById('message-input').value = ''
  );
}

/**
 * Sends a request to the server to get the next message.
 * This method is recursive (upon success) so that each client is always waiting for the next message.
 * Requests use asynchronous Promises, so the client can perform other functions while waiting for a message.
 */
function getNextMessage() {
  var request = new Request("/new-message", {method: 'GET'})
  fetch(request)
    .then(response => {
      if (response.ok) {
        return response.text();
      } else {
        throw new Error('Servlet closed');
      }
    }).then(message => {
      addMessage(message);
      getNextMessage();
    })
    .catch(err => {
      alert(err);
      window.location.href = 'index.html';
    });
}

/** Given a groupchat key, redirects the client to that groupchat. */
function redirectToGroupchat(keyParameter) {
  const key = keyParameter ? keyParameter : document.getElementById('groupchat-key').value;
  window.location.href = "/groupchat.html?key=" + key;
}

/** Adds a <p> element containing a message to a groupchat. */
function addMessage(message) {
  var messagesContainer = document.getElementById('messages');
  const messageElement = document.createElement('p');
  messageElement.innerText = message;
  messagesContainer.appendChild(messageElement);
}
