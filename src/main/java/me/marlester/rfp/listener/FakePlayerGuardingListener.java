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
import io.papermc.paper.event.player.ChatEvent;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import me.marlester.rfp.faketools.FakeLister;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerPreLoginEvent;

/**
 * Listens to various player-related events to protect the integrity of fake players managed by the
 * server. This class intervenes in certain events to ensure that actions by other plugins or
 * processes do not inappropriately affect fake players. It operates by cancelling or un-cancelling
 * specific events based on the fake player status.
 */
@RequiredArgsConstructor(onConstructor_ = {@Inject}, access = AccessLevel.PACKAGE)
@Singleton
public class FakePlayerGuardingListener implements Listener {

  private final FakeLister fakeLister;

  /**
   * Handles the {@link ChatEvent}.
   * If the event is cancelled and the player is a fake player, this method un-cancels the event,
   * allowing the fake player to chat.
   *
   * @param e the {@link ChatEvent} event that is occurring.
   */
  @EventHandler(priority = EventPriority.HIGHEST)
  public void onPlayerChat(ChatEvent e) {
    if (e.isCancelled() && fakeLister.isFakePlayer(e.getPlayer().getUniqueId())) {
      e.setCancelled(false);
    }
  }

  /**
   * Handles the {@link PlayerCommandPreprocessEvent}.
   * If the event is cancelled and the player is a fake player, this method un-cancels the event,
   * allowing the fake player to process commands.
   *
   * @param e the {@link PlayerCommandPreprocessEvent} event that is occurring.
   */
  @EventHandler(priority = EventPriority.HIGHEST)
  public void onPlayerCommand(PlayerCommandPreprocessEvent e) {
    if (e.isCancelled() && fakeLister.isFakePlayer(e.getPlayer().getUniqueId())) {
      e.setCancelled(false);
    }
  }

  /**
   * Handles the {@link PlayerKickEvent}.
   * If the event is not cancelled and the player is a fake player, this method cancels the event,
   * preventing the fake player from being kicked.
   *
   * @param e the {@link PlayerKickEvent} event that is occurring.
   */
  @EventHandler(priority = EventPriority.HIGHEST)
  public void onPlayerKick(PlayerKickEvent e) {
    if (!e.isCancelled() && fakeLister.isFakePlayer(e.getPlayer().getUniqueId())) {
      e.setCancelled(true);
    }
  }

  /**
   * Handles the {@link PlayerLoginEvent}.
   * If the result is not ALLOWED and the player is a fake player, this method alters the result to
   * ALLOWED, permitting the fake player to log in.
   *
   * @param e the {@link PlayerLoginEvent} event that is occurring.
   */
  @EventHandler(priority = EventPriority.HIGHEST)
  public void onPlayerLogin(PlayerLoginEvent e) {
    if (!(e.getResult() == PlayerLoginEvent.Result.ALLOWED)
        && fakeLister.isFakePlayer(e.getPlayer().getUniqueId())) {
      e.allow();
    }
  }

  /**
   * Handles the {@link PlayerPreLoginEvent}.
   * If the login result is not ALLOWED and the player is identified as a fake player, this method
   * changes the result to ALLOWED, allowing the fake player's pre-login process.
   *
   * @param e the {@link PlayerPreLoginEvent} that is occurring.
   */
  @EventHandler(priority = EventPriority.HIGHEST)
  public void onPlayerPreLogin(PlayerPreLoginEvent e) {
    if (!(e.getResult() == PlayerPreLoginEvent.Result.ALLOWED)
        && fakeLister.isFakePlayer(e.getUniqueId())) {
      e.allow();
    }
  }
}
