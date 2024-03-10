package me.marlester.rfp.command;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import me.marlester.rfp.ReallyFakePlayers;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.SenderMapper;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.paper.PaperCommandManager;

public class CommandsModule extends AbstractModule {

  @Provides
  @Singleton
  PaperCommandManager<CommandSender> provideCommandManager(ReallyFakePlayers pl) {
    return new PaperCommandManager<>(
        pl,
        ExecutionCoordinator.simpleCoordinator(),
        SenderMapper.identity()
    );
  }
}
