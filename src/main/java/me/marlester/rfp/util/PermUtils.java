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

package me.marlester.rfp.util;

import lombok.experimental.UtilityClass;
import org.bukkit.command.CommandSender;

/**
 * Utils related to permissions.
 */
@UtilityClass
public class PermUtils {

  /**
   * Prefix used for all permissions of this plugin.
   */
  public final String PERMISSIONS_PREFIX = "rfp.";

  /**
   * Checks if the given CommandSender has a specific permission. This method appends the predefined
   * plugin permission prefix to the permission key before checking the sender's permissions.
   * Additionally, it checks if the sender is an operator (OP) in the game.
   *
   * @param permissionKey The specific permission key to be checked, without the plugin prefix.
   * @param sender        The CommandSender (player, console, command block, etc.) whose permission
   *                      is being checked.
   * @return {@code true} if the sender has the specified permission or is an operator, otherwise
   *     {@code false}.
   */
  public boolean hasPermission(String permissionKey, CommandSender sender) {
    return sender.hasPermission(PERMISSIONS_PREFIX + permissionKey) || sender.isOp();
  }
}
