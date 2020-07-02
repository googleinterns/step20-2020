class ParameterField extends HTMLElement {

  static get observedAttributes() {
    return ['index'];
  }

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

  attributeChangedCallback(name, newValue, oldValue) {
    var name = this.getAttribute('name');
    var index = this.getAttribute('index');
    var paramName = name.toLowerCase() + index;

    var label = this.shadowRoot.querySelector('.parameter-label');
    label.innerText = name + " " + (parseInt(index) + 1);
    label.for = paramName;

    var textArea = this.shadowRoot.querySelector('.parameter-textarea');
    textArea.id = paramName;
    textArea.name = paramName;
    console.log(textArea.name);
    textArea.rows = "1";

    var button = this.shadowRoot.querySelector('.parameter-button');
    button.id = name;
    button.onclick = event => addParameterField(event);
    button.innerText = "Add " + name;
  }
}
customElements.define('parameter-field', ParameterField);

function addParameterField(event, name, index) {
  const container = document.getElementById(event.target.id + 's');
  var newField = document.createElement('parameter-field');
  newField.setAttribute('name', event.target.id);
  newField.setAttribute('index', container.childNodes.length - 2);
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
  var componentNum = 1;
  for (var i = 0; i < data.length; i++) {
    var component = document.getElementById(componentName + componentNum++);
    component.value = data[i];
  }
}