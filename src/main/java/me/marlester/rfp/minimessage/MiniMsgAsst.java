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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

/**
 * Assists in deserializing messages with MiniMessage syntax and placeholders.
 * <p>
 * This class provides methods to deserialize strings using MiniMessage format. It supports global,
 * audience-specific, and relational placeholders, provided by the internal expansion.
 * </p>
 */
@RequiredArgsConstructor(onConstructor_ = {@Inject}, access = AccessLevel.PACKAGE)
@Singleton
public class MiniMsgAsst {

  @Named("internalPlaceholdersExpansion")
  private final Expansion internalExpansion;

  /**
   * Deserializes a string using MiniMessage format with global placeholders.
   *
   * @param input The string to be deserialized.
   * @return The deserialized {@link Component}.
   */
  public Component deserialize(String input) {
    return MiniMessage.miniMessage().deserialize(input,
        MiniPlaceholders.getGlobalPlaceholders(),
        internalExpansion.globalPlaceholders()
    );
  }

  /**
   * Deserializes a string with audience-specific global placeholders.
   *
   * @param input               The string to be deserialized.
   * @param placeholderAudience The audience for placeholders.
   * @return The deserialized {@link Component}.
   */
  public Component deserialize(String input, Audience placeholderAudience) {
    return MiniMessage.miniMessage().deserialize(input,
        MiniPlaceholders.getAudienceGlobalPlaceholders(placeholderAudience),
        internalExpansion.audiencePlaceholders(placeholderAudience)
    );
  }

  /**
   * Deserializes a string with relational placeholders for two audiences.
   *
   * @param input                    The string to be deserialized.
   * @param placeholderAudience      The primary audience for placeholders.
   * @param otherPlaceholderAudience The secondary audience for relational placeholders.
   * @return The deserialized {@link Component}.
   */
  public Component deserialize(String input, Audience placeholderAudience,
      Audience otherPlaceholderAudience) {
    return MiniMessage.miniMessage().deserialize(input,
        MiniPlaceholders.getRelationalGlobalPlaceholders(
            placeholderAudience, otherPlaceholderAudience),
        internalExpansion.relationalPlaceholders(placeholderAudience, otherPlaceholderAudience)
    );
  }

  /**
   * Converts a string formatted in MiniMessage syntax to plain text using global placeholders. This
   * method first deserializes the input string to a {@link Component} with global placeholders
   * applied, and then serializes the {@link Component} back to a plain text string.
   *
   * @param input The MiniMessage formatted string to be processed.
   * @return The serialized string in plain text format, with global placeholders applied.
   */
  public String serializeAsPlainText(String input) {
    return PlainTextComponentSerializer.plainText().serialize(deserialize(input));
  }

  /**
   * Converts a string formatted in MiniMessage syntax to plain text using placeholders specific to
   * a given audience. This method first deserializes the input string to a {@link Component} with
   * audience-specific placeholders applied, and then serializes the {@link Component} back to a
   * plain text string.
   *
   * @param input               The MiniMessage formatted string to be processed.
   * @param placeholderAudience The audience for which placeholders are specifically tailored.
   * @return The serialized string in plain text format, with audience-specific placeholders
   *     applied.
   */
  public String serializeAsPlainText(String input, Audience placeholderAudience) {
    return PlainTextComponentSerializer.plainText()
        .serialize(deserialize(input, placeholderAudience));
  }

  /**
   * Converts a string formatted in MiniMessage syntax to plain text using relational placeholders
   * for two distinct audiences. This method first deserializes the input string to a
   * {@link Component} with relational placeholders applied for both specified audiences, and then
   * serializes the {@link Component} back to a plain text string.
   *
   * @param input                    The MiniMessage formatted string to be processed.
   * @param placeholderAudience      The primary audience for which placeholders are applied.
   * @param otherPlaceholderAudience The secondary audience for relational placeholders.
   * @return The serialized string in plain text format, with relational placeholders for both
   *     audiences applied.
   */
  public String serializeAsPlainText(String input, Audience placeholderAudience,
      Audience otherPlaceholderAudience) {
    return PlainTextComponentSerializer.plainText()
        .serialize(deserialize(input, placeholderAudience, otherPlaceholderAudience));
  }

