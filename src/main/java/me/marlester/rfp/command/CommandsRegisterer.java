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
import org.bukkit.command.CommandSender;
import org.incendo.cloud.bukkit.CloudBukkitCapabilities;
import org.incendo.cloud.paper.PaperCommandManager;

/**
 * Class that's used for registering commands of this plugin.
 */
@RequiredArgsConstructor(onConstructor_ = {@Inject}, access = AccessLevel.PACKAGE)
@Singleton
public class CommandsRegisterer {

  // {@link CommandsModule#provideCommandManager(ReallyFakePlayers)}
  private final PaperCommandManager<CommandSender> manager;

  private final RfpCommand rfpCommand;

  /**
   * Registers commands of this plugin.
   */
  public void registerCommands() {
    // Configure based on capabilities like over there: https://cloud.incendo.org/minecraft/paper/#execution-coordinators
    if (manager.hasCapability(CloudBukkitCapabilities.NATIVE_BRIGADIER)) {
      manager.registerBrigadier();
    } else if (manager.hasCapability(CloudBukkitCapabilities.ASYNCHRONOUS_COMPLETION)) {
      manager.registerAsynchronousCompletions();
    }

    // Register commands
    manager.command(rfpCommand.getCommand());
  }
}
