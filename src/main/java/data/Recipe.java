import java.util.LinkedList;

public class Recipe {

  protected String name, description;
  protected LinkedList<Step> steps;
  private LinkedList<Step> spinOffs;
  
  public Recipe(String recipeName, String recipeDescription, LinkedList<Step> recipeSteps) {
    this.name = recipeName;
    this.description = recipeDescription;
    this.steps = recipeSteps;
    this.spinOffs = new LinkedList<>();
  }

  public void addSpinOff(SpinOff spinOff) {
    spinOffs.add(spinOff);
  }

}