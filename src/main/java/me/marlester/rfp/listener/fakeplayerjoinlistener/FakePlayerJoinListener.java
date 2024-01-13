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

package me.marlester.rfp.listener.fakeplayerjoinlistener;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import com.google.inject.name.Named;
import dev.dejvokep.boostedyaml.YamlDocument;
import java.util.concurrent.ThreadLocalRandom;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import me.marlester.rfp.ReallyFakePlayers;
import me.marlester.rfp.chatting.Chatting;
import me.marlester.rfp.fakeplayers.FakePlayer;
import me.marlester.rfp.faketools.FakeLister;
import me.marlester.rfp.minimessage.MiniMsgAsst;
import me.marlester.rfp.vault.VaultIntegration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * Handles the joining process of fake players into the game.
 * This class is responsible for setting up fake players when they join the game, including hiding
 * them from real players, applying various effects and configurations, and handling their
 * interactions with the game world. It also ensures that once a fake player has joined,
 * the listener is unregistered.
 */
@RequiredArgsConstructor(onConstructor_ = {@AssistedInject}, access = AccessLevel.PACKAGE)
public class FakePlayerJoinListener implements Listener {

  private final ReallyFakePlayers pl;
  private final FakeLister fakeLister;
  private final Chatting chatting;
  @Named("config")
  private final YamlDocument config;
  private final VaultIntegration vaultIntegration;
  private final MiniMsgAsst miniMsgAsst;

  @Assisted
  private final FakePlayer fakePlayer;

  /**
   * Manages actions upon a fake player's join event.
   * Sets up the fake player upon joining by applying necessary configurations and effects.
   * Unregisters the listener post setup to avoid redundant interactions.
   *
   * @param e the player join event.
   */
  @EventHandler
  public void onJoin(PlayerJoinEvent e) {
    var player = e.getPlayer();
    if (player.getUniqueId().equals(fakePlayer.getUuid())) {
      fakePlayer.setPlayer(player);
      handleJoin();
      // now let's make this listener disappear
      stopListening();
      fakePlayer.setJoinListener(null);
    }
  }

  private void handleJoin() {
    fakeLister.getFakePlayers().add(fakePlayer);
    fakeLister.getFakePlayersByUuid().put(fakePlayer.getUuid(), fakePlayer);
    fakeLister.getFakePlayersByName().put(fakePlayer.getName(), fakePlayer);

    var player = fakePlayer.getPlayer();
    if (config.getBoolean("hide")) {
      for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
        onlinePlayer.hidePlayer(pl, player);
      }
    }
    if (config.getBoolean("invisibility-effect")) {
      player.addPotionEffect(
          new PotionEffect(
              PotionEffectType.INVISIBILITY,
              PotionEffect.INFINITE_DURATION,
              1,
              false,
              false
          )
      );
    }
    if (config.getBoolean("invulnerable")) {
      player.setInvulnerable(true);
    }
    if (config.getBoolean("no-collision")) {
      player.setCollidable(false);
    }
    if (config.getBoolean("no-gravity")) {
      player.setGravity(false);
    }
    if (vaultIntegration.isActive()) {
      vaultIntegration.giveGroup(player);
      vaultIntegration.givePermissions(player);
    }
    config.getOptionalStringList("join-commands.as-console").ifPresent(consoleCommands -> {
      var consoleSender = Bukkit.getConsoleSender();
      consoleCommands.forEach(cmd -> {
        Bukkit.dispatchCommand(consoleSender, miniMsgAsst.deserializeAsPlainText(cmd, player));
      });
    });
    config.getOptionalStringList("join-commands.as-fake-player").ifPresent(playerCommands -> {
      playerCommands.forEach(cmd -> {
        player.performCommand(miniMsgAsst.deserializeAsPlainText(cmd, player));
      });
    });
    chatting.startChatting(fakePlayer);
    if (config.getBoolean("auto-quit.enable")) {
      int delay = 20 * ThreadLocalRandom.current().nextInt(
          config.getInt("auto-quit.delay.min"),
          config.getInt("auto-quit.delay.max") + 1
      );
      Bukkit.getScheduler().runTaskLater(pl, fakePlayer::remove, delay);
    }
  }

  /**
   * Registers the listener to start receiving join events.
   */
  public void startListening() {
    Bukkit.getPluginManager().registerEvents(this, pl);
  }

  /**
   * Unregisters the listener to stop receiving join events.
   * Typically called after the fake player has joined and is fully set up.
   */
  public void stopListening() {
    PlayerJoinEvent.getHandlerList().unregister(this);
  }
}
