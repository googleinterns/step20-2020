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

  public void appendStep(Step newStep) {
    steps.add(newStep);
  }

  public void addStep(int position, Step newStep) {
    if (position < 0) {
      return;
    } else if (position >= steps.size()) {
      appendStep(newStep);
    }
    steps.add(position, newStep);
  }

  public void setStep(int position, Step newStep) {
    if (position < 0 || position > steps.size() - 1) {
      return;
    }
    steps.set(position, newStep);
  }

  public void removeStep(int position) {
    if (position < 0 || position > steps.size() - 1) {
      return;
    }
    steps.remove(position);
  }

  private void addSpinOff(SpinOff spinOff) {
    spinOffs.add(spinOff);
  }

}