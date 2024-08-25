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

package me.marlester.rfp.placeholders;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.github.miniplaceholders.api.Expansion;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import me.marlester.rfp.faketools.FakeLister;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.Tag;
import org.bukkit.Bukkit;

/**
 * Class used to create a placeholders expansion for the plugin.
 */
@RequiredArgsConstructor(onConstructor_ = {@Inject}, access = AccessLevel.PACKAGE)
@Singleton
public class PlaceholdersExpansionCreator {

  private final FakeLister fakeLister;

  /**
   * Creates an Expansion with placeholders.
   *
   * @return Expansion object with defined placeholders.
   */
  public Expansion createExpansion() {
    return Expansion.builder("rfp")
        .globalPlaceholder("fakeplayers_count", (ctx, queue) -> {
          return Tag.selfClosingInserting(Component.text(fakeLister.getFakePlayers().size()));
        })
        .globalPlaceholder("rfp_player_count_no_fakeplayers", (ctx, queue) -> {
          int fakePlayersCount = fakeLister.getFakePlayers().size();
          int total = Bukkit.getOnlinePlayers().size();
          return Tag.selfClosingInserting(Component.text(total - fakePlayersCount));
        })
        .build();
  }
}
