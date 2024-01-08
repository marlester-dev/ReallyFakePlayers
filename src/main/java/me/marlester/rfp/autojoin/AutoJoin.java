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

package me.marlester.rfp.autojoin;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import dev.dejvokep.boostedyaml.YamlDocument;
import java.util.concurrent.ThreadLocalRandom;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import me.marlester.rfp.ReallyFakePlayers;
import me.marlester.rfp.fakeplayers.FakePlayerManager;
import org.bukkit.Bukkit;

/**
 * Fake player auto join, automatically add fake players.
 */
@RequiredArgsConstructor(onConstructor_ = {@Inject}, access = AccessLevel.PACKAGE)
@Singleton
public class AutoJoin {

  private final ReallyFakePlayers pl;
  @Named("config")
  private final YamlDocument config;
  private final FakePlayerManager fakePlayerManager;

  /**
   * Start auto-adding fake players.
   */
  public void startAutoJoin() {
    if (!config.getBoolean("auto-join.enable")) {
      return;
    }
    int startupDelay = 20 * config.getInt("auto-join.startup-delay");
    var scheduler = Bukkit.getScheduler();
    scheduler.runTaskLater(pl, new Runnable() {
      @Override
      public void run() {
        fakePlayerManager.addNumber(1);
        int delay = 20 * ThreadLocalRandom.current().nextInt(
            config.getInt("auto-join.delay.min"),
            config.getInt("auto-join.delay.max") + 1
        );
        // Schedule the next execution(s)
        scheduler.runTaskLater(pl, this, delay);
      }
    }, startupDelay);
  }
}