  /**
   * Deserializes a list of strings formatted in MiniMessage syntax into a list of {@link Component}
   * objects using global placeholders. This method processes each string individually,
   * deserializing it to a {@link Component} with global placeholders applied.
   *
   * @param strings The list of MiniMessage formatted strings to be deserialized.
   * @return A list of {@link Component} objects, each representing the deserialized form of the
   *     input strings.
   */
  public List<Component> deserializeStringList(List<String> strings) {
    List<Component> componentList = new ArrayList<>();
    for (String s : strings) {
      componentList.add(deserialize(s));
    }
    return componentList;
  }

  /**
   * Deserializes a list of strings formatted in MiniMessage syntax into a list of {@link Component}
   * objects, using placeholders specific to a given audience. This method processes each string
   * individually, deserializing it to a {@link Component} with audience-specific placeholders
   * applied.
   *
   * @param strings             The list of MiniMessage formatted strings to be deserialized.
   * @param placeholderAudience The audience for which placeholders are specifically tailored.
   * @return A list of {@link Component} objects, each representing the deserialized form of the
   *     input strings with audience-specific placeholders.
   */
  public List<Component> deserializeStringList(List<String> strings, Audience placeholderAudience) {
    List<Component> componentList = new ArrayList<>();
    for (String s : strings) {
      componentList.add(deserialize(s, placeholderAudience));
    }
    return componentList;
  }

  /**
   * Deserializes a list of strings formatted in MiniMessage syntax into a list of {@link Component}
   * objects, using relational placeholders for two distinct audiences. This method processes each
   * string individually, deserializing it to a {@link Component} with relational placeholders
   * applied for both specified audiences.
   *
   * @param strings                  The list of MiniMessage formatted strings to be deserialized.
   * @param placeholderAudience      The primary audience for which placeholders are applied.
   * @param otherPlaceholderAudience The secondary audience for relational placeholders.
   * @return A list of {@link Component} objects, each representing the deserialized form of the
   *     input strings with relational placeholders for both audiences.
   */
  public List<Component> deserializeStringList(List<String> strings, Audience placeholderAudience,
      Audience otherPlaceholderAudience) {
    List<Component> componentList = new ArrayList<>();
    for (String s : strings) {
      componentList.add(deserialize(s, placeholderAudience, otherPlaceholderAudience));
    }
    return componentList;
  }

  /**
   * Serializes a list of strings formatted in MiniMessage syntax into plain text using global
   * placeholders. This method processes each string individually, serializing it to plain text with
   * global placeholders after deserialization.
   *
   * @param strings The list of MiniMessage formatted strings to be serialized.
   * @return A list of plain text strings, each representing the serialized form of the input
   *     strings with global placeholders.
   */
  public List<String> serializeStringListAsPlainText(List<String> strings) {
    List<String> serializedStrings = new ArrayList<>();
    for (String s : strings) {
      serializedStrings.add(serializeAsPlainText(s));
    }
    return serializedStrings;
  }

  /**
   * Serializes a list of strings formatted in MiniMessage syntax into plain text using placeholders
   * specific to a given audience. This method processes each string individually, serializing it to
   * plain text with audience-specific placeholders after deserialization.
   *
   * @param strings             The list of MiniMessage formatted strings to be serialized.
   * @param placeholderAudience The audience for which placeholders are specifically tailored.
   * @return A list of plain text strings, each representing the serialized form of the input
   *     strings with audience-specific placeholders.
   */
  public List<String> serializeStringListAsPlainText(List<String> strings,
      Audience placeholderAudience) {
    List<String> serializedStrings = new ArrayList<>();
    for (String s : strings) {
      serializedStrings.add(serializeAsPlainText(s, placeholderAudience));
    }
    return serializedStrings;
  }

