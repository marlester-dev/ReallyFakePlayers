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
import net.minecraft.network.protocol.login.ServerboundHelloPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;
import xyz.jpenilla.reflectionremapper.ReflectionRemapper;

/**
 * Utility class for remapping various method names and field names.
 *
 * <p>This class leverages ReflectionRemapper to dynamically remap method and field names used in
 * the PaperMC environment. It is designed to adapt to obfuscated or reobfuscated naming schemes
 * often encountered in Minecraft server development.
 */
@UtilityClass
public class RemapUtil {

  /**
   * The remapped name of the 'handleHello' method in ServerLoginPacketListenerImpl.
   */
  public final String HANDLE_HELLO_METHOD_NAME;

  /**
   * The remapped name of the 'usesAuthentication' method in MinecraftServer.
   */
  public final String USES_AUTH_METHOD_NAME;

  /**
   * The remapped name of the 'profileId' method in ServerboundHelloPacket.
   */
  public final String PROFILE_ID_METHOD_NAME;

  /**
   * The remapped name of the 'connection' field in ServerLoginPacketListenerImpl.
   */
  public final String CONNECTION_FIELD_NAME;

  static {
    final ReflectionRemapper reflectionRemapper = ReflectionRemapper.forReobfMappingsInPaperJar();
    HANDLE_HELLO_METHOD_NAME = reflectionRemapper.remapMethodName(
        ServerLoginPacketListenerImpl.class,
        "handleHello",
        ServerboundHelloPacket.class
    );
    USES_AUTH_METHOD_NAME = reflectionRemapper.remapMethodName(
        MinecraftServer.class,
        "usesAuthentication"
    );
    PROFILE_ID_METHOD_NAME = reflectionRemapper.remapMethodName(
        ServerboundHelloPacket.class,
        "profileId"
    );
    CONNECTION_FIELD_NAME = reflectionRemapper.remapFieldName(
        ServerLoginPacketListenerImpl.class,
        "connection"
    );
  }
}

