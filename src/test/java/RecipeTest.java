// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import java.util.LinkedList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;
import java.util.Arrays;
import java.util.HashSet;
import com.google.sps.data.Recipe;
import com.google.sps.data.Step;

@RunWith(JUnit4.class)
public final class RecipeTest {

  private static final String NAME = "Grilled Cheese";
  private static final String DESCRIPTION = "It's literally just melted cheese on bread";
  private static final Set<String> TAGS = new HashSet<>(Arrays.asList("grilledcheese", "quick"));
  private static final Set<String> INGREDIENTS = new HashSet<>(Arrays.asList("bread", "cheese", "butter"));
  private static final List<Step> STEPS = Arrays.asList(
      new Step("Toast the bread for 2 minutes"),
      new Step("Melt the cheese"),
      new Step("Put the cheese in the bread")
  );
  private static final Recipe spinOff = new Recipe("Spicy grilled cheese", "it's hot", TAGS, INGREDIENTS, STEPS);
  private static final SpinOff SPINOFF = new SpinOff(spinOff);
  private Recipe recipe;

  @Before
  public void setup() {
    recipe = new Recipe(NAME, DESCRIPTION, new HashSet(TAGS), new HashSet(INGREDIENTS), new LinkedList(STEPS));
  }

  @Test
  public void appendStep() {
    List<Step> expectedSteps = new LinkedList<>(STEPS);
    expectedSteps.add(new Step("butter the bread"));
    Recipe expected = new Recipe(NAME, DESCRIPTION, TAGS, INGREDIENTS, expectedSteps);
    recipe.appendStep(new Step("butter the bread"));
    Assert.assertEquals(expected, recipe);
  }

  @Test
  public void addBeginningStep() {
    List<Step> expectedSteps = Arrays.asList(
        new Step("Index 0!"),
        new Step("Toast the bread for 2 minutes"),
        new Step("Melt the cheese"),
        new Step("Put the cheese in the bread")
    );

    Recipe expected = new Recipe(NAME, DESCRIPTION, TAGS, INGREDIENTS, expectedSteps);
    recipe.addStep(0, new Step("Index 0!"));
    Assert.assertEquals(expected, recipe);
  }

  @Test
  public void addIntermediateStep() {
    List<Step> expectedSteps = Arrays.asList(
        new Step("Toast the bread for 2 minutes"),
        new Step("Turn on the burner"),
        new Step("Melt the cheese"),
        new Step("Put the cheese in the bread")
    );

    Recipe expected = new Recipe(NAME, DESCRIPTION, TAGS, INGREDIENTS, expectedSteps);
    recipe.addStep(1, new Step("Turn on the burner"));
    Assert.assertEquals(expected, recipe);
  }

  @Test
  public void addEndStep() {
    List<Step> expectedSteps = Arrays.asList(
        new Step("Toast the bread for 2 minutes"),
        new Step("Melt the cheese"),
        new Step("Put the cheese in the bread"),
        new Step("Index 3!")
    );

    Recipe expected = new Recipe(NAME, DESCRIPTION, TAGS, INGREDIENTS, expectedSteps);
    recipe.addStep(3, new Step("Index 3!"));
    Assert.assertEquals(expected, recipe);
  }

  @Test
  public void setBeginningStep() {
    List<Step> expectedSteps = Arrays.asList(
        new Step("New first step"),
        new Step("Melt the cheese"),
        new Step("Put the cheese in the bread")
    );

    Recipe expected = new Recipe(NAME, DESCRIPTION, TAGS, INGREDIENTS, expectedSteps);
    recipe.setStep(0, new Step("New first step"));
    Assert.assertEquals(expected, recipe);
  }

  @Test
  public void setIntermediateStep() {
    List<Step> expectedSteps = Arrays.asList(
        new Step("Toast the bread for 2 minutes"),
        new Step("New middle step"),
        new Step("Put the cheese in the bread")
    );

    Recipe expected = new Recipe(NAME, DESCRIPTION, TAGS, INGREDIENTS, expectedSteps);
    recipe.setStep(1, new Step("New middle step"));
    Assert.assertEquals(expected, recipe);
  }

  @Test
  public void setEndStep() {
    List<Step> expectedSteps = Arrays.asList(
        new Step("Toast the bread for 2 minutes"),
        new Step("Melt the cheese"),
        new Step("New last step")
    );

    Recipe expected = new Recipe(NAME, DESCRIPTION, TAGS, INGREDIENTS, expectedSteps);
    recipe.setStep(2, new Step("New last step"));
    Assert.assertEquals(expected, recipe);
  }

