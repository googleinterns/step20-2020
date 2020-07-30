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

package shef.servlets;

import shef.data.User;
import java.io.IOException;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Arrays;
import java.util.Map;
import com.google.gson.Gson;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.EntityNotFoundException;

/** Servlet to set the user's preferences. */
@WebServlet("/preferences")
public class PreferenceServlet extends HttpServlet {

  /** This is a mapping of responses on the quiz to a list of preferences.
    * For example, if the user selected "vegetarian" on the quiz, we must
    * add all of the preferences defined here. (MEAT-FREE, VEGETARIAN, etc.)
    */
  public Map<String, List<String>> preferenceMap = new HashMap<String, List<String>>() {
	  {
      // Question 1: Which of these terms best describe you?
      put("student", Arrays.asList("CHEAP", "INEXPENSIVE", "EASY TO MAKE", "EASY", "SIMPLE", "QUICK", "QUICK TO MAKE"));
      put("influencer", Arrays.asList("DESSERT", "CAKE", "ICE CREAM"));
      put("parent", Arrays.asList("CHEAP", "INEXPENSIVE", "EASY TO MAKE", "EASY", "SIMPLE", "QUICK", "QUICK TO MAKE"));
      put("adventurous", Arrays.asList("AFRICAN, AMERICAN, ASIAN, CARRIBEAN", "EASTERN EUROPEAN", "WESTERN EUROPEAN", "LATIN", "LATIN AMERICAN", "MEDITERRANEAN", "MIDDLE EASTERN", "SOUTH ASIAN"));
      put("picky-eater", Arrays.asList("SIMPLE"));
      put("sweets-lover", Arrays.asList("SWEET", "CAKE", "COOKIES", "SUGARY", "DESSERT", "PIE", "ICE CREAM"));
      put("newbie", Arrays.asList("EASY TO MAKE", "EASY", "SIMPLE", "QUICK", "QUICK TO MAKE"));

      // Question 2: What dietary requirements do you have?
      put("vegetarian", Arrays.asList("MEAT-FREE", "MEAT FREE", "NO MEAT", "VEGETARIAN", "VEGAN", "FISH-FREE", "FISH FREE", "NO FISH"));
      put("pescetarian", Arrays.asList("FISH","PESCATARIAN", "PESCETARIAN", "TUNA", "SHRIMP", "SALMON", "SHELLFISH", "HALIBUT", "MEAT-FREE", "MEAT FREE", "NO MEAT"));
      put("vegan", Arrays.asList("MEAT-FREE", "MEAT FREE", "NO MEAT", "VEGAN", "FISH-FREE", "FISH FREE", "NO FISH", "DAIRY-FREE", "DAIRY FREE"));
      put("gluten-free", Arrays.asList("GLUTEN-FREE", "GLUTEN FREE", "NO GLUTEN"));
      put("kosher", Arrays.asList("KOSHER", "PORK-FREE", "PORK FREE", "NO PORK"));
      put("halal", Arrays.asList("HALAL", "ALCOHOL-FREE", "ALCOHOL FREE", "NO ALCOHOL", "PORK-FREE", "PORK FREE", "NO PORK"));
      put("no-dairy", Arrays.asList("DAIRY-FREE", "DAIRY FREE", "NO DAIRY"));
      put("no-shellfish", Arrays.asList("SHELLFISH-FREE", "SHELLFISH FREE", "NO SHELLFISH"));
      put("no-seafood", Arrays.asList("SEAFOOD-FREE", "SEAFOOD FREE", "NO SEAFOOD", "FISH-FREE", "FISH FREE", "NO FISH", "SHELLFISH-FREE", "SHELLFISH FREE", "NO SHELLFISH"));
      put("no-soy", Arrays.asList("SOY-FREE", "SOY FREE", "NO SOY"));
      put("no-eggs", Arrays.asList("EGG-FREE", "EGG FREE", "NO EGGS"));
      put("no-alcohol", Arrays.asList("ALCOHOL-FREE", "ALCOHOL FREE", "NO ALCOHOL"));

      // Question 3: What are your favorite types of cuisine?
      put("african", Arrays.asList("AFRICAN", "NIGERIAN", "ETHIOPIAN", "EGYPTIAN", "CONGOLESE", "TANZANIAN", "SOUTH AFRICAN", "KENYAN", "UGANDAN", "ALGERIAN", "SUDANESE"));
      put("american", Arrays.asList("AMERICAN"));
      put("asian", Arrays.asList("ASIAN", "CHINESE", "INDIAN", "INDONESIAN", "PAKISTANI", "BANGLADESHI", "JAPANESE", "PHILIPINO", "FILIPINO", "VIETNAMESE", "KOREAN", "THAI"));
      put("carribean", Arrays.asList("CARRIBEAN", "CUBAN", "HAITIAN", "DOMINICAN", "PUERTO RICAN", "JAMAICAN", "TRINIDADIAN", "COSTA RICAN"));
      put("eastern-european", Arrays.asList("EASTERN EUROPEAN", "RUSSIAN", "UKRANIAN", "BELARUSIAN", "SLAVIC", "POLISH", "ROMANIAN", "MOLDOVAN", "SERBIAN", "SLOVAKIAN", "CROATIAN"));
      put("western-european", Arrays.asList("WESTERN EUROPEAN", "GERMAN", "ENGLISH", "IRISH", "FRENCH", "SWISS", "BELGIAN", "DUTCH"));
      put("latin-american", Arrays.asList("LATIN", "LATIN AMERICAN", "MEXICAN", "BRAZILIAN", "COLOMBIAN", "ARGENTINIAN", "PERUVIAN", "VENEZUELAN", "CHILEAN", "ECUADORIAN", "BOLIVIAN"));
      put("mediterranean", Arrays.asList("MEDITERRANEAN", "GREEK", "ITALIAN", "SARDINIAN", "SPANISH"));
      put("middle-eastern", Arrays.asList("MIDDLE EASTERN", "MIDDLE-EASTERN", "IRANIAN", "EGYPTIAN", "TURKISH", "IRAQI", "SAUDI ARABIAN", "ISRAELI", "JORDINIAN", "PALESTINIAN"));
      put("south-asian", Arrays.asList("SOUTH ASIAN", "THAI", "CAMBODIAN", "MYANMA", "BURMESE", "INDONESIAN", "LAO", "LAOTIAN", "INDIAN"));

      // Question 4: What are your general tastes?
      put("spicy", Arrays.asList("SPICY"));
      put("sweet", Arrays.asList("SWEET"));
      put("salty", Arrays.asList("SALTY"));
      put("crispy", Arrays.asList("CRISPY"));
	  }
  };

  // Set the user's preferences.
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    UserService userService = UserServiceFactory.getUserService();

    // Retrieve the values of all checkboxes that the user checked in the quiz.
    String[] quizResponses = request.getParameterValues("choice");
    LinkedList<String> preferences = new LinkedList<String>();

    if(quizResponses != null) {
      // Add the appropriate preferences for every checkbox the user checked.
      for(String quizResponse : quizResponses) {
        preferences.addAll(preferenceMap.get(quizResponse));
      }
    }

    // Get the current user's key.
    Key userKey = KeyFactory.createKey("User", userService.getCurrentUser().getUserId());
    Entity user;

    try {
      // Retrieve the user from Datastore and set preferences.
      user = datastore.get(userKey);
      user.setProperty("preferences", preferences);
    } catch(EntityNotFoundException e) {
      /** This means the current user, for whom we're trying to set preferences for, doesn't exist.
        * This should never happen. If it does, multiple things have gone seriously wrong.
        */
      e.printStackTrace();
      return;
    }

    // Store the user in Datastore.
    datastore.put(user);
    response.sendRedirect("/account-creation-finish.html");
  }
}
