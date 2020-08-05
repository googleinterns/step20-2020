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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.logging.*;
import java.util.Iterator;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EmbeddedEntity;

/** Stores a recipe's data. */
public class Recipe {

  private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
  
  private Entity entity;
  private String key;
  private String name;
  private String description;
  private double time;
  private double servings;
  private String imageKey;
  private Set<String> tags = new LinkedHashSet<>();
  private Set<Ingredient> ingredients = new LinkedHashSet<>();
  private Set<String> equipment = new LinkedHashSet<>();
  private List<String> steps = new LinkedList<>();
  private Set<String> searchStrings = new HashSet<>();
  private Set<SpinOff> spinOffs;
  private long timestamp;

  /** 
   * Creates a Recipe from a Datastore entity.
   * Servlets can cleanly create JSON-ready Recipes by passing a recipe entity into this constructor.
   */
  public Recipe(Entity recipeEntity) {
    this.entity = recipeEntity;
    this.name = (String) recipeEntity.getProperty("name");
    this.description = (String) recipeEntity.getProperty("description");
    this.time = (double) recipeEntity.getProperty("time");
    this.servings = (double) recipeEntity.getProperty("servings");
    this.imageKey = (String) recipeEntity.getProperty("imageKey");
    this.tags = new LinkedHashSet((ArrayList<String>) recipeEntity.getProperty("tags"));
    this.ingredients = getIngredientsFromEntity((Collection<EmbeddedEntity>) recipeEntity.getProperty("ingredients"));
    this.equipment = new LinkedHashSet((ArrayList<String>) recipeEntity.getProperty("equipment"));
    this.steps = new LinkedList((ArrayList<String>) recipeEntity.getProperty("steps"));
    this.timestamp = (long) recipeEntity.getProperty("timestamp");
  }

