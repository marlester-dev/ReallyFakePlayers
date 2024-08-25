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

package me.marlester.rfp.config;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.dvs.versioning.BasicVersioning;
import dev.dejvokep.boostedyaml.settings.dumper.DumperSettings;
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings;
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings;
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings;
import dev.dejvokep.boostedyaml.spigot.SpigotSerializer;
import java.io.File;
import java.io.IOException;
import java.util.List;
import me.marlester.rfp.ReallyFakePlayers;
import me.marlester.rfp.minimessage.MiniMsgAsst;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;

public class RfpYamlDoc extends YamlDocument {

  private final MiniMsgAsst miniMsgAsst;

  @AssistedInject
  RfpYamlDoc(@Assisted String name, @Assisted boolean update,
      MiniMsgAsst miniMsgAsst, ReallyFakePlayers pl) throws IOException {
    super(
        new File(pl.getDataFolder(), name),
        pl.getResource(name),
        GeneralSettings.builder().setUseDefaults(false)
            .setSerializer(SpigotSerializer.getInstance()).build(),
        update ? LoaderSettings.builder().setAutoUpdate(true).build() : LoaderSettings.DEFAULT,
        DumperSettings.DEFAULT,
        update ? UpdaterSettings.builder().setVersioning(new BasicVersioning("config-version"))
            .build() : UpdaterSettings.DEFAULT
    );
    this.miniMsgAsst = miniMsgAsst;
  }

  /**
   * Retrieves and deserializes a configuration value specified by a route into a {@link Component}
   * using global placeholders. This method uses the MiniMessage format to deserialize the string
   * obtained from the configuration.
   *
   * @param route The configuration path that specifies the location of the string to be
   *              deserialized.
   * @return The deserialized {@link Component} with global placeholders applied.
   */
  public Component getComponent(String route) {
    return miniMsgAsst.deserialize(getString(route));
  }

  /**
   * Retrieves and deserializes a configuration value specified by a route into a {@link Component}
   * using placeholders specific to a given audience. This method uses the MiniMessage format to
   * deserialize the string obtained from the configuration with audience-specific placeholders.
   *
   * @param route               The configuration path that specifies the location of the string to
   *                            be deserialized.
   * @param placeholderAudience The audience for which placeholders are specifically tailored.
   * @return The deserialized {@link Component} with audience-specific placeholders applied.
   */
  public Component getComponent(String route, Audience placeholderAudience) {
    return miniMsgAsst.deserialize(getString(route), placeholderAudience);
  }

  /**
   * Retrieves and deserializes a configuration value specified by a route into a {@link Component}
   * using relational placeholders for two distinct audiences. This method uses the MiniMessage
   * format to deserialize the string obtained from the configuration with relational placeholders
   * applied for both specified audiences.
   *
   * @param route                    The configuration path that specifies the location of the
   *                                 string to be deserialized.
   * @param placeholderAudience      The primary audience for which placeholders are applied.
   * @param otherPlaceholderAudience The secondary audience for relational placeholders.
   * @return The deserialized {@link Component} with relational placeholders for both audiences
   *     applied.
   */
  public Component getComponent(String route, Audience placeholderAudience,
      Audience otherPlaceholderAudience) {
    return miniMsgAsst.deserialize(getString(route), placeholderAudience,
        otherPlaceholderAudience);
  }

  /**
   * Retrieves and serializes a configuration value specified by a route into plain text using
   * global placeholders. This method uses the MiniMessage format to serialize the string obtained
   * from the configuration after deserializing it with global placeholders.
   *
   * @param route The configuration path that specifies the location of the string to be
   *              serialized.
   * @return The serialized string in plain text format, with global placeholders applied.
   */
  public String getSerializedString(String route) {
    return miniMsgAsst.serializeAsPlainText(route);
  }

  /**
   * Retrieves and serializes a configuration value specified by a route into plain text using
   * placeholders specific to a given audience. This method uses the MiniMessage format to serialize
   * the string obtained from the configuration after deserializing it with audience-specific
   * placeholders.
   *
   * @param route               The configuration path that specifies the location of the string to
   *                            be serialized.
   * @param placeholderAudience The audience for which placeholders are specifically tailored.
   * @return The serialized string in plain text format, with audience-specific placeholders
   *     applied.
   */
  public String getSerializedString(String route, Audience placeholderAudience) {
    return miniMsgAsst.serializeAsPlainText(route, placeholderAudience);
  }

  /**
   * Retrieves and serializes a configuration value specified by a route into plain text using
   * relational placeholders for two distinct audiences. This method uses the MiniMessage format to
   * serialize the string obtained from the configuration after deserializing it with relational
   * placeholders applied for both specified audiences.
   *
   * @param route                    The configuration path that specifies the location of the
   *                                 string to be serialized.
   * @param placeholderAudience      The primary audience for which placeholders are applied.
   * @param otherPlaceholderAudience The secondary audience for relational placeholders.
   * @return The serialized string in plain text format, with relational placeholders for both
   *     audiences applied.
   */
  public String getSerializedString(String route, Audience placeholderAudience,
      Audience otherPlaceholderAudience) {
    return miniMsgAsst.serializeAsPlainText(route, placeholderAudience, otherPlaceholderAudience);
  }

