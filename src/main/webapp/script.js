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

// Set the sign-in link for the sign-in page.
function getSignInLink() {
  fetch('/sign-in').then(response => response.json()).then(info => {
    const linkEl = document.getElementById('sign-in-button');
    linkEl.href = info.url;
  });
}

// Show sign-in fail message if appropriate.
function signInFailMessage() {
  var url = window.location.href;
  var status = url.split('=')[1];

  if(status === 'fail') {
    document.getElementById('fail-message').classList.remove('d-none');
    document.getElementById('normal-title').classList.add('d-none');
    document.getElementById('normal-message').classList.add('d-none');
  }
}

// Set the sign-up link for the sign-up page.
function getSignUpLink() {
  fetch('/sign-up').then(response => response.text()).then(link => {
    const linkEl = document.getElementById('sign-up-button');
    linkEl.href = link;
  });
}

// Get a specific user's data to populate their profile page.
function getProfilePageData() {
  var url = window.location.href;
  var key = url.split('?')[1];

  fetch('/user?' + key).then(response => response.json()).then(userInfo => {
    document.getElementById('profile-pic-display').src = '/blob?blob-key=' +  userInfo.profilePicKey;
    document.getElementById('username-display').innerHTML = userInfo.username;
    document.getElementById('location-display').innerHTML = userInfo.location;
    document.getElementById('bio-display').innerHTML = userInfo.bio;

    document.getElementById('username').innerHTML = userInfo.username;
    document.getElementById('location').innerHTML = userInfo.location;
    document.getElementById('bio').innerHTML = userInfo.bio;

    if(!userInfo.isCurrentUser) {
      document.getElementById('edit-button').classList.add('d-none');
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
  const staticEl = document.getElementById('static-user-info');
  const editableEl = document.getElementById('user-form');
  const buttonEl = document.getElementById('edit-button');

  if(staticEl.classList.contains('d-none')) {
    staticEl.classList.remove('d-none');
    editableEl.classList.add('d-none');
    buttonEl.innerHTML = 'Edit Profile';
  }
  else {
    staticEl.classList.add('d-none');
    editableEl.classList.remove('d-none');
    buttonEl.innerHTML = 'Back';
  }
}

/** Fetches comments from the server and adds them to the DOM. */
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
  // If there is no associated user (i.e. anon commenter), the userProfile is not hyperlinked,
  // because it does not exist.
  let userProfile = "Anonymous";
  if (comment.username !== "Anonymous" || comment.location !== "Unknown") {
    // Username hyperlinks to the profile of the user who left the comment.
    userProfile = hyperlinkText(comment.username, "/profile-page.html?key=" + comment.userKeyString);
  }
  var userInfoDisplayed = userProfile + " • " + comment.location + " • " + comment.MMDDYYYY;
  userComment.innerHTML += addParagraph(userInfoDisplayed) + addParagraph(comment.comment);

  commentElement.appendChild(userComment);
  return commentElement;
}

function hyperlinkText(text, link) {
  return "<a href=" + link + ">" + text + "</a>";
}

/** Fetches recipes from the server and adds them to the DOM. */
function loadRecipes() {
  // rowVars used to dynamically name divs of class row, for up to 3 recipes.
  var rowVars = {};
  let recipeCount = 0;
  let rowCount = 0;
  fetch('/display-recipes').then(response => response.json()).then((recipes) => {
    const recipeGrid = document.getElementById('recipe-grid');
    recipes.forEach((recipe) => {
      // Every three live streams, create a new row.
      if (recipeCount % 3 == 0) {
        rowCount++;
        rowVars['recipeRow' + rowCount] = document.createElement('div');
        rowVars['recipeRow' + rowCount].className = "row";
      }
      rowVars['recipeRow' + rowCount].appendChild(createFeedElement(recipe));
      recipeGrid.appendChild(rowVars['recipeRow' + rowCount]);
      recipeCount++;
    })
  });
}
 
/** Creates an element that represents a feed item,
    for example a Recipe or Live Stream. */
function createFeedElement(item) {
  const feedItem = document.createElement('div');
  feedItem.className = 'col feed-img-container';
  // Using a constant image because, as is, recipes doesn't support photos.
  feedItem.innerHTML += "<img src=" + "https://tinyurl.com/y8eph3n6" + ">";
  // On click, redirect to corresponding recipe.
  feedItem.onclick = function() {
    window.location="/recipe.html?key=" + item.key;
  }
 
  const overlay = document.createElement('div');
  overlay.className = "overlay";
 
  const unorderedList = document.createElement('ul');
  unorderedList.className = "list-unstyled";
 
  const listElement = document.createElement('li');
  listElement.className = "list-space";
  listElement.innerText= item.name;
  
  unorderedList.appendChild(listElement);
  overlay.appendChild(unorderedList);
  feedItem.appendChild(overlay);
  return feedItem;
}

function recipePageInit() {
  getRecipeInfo();
  loadComments();
}

function getRecipeInfo() {
  var url = window.location.href;
  var key = url.split('?')[1];

  fetch('/new-recipe?' + key).then(response => response.json()).then(recipe => {
    document.getElementById('recipe-title').innerHTML = recipe.name;
    document.getElementById('recipe-description').innerHTML = recipe.description;
    displayTags(recipe.tags);
    displayIngredients(recipe.ingredients);
    displaySteps(recipe.steps);
  });
}

/** Formats and displays tags on the page. */
function displayTags(tagsList) {
  var tagSection = document.getElementById('recipe-tags');
  let tagCount = 0;
  tagsList.forEach((tag) => {
    tagCount++;
    tagSection.innerHTML += "#" + tag
    if (tagCount < tagsList.length) {
      tagSection.innerHTML += ", ";
    }
  });
}

/** Formats and displays ingredients, with corresponding checkboxes, on the page. */
function displayIngredients(ingredList) {
  var ingredElements = {};  // ingredElements used to dynamically name divs of class form-check small-sep, for a single ingredient.
  let ingredCount = 0;
  var ingredSection = document.getElementById('recipe-ingredients');
  ingredList.forEach((ingredient) => {
    ingredElements['ingredElement' + ingredCount] = document.createElement('div');
    ingredElements['ingredElement' + ingredCount].className = "form-check small-sep";

    // Create a checkbox.
    var input = document.createElement("input");
    input.type = "checkbox";
    input.class = "form-check-input";
    // Label the checkbox with the individual ingredient.
    var label = document.createElement("label");
    label.label = "form-check-label";
    label.innerHTML = "<p>" + ingredient + "</p>";

    ingredElements['ingredElement' + ingredCount].appendChild(input);
    ingredElements['ingredElement' + ingredCount].appendChild(label);
    ingredSection.appendChild(ingredElements['ingredElement' + ingredCount]);
    ingredCount++;
  });
}

/** Formats and displays steps on the page. */
function displaySteps(stepList) {
  var rowVars = {};  // rowVars used to dynamically name divs of class row, for a single step.
  let stepCount = 1;
  var stepSection = document.getElementById('recipe-steps');
  stepList.forEach((step) => {
    rowVars['stepElement' + stepCount] = document.createElement('div');
    rowVars['stepElement' + stepCount].className = "row";

    // Create and format the step number.
    var stepNumElement = document.createElement("div");
    stepNumElement.class = "col-sm-2 col-md-2 col-lg-2";
    var stepNum = document.createElement("h3");
    stepNum.innerText += stepCount + ". ";
    stepNumElement.appendChild(stepNum);
    // Create and format the step text.
    var stepTextElement = document.createElement("p");
    stepTextElement.class = "col-sm-10 col-md-10 col-lg-10";
    stepTextElement.innerHTML = step;

    rowVars['stepElement' + stepCount].appendChild(stepNumElement);
    rowVars['stepElement' + stepCount].appendChild(stepTextElement);
    stepSection.appendChild(rowVars['stepElement' + stepCount]);
    stepCount++;
  });
}

// Sets up the navbar for any page.
function navBarSetup() {
  fetch('/sign-in').then(response => response.json()).then(info => {
    if(info.status) {
      document.getElementById('navbar-dropdown').classList.remove('d-none');
      document.getElementById('sign-in-button').classList.add('d-none');
      document.getElementById('sign-out-link').href = info.url;
      getProfilePicture();
    }
  });
}

// Gets the profile picture for the navbar
function getProfilePicture() {
  fetch('/user').then(response => response.json()).then(userInfo => {
    document.getElementById('profile-pic-nav').src = '/blob?blob-key=' +  userInfo.profilePicKey;
  });
}

function addParagraph(content) {
  return "<p>" + content + "</p>";
}

function shareViaGmail() {
  let msgbody = "Yum!";
  let url = 'https://mail.google.com/mail/?view=cm&fs=1&tf=1&to=&su=Check+out+this+recipe!&body='+msgbody+'&ui=2&tf=1&pli=1';
  window.open(url, 'sharer', 'toolbar=0,status=0,width=648,height=395');
}

/** @class A custom element that represents a parameter input. */
class ParameterInput extends HTMLElement {
  constructor() {
    super();
    this.label = document.createElement('label');
    this.textArea = document.createElement('textarea');
    this.addButton = document.createElement('button');
    this.deleteButton = document.createElement('button');
    this.container = document.createElement('div');

    this.container.appendChild(this.label);
    this.container.appendChild(this.textArea);
    this.container.appendChild(this.addButton);
    this.container.appendChild(this.deleteButton);
  }

  /** Once the attributes for the ParameterInput exist, set its values accordingly. */
  connectedCallback() {
    this.name = this.getAttribute('name');
    this.index = parseInt(this.getAttribute('index'));
    this.parent = this.name + 's';

    this.textArea.rows = '1';
    this.addButton.type = 'button';
    this.addButton.innerText = 'Add ' + this.name;
    this.deleteButton.type = 'button';
    this.deleteButton.innerText = 'Delete ' + this.name;
    this.setIndexAttributes();

    this.appendChild(this.container);
  }

  /**
   * Sets all attributes that depend on the ParameterInput's index.
   * These attributes are separated into their own method so that they can be updated
   * when ParameterInputs change position.
   */
  setIndexAttributes() {
    var paramName = this.name.toLowerCase() + this.index;
    this.id = this.name + this.index;
    this.label.innerText = this.name + ' ' + (this.index + 1);
    this.label.for = paramName;
    this.textArea.name = paramName;

    // Inserts a new ParameterInput below the one clicked.
    this.addButton.onclick = event => {
      var newParameter = createParameterInput(this.name, this.index + 1);
      insertParameterInput(this, newParameter);
    }

    // Deletes the ParameterInput clicked.
    this.deleteButton.onclick = event => {
      const fieldName = this.field;
      const startIndex = this.index;
      this.remove();
      updateIndices(fieldName, startIndex);
    }
  }

  /** Gets the text in a ParameterInput's text area. */
  get text() {
    return this.textArea.value;
  }

  /** Gets the ParameterInput's index. */
  get position() {
    return this.index;
  }

  /** Gets the parent field of the ParamterInput (Tags, Ingredients, or Steps). */
  get field() {
    return this.parent;
  }

  /** Sets the text in a ParameterInput's text area. */
  set text(value) {
    this.textArea.value = value;
  }

  /** Sets the ParameterInput's index. */
  set position(value)  {
    this.index = parseInt(value);
  }

  /** Sets the parent field of the ParamterInput (Tags, Ingredients, or Steps). */
  set field(value) {
    this.parent = value;
  }
}
customElements.define('parameter-input', ParameterInput);

/** 
 * Creates a ParameterInput.
 * @param {string} name Used for displaying and determining the parent field.
 * @param {string} index The position of the ParameterInput.
 */
function createParameterInput(name, index) {
  var newParameter = document.createElement('parameter-input');
  newParameter.setAttribute('name', name);
  newParameter.setAttribute('index', index);
  newParameter.setAttribute('id', name + index);
  return newParameter;
}

/** 
 * Inserts a ParameterInput.
 * @param {ParameterInput} previous The ParameterInput to insert after.
 * @param {ParameterInput} parameterInput The ParameterInput to insert.
 */
function insertParameterInput(previous, parameterInput) {
  previous.insertAdjacentElement('afterend', parameterInput);
  updateIndices(parameterInput.field, parameterInput.position + 1);
}

/** 
 * Appends an existing ParameterInput.
 * @param {string} fieldName The field to append to.
 * @param {ParameterInput} parameterInput The ParameterInput to append.
 */
function appendParameterInput(fieldName, parameterInput) {
  const field = document.getElementById(fieldName);
  field.appendChild(parameterInput);
}

/** 
 * Appends a new ParameterInput.
 * @param {string} fieldName The field to append to.
 */
function appendNewParameterInput(fieldName) {
  const field = document.getElementById(fieldName);
  field.appendChild(createParameterInput(fieldName.slice(0, -1), field.children.length));
}

/**
 * Updates the indices of ParameterInputs after an insertion or deletion.
 * @param {string} fieldName The field with the ParameterInputs to update.
 * @param {number} startIndex The index of the first ParameterInput to update.
 */
function updateIndices(fieldName, startIndex) {
  var parameters = document.getElementById(fieldName).children;
  for (var i = startIndex; i < parameters.length; i++) {
    parameters[i].position = i;
    parameters[i].setIndexAttributes();
  }
}

/** Gets a parent recipe's data from Datastore. */
function getOriginalRecipe() {
  const key = document.getElementById('key').value;
  if (key) {
    fetch('/new-recipe?key=' + key).then(response => response.json()).then((recipe) => {
      populateRecipeCreationForm(recipe);
    });
  }
}

/** Populates the fields of the recipe editor with a parent recipe's data. */
function populateRecipeCreationForm(recipe) {
  document.getElementById('name').value = recipe.name;
  document.getElementById('description').value = recipe.description;
  populateFormField('Tag', recipe.tags);
  populateFormField('Ingredient', recipe.ingredients);
  populateFormField('Step', recipe.steps);
}

function populateFormField(fieldName, data) {
  for (var i = 0; i < data.length; i++) {
    var parameter = document.getElementById(fieldName + i);
    if (parameter !== null) {
      parameter.text = data[i];
    } else {
      var newParameter = createParameterInput(fieldName, i);
      newParameter.text = data[i];
      appendParameterInput(fieldName + 's', newParameter);
    }
  }
}
