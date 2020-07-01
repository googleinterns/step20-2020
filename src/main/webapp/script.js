class ParameterField extends HTMLElement {
  constructor() {
    super();

    const shadow = this.attachShadow({mode: 'open'});
    const label = document.createElement('label');
    const textArea = document.createElement('textarea');
    const button = document.createElement('button');

    label.className = 'parameter-label';
    textArea.className = 'parameter-textarea';
    button.className = 'parameter-button';

    shadow.appendChild(label);
    shadow.appendChild(textArea);
    shadow.appendChild(button);
  }

  connectedCallback() {
    var name = this.getAttribute('name');
    var index = this.getAttribute('index');
    var paramName = name.toLowerCase() + index;

    var label = this.shadowRoot.querySelector('.parameter-label');
    label.innerText = name + " " + index;
    label.for = paramName;

    var textArea = this.shadowRoot.querySelector('.parameter-textarea');
    textArea.id = paramName;
    textArea.name = paramName;
    textArea.rows = "1";

    var button = this.shadowRoot.querySelector('.parameter-button');
    button.onclick = "addParameterField(" + name + ", " + (index + 1) + ")";
    button.innerText = "Add " + name;
  }
}
customElements.define('parameter-field', ParameterField);

function getOriginalRecipe() {
  const key = document.getElementById("key").value;
  if (key !== "") {
    fetch("/new-recipe?key=" + key).then(response => response.json()).then((recipe) => {
      console.log(recipe);
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
  var componentNum = 1;
  for (var i = 0; i < data.length; i++) {
    var component = document.getElementById(componentName + componentNum++);
    console.log(data[i]);
    component.value = data[i];
  }
}