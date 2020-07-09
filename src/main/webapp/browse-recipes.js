
function getRecipes(algorithm) {
  const results = document.getElementById('results');
  results.innerHTML = '';
  fetch('/get-browsing-recipes?algorithm=' + algorithm).then(response => response.json()).then((recipes) => {
    for (var i = 0; i < recipes.length; i++) {
      results.appendChild(createRecipeForBrowsing(recipes[i]));
      results.appendChild(document.createElement('br'));
    }
  });
}

function createRecipeForBrowsing(recipe) {
  const container = document.createElement('div');
  container.id = 'recipe';
  container.style.border = 'thick solid #000';
  container.style.width = '30%';
  container.style.backgroundColor = '#ccc'

  const name = document.createElement('h2');
  name.innerText = recipe.name;

  const description = document.createElement('p');
  description.innerText = recipe.description;

  container.appendChild(name);
  container.appendChild(description);
  return container;
}