  @Test
  public void removeBeginningStep() {
    List<Step> expectedSteps = Arrays.asList(
        new Step("Melt the cheese"),
        new Step("Put the cheese in the bread")
    );

    Recipe expected = new Recipe(NAME, DESCRIPTION, TAGS, INGREDIENTS, expectedSteps);
    recipe.removeStep(0);
    Assert.assertEquals(expected, recipe);
  }

  @Test
  public void removeIntermediateStep() {
    List<Step> expectedSteps = Arrays.asList(
        new Step("Toast the bread for 2 minutes"),
        new Step("Put the cheese in the bread")
    );

    Recipe expected = new Recipe(NAME, DESCRIPTION, TAGS, INGREDIENTS, expectedSteps);
    recipe.removeStep(1);
    Assert.assertEquals(expected, recipe);
  }

  @Test
  public void removeEndStep() {
    List<Step> expectedSteps = Arrays.asList(
        new Step("Toast the bread for 2 minutes"),
        new Step("Melt the cheese")
    );

    Recipe expected = new Recipe(NAME, DESCRIPTION, TAGS, INGREDIENTS, expectedSteps);
    recipe.removeStep(2);
    Assert.assertEquals(expected, recipe);
  }

  @Test(expected = IndexOutOfBoundsException.class)
  public void addStepFailureTooHigh() {
    recipe.addStep(99, new Step("never added"));
  }

  @Test(expected = IndexOutOfBoundsException.class)
  public void addStepFailureTooLow() {
    recipe.addStep(-1, new Step("never added"));
  }

  @Test(expected = IndexOutOfBoundsException.class)
  public void setStepFailureTooHigh() {
    recipe.setStep(5, new Step("never set"));
  }

  @Test(expected = IndexOutOfBoundsException.class)
  public void setStepFailureTooLow() {
    recipe.setStep(-1, new Step("never set"));
  }

  @Test(expected = IndexOutOfBoundsException.class)
  public void removeStepFailureTooHigh() {
    recipe.removeStep(99);
  }

  @Test(expected = IndexOutOfBoundsException.class)
  public void removeStepFailureTooLow() {
    recipe.removeStep(-10);
  }

  @Test
  public void addTag() {
    Set<String> expected = new HashSet<>(Arrays.asList("grilledcheese", "quick", "cheese"));
    recipe.addTag("cheese");
    Assert.assertEquals(expected, recipe.getTags());
  }

  @Test
  public void removeTag() {
    Set<String> expected = new HashSet<>(Arrays.asList("grilledcheese"));
    recipe.removeTag("quick");
    Assert.assertEquals(expected, recipe.getTags());
  }

  @Test
  public void removeNonexistantTag() {
    Set<String> expected = new HashSet<>(Arrays.asList("grilledcheese", "quick"));
    recipe.removeTag("chicken");
    Assert.assertEquals(expected, recipe.getTags());
  }

  @Test
  public void addIngredient() {
    Set<String> expected = new HashSet<>(Arrays.asList("bread", "cheese", "butter", "oil"));
    recipe.addIngredient("oil");
    Assert.assertEquals(expected, recipe.getIngredients());
  }

  @Test
  public void removeIngredient() {
    Set<String> expected = new HashSet<>(Arrays.asList("cheese", "butter"));
    recipe.removeIngredient("bread");
    Assert.assertEquals(expected, recipe.getIngredients());
  }

  @Test
  public void removeNonexistantIngredient() {
    Set<String> expected = new HashSet<>(Arrays.asList("bread", "cheese", "butter"));
    recipe.removeIngredient("chicken");
    Assert.assertEquals(expected, recipe.getIngredients());
  }

  @Test
  public void addSpinOff() {
    Set<SpinOff> expected = new HashSet<>(Arrays.asList(SPINOFF));
    recipe.addSpinOff(SPINOFF);
    Assert.assertEquals(expected, recipe.getSpinOffs());
  }

  @Test
  public void removeSpinOff() {
    Set<SpinOff> expected = new HashSet<SpinOff>();
    recipe.removeSpinOff(SPINOFF);
    Assert.assertEquals(expected, recipe.getSpinOffs());
  }

  @Test 
  public void toStringTest() {
    String expected = "\nName: NAME";
    expected += "\nDescription: DESC";
    expected += "\nSteps:\n";
    expected += "\tA step\n";

    Recipe testRecipe = new Recipe("NAME", "DESC", TAGS, INGREDIENTS, Arrays.asList(new Step("A step")));
    Assert.assertEquals(expected, testRecipe.toString());
  }
}
