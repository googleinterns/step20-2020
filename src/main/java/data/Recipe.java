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

import java.util.LinkedList;
import java.util.List;
import java.util.logging.*;
import java.util.Iterator;

/**
 * Stores a recipe's data.
 */
public class Recipe {

  private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

  protected String name;
  protected String description;
  protected List<Step> steps;
  protected List<SpinOff> spinOffs;

  /**
   * Copy constructor called when creating spin-offs.
   */
  public Recipe(Recipe recipe) {
    this.name = recipe.name;
    this.description = recipe.description;
    this.steps = recipe.steps;
    this.spinOffs = recipe.spinOffs;
  }
  
  /**
   * Default constructor called when creating a new recipe.
   */
  public Recipe(String name, String description, List<Step> steps) {
    this.name = name;
    this.description = description;
    this.steps = steps;
    this.spinOffs = new LinkedList<>();
  }

  /**
   * Appends a new step to a recipe's list of steps.
   */
  public void appendStep(Step newStep) {
    steps.add(newStep);
  }

  /**
   * Adds a step to a specified position in a recipe's list of steps.
   * @param position The position at which to insert. 
   * @param newStep The new step to insert.
   * @throws IndexOutOfBoundsException Thrown if position is out of bounds of the recipe's list of steps.
   */
  public void addStep(int position, Step newStep) throws IndexOutOfBoundsException {
    if (position == steps.size()) {
      appendStep(newStep);
    } else if (!isValidStepPosition(position)) {
      handleStepException("Position " + position + " is out of bounds [0, " + (steps.size() - 1) + "]. " +
          "Failed to add step \"" + newStep + "\".");
    } else {
      steps.add(position, newStep);
    }
  }

  /**
   * Sets the step at the specified position in a recipe's list of steps.
   * @param position The position of the step to set. 
   * @param newStep The replacing step.
   * @throws IndexOutOfBoundsException Thrown if position is out of bounds of the recipe's list of steps.
   */
  public void setStep(int position, Step newStep) throws IndexOutOfBoundsException {
    if (!isValidStepPosition(position)) {
      handleStepException("Position " + position + " is out of bounds [0, " + (steps.size() - 1) + "]. " +
          "Failed to set step \"" + newStep + "\".");
      return;
    }
    steps.set(position, newStep);
  }

  /**
   * Removes the step at the specified position in a recipe's list of steps.
   * @param position The position of the step to remove. 
   * @throws IndexOutOfBoundsException if position is out of bounds of the recipe's list of steps.
   */
  public void removeStep(int position) throws IndexOutOfBoundsException {
    if (!isValidStepPosition(position)) {
      handleStepException("Position " + position + " is out of bounds [0, " + (steps.size() - 1) + "]. " +
          "Failed to remove step.");
      return;
    }
    steps.remove(position);
  }

  /** Returns the recipe's list of steps. */
  public List<Step> getSteps() {
    return steps;
  }

  /** Returns the recipe as a string. */
  @Override
  public String toString() {
    String str = String.format("\nName: %s", name);
    str += String.format("\nDescription: %s", description);
    str += "\nSteps:\n";
    for (Step step : steps) {
      str += String.format("\t%s\n", step.getInstruction());
    }
    return str;
  }

  /** Checks if this recipe is equal to another object. */
  @Override
  public boolean equals(Object other) {
    return other instanceof Recipe && equals(this, (Recipe) other);
  }

  /** Adds a spin-off to the recipe's list of spin-offs. */
  protected void addSpinOff(SpinOff spinOff) {
    spinOffs.add(spinOff);
  }

  /**
   * Checks if a position is valid within the recipe's list of steps.
   * @param position The position to check.
   * @return True if the position is valid, false otherwise.
   */
  protected boolean isValidStepPosition(int position) {
    return position >= 0 && position < steps.size();
  }

  private void handleStepException(String exceptionText) throws IndexOutOfBoundsException {
    LOGGER.log(Level.INFO, exceptionText);
    throw new IndexOutOfBoundsException(exceptionText);
  }

  private static boolean equals(Recipe a, Recipe b) {
    if (a.steps.size() != b.steps.size() || !a.name.equals(b.name) || !a.description.equals(b.description)) {
      return false;
    }
    Iterator<Step> aSteps = a.steps.iterator();
    Iterator<Step> bSteps = b.steps.iterator();
    while (aSteps.hasNext() && bSteps.hasNext()) {
      Step aStep = aSteps.next();
      Step bStep = bSteps.next();
      if (!aStep.getInstruction().equals(bStep.getInstruction())) {
        return false;
      }
    }
    return true;
  }
}
