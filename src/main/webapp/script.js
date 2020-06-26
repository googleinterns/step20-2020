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