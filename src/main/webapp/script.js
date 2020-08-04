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
  const request = new Request("/load-groupchat", {method: 'POST'});
  fetch(request).then(response => response.text()).then((key) => {
    redirectToGroupchat(key);
  });
}

function loadGroupchat() {
  const urlParams = new URLSearchParams(window.location.search);
  const key = urlParams.get('key');
  fetch('/load-groupchat?key=' + key).then(response => response.json()).then((messages) => {
    const messageContainer = document.getElementById('messages');
    messageContainer.innerHTML = '';
    for (var i = 0; i < messages.length; i++) {
      const message = document.createElement('p');
      message.innerText = messages[i];
      messageContainer.appendChild(message);
    }
  }).catch(error =>
    alert('Error: Groupchat does not exist')
  );
}

function redirectToGroupchat(keyParameter) {
  const key = keyParameter ? keyParameter : document.getElementById('groupchat-key').value;
  window.location.href = "/groupchat.html?key=" + key;
}
