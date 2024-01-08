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

import com.google.inject.Inject;
import com.google.inject.Singleton;
import dev.dejvokep.boostedyaml.YamlDocument;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

/**
 * Class used for creating config.yml.
 */
@RequiredArgsConstructor(onConstructor_ = {@Inject}, access = AccessLevel.PACKAGE)
@Singleton
public class ConfigYml {

  private final YamlDocEngineer yamlDocEngineer;

  public static final String FILE_NAME = "config.yml";
  @Getter
  private YamlDocument configuration;

  private boolean created;

  @SneakyThrows
  void create() {
    if (created) {
      return;
    }
    configuration = yamlDocEngineer.create(FILE_NAME, true);
    created = true;
  }
}