  /**
   * Serializes a list of strings formatted in MiniMessage syntax into plain text using relational
   * placeholders for two distinct audiences. This method processes each string individually,
   * serializing it to plain text with relational placeholders after deserialization for both
   * specified audiences.
   *
   * @param strings                  The list of MiniMessage formatted strings to be serialized.
   * @param placeholderAudience      The primary audience for which placeholders are applied.
   * @param otherPlaceholderAudience The secondary audience for relational placeholders.
   * @return A list of plain text strings, each representing the serialized form of the input
   *     strings with relational placeholders for both audiences.
   */
  public List<String> serializeStringListAsPlainText(List<String> strings,
      Audience placeholderAudience, Audience otherPlaceholderAudience) {
    List<String> serializedStrings = new ArrayList<>();
    for (String s : strings) {
      serializedStrings.add(serializeAsPlainText(s, placeholderAudience, otherPlaceholderAudience));
    }
    return serializedStrings;
  }

  /**
   * Randomly selects a string from a list and deserializes it using MiniMessage format with global
   * placeholders.
   *
   * @param list The list of strings to choose from.
   * @return The deserialized {@link Component}.
   */
  public Component deserializeStringListRandom(List<String> list) {
    var randomString = list.get(ThreadLocalRandom.current().nextInt(list.size()));
    return deserialize(randomString);
  }

  /**
   * Randomly selects a string from a list and deserializes it with audience-specific global
   * placeholders.
   *
   * @param list                The list of strings to choose from.
   * @param placeholderAudience The audience for placeholders.
   * @return The deserialized {@link Component}.
   */
  public Component deserializeStringListRandom(List<String> list, Audience placeholderAudience) {
    var randomString = list.get(ThreadLocalRandom.current().nextInt(list.size()));
    return deserialize(randomString, placeholderAudience);
  }

  /**
   * Randomly selects a string from a list and deserializes it with relational placeholders for two
   * audiences.
   *
   * @param list                     The list of strings to choose from.
   * @param placeholderAudience      The primary audience for placeholders.
   * @param otherPlaceholderAudience The secondary audience for relational placeholders.
   * @return The deserialized {@link Component}.
   */
  public Component deserializeStringListRandom(List<String> list, Audience placeholderAudience,
      Audience otherPlaceholderAudience) {
    var randomString = list.get(ThreadLocalRandom.current().nextInt(list.size()));
    return deserialize(randomString, placeholderAudience, otherPlaceholderAudience);
  }

  /**
   * Randomly selects a string from a list and converts it to plain text using global placeholders.
   * This method first deserializes the randomly selected MiniMessage formatted string with global
   * placeholders, then serializes it back to plain text.
   *
   * @param list The list of MiniMessage formatted strings to choose from.
   * @return The serialized string in plain text format, with global placeholders applied.
   */
  public String serializeStringListRandomAsPlainText(List<String> list) {
    var randomString = list.get(ThreadLocalRandom.current().nextInt(list.size()));
    return serializeAsPlainText(randomString);
  }

  /**
   * Randomly selects a string from a list and converts it to plain text using placeholders specific
   * to a given audience. This method first deserializes the randomly selected MiniMessage formatted
   * string with audience-specific placeholders, then serializes it back to plain text.
   *
   * @param list                The list of MiniMessage formatted strings to choose from.
   * @param placeholderAudience The audience for which placeholders are specifically tailored.
   * @return The serialized string in plain text format, with audience-specific placeholders
   *     applied.
   */
  public String serializeStringListRandomAsPlainText(List<String> list,
      Audience placeholderAudience) {
    var randomString = list.get(ThreadLocalRandom.current().nextInt(list.size()));
    return serializeAsPlainText(randomString, placeholderAudience);
  }

  /**
   * Randomly selects a string from a list and converts it to plain text using relational
   * placeholders for two distinct audiences. This method first deserializes the randomly selected
   * MiniMessage formatted string with relational placeholders for both specified audiences, then
   * serializes it back to plain text.
   *
   * @param list                     The list of MiniMessage formatted strings to choose from.
   * @param placeholderAudience      The primary audience for which placeholders are applied.
   * @param otherPlaceholderAudience The secondary audience for relational placeholders.
   * @return The serialized string in plain text format, with relational placeholders for both
   *     audiences applied.
   */
  public String serializeStringListRandomAsPlainText(List<String> list,
      Audience placeholderAudience, Audience otherPlaceholderAudience) {
    var randomString = list.get(ThreadLocalRandom.current().nextInt(list.size()));
    return serializeAsPlainText(randomString, placeholderAudience, otherPlaceholderAudience);
  }
}
