/*
 * Copyright 2023 Marlester
 *
 * Licensed under the EUPL, Version 1.2 (the "License");
 *
 * You may not use this work except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 * https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 */

package me.marlester.rfp.util;

import lombok.experimental.UtilityClass;

/**
 * Utils related to versions.
 */
@UtilityClass
public class VersionUtils {

  /**
   * Method to compare two versions.
   *
   * @param v1 version "number 1"
   * @param v2 version "number 2"
   * @return 1 if v2 is smaller, -1 if v1 is smaller, 0 if equal
   */
  public int versionCompare(String v1, String v2) {
    // vnum stores each numeric part of version
    int vnum1 = 0;
    int vnum2 = 0;

    // loop until both String are processed
    for (int i = 0, j = 0; (i < v1.length()
        || j < v2.length()); ) {
      // Storing numeric part of
      // version 1 in vnum1
      while (i < v1.length()
          && v1.charAt(i) != '.') {
        vnum1 = vnum1 * 10
            + (v1.charAt(i) - '0');
        i++;
      }

      // storing numeric part
      // of version 2 in vnum2
      while (j < v2.length()
          && v2.charAt(j) != '.') {
        vnum2 = vnum2 * 10
            + (v2.charAt(j) - '0');
        j++;
      }

      if (vnum1 > vnum2) {
        return 1;
      }
      if (vnum2 > vnum1) {
        return -1;
      }

      // if equal, reset variables and
      // go for next numeric part
      vnum1 = vnum2 = 0;
      i++;
      j++;
    }
    return 0;
  }

}
