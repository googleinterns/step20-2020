import java.util.LinkedList;
import java.util.List;

public class Recipe {

  protected String name, description;
  protected List<Step> steps;
  private List<SpinOff> spinOffs;

  public Recipe() {}
  
  public Recipe(String recipeName, String recipeDescription, List<Step> recipeSteps) {
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

  protected void addSpinOff(SpinOff spinOff) {
    spinOffs.add(spinOff);
  }

  public List<Step> getSteps() {
    return steps;
  }

  @Override
  public String toString() {
    String printed = "\nName: ";
    printed += name;
    printed += "\nDescription: ";
    printed += description;
    printed += "\nSteps:\n";
    for (Step step : steps) {
      printed += "\t";
      printed += step.getDirection();
      printed += "\n";
    }
    return printed;
  }

  @Override
  public boolean equals(Object other) {
    return other instanceof Recipe && equals(this, (Recipe) other);
  }

  private static boolean equals(Recipe a, Recipe b) {
    if (a.steps.size() != b.steps.size()) {
      return false;
    }
    boolean sameSteps = true;
    for (int i = 0; i < a.steps.size(); i++) {
      Step aStep = a.steps.get(i);
      Step bStep = b.steps.get(i);
      sameSteps = aStep.getDirection().equals(bStep.getDirection());
    }
    return a.name.equals(b.name) && a.description.equals(b.description) && sameSteps;
  }
}