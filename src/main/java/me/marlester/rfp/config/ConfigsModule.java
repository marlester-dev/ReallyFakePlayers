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

package me.marlester.rfp.config;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import dev.dejvokep.boostedyaml.YamlDocument;

/**
 * Class used for providing configuration files.
 */
public class ConfigsModule extends AbstractModule {

  @Provides
  @Singleton
  @Named("config")
  public YamlDocument provideConfig(ConfigYml configYml) {
    return configYml.getConfiguration();
  }

  @Provides
  @Singleton
  @Named("nameList")
  public YamlDocument provideNameList(NameListYml nameListYml) {
    return nameListYml.getConfiguration();
  }
}
