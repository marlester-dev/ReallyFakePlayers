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
import me.marlester.rfp.ReallyFakePlayers;
import org.bukkit.Bukkit;

/**
 * Class responsible for registering various event listeners related to fake player management. This
 * class holds references to different listener objects and provides a method to register all these
 * listeners with the Bukkit plugin manager. Each listener handles specific aspects of fake player
 * interaction within the game.
 */
@RequiredArgsConstructor(onConstructor_ = {@Inject}, access = AccessLevel.PACKAGE)
@Singleton
public class ListenersRegistrar {

  private final ReallyFakePlayers pl;

  private final FakePlayerGuardingListener fakePlayerGuardingListener;
  private final CommandBlockerListener commandBlockerListener;
  private final FakePlayerQuitListener fakePlayerQuitListener;
  private final WelcomeListener welcomeListener;
  private final SpawnLocationListener spawnLocationListener;
  private final FakePlayerHideListener fakePlayerHideListener;
  private final UpdateCheckListener updateCheckListener;

  /**
   * Registers all listeners with the Bukkit plugin manager. This method ensures that each listener
   * is activated and begins receiving the events they are designed to handle.
   */
  public void registerListeners() {
    var pluginManager = Bukkit.getPluginManager();
    pluginManager.registerEvents(fakePlayerGuardingListener, pl);
    pluginManager.registerEvents(commandBlockerListener, pl);
    pluginManager.registerEvents(spawnLocationListener, pl);
    pluginManager.registerEvents(fakePlayerQuitListener, pl);
    pluginManager.registerEvents(fakePlayerHideListener, pl);
    pluginManager.registerEvents(welcomeListener, pl);
    pluginManager.registerEvents(updateCheckListener, pl);
  }
}
