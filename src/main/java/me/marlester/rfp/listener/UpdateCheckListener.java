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
import me.marlester.rfp.update.UpdateChecker;
import me.marlester.rfp.util.PermUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Class used to notify admins if an update is available on their join.
 */
@RequiredArgsConstructor(onConstructor_ = {@Inject}, access = AccessLevel.PACKAGE)
@Singleton
public class UpdateCheckListener implements Listener {

  private final UpdateChecker updateChecker;
  @Named("config")
  private final YamlDocument config;

  /**
   * Listens to PlayerJoinEvent, if the feature is enabled it checks if the player
   * is an admin and notifies the admin about any updates if so.
   *
   * @param e the PlayerJoinEvent.
   */
  @EventHandler
  public void onJoin(PlayerJoinEvent e) {
    if (!config.getBoolean("update-check.notify-staff")) {
      return;
    }
    var player = e.getPlayer();
    if (!PermUtils.hasPermission("admin", player)) {
      return;
    }
    updateChecker.checkUpdates(player);
  }
}
