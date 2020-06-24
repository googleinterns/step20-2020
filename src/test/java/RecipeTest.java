import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.Arrays;

/** */
@RunWith(JUnit4.class)
public final class RecipeTest {

  private static final String NAME = "Grilled Cheese";
  private static final String DESCRIPTION = "It's literally just melted cheese on bread";
  private static final List<Step> STEPS = Arrays.asList(
      new Step("Toast the bread for 2 minutes"),
      new Step("Melt the cheese"),
      new Step("Put the cheese in the bread")
  );
  private Recipe recipe;

  @Before
  public void setup() {
    recipe = new Recipe(NAME, DESCRIPTION, new LinkedList(STEPS));
  }

  @Test
  public void appendStep() {
    List<Step> expectedSteps = new LinkedList<>(STEPS);
    expectedSteps.add(new Step("butter the bread"));

    Recipe expected = new Recipe(NAME, DESCRIPTION, expectedSteps);
    recipe.appendStep(new Step("butter the bread"));

    Assert.assertEquals(expected, recipe);
  }

}