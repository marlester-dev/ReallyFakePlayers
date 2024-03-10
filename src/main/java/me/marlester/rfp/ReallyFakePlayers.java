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

package me.marlester.rfp;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;
import dev.dejvokep.boostedyaml.YamlDocument;
import io.github.miniplaceholders.api.Expansion;
import me.marlester.rfp.autojoin.AutoJoin;
import me.marlester.rfp.bytecodeedit.BytecodeEditingRegisterer;
import me.marlester.rfp.command.CommandsModule;
import me.marlester.rfp.command.CommandsRegisterer;
import me.marlester.rfp.config.ConfigsModule;
import me.marlester.rfp.config.ConfigsRegisterer;
import me.marlester.rfp.fakeplayers.FakePlayerManager;
import me.marlester.rfp.fakeplayers.FakePlayersModule;
import me.marlester.rfp.listener.ListenersRegisterer;
import me.marlester.rfp.listener.fakeplayerjoinlistener.FakePlayerJoinListenerModule;
import me.marlester.rfp.minimessage.MiniMessageModule;
import me.marlester.rfp.placeholders.PlaceholdersModule;
import me.marlester.rfp.update.UpdateChecker;
import me.marlester.rfp.vault.VaultIntegration;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Dear plugin's main class. It starts all processes of the plugin, but
 * generally prefers not to do anything by itself, it uses other classes for it.
 */
public final class ReallyFakePlayers extends JavaPlugin {

  /*
   * Important Information:
   * Please follow the Google Java Style Guide.
   * Version-Dependent code parts are labeled with a "VDP" comment
   * for an easier identification (not 100% reliable).
   */

  private Injector injector;

  @Override
  public void onEnable() {
    injector = Guice.createInjector(
        new MainModule(this),
        new ConfigsModule(),
        new FakePlayersModule(),
        new FakePlayerJoinListenerModule(),
        new PlaceholdersModule(),
        new MiniMessageModule(),
        new CommandsModule()
    );
    // Registering time
    injector.getInstance(ConfigsRegisterer.class).registerConfigs();
    injector.getInstance(BytecodeEditingRegisterer.class).registerBytecodeEditions();
    injector.getInstance(VaultIntegration.class).setupPermissions();
    injector.getInstance(ListenersRegisterer.class).registerListeners();
    injector.getInstance(CommandsRegisterer.class).registerCommands();
    injector.getInstance(Key.get(Expansion.class, Names.named("placeholdersExpansion")))
        .register();

    injector.getInstance(AutoJoin.class).startAutoJoin();
    Bukkit.getScheduler().runTaskLater(this, () -> {
      var config = injector.getInstance(Key.get(YamlDocument.class, Names.named("config")));
      if (config.getBoolean("update-check.on-startup")) {
        injector.getInstance(UpdateChecker.class).checkUpdatesConsole();
      }
    }, 1);
  }

  @Override
  public void onDisable() {
    injector.getInstance(FakePlayerManager.class).removeAll();
  }
}
