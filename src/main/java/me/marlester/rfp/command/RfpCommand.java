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
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import dev.dejvokep.boostedyaml.YamlDocument;
import java.io.IOException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import me.marlester.rfp.fakeplayers.FakePlayerManager;
import me.marlester.rfp.faketools.FakeLister;
import me.marlester.rfp.update.UpdateChecker;
import me.marlester.rfp.util.PermCheckUtils;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;

/**
 * Class implementing the /rfp command.
 * The main plugin's command having lots of subcommands.
 */
@RequiredArgsConstructor(onConstructor_ = {@Inject}, access = AccessLevel.PACKAGE)
@Singleton
public class RfpCommand implements CommandExecutor {

  /**
   * The name of the command well-known in this class.
   */
  public static final String COMMAND_NAME = "rfp";

  private final ComponentLogger logger;
  @Named("config")
  private final YamlDocument config;
  private final FakePlayerManager fakePlayerManager;
  private final FakeLister fakeLister;
  private final UpdateChecker updateChecker;

  @Override
  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    if (!(PermCheckUtils.hasPermission("admin", sender))) {
      sender.sendMessage("You do not have appropriate permissions to execute this command!");
      return true;
    }
    if (args.length <= 0) {
      help(sender);
      return true;
    }

    switch (args[0]) {
      case "reload" -> {
        try {
          config.reload();
        } catch (IOException e) {
          sender.sendMessage("Something went wrong while config reload, log is in console!");
          logger.error("Error while reloading config!", e);
          break;
        }
        sender.sendMessage("Config reloaded.");
      }
      case "add" -> {
        if (args.length <= 1) {
          sender.sendMessage("Proper use: add <#/name>");
          break;
        }
        if (fakeLister.getRawFakePlayers().size() > config.getInt("max-fake-players")) {
          sender.sendMessage("Unable to add fake player(s), number of fake players exceeds"
              + " the maximal number of fake players.");
          break;
        }
        Integer numbered = Ints.tryParse(args[1]);
        if (numbered != null) {
          fakePlayerManager.addNumber(numbered);
          sender.sendMessage("Added fake players number " + args[1] + ".");
        } else {
          fakePlayerManager.add(args[1]);
          sender.sendMessage("Added fake player with name " + args[1] + ".");
        }
      }
      case "remove" -> {
        if (args.length <= 1) {
          sender.sendMessage("Proper use: remove <#/name>");
          break;
        }
        if (args[1].equalsIgnoreCase("all")) {
          fakePlayerManager.removeAll();
        } else {
          Integer numbered = Ints.tryParse(args[1]);
          if (numbered != null) {
            fakePlayerManager.removeNumber(numbered);
            sender.sendMessage(args[1] + " fake players were removed.");
          } else {
            if (fakeLister.getRawFakePlayersByName().containsKey(args[1])) {
              fakePlayerManager.remove(args[1]);
              sender.sendMessage(args[1] + " fake player was removed.");
            } else {
              sender.sendMessage(args[1] + " no fake player with that name.");
            }
          }
        }
      }
      case "list" -> {
        sender.sendMessage("There are %s of a max of %s fake players online:".formatted(
            fakeLister.getFakePlayers().size(),
            config.getInt("max-fake-players")
        ));
        fakeLister.getFakePlayersByName().keySet().forEach(sender::sendMessage);
      }
      case "checkupdates" -> {
        updateChecker.checkUpdates(sender);
      }
      case "setspawn" -> {
        if (!(sender instanceof Entity entity)) {
          sender.sendMessage("You must be an entity to execute this!");
          break;
        }
        try {
          config.set("spawn-location", entity.getLocation());
          config.save();
        } catch (IOException e) {
          sender.sendMessage("Something went wrong while setting a spawn location in the config, "
              + "log is in the console!");
          logger.error("Error while setting spawn location in config!", e);
          break;
        }
        sender.sendMessage("Set fake players' spawn location to your current location.");
      }
      default -> {
        help(sender);
      }
    }

    return true;
  }

  private void help(CommandSender sender) {
    sender.sendMessage(
        "The command \"/" + COMMAND_NAME + "\" doesn't do anything by itself,",
        "Please provide a subcommand - \"/" + COMMAND_NAME + " <subcommand>\",",
        "Here is a list of valid subcommands:",
        "help - help command",
        "add <#> - add number of fake players",
        "add <name> - add fake player",
        "remove <#> - remove number of fake players",
        "remove <name> - remove fake player",
        "remove all - remove all fake players",
        "list - list of fake players",
        "reload - reload config",
        "setspawn - set fake players' spawn location where you stay",
        "checkupdates - check updates"
    );
  }

}
