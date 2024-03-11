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

package me.marlester.rfp.command;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import me.marlester.rfp.ReallyFakePlayers;
import revxrsal.commands.bukkit.BukkitCommandHandler;

/**
 * Class that's used for registering commands of this plugin.
 */
@RequiredArgsConstructor(onConstructor_ = {@Inject}, access = AccessLevel.PACKAGE)
@Singleton
public class CommandsRegisterer {

  private final ReallyFakePlayers pl;

  private final RfpCommand rfpCommand;

  /**
   * Registers commands of this plugin.
   */
  public void registerCommands() {
    BukkitCommandHandler handler = BukkitCommandHandler.create(pl);
    handler.register(rfpCommand);
  }
}
