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
    var paramName = this.name.toLowerCase() + this.index;

    this.label.innerText = this.name + " " + (this.index + 1);
    this.label.for = paramName;

    this.textArea.id = paramName;
    this.textArea.name = paramName;
    this.textArea.rows = "1";

    this.button.type = "button";
    this.button.onclick = event => addParameterInput(this.name, this.index + 1);
    this.button.innerText = "Add " + this.name;

    this.field = document.getElementById(this.name + 's');
    this.field.appendChild(this.container);
  }
}
customElements.define('parameter-input', ParameterInput);

function addParameterInput(name, index) {
  const container = document.getElementById(name + 's');
  var newField = document.createElement('parameter-input');
  newField.setAttribute('name', name);
  newField.setAttribute('index', index);
  container.appendChild(newField);
}

function getOriginalRecipe() {
  const key = document.getElementById("key").value;
  if (key !== "") {
    fetch("/new-recipe?key=" + key).then(response => response.json()).then((recipe) => {
      populateRecipeCreationForm(recipe);
    });
  }
}

function populateRecipeCreationForm(recipe) {
  var name = document.getElementById("name");
  name.value = recipe.name;

  var description = document.getElementById("description");
  description.value = recipe.name;

  populateFormComponent("tag", recipe.tags);
  populateFormComponent("ingredient", recipe.ingredients);
  populateFormComponent("step", recipe.steps);
}

function populateFormComponent(componentName, data) {
  console.log(data);
  var componentNum = 1;
  for (var i = 0; i < data.length; i++) {
    var component = document.getElementById(componentName + componentNum++);
    component.value = data[i];
  }
}