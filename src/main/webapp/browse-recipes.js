
function getRecipes(algorithm) {
  fetch('/browse-recipes?algorithm=' + algorithm).then(response => response.json()).then(recipes => {
    console.log(recipes);
  });
}