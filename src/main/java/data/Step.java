public class Step {

  private String direction;

  public Step(String stepDirection) {
    this.direction = stepDirection;
  }

  public String getDirection() {
    return direction;
  }

  @Override
  public String toString() {
    return direction;
  }

}