class ParameterInput extends HTMLElement {

  constructor() {
    super();
    this.label = document.createElement('label');
    this.textArea = document.createElement('textarea');
    this.button = document.createElement('button');
    this.container = document.createElement('div');

    this.container.appendChild(this.label);
    this.container.appendChild(this.textArea);
    this.container.appendChild(this.button);
  }

  connectedCallback() {
    this.name = this.getAttribute('name');
    this.index = parseInt(this.getAttribute('index'));
    this.parent = this.name + 's';

    this.textArea.rows = '1';
    this.button.type = 'button';
    this.button.innerText = 'Add ' + this.name;
    this.setIndexAttributes();

    this.appendChild(this.container);
  }

  setIndexAttributes() {
    var paramName = this.name.toLowerCase() + this.index;
    this.id = this.name + this.index;

    this.label.innerText = this.name + ' ' + (this.index + 1);
    this.label.for = paramName;

    this.textArea.name = paramName;

    this.button.onclick = event => {
      console.log('new parameter will have index ' + (this.index+1));
      var newParameter = createParameterInput(this.name, this.index + 1);
      insertParameterInput(this, newParameter);
    }
  }

  get text() {
    return this.textArea.value;
  }

  get position() {
    return this.index;
  }

  get field() {
    return this.parent;
  }

  set text(value) {
    this.textArea.value = value;
  }

  set position(value)  {
    this.index = parseInt(value);
  }

  set field(value) {
    this.parent = value;
  }
}
customElements.define('parameter-input', ParameterInput);

function createParameterInput(name, index) {
  var newParameter = document.createElement('parameter-input');
  newParameter.setAttribute('name', name);
  newParameter.setAttribute('index', index);
  newParameter.setAttribute('id', name + index);
  console.log(newParameter.position);
  return newParameter;
}

function insertParameterInput(previous, parameterInput) {
  previous.insertAdjacentElement('afterend', parameterInput);
  updateIndeces(parameterInput.field, parameterInput.position + 1);
}

function appendParameterInput(fieldName, parameterInput) {
  const field = document.getElementById(fieldName);
  field.appendChild(parameterInput);
}

function updateIndeces(fieldName, startIndex) {
  var parameters = document.getElementById(fieldName).children;
  for (var i = startIndex; i < parameters.length; i++) {
    parameters[i].position = i;
    parameters[i].setIndexAttributes();
  }
  for (var i = 0; i < parameters.length; i++) {
    console.log('parameter label: ' + parameters[i].label.innerText);
    console.log('parameter textarea name: ' + parameters[i].textArea.name);
    console.log('parameter id: ' + parameters[i].id);
  }
}

function getOriginalRecipe() {
  const key = document.getElementById('key').value;
  if (key !== '') {
    fetch('/new-recipe?key=' + key).then(response => response.json()).then((recipe) => {
      populateRecipeCreationForm(recipe);
    });
  }
}

function populateRecipeCreationForm(recipe) {
  var name = document.getElementById('name');
  name.value = recipe.name;

  var description = document.getElementById('description');
  description.value = recipe.description;

  populateFormField('Tag', recipe.tags);
  populateFormField('Ingredient', recipe.ingredients);
  populateFormField('Step', recipe.steps);
}

function populateFormField(fieldName, data) {
  console.log(data);
  for (var i = 0; i < data.length; i++) {
    var parameter = document.getElementById(fieldName + i);
    if (parameter !== null) {
      parameter.text = data[i];
    } else {
      const newParameter = createParameterInput(fieldName, i);
      newParameter.text = data[i];
      appendParameterInput(fieldName + 's', newParameter);
    }
  }
}