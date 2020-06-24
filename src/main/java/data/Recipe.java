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

public class Recipe {

  protected String name;
  protected String description;
  protected List<Step> steps;
  protected List<SpinOff> spinOffs;

  public Recipe(Recipe original) {
    this.name = original.name;
    this.description = original.description;
    this.steps = original.steps;
    this.spinOffs = original.spinOffs;
  }
  
  public Recipe(String name, String description, List<Step> steps) {
    this.name = name;
    this.description = description;
    this.steps = steps;
    this.spinOffs = new LinkedList<>();
  }

  public void appendStep(Step newStep) {
    steps.add(newStep);
  }

  public void addStep(int position, Step newStep) {
    if (position < 0) {
      System.err.println("Position " + position + " out of bounds, failed to add step [" + newStep + "]");
      return;
    } else if (position >= steps.size()) {
      System.err.println("Position " + position + " out of bounds, appending step [" + newStep + "]");
      appendStep(newStep);
    } else {
      steps.add(position, newStep);
    }
  }

  public void setStep(int position, Step newStep) {
    if (!isValidStepPosition(position)) {
      return;
    }
    steps.set(position, newStep);
  }

  public void removeStep(int position) {
    if (!isValidStepPosition(position)) {
      return;
    }
    steps.remove(position);
  }

  public List<Step> getSteps() {
    return steps;
  }

  protected void addSpinOff(SpinOff spinOff) {
    spinOffs.add(spinOff);
  }

  protected boolean isValidStepPosition(int position) {
    if (position < 0 || position > steps.size() - 1) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    String str = "\nName: ";
    str += name;
    str += "\nDescription: ";
    str += description;
    str += "\nSteps:\n";
    for (Step step : steps) {
      str += "\t";
      str += step.getInstruction();
      str += "\n";
    }
    return str;
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
      sameSteps = aStep.getInstruction().equals(bStep.getInstruction());
    }
    return sameSteps && a.name.equals(b.name) && a.description.equals(b.description);
  }
}
