class ParameterInput extends HTMLElement {

  constructor() {
    super();
  }

  connectedCallback() {
    const name = this.getAttribute('name');
    const index = parseInt(this.getAttribute('index'));
    var paramName = name.toLowerCase() + index;

    const field = document.getElementById(name + 's');
    const container = document.createElement('div');

    var label = document.createElement('label');;
    label.innerText = name + " " + (index + 1);
    label.for = paramName;

    var textArea = document.createElement('textarea');
    textArea.id = paramName;
    textArea.name = paramName;
    textArea.rows = "1";

    var button = document.createElement('button');
    button.type = "button";
    button.onclick = event => addParameterInput(name, index + 1);
    button.innerText = "Add " + name;

    container.appendChild(label);
    container.appendChild(textArea);
    container.appendChild(button);
    field.appendChild(container);
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