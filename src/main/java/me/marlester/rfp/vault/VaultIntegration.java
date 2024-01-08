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

package me.marlester.rfp.vault;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import dev.dejvokep.boostedyaml.YamlDocument;
import java.util.concurrent.ThreadLocalRandom;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

/**
 * Vault integration - Vault support, permissions and groups.
 */
@RequiredArgsConstructor(onConstructor_ = {@Inject}, access = AccessLevel.PACKAGE)
@Singleton
public class VaultIntegration {

  @Named("config")
  private final YamlDocument config;

  private Object perms = null;

  /**
   * Setups Vault's permissions for this class.
   */
  public void setupPermissions() {
    if (!config.getBoolean("vault-integration.enable")
        || Bukkit.getPluginManager().getPlugin("Vault") == null) {
      return;
    }
    RegisteredServiceProvider<Permission> rsp = Bukkit.getServer()
        .getServicesManager().getRegistration(Permission.class);
    perms = rsp.getProvider();
  }

  /**
   * Checks if the vault integration is active.
   *
   * @return true if the vault integration is active, false if not.
   */
  public boolean isActive() {
    return perms != null;
  }

  /**
   * Gives fake player a random group chosen from the configuration file.
   *
   * @param player which player should receive the group
   */
  public void giveGroup(Player player) {
    var groups = config.getStringList("vault-integration.groups");
    if (groups.isEmpty()) {
      return;
    }
    var group = groups.get(ThreadLocalRandom.current().nextInt(groups.size()));
    ((Permission) perms).playerAddGroup(null, player, group);
  }

  /**
   * Gives fake player permissions from the configuration file.
   *
   * @param player which player should receive the permissions
   */
  public void givePermissions(Player player) {
    var permissions = config.getStringList("vault-integration.permissions");
    if (permissions.isEmpty()) {
      return;
    }
    for (String permission : permissions) {
      ((Permission) perms).playerAdd(null, player, permission);
    }
  }
}
