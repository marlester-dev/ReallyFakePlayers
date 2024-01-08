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
import io.github.miniplaceholders.api.utils.TagsUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.minimessage.tag.Tag;
import org.bukkit.entity.Player;

/**
 * Class used to create an 'internal' placeholders expansion for the plugin.
 */
@RequiredArgsConstructor(onConstructor_ = {@Inject}, access = AccessLevel.PACKAGE)
@Singleton
public class IntlPlaceholdersExpansionCreator {

  /**
   * Creates an 'internal' Expansion with placeholders.
   *
   * @return Expansion object with defined placeholders.
   */
  public Expansion createExpansion() {
    return Expansion.builder("internal")
        .filter(Player.class)
        .audiencePlaceholder("player_name", (audience, queue, ctx) -> {
          return getPlayerNameTag(audience);
        })
        .relationalPlaceholder("1_player_name", (audience, otherAudience, queue, ctx) -> {
          return getPlayerNameTag(audience);
        })
        .relationalPlaceholder("2_player_name", (audience, otherAudience, queue, ctx) -> {
          return getPlayerNameTag(otherAudience);
        })
        .build();
  }

  static Tag getPlayerNameTag(Audience audience) {
    return TagsUtils.staticTag(((Player) audience).getName());
  }
}