  /**
   * Retrieves and deserializes a list of strings specified by a route in the configuration into a
   * list of {@link Component} objects using global placeholders. This method fetches the strings
   * from the configuration, then processes each using the MiniMessage format with global
   * placeholders.
   *
   * @param route The configuration path that specifies the location of the list of MiniMessage
   *              formatted strings.
   * @return A list of {@link Component} objects, each representing the deserialized form of the
   *     input strings with global placeholders.
   */
  public List<Component> getComponentList(String route) {
    List<String> stringList = getStringList(route);
    return miniMsgAsst.deserializeStringList(stringList);
  }

  /**
   * Retrieves and deserializes a list of strings specified by a route in the configuration into a
   * list of {@link Component} objects using placeholders specific to a given audience. This method
   * fetches the strings from the configuration, then processes each using the MiniMessage format
   * with audience-specific placeholders.
   *
   * @param route               The configuration path that specifies the location of the list of
   *                            MiniMessage formatted strings.
   * @param placeholderAudience The audience for which placeholders are specifically tailored.
   * @return A list of {@link Component} objects, each representing the deserialized form of the
   *     input strings with audience-specific placeholders.
   */
  public List<Component> getComponentList(String route, Audience placeholderAudience) {
    List<String> stringList = getStringList(route);
    return miniMsgAsst.deserializeStringList(stringList, placeholderAudience);
  }

  /**
   * Retrieves and deserializes a list of strings specified by a route in the configuration into a
   * list of {@link Component} objects using relational placeholders for two distinct audiences.
   * This method fetches the strings from the configuration, then processes each using the
   * MiniMessage format with relational placeholders applied for both specified audiences.
   *
   * @param route                    The configuration path that specifies the location of the list
   *                                 of MiniMessage formatted strings.
   * @param placeholderAudience      The primary audience for which placeholders are applied.
   * @param otherPlaceholderAudience The secondary audience for relational placeholders.
   * @return A list of {@link Component} objects, each representing the deserialized form of the
   *     input strings with relational placeholders for both audiences.
   */
  public List<Component> getComponentList(String route, Audience placeholderAudience,
      Audience otherPlaceholderAudience) {
    List<String> stringList = getStringList(route);
    return miniMsgAsst.deserializeStringList(stringList, placeholderAudience,
        otherPlaceholderAudience);
  }

  /**
   * Retrieves and serializes a list of strings specified by a route in the configuration into plain
   * text using global placeholders. Each string is first deserialized using global placeholders and
   * then serialized back to plain text.
   *
   * @param route The configuration path that specifies the location of the list of MiniMessage
   *              formatted strings.
   * @return A list of plain text strings, each representing the serialized form of the strings from
   *     the configuration with global placeholders.
   */
  public List<String> getSerializedStringList(String route) {
    return miniMsgAsst.serializeStringListAsPlainText(getStringList(route));
  }

  /**
   * Retrieves and serializes a list of strings specified by a route in the configuration into plain
   * text using placeholders specific to a given audience. Each string is first deserialized with
   * audience-specific placeholders and then serialized back to plain text.
   *
   * @param route               The configuration path that specifies the location of the list of
   *                            MiniMessage formatted strings.
   * @param placeholderAudience The audience for which placeholders are specifically tailored.
   * @return A list of plain text strings, each representing the serialized form of the strings from
   *     the configuration with audience-specific placeholders.
   */
  public List<String> getSerializedStringList(String route, Audience placeholderAudience) {
    return miniMsgAsst.serializeStringListAsPlainText(getStringList(route), placeholderAudience);
  }

  /**
   * Retrieves and serializes a list of strings specified by a route in the configuration into plain
   * text using relational placeholders for two distinct audiences. Each string is first
   * deserialized with relational placeholders for both specified audiences and then serialized back
   * to plain text.
   *
   * @param route                    The configuration path that specifies the location of the list
   *                                 of MiniMessage formatted strings.
   * @param placeholderAudience      The primary audience for which placeholders are applied.
   * @param otherPlaceholderAudience The secondary audience for relational placeholders.
   * @return A list of plain text strings, each representing the serialized form of the strings from
   *     the configuration with relational placeholders for both audiences.
   */
  public List<String> getSerializedStringList(String route, Audience placeholderAudience,
      Audience otherPlaceholderAudience) {
    return miniMsgAsst.serializeStringListAsPlainText(getStringList(route), placeholderAudience,
        otherPlaceholderAudience);
  }
}
