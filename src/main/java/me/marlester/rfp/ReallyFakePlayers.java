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
import io.github.miniplaceholders.api.Expansion;
import lombok.Getter;
import lombok.SneakyThrows;
import me.marlester.rfp.autojoin.AutoJoin;
import me.marlester.rfp.bytecodeedit.BytecodeEditingRegistrar;
import me.marlester.rfp.command.CommandsRegistrar;
import me.marlester.rfp.config.ConfigModule;
import me.marlester.rfp.config.RfpYamlDoc;
import me.marlester.rfp.config.RfpYamlDocFactory;
import me.marlester.rfp.fakeplayers.FakePlayerManager;
import me.marlester.rfp.fakeplayers.FakePlayersModule;
import me.marlester.rfp.listener.ListenersRegistrar;
import me.marlester.rfp.listener.fakeplayerloginlisteners.FakePlayerJoinListenerModule;
import me.marlester.rfp.placeholders.PlaceholdersModule;
import me.marlester.rfp.update.UpdateChecker;
import me.marlester.rfp.vault.VaultIntegration;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * The main class of this plugin. It creates the injector, loads up all the needed processes and
 * creates the config files.
 */
public class ReallyFakePlayers extends JavaPlugin {

  private Injector injector;
  @Getter
  private RfpYamlDoc confik;
  @Getter
  private RfpYamlDoc nameList;

  @SneakyThrows
  @Override
  public void onEnable() {
    injector = Guice.createInjector(
        new MainModule(this),
        new ConfigModule(),
        new FakePlayersModule(),
        new FakePlayerJoinListenerModule(),
        new PlaceholdersModule()
    );

    var configFactory = injector.getInstance(RfpYamlDocFactory.class);
    confik = configFactory.create("config.yml", true);
    nameList = configFactory.create("name-list.yml", false);
    injector.getInstance(BytecodeEditingRegistrar.class).registerBytecodeEditions();
    injector.getInstance(Key.get(Expansion.class, Names.named("placeholdersExpansion")))
        .register();
    injector.getInstance(VaultIntegration.class).setupPermissions();
    injector.getInstance(ListenersRegistrar.class).registerListeners();
    injector.getInstance(CommandsRegistrar.class).registerCommands();

    injector.getInstance(AutoJoin.class).startAutoJoin();
    Bukkit.getScheduler().runTaskLater(this, () -> {
      var config = injector.getInstance(Key.get(RfpYamlDoc.class, Names.named("config")));
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
