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

import shef.servlets.NewCommentServlet;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import java.util.LinkedList;
import java.util.List;
import java.util.Arrays;

/** Tests for NewCommentServlet. */
@RunWith(JUnit4.class)
public class NewCommentServletTest {

  @Test
  public void timestampToMMDDYYYYTest() {
    long timestamp = 1595263347000L;
    String expectedMMDDYYYY = "07/20/2020";

    String actualMMDDYYYY = NewCommentServlet.timestampToMMDDYYYY(timestamp);

    Assert.assertEquals(expectedMMDDYYYY, actualMMDDYYYY);
  }
}
