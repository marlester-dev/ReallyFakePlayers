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
import java.util.concurrent.ThreadLocalRandom;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import me.marlester.rfp.ReallyFakePlayers;
import me.marlester.rfp.chatting.Chatting;
import me.marlester.rfp.config.RfpYamlDoc;
import me.marlester.rfp.fakeplayers.FakePlayer;
import me.marlester.rfp.faketools.FakeLister;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * Handles the joining process of fake players into the game. This class is responsible for setting
 * up fake players when they join the game, including hiding them from real players, applying
 * various effects and configurations, and handling their interactions with the game world.
 */
@RequiredArgsConstructor(onConstructor_ = {@Inject}, access = AccessLevel.PACKAGE)
@Singleton
public class FakePlayerJoinListener implements Listener {

  private final ReallyFakePlayers pl;
  private final FakeLister fakeLister;
  private final Chatting chatting;
  @Named("config")
  private final RfpYamlDoc config;

  /**
   * Manages actions upon a fake player's join event. Sets up the fake player upon joining by
   * applying necessary configurations and effects. Unregisters the listener post setup to avoid
   * redundant interactions.
   *
   * @param e the player join event.
   */
  @EventHandler(priority = EventPriority.LOW)
  public void onJoin(PlayerJoinEvent e) {
    var player = e.getPlayer();
    var uuid = player.getUniqueId();
    var fakePlayer = fakeLister.getRawFakePlayersByUuid().get(uuid);
    if (fakePlayer == null) {
      return;
    }
    fakePlayer.setPlayer(player);
    handleJoin(fakePlayer);
  }

  private void handleJoin(FakePlayer fakePlayer) {
    fakeLister.getFakePlayers().add(fakePlayer);
    fakeLister.getFakePlayersByUuid().put(fakePlayer.getUuid(), fakePlayer);
    fakeLister.getFakePlayersByName().put(fakePlayer.getName(), fakePlayer);

    var player = fakePlayer.getPlayer();
    if (config.getBoolean("hide")) {
      for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
        onlinePlayer.hidePlayer(pl, player);
      }
    }
    onSpawn(player);
    if (config.getBoolean("invulnerable")) {
      player.setInvulnerable(true);
    }
    if (config.getBoolean("no-collision")) {
      player.setCollidable(false);
    }
    if (config.getBoolean("no-gravity")) {
      player.setGravity(false);
    }
    var consoleSender = Bukkit.getConsoleSender();
    config.getSerializedStringList("join-commands.as-console", player).forEach(c -> {
      Bukkit.dispatchCommand(consoleSender, c);
    });
    config.getSerializedStringList("join-commands.as-fake-player", player).forEach(c -> {
      player.performCommand(c);
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

  public void onSpawn(Player fakePlayer) {
    onSpawn(fakePlayer, false);
  }

  public void onSpawn(Player fakePlayer, boolean respawn) {
    if (config.getBoolean("invisibility-effect")) {
      addInvis(fakePlayer);
    }

    if (respawn) {
      var consoleSender = Bukkit.getConsoleSender();
      config.getSerializedStringList("respawn-commands.as-console", fakePlayer).forEach(c -> {
        Bukkit.dispatchCommand(consoleSender, c);
      });
      config.getSerializedStringList("respawn-commands.as-fake-player", fakePlayer).forEach(c -> {
        fakePlayer.performCommand(c);
      });
    }
  }

  private static void addInvis(Player player) {
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
}
