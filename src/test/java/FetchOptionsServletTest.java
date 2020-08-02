import shef.servlets.FetchOptionsServlet;
import static org.mockito.Mockito.*;
import org.mockito.junit.MockitoJUnitRunner;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RunWith(JUnit4.class)
public class FetchOptionsServletTest {

  /** Ensures that the substring splitting works for just one letter. */
  @Test
  public void titleCaseItemsTest_oneLetter() {
    Set<String> expectedList = new HashSet<>(Arrays.asList("E")); 

    Set<String> testList = new HashSet<>(Arrays.asList("e")); 
    Set<String> actualList = FetchOptionsServlet.titleCaseItems(testList);

    Assert.assertEquals(expectedList, actualList);
  }

  @Test
  public void titleCaseItemsTest_allCaps() {
    Set<String> expectedList = new HashSet<>(Arrays.asList("Egg"));

    Set<String> testList = new HashSet<>(Arrays.asList("EGG"));
    Set<String> actualList = FetchOptionsServlet.titleCaseItems(testList);

    Assert.assertEquals(expectedList, actualList);
  }

  @Test
  public void titleCaseItemsTest_noCaps() {
    Set<String> expectedList = new HashSet<>(Arrays.asList("Egg"));

    Set<String> testList = new HashSet<>(Arrays.asList("egg"));
    Set<String> actualList = FetchOptionsServlet.titleCaseItems(testList);

    Assert.assertEquals(expectedList, actualList);
  }

  @Test
  public void titleCaseItemsTest_erraticCaps() {
    Set<String> expectedList = new HashSet<>(Arrays.asList("Bread"));

    Set<String> testList = new HashSet<>(Arrays.asList("bReAD"));
    Set<String> actualList = FetchOptionsServlet.titleCaseItems(testList);

    Assert.assertEquals(expectedList, actualList);
  }
}
