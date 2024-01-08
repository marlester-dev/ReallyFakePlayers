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

package me.marlester.rfp.placeholders;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import io.github.miniplaceholders.api.Expansion;

/**
 * Guice module for providing instances of placeholder expansions.
 */
public class PlaceholdersModule extends AbstractModule {

  /**
   * Provides a singleton instance of the standard placeholders expansion.
   *
   * @param creator The creator object responsible for generating the placeholders expansion.
   * @return An Expansion instance representing the standard placeholders.
   */
  @Provides
  @Singleton
  @Named("placeholdersExpansion")
  public Expansion providePlaceholdersExpansion(PlaceholdersExpansionCreator creator) {
    return creator.createExpansion();
  }

  /**
   * Provides a singleton instance of the internal placeholders expansion.
   *
   * @param creator The creator object responsible for generating the internal placeholders
   *                expansion.
   * @return An Expansion instance representing the internal placeholders.
   */
  @Provides
  @Singleton
  @Named("internalPlaceholdersExpansion")
  public Expansion provideIntlPlaceholdersExpansion(IntlPlaceholdersExpansionCreator creator) {
    return creator.createExpansion();
  }
}
