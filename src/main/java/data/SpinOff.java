
public class SpinOff extends Recipe {

  private Recipe parent;

  public SpinOff(Recipe original) {
    this.name = original.name;
    this.description = original.description;
    this.steps = original.steps;
    this.parent = original;
  }

}