
function getRecipes(algorithm) {
  fetch('/get-browsing-recipes?algorithm=' + algorithm).then(response => response.json()).then((recipes) => {
    console.log(recipes);
  });
}