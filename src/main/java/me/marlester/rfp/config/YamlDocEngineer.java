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
import dev.dejvokep.boostedyaml.dvs.versioning.BasicVersioning;
import dev.dejvokep.boostedyaml.settings.dumper.DumperSettings;
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings;
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings;
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings;
import dev.dejvokep.boostedyaml.spigot.SpigotSerializer;
import java.io.File;
import java.io.IOException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import me.marlester.rfp.ReallyFakePlayers;

/**
 * This class simplifies the creation of YAML documents.
 */
@RequiredArgsConstructor(onConstructor_ = {@Inject}, access = AccessLevel.PACKAGE)
@Singleton
public class YamlDocEngineer {

  private final ReallyFakePlayers pl;

  /**
   * Creates a new YamlDocument with no updater.
   *
   * @param name name of the document.
   */
  public YamlDocument create(String name) throws IOException {
    return create(name, false);
  }

  /**
   * Creates a new YamlDocument.
   *
   * @param name   name of the document.
   * @param update enable updater? updater should have a config-version option.
   */
  public YamlDocument create(String name, boolean update) throws IOException {
    return YamlDocument.create(
        new File(pl.getDataFolder(), name),
        pl.getResource(name),
        GeneralSettings.builder().setSerializer(SpigotSerializer.getInstance()).build(),
        update ? LoaderSettings.builder().setAutoUpdate(true).build() : LoaderSettings.DEFAULT,
        DumperSettings.DEFAULT,
        update ? UpdaterSettings.builder().setVersioning(new BasicVersioning("config-version"))
            .build() : UpdaterSettings.DEFAULT
    );
  }
}
