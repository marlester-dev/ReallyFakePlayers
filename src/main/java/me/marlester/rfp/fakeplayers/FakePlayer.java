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

package me.marlester.rfp.fakeplayers;

import com.github.steveice10.packetlib.Session;
import java.util.UUID;
import me.marlester.rfp.listener.fakeplayerjoinlistener.FakePlayerJoinListener;
import org.bukkit.entity.Player;

/**
 * Represents a fake player, connected or not.
 */
public interface FakePlayer {

  /**
   * Returns the name of this fake player.
   *
   * @return Fake player's name.
   */
  String getName();

  /**
   * Returns the UUID of this fake player.<br>
   * Fake players' UUIDs are different from players' UUIDs, so a player with the same nick as a
   * fake player will not have the same UUID.
   *
   * @return Fake player's UUID.
   */
  UUID getUuid();

  /**
   * Returns the {@link Player} of this fake player.<br>
   * Might be null.
   *
   * @return Fake player's {@link Player}.
   */
  Player getPlayer();

  /**
   * Sets the {@link Player} of this fake player.
   *
   * @param value Player instance.
   */
  void setPlayer(Player value);

  /**
   * Returns the key of this fake player.<br>
   * Key acts as a unique identifier of a fake player during an early login.
   * It's being added to one of packets for a server-side identification.
   * Please don't do much stuff with this without a great reason.
   *
   * @return Fake player's key.
   */
  UUID getKey();

  /**
   * Returns the MCProtocolLib's Session of this fake player.
   *
   * @return MCProtocolLib's Session of this fake player.
   */
  Session getClient();

  /**
   * Returns a removed boolean.<br>
   * It's determined by whether this fake player was removed or not BY THE PLUGIN.
   *
   * @return Fake player's removed boolean.
   */
  boolean isRemoved();

  /**
   * Sets the {@link FakePlayerJoinListener} of this fake player.
   *
   * @param value FakePlayerJoinListener instance.
   */
  void setJoinListener(FakePlayerJoinListener value);

  /**
   * Tries to connect this fake player to the server.
   */
  void join();

  /**
   * Removes this fake player from all the systems.
   * Disconnects from server, removes from fake player lists, etc.
   * Will not try to remove if already removed.
   */
  void remove();
}
