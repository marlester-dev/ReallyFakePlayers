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

package me.marlester.rfp;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;

/**
 * This is the main module of this plugin.
 * It provides some general things for the entire plugin.
 */
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class MainModule extends AbstractModule {

  private final ReallyFakePlayers pl;

  @Override
  protected void configure() {
    bind(ReallyFakePlayers.class).toInstance(pl);
    bind(String.class).annotatedWith(Names.named("pluginName")).toInstance(pl.getName());
    bind(ComponentLogger.class).toInstance(pl.getComponentLogger());
  }
}
