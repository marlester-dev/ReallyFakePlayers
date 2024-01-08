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
import me.marlester.rfp.command.RfpCommand;
import me.marlester.rfp.faketools.FakeLister;
import me.marlester.rfp.minimessage.MiniMsgAsst;
import me.marlester.rfp.util.PermCheckUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

/**
 * Makes blocking commands having fakeplayers names real.
 */
@RequiredArgsConstructor(onConstructor_ = {@Inject}, access = AccessLevel.PACKAGE)
@Singleton
public class CommandBlockerListener implements Listener {

  private final FakeLister fakeLister;
  @Named("config")
  private final YamlDocument config;
  private final MiniMsgAsst miniMsgAsst;

  /**
   * Listens to PlayerCommandPreprocessEvent and blocks commands containing names
   * of online fake players if configured so.
   *
   * @param e the PlayerCommandPreprocessEvent event.
   */
  @EventHandler(priority = EventPriority.HIGHEST)
  public void onPlayerCommand(PlayerCommandPreprocessEvent e) {
    var player = e.getPlayer();
    if (!config.getBoolean("block-interaction.enable")
        || PermCheckUtils.hasPermission("interaction", player)) {
      return;
    }
    String command = e.getMessage().toLowerCase();
    if (command.startsWith("/" + RfpCommand.COMMAND_NAME)) {
      return;
    }
    for (String fakePlayerName : fakeLister.getRawFakePlayersByName().keySet()) {
      fakePlayerName = fakePlayerName.toLowerCase();
      if (command.contains(" " + fakePlayerName + " ")
          || command.endsWith(" " + fakePlayerName)) {
        e.setCancelled(true);
        Component blockMessage = miniMsgAsst.deserialize(
            config.getString("block-interaction.message"),
            player
        );
        player.sendMessage(blockMessage);
      }
    }
  }
}
