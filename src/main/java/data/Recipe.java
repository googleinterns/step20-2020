import java.util.LinkedList;

public class Recipe {

  protected String name, description;
  protected LinkedList<Step> steps;
  
  public Recipe(String recipeName, String recipeDescription, LinkedList<Step> recipeSteps) {
    this.name = recipeName;
    this.description = recipeDescription;
    this.steps = recipeSteps;
  }

}