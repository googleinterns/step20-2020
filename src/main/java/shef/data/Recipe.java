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

package shef.data;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.logging.*;
import java.util.Iterator;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EmbeddedEntity;

/** Stores a recipe's data. */
public class Recipe {

  private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
  
  private String key;
  private String name;
  private String description;
  private double time;
  private double servings;
  private String imageKey;
  private Set<String> tags;
  private Set<String> ingredients;
  private Set<String> equipment;
  private List<Step> steps;
  private Set<SpinOff> spinOffs;
  private long timestamp;

  /**
   * Copy constructor called when creating spin-offs.
   */
  public Recipe(Recipe recipe) {
    this.name = recipe.name;
    this.description = recipe.description;
    this.tags = new HashSet<>(recipe.tags);
    this.ingredients = new HashSet<>(recipe.ingredients);
    this.steps = new LinkedList<>(recipe.steps);
    this.spinOffs = new HashSet<>();
    this.timestamp = System.currentTimeMillis();
  }

  /** Default constructor called when creating a new recipe. */
  public Recipe(String name, String description, Set<String> tags, Set<String> ingredients, List<Step> steps, long timestamp) {
    this.name = name;
    this.description = description;
    this.tags = tags;
    this.ingredients = ingredients;
    this.steps = steps;
    this.spinOffs = new HashSet<>();
    this.timestamp = timestamp;
  }

  /** Creates a Recipe from a Datastore entity. */
  public Recipe(Entity recipeEntity) {
    this.name = (String) recipeEntity.getProperty("name");
    this.description = (String) recipeEntity.getProperty("description");
    this.time = (double) recipeEntity.getProperty("time");
    this.servings = (double) recipeEntity.getProperty("servings");
    this.imageKey = (String) recipeEntity.getProperty("imageKey");
    this.tags = getTagsFromEntity((Collection<EmbeddedEntity>) recipeEntity.getProperty("tags"));
    this.ingredients = getIngredientsFromEntity((Collection<EmbeddedEntity>) recipeEntity.getProperty("ingredients"));
    this.equipment = getEquipmentFromEntity((Collection<EmbeddedEntity>) recipeEntity.getProperty("equipment"));
    this.steps = getStepsFromEntity((Collection<EmbeddedEntity>) recipeEntity.getProperty("steps"));
    this.timestamp = (long) recipeEntity.getProperty("timestamp");
  }

  /** Constructor called when creating a recipe to display on the recipe feed. */
  public Recipe(String key, String name, String description, Set<String> tags, Set<String> ingredients, List<Step> steps, long timestamp) {
    this.key = key;
    this.name = name;
    this.tags = tags;
    this.ingredients = ingredients;
    this.description = description;
    this.tags = tags;
    this.ingredients = ingredients;
    this.steps = steps;
    this.timestamp = timestamp;
    this.spinOffs = new HashSet<>();
  }

  /** Gets the recipe's name. */
  public String getName() {
    return name;
  }

  /** Sets the recipe's name. */
  public void setName(String newName) {
    name = newName;
  }

  /** Gets the recipe's description. */
  public String getDescription() {
    return description;
  }

  /** Sets the recipe's description. */
  public void setDescription(String newDescription) {
    description = newDescription;
  }

  /** Gets the recipe's tags. */
  public Set<String> getTags() {
    return tags;
  }

  /** Gets the recipe's ingredients. */
  public Set<String> getIngredients() {
    return ingredients;
  }

  /** Gets the recipe's spin-offs. */
  public Set<SpinOff> getSpinOffs() {
    return spinOffs;
  }

  /** Adds a tag to the recipe. */
  public void addTag(String tag) {
    tags.add(tag);
  }

  /** Adds an ingredient to the recipe. */
  public void addIngredient(String ingredient) {
    ingredients.add(ingredient);
  }

  /** Adds a spin-off to the recipe. */
  public void addSpinOff(SpinOff spinOff) {
    spinOffs.add(spinOff);
  }

  /** Removes a tag from the recipe. */
  public void removeTag(String tag) {
    tags.remove(tag);
  }

  /** Removes an ingredient from the recipe. */
  public void removeIngredient(String ingredient) {
    ingredients.remove(ingredient);
  }

  /** Removes a spin-off from the recipe. */
  public void removeSpinOff(SpinOff spinOff) {
    spinOffs.remove(spinOff);
  }

  /** Appends a new step to a recipe's list of steps. */
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

  /**
   * Checks if a position is valid within the recipe's list of steps.
   * @param position The position to check.
   * @return True if the position is valid, false otherwise.
   */
  protected boolean isValidStepPosition(int position) {
    return position >= 0 && position < steps.size();
  }

  /** Returns the tags of an EmbeddedEntity as a Set. */
  private Set<String> getTagsFromEntity(Collection<EmbeddedEntity> entityTags) {
    Set<String> tagsSet = new HashSet<>();
    for (EmbeddedEntity tag : entityTags) {
      tagsSet.add((String) tag.getProperty("tag"));
    }
    return tagsSet;
  }

  /** Returns the ingredients of an EmbeddedEntity as a Set. */
  private Set<String> getIngredientsFromEntity(Collection<EmbeddedEntity> entityIngredients) {
    Set<String> ingredientsSet = new HashSet<>();
    for (EmbeddedEntity ingredient : entityIngredients) {
      ingredientsSet.add((String) ingredient.getProperty("ingredient"));
    }
    return ingredientsSet;
  }

  /** Returns the equipment of an EmbeddedEntity as a Set. */
  private Set<String> getEquipmentFromEntity(Collection<EmbeddedEntity> entityEquipment) {
    Set<String> equipmentSet = new HashSet<>();
    for (EmbeddedEntity equipment : entityEquipment) {
      equipmentSet.add((String) equipment.getProperty("equipment"));
    }
    return equipmentSet;
  }

  /** Returns the steps of an EmbeddedEntity as a List. */
  private List<Step> getStepsFromEntity(Collection<EmbeddedEntity> entitySteps) {
    List<Step> stepsList = new LinkedList<>();
    for (EmbeddedEntity step : entitySteps) {
      stepsList.add(new Step((String) step.getProperty("step")));
    }
    return stepsList;
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
