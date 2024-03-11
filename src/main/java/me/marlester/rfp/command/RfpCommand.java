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

import com.google.common.primitives.Ints;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import dev.dejvokep.boostedyaml.YamlDocument;
import java.io.IOException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import me.marlester.rfp.fakeplayers.FakePlayerManager;
import me.marlester.rfp.faketools.FakeLister;
import me.marlester.rfp.update.UpdateChecker;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

/**
 * The main command of the plugin. This class is all about this command.
 */
@Command("rfp")
@Description("The main command of ReallyFakePlayers.")
@CommandPermission("rfp.admin")
@RequiredArgsConstructor(onConstructor_ = {@Inject}, access = AccessLevel.PACKAGE)
public class RfpCommand {

  @Named("config")
  private final YamlDocument config;
  private final ComponentLogger logger;
  private final FakeLister fakeLister;
  private final FakePlayerManager fakePlayerManager;
  private final UpdateChecker updateChecker;

  public static final String COMMAND_NAME = "rfp";

  @Subcommand("checkupdates")
  public void checkUpdates(BukkitCommandActor actor) {
    updateChecker.checkUpdates(actor.getSender());
  }

  @Subcommand("list")
  public void list(BukkitCommandActor actor) {
    actor.reply("There are %s of a max of %s fake players online:".formatted(
        fakeLister.getFakePlayers().size(),
        config.getInt("max-fake-players")
    ));
    fakeLister.getFakePlayersByName().keySet().forEach(actor::reply);
  }

  @Subcommand("setspawn")
  public void setSpawn(BukkitCommandActor actor) {
    Player player = actor.requirePlayer();
    try {
      config.set("spawn-location", player.getLocation());
      config.save();
      actor.reply("Set spawn location of fake players to your current location.");
    } catch (IOException e) {
      actor.reply("Something went wrong while setting a spawn location"
          + " in the config, log is in the console!");
      logger.error("Error while setting spawn location in config!", e);
    }
  }

  @Subcommand("reload")
  public void reload(BukkitCommandActor actor) {
    try {
      config.reload();
      actor.reply("Config reloaded.");
    } catch (IOException e) {
      actor.reply("Something went wrong whilst config was reloading,"
          + " the error log is in your console!");
      logger.error("Error while config was reloading!", e);
    }
  }

  // TODO: make this cleaner and remove pyramids of doom
  @Subcommand("add")
  public void add(BukkitCommandActor actor, String args) {
    var fakePlayersNumber = fakeLister.getRawFakePlayers().size();
    var maxFakePlayers = config.getInt("max-fake-players");
    Integer numbered = Ints.tryParse(args);
    if (numbered != null) {
      if ((numbered + fakePlayersNumber) > maxFakePlayers) {
        actor.reply("Unable to add fake players, number of fake players exceeds the maximal number"
            + " of fake players.");
      } else {
        fakePlayerManager.addNumber(numbered);
        actor.reply("Added fake players number " + numbered + ".");
      }
    } else {
      if (fakePlayersNumber >= maxFakePlayers) {
        actor.reply("Unable to add a fake player, number of fake players exceeds the maximal"
            + " number of fake players.");
      } else if (fakeLister.getRawFakePlayersByName().containsKey(args)) {
        actor.reply("Fake player " + args + " already exists!");
      } else {
        fakePlayerManager.add(args);
        actor.reply("Added fake player named " + args + ".");
      }
    }
  }

  @Subcommand("remove")
  public void remove(BukkitCommandActor actor, String args) {
    if (args.equalsIgnoreCase("all")) {
      fakePlayerManager.removeAll();
    } else {
      Integer numbered = Ints.tryParse(args);
      if (numbered != null) {
        fakePlayerManager.removeNumber(numbered);
        actor.reply("Attempted to remove " + numbered + " fake players.");
      } else {
        if (fakeLister.getRawFakePlayersByName().containsKey(args)) {
          fakePlayerManager.remove(args);
          actor.reply(args + " fake player was removed.");
        } else {
          actor.reply(args + " no fake player with that name.");
        }
      }
    }
  }
}
