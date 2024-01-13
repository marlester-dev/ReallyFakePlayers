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

package me.marlester.rfp.update;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import dev.dejvokep.boostedyaml.YamlDocument;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.marlester.rfp.ReallyFakePlayers;
import me.marlester.rfp.util.VersionUtils;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * Class that has a single purpose for checking updates via curse api.
 * It doesn't check if config disabled update check on purpose,
 * so you should decide whether to do it first.
 */
@RequiredArgsConstructor(onConstructor_ = {@Inject}, access = AccessLevel.PACKAGE)
@Singleton
public class UpdateChecker {

  private final ReallyFakePlayers pl;
  @Named("config")
  private final YamlDocument config;
  private final ComponentLogger logger;

  // VDP, hoping that api doesn't change
  @Getter
  @Setter
  private static String defaultApiUrl = "https://api.curseforge.com/v1";
  // You can't use the api key below in your purposes.
  @Getter
  @Setter
  private static String defaultApiKey =
      "$2a$10$3JERPpWqsKFXMzXGqjYTPOpHIrz/V7rPB3S8Zw59jd.uTONbGnwbG";
  @Getter
  @Setter
  private static int defaultProjectId = 873451;

  /**
   * {@link #checkUpdates(CommandSender)} but the command sender
   * is {@link Bukkit#getConsoleSender()}.
   */
  public void checkUpdatesConsole() {
    checkUpdates(Bukkit.getConsoleSender());
  }

  /**
   * Checks for updates and reports to the specified CommandSender.
   *
   * @param sender The CommandSender to whom the result of the update check will be reported.
   *               This can be any command sender, including a player or the console.
   */
  public void checkUpdates(CommandSender sender) {
    String plName = pl.getName();
    try {
      String apiUrl = config.getOptionalString("update-check.curse-api-url")
          .orElse(defaultApiUrl);
      String apiKey = config.getOptionalString("update-check.curse-api-key")
          .orElse(defaultApiKey);
      int projectId = config.getOptionalInt("update-check.curse-project-id")
          .orElse(defaultProjectId);
      String mcVersion = Bukkit.getMinecraftVersion();

      /*
       * With this technic we get the latest file of server's minecraft version,
       * pageSize is controlling the amount of files on page, we set it to only 1,
       * so the page isn't too large and has exactly what we need.
       */
      URL obj = new URL("%s/mods/%s/files?gameVersion=%s&pageSize=1"
          .formatted(apiUrl, projectId, mcVersion));
      HttpURLConnection con = (HttpURLConnection) obj.openConnection();
      con.setRequestMethod("GET");
      con.setRequestProperty("x-api-key", apiKey);
      BufferedReader in = new BufferedReader(
          new InputStreamReader(con.getInputStream()));
      String inputLine;
      StringBuilder response = new StringBuilder();
      while ((inputLine = in.readLine()) != null) {
        response.append(inputLine);
      }
      in.close();

      JsonObject responseObject = JsonParser.parseString(response.toString()).getAsJsonObject();
      JsonArray dataArray = responseObject.getAsJsonArray("data");
      if (dataArray.isEmpty()) {
        sender.sendMessage(ChatColor.RED + "Error - page is empty! Due to this the update checker"
            + " couldn't find an update for " + plName + ".");
        return;
      }
      JsonObject fileObject = dataArray.get(0).getAsJsonObject();
      // get file's display name, should be in a name-version format
      String fileDisplayName = fileObject.get("displayName").getAsString().replace(".jar", "");

      var plMeta = pl.getPluginMeta();
      String plVersion = plMeta.getVersion();
      if (fileDisplayName.contains(plVersion)) {
        sender.sendMessage(ChatColor.GREEN + "You are running the latest version of " + plName
            + " for mcversion-" + mcVersion + "!");
      } else {
        // currently it only supports name-version
        String fileVersion = fileDisplayName.split("-")[1];
        // if plugin version is greater or equals file version, it was PROBABLY a mistake.
        if (VersionUtils.versionCompare(fileVersion, plVersion) <= 0) {
          sender.sendMessage(ChatColor.DARK_GREEN + "You are likely running the latest version of "
              + plName + " for mcversion-" + mcVersion + ".");
        } else {
          sender.sendMessage(ChatColor.LIGHT_PURPLE + "New version found for " + plName + ": "
              + fileDisplayName + ". Please install it from " + plName + "'s website: "
              + plMeta.getWebsite() + "!");
        }
      }
    } catch (Exception e) {
      // The error in update checker shouldn't halt the whole plugin
      sender.sendMessage("Error occurred while checking for " + plName
          + "'s updates! Please check console for error logs.");
      logger.warn("Error while checking for " + plName + "'s updates!", e);
    }
  }
}
