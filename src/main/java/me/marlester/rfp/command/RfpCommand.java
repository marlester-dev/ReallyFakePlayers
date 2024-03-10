package me.marlester.rfp.command;

import static org.incendo.cloud.parser.standard.StringParser.stringParser;

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
import me.marlester.rfp.util.PermUtils;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.incendo.cloud.Command;
import org.incendo.cloud.description.CommandDescription;
import org.incendo.cloud.paper.PaperCommandManager;

@RequiredArgsConstructor(onConstructor_ = {@Inject}, access = AccessLevel.PACKAGE)
@Singleton
public class RfpCommand {

  private final PaperCommandManager<CommandSender> manager;
  @Named("config")
  private final YamlDocument config;
  private final ComponentLogger logger;
  private final FakeLister fakeLister;
  private final FakePlayerManager fakePlayerManager;
  private final UpdateChecker updateChecker;

  public static final String COMMAND_NAME = "rfp";

  private Command<? extends CommandSender> command;

  /**
   * Constructs or gets you a command with the {@link #COMMAND_NAME}.
   */
  public Command<? extends CommandSender> getCommand() {
    if (command != null) {
      return command;
    }
    command = manager.commandBuilder(COMMAND_NAME)
        .permission(new StringBuilder(PermUtils.PERMISSIONS_PREFIX).append("admin").toString())
        .commandDescription(CommandDescription.commandDescription(""))
        .literal("reload")
        .handler(ctx -> {
          try {
            config.reload();
            ctx.sender().sendMessage("Config reloaded.");
          } catch (IOException e) {
            ctx.sender().sendMessage("Something went wrong whilst config was reloading,"
                + "the error log is in your console!");
            logger.error("Error while config was reloading!", e);
          }
        })
        .literal("add")
        .required("string", stringParser())
        .handler(ctx -> {
          var fakePlayersNumber = fakeLister.getRawFakePlayers().size();
          var maxFakePlayers = config.getInt("max-fake-players");
          Integer numbered = Ints.tryParse(ctx.get("string"));
          if (numbered != null) {
            if ((numbered + fakePlayersNumber) > maxFakePlayers) {
              ctx.sender().sendMessage("Unable to add fake players, number"
                  + " of fake players exceeds the maximal number of fake players.");
            } else {
              fakePlayerManager.addNumber(numbered);
              ctx.sender().sendMessage("Added fake players number " + numbered + ".");
            }
          } else {
            if (fakePlayersNumber >= maxFakePlayers) {
              ctx.sender().sendMessage("Unable to add a fake player, number of "
                  + "fake players exceeds the maximal number of fake players.");
            } else if (fakeLister.getRawFakePlayersByName().containsKey(ctx.get("string"))) {
              ctx.sender().sendMessage("Fake player " + ctx.get("string") + " already exists!");
            } else {
              fakePlayerManager.add(ctx.get("string"));
              ctx.sender().sendMessage("Added fake player named " + ctx.get("string") + ".");
            }
          }
        })
        .literal("remove")
        .required("string", stringParser())
        .handler(ctx -> {
          if (((String) ctx.get("string")).equalsIgnoreCase("all")) {
            fakePlayerManager.removeAll();
          } else {
            Integer numbered = Ints.tryParse(ctx.get("string"));
            if (numbered != null) {
              fakePlayerManager.removeNumber(numbered);
              ctx.sender().sendMessage("Attempted to remove " + numbered + " fake players.");
            } else {
              if (fakeLister.getRawFakePlayersByName().containsKey(ctx.get("string"))) {
                fakePlayerManager.remove((String) ctx.get("string"));
                ctx.sender().sendMessage(ctx.get("string") + " fake player was removed.");
              } else {
                ctx.sender().sendMessage(ctx.get("string") + " no fake player with that name.");
              }
            }
          }
        })
        .literal("list")
        .handler(ctx -> {
          ctx.sender().sendMessage("There are %s of a max of %s fake players online:".formatted(
              fakeLister.getFakePlayers().size(),
              config.getInt("max-fake-players")
          ));
          fakeLister.getFakePlayersByName().keySet().forEach(ctx.sender()::sendMessage);
        })
        .literal("checkupdates")
        .handler(ctx -> {
          updateChecker.checkUpdates(ctx.sender());
        })
        .literal("setspawn")
        .handler(ctx -> {
          if (ctx.sender() instanceof Entity entity) {
            try {
              config.set("spawn-location", entity.getLocation());
              config.save();
              ctx.sender().sendMessage("Set spawn location of fake players to your"
                  + " current location.");
            } catch (IOException e) {
              ctx.sender().sendMessage("Something went wrong while setting a spawn location"
                  + " in the config, log is in the console!");
              logger.error("Error while setting spawn location in config!", e);
            }
          }
        })
        .build();
    return command;
  }
}
