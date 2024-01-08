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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import me.marlester.rfp.ReallyFakePlayers;
import me.marlester.rfp.fakeplayers.FakePlayer;
import me.marlester.rfp.faketools.FakeLister;
import me.marlester.rfp.minimessage.MiniMsgAsst;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Class used for forcing fakeplayers to welcome incoming players.
 */
@RequiredArgsConstructor(onConstructor_ = {@Inject}, access = AccessLevel.PACKAGE)
@Singleton
public class WelcomeListener implements Listener {

  @Named("config")
  private final YamlDocument config;
  private final FakeLister fakeLister;
  private final ReallyFakePlayers pl;
  private final MiniMsgAsst miniMsgAsst;

  private final List<FakePlayer> welcomers = new ArrayList<>();

  /**
   * Listens to PlayerJoinEvent and gets some random fake player to welcome them
   * if configured so.
   *
   * @param e the PlayerJoinEvent.
   */
  @EventHandler(priority = EventPriority.LOWEST)
  public void welcomeOnJoin(PlayerJoinEvent e) {
    if (!config.getBoolean("welcome.enable")) {
      return;
    }
    if (welcomers.size() >= config.getInt("welcome.max-welcomers")) {
      return;
    }
    var fakePlayers = fakeLister.getFakePlayers();
    if (fakePlayers.isEmpty()) {
      return;
    }
    int delay = 20 * ThreadLocalRandom.current().nextInt(
        config.getInt("welcome.delay.min"),
        config.getInt("welcome.delay.max") + 1
    );
    var player = e.getPlayer();
    var scheduler = Bukkit.getScheduler();
    scheduler.runTaskLater(pl, () -> {
      // Better safe than sorry
      if (!player.isOnline()) {
        return;
      }
      // Double check is important here, we want before/after
      if (fakePlayers.isEmpty()) {
        return;
      }
      var upForGrabsFakePlayers = new ArrayList<>(fakePlayers);
      var possibleSelfFakePlayer = fakeLister.getFakePlayersByName().get(player.getName());
      if (possibleSelfFakePlayer != null) {
        upForGrabsFakePlayers.remove(possibleSelfFakePlayer);
      }
      upForGrabsFakePlayers.removeAll(welcomers);
      if (upForGrabsFakePlayers.isEmpty()) {
        return;
      }
      var fakePlayer = upForGrabsFakePlayers.get(ThreadLocalRandom.current()
          .nextInt(upForGrabsFakePlayers.size()));
      var fakePlayerBukkit = fakePlayer.getPlayer();
      var messages = player.hasPlayedBefore()
          ? config.getStringList("welcome.rejoin-messages")
          : config.getStringList("welcome.first-join-messages");
      var message = messages.get(ThreadLocalRandom.current().nextInt(messages.size()));
      message = miniMsgAsst.deserializeAsPlainText(message, fakePlayerBukkit, player);
      fakePlayerBukkit.chat(message);
      welcomers.add(fakePlayer);
      scheduler.runTaskLater(pl, () -> {
        welcomers.remove(fakePlayer);
      }, 20L * config.getInt("welcome.period"));
    }, delay);
  }
}
