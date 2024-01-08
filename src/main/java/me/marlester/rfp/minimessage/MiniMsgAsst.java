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

package me.marlester.rfp.minimessage;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import io.github.miniplaceholders.api.Expansion;
import io.github.miniplaceholders.api.MiniPlaceholders;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

/**
 * Assists in deserializing messages with MiniMessage syntax and placeholders.
 * <p>
 * This class provides methods to deserialize strings using MiniMessage format. It supports
 * global, audience-specific, and relational placeholders, provided by the internal expansion.
 * </p>
 */
@RequiredArgsConstructor(onConstructor_ = {@Inject}, access = AccessLevel.PACKAGE)
@Singleton
public class MiniMsgAsst {

  private final MiniMessage miniMessage;
  @Named("internalPlaceholdersExpansion")
  private final Expansion internalExpansion;

  /**
   * Deserializes a string using MiniMessage format with global placeholders.
   *
   * @param input The string to be deserialized.
   * @return The deserialized {@link Component}.
   */
  public Component deserialize(String input) {
    return miniMessage.deserialize(input,
        MiniPlaceholders.getGlobalPlaceholders(),
        internalExpansion.globalPlaceholders()
    );
  }

  /**
   * Deserializes a string with audience-specific global placeholders.
   *
   * @param input The string to be deserialized.
   * @param placeholderAudience The audience for placeholders.
   * @return The deserialized {@link Component}.
   */
  public Component deserialize(String input, Audience placeholderAudience) {
    return miniMessage.deserialize(input,
        MiniPlaceholders.getAudienceGlobalPlaceholders(placeholderAudience),
        internalExpansion.audiencePlaceholders(placeholderAudience)
    );
  }

  /**
   * Deserializes a string with relational placeholders for two audiences.
   *
   * @param input The string to be deserialized.
   * @param placeholderAudience The primary audience for placeholders.
   * @param otherPlaceholderAudience The secondary audience for relational placeholders.
   * @return The deserialized {@link Component}.
   */
  public Component deserialize(String input, Audience placeholderAudience,
      Audience otherPlaceholderAudience) {
    return miniMessage.deserialize(input,
        MiniPlaceholders.getRelationalGlobalPlaceholders(
            placeholderAudience, otherPlaceholderAudience),
        internalExpansion.relationalPlaceholders(placeholderAudience, otherPlaceholderAudience)
    );
  }

  /**
   * Deserializes a string into plain text using global placeholders.
   *
   * @param input The string to be deserialized.
   * @return The deserialized string in plain text.
   */
  public String deserializeAsPlainText(String input) {
    return PlainTextComponentSerializer.plainText().serialize(deserialize(input));
  }

  /**
   * Deserializes a string into plain text with audience-specific placeholders.
   *
   * @param input The string to be deserialized.
   * @param placeholderAudience The audience for placeholders.
   * @return The deserialized string in plain text.
   */
  public String deserializeAsPlainText(String input, Audience placeholderAudience) {
    return PlainTextComponentSerializer.plainText().serialize(deserialize(input,
        placeholderAudience));
  }

  /**
   * Deserializes a string into plain text with relational placeholders for two audiences.
   *
   * @param input The string to be deserialized.
   * @param placeholderAudience The primary audience for placeholders.
   * @param otherPlaceholderAudience The secondary audience for relational placeholders.
   * @return The deserialized string in plain text.
   */
  public String deserializeAsPlainText(String input, Audience placeholderAudience,
      Audience otherPlaceholderAudience) {
    return PlainTextComponentSerializer.plainText().serialize(deserialize(input,
        placeholderAudience, otherPlaceholderAudience));
  }
}
