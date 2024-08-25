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
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import me.marlester.rfp.faketools.FakeLister;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Listens to player quit events to manage the removal of fake players from the game. This class is
 * responsible for detecting when a fake player leaves the game and performs necessary cleanup
 * actions. It ensures that the fake player's data is appropriately handled upon their exit.
 */
@RequiredArgsConstructor(onConstructor_ = {@Inject}, access = AccessLevel.PACKAGE)
@Singleton
public class FakePlayerQuitListener implements Listener {

  private final FakeLister fakeLister;

  /**
   * Handles the {@link PlayerQuitEvent}. When a player quits, this method checks if the player is a
   * fake player. If so, it proceeds to remove the fake player, performing necessary cleanup
   * operations.
   *
   * @param e the player quit event.
   */
  @EventHandler(priority = EventPriority.LOW)
  public void onQuit(PlayerQuitEvent e) {
    var uuid = e.getPlayer().getUniqueId();
    var fakePlayer = fakeLister.getRawFakePlayersByUuid().get(uuid);
    if (fakePlayer == null) {
      return;
    }
    fakePlayer.remove();
  }
}