  /**
   * Creates a recipe from a map of parameters. 
   * This constructor allows servlets to cleanly create recipes by passing in an HTTPRequest's paramater map.
   */
  public Recipe(Map<String, String[]> parameterMap) {
    // First, store the parameter data in a Recipe object.
    for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
      handleParameter(entry.getKey(), entry.getValue());
    }
    // Then build a recipe entity that is ready to be saved in Datastore.
    this.entity = buildEntity();
  }

  /** Constructor called when creating a recipe to display on the recipe feed. */
  public Recipe(String key, String name, String description, Set<String> tags, Set<Ingredient> ingredients, List<String> steps, long timestamp) {
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

  /** Copy constructor called to create a spin-offs. */
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
  public Recipe(String name, String description, Set<String> tags, Set<Ingredient> ingredients, List<String> steps, long timestamp) {
    this.name = name;
    this.description = description;
    this.tags = tags;
    this.ingredients = ingredients;
    this.steps = steps;
    this.spinOffs = new HashSet<>();
    this.timestamp = timestamp;
  }

  /** 
   * A helper method that sets the Recipe's fields.
   * It allows iteration over the parameter map by specifically setting each field with different behavior.
   */
  private void handleParameter(String name, String[] values) {
    // No values associated with this parameter.
    if (values == null || values.length < 1 || values[0] == null || values[0].equals("")) {
      return;
    } 

    // Handle each parameter with a different case block for each field's specific behavior.
    switch(name) {
      case "name":
        this.name = values[0];
        addToSearchStrings(this.name);
        break;
      case "description":
        this.description = values[0];
        addToSearchStrings(this.description);
        break;
      case "imageKey":
        this.imageKey = values[0];
        break;
      case "time":
        this.time = Double.parseDouble(values[0]);
        break;
      case "servings":
        this.servings = Double.parseDouble(values[0]);
        break;
      default:
        // Handle, tags, ingredients, equipment, and steps by adding them to the Recipe's lists.
        if (name.contains("tag")) {
          addTag(values[0]);
          addToSearchStrings(values[0]);
        } else if (name.contains("ingredient")) {
          double amount = Double.parseDouble(values[0]);
          addIngredient(new Ingredient(amount, values[1], values[2]));
          addToSearchStrings(values[2]);
        } else if (name.contains("equipment")) {
          addEquipment(values[0]);
        } else if (name.contains("step")) {
          appendStep(values[0]);
        }
    }
  }

  /**
   * Builds a recipe entity from a Recipe object.
   * Automatically called by the Recipe constructor that builds a Recipe from a parameter map.
   */
  public Entity buildEntity() {
    Entity recipeEntity = new Entity("Recipe");
    recipeEntity.setProperty("name", name);
    recipeEntity.setProperty("time", time);
    recipeEntity.setProperty("servings", servings);
    recipeEntity.setProperty("imageKey", imageKey);
    recipeEntity.setProperty("description", description);
    recipeEntity.setProperty("tags", tags);
    recipeEntity.setProperty("ingredients", embeddedIngredients());
    recipeEntity.setProperty("equipment", equipment);
    recipeEntity.setProperty("steps", steps);
    recipeEntity.setProperty("search-strings", getSearchStrings());
    recipeEntity.setProperty("timestamp", System.currentTimeMillis());
    return recipeEntity;
  }

  /** Gets the recipe's corresponding Datastore entity. */
  public Entity getEntity() {
    return entity;
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
  public Set<Ingredient> getIngredients() {
    return ingredients;
  }

  /** Gets the recipe's spin-offs. */
  public Set<SpinOff> getSpinOffs() {
    return spinOffs;
  }

  /**
   * Returns a recipe's search strings.
   * Return type is an ArrayList because Filters and CompositeFilters require lists.
   */
  public ArrayList<String> getSearchStrings() {
    return new ArrayList<String>(searchStrings);
  }

  /** Adds a tag to the recipe. */
  public void addTag(String tag) {
    tags.add(tag);
  }

  /** Adds an ingredient to the recipe. */
  public void addIngredient(Ingredient ingredient) {
    ingredients.add(ingredient);
  }

  /** Adds equipment to the recipe. */
  public void addEquipment(String newEquipment) {
    equipment.add(newEquipment);
  }


  /** Adds a spin-off to the recipe. */
  public void addSpinOff(SpinOff spinOff) {
    spinOffs.add(spinOff);
  }

  /** 
   * Adds strings to a recipe's search strings.
   * Each token in the input string is upper-cased and added as a separate search string.
   */
  public void addToSearchStrings(String string) {
    String[] tokens = string.split(" ");
    for (String token : tokens) {
      searchStrings.add(token.toUpperCase());
    }
  }

  /** Removes a tag from the recipe. */
  public void removeTag(String tag) {
    tags.remove(tag);
  }

  /** Removes an ingredient from the recipe. */
  public void removeIngredient(Ingredient ingredient) {
    ingredients.remove(ingredient);
  }

  /** Removes a spin-off from the recipe. */
  public void removeSpinOff(SpinOff spinOff) {
    spinOffs.remove(spinOff);
  }

  /** Appends a new step to a recipe's list of steps. */
  public void appendStep(String newStep) {
    steps.add(newStep);
  }

  /**
   * Adds a step to a specified position in a recipe's list of steps.
   * @param position The position at which to insert. 
   * @param newStep The new step to insert.
   * @throws IndexOutOfBoundsException Thrown if position is out of bounds of the recipe's list of steps.
   */
  public void addStep(int position, String newStep) throws IndexOutOfBoundsException {
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
  public void setStep(int position, String newStep) throws IndexOutOfBoundsException {
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
  public List<String> getSteps() {
    return steps;
  }

  /** Returns the recipe as a string. */
  @Override
  public String toString() {
    String str = String.format("\nName: %s", name);
    str += String.format("\nDescription: %s", description);
    str += "\nSteps:\n";
    for (String step : steps) {
      str += String.format("\t%s\n", step);
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

  /** Returns the ingredients of an EmbeddedEntity as a Set. */
  private Set<Ingredient> getIngredientsFromEntity(Collection<EmbeddedEntity> entityIngredients) {
    Set<Ingredient> ingredientsSet = new HashSet<>();
    for (EmbeddedEntity ingredient : entityIngredients) {
      ingredientsSet.add(new Ingredient(ingredient));
    }
    return ingredientsSet;
  }

  /** Converts a set of Ingredients into a Datastore-ready Collection of EmbeddedEntities. */
  private Collection<EmbeddedEntity> embeddedIngredients() {
    Collection<EmbeddedEntity> embeddedIngredients = new LinkedHashSet<>();
    for (Ingredient ingredient : ingredients) {
      EmbeddedEntity ingredientEntity = new EmbeddedEntity();
      ingredientEntity.setProperty("amount", ingredient.amount());
      ingredientEntity.setProperty("unit", ingredient.unit());
      ingredientEntity.setProperty("name", ingredient.name());
      embeddedIngredients.add(ingredientEntity);
    }
    return embeddedIngredients;
  }

  private void handleStepException(String exceptionText) throws IndexOutOfBoundsException {
    LOGGER.log(Level.INFO, exceptionText);
    throw new IndexOutOfBoundsException(exceptionText);
  }

  private static boolean equals(Recipe a, Recipe b) {
    if (a.steps.size() != b.steps.size() || !a.name.equals(b.name) || !a.description.equals(b.description)) {
      return false;
    }
    Iterator<String> aSteps = a.steps.iterator();
    Iterator<String> bSteps = b.steps.iterator();
    while (aSteps.hasNext() && bSteps.hasNext()) {
      String aStep = aSteps.next();
      String bStep = bSteps.next();
      if (!aStep.equals(bStep)) {
        return false;
      }
    }
    return true;
  }
}
