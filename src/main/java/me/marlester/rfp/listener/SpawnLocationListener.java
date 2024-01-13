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

package me.marlester.rfp.listener;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import dev.dejvokep.boostedyaml.YamlDocument;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import me.marlester.rfp.faketools.FakeLister;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

/**
 * Sets spawn location from config for fake players.
 */
@RequiredArgsConstructor(onConstructor_ = {@Inject}, access = AccessLevel.PACKAGE)
@Singleton
public class SpawnLocationListener implements Listener {

  private final FakeLister fakeLister;
  @Named("config")
  private final YamlDocument config;

  /**
   * Listens to PlayerSpawnLocationEvent, if a fake player then change spawn
   * location to the one from config if it's present.
   *
   * @param e the PlayerSpawnLocationEvent
   */
  @EventHandler(priority = EventPriority.HIGHEST)
  public void onSpawnLocation(PlayerSpawnLocationEvent e) {
    if (!fakeLister.isFakePlayer(e.getPlayer().getUniqueId())) {
      return;
    }

    var configSpawnLocation = config.getAsOptional("spawn-location", Location.class);
    if (configSpawnLocation.isPresent()) {
      e.setSpawnLocation(configSpawnLocation.get());
    }
  }
}
