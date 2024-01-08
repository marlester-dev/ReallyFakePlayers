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
import me.marlester.rfp.ReallyFakePlayers;
import me.marlester.rfp.faketools.FakeLister;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Handles visibility of fake players during player join events.
 * It ensures that real players, upon joining the server, do not see fake players if the config
 * is set to hide them. This listener checks the configuration and acts accordingly, maintaining
 * the intended visibility of fake players.
 */
@RequiredArgsConstructor(onConstructor_ = {@Inject}, access = AccessLevel.PACKAGE)
@Singleton
public class FakePlayerHideListener implements Listener {

  private final ReallyFakePlayers pl;
  private final FakeLister fakeLister;
  @Named("config")
  private final YamlDocument config;

  /**
   * Handles the {@link PlayerJoinEvent}.
   * When a player joins, this method checks the configuration. If hiding fake players is enabled,
   * it hides all fake players from the newly joined real player, unless the joining player is a
   * fake player.
   *
   * @param e the {@link PlayerJoinEvent} event that is occurring.
   */
  @EventHandler(priority = EventPriority.LOWEST)
  public void onJoin(PlayerJoinEvent e) {
    if (!config.getBoolean("hide")) {
      return;
    }
    var player = e.getPlayer();
    if (fakeLister.isFakePlayer(player.getUniqueId())) {
      return;
    }
    fakeLister.getFakePlayers().forEach(fakePlayer -> {
      player.hidePlayer(pl, fakePlayer.getPlayer());
    });
  }
}
