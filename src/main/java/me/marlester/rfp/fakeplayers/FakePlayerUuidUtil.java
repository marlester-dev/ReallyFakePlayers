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

package me.marlester.rfp.fakeplayers;

import java.nio.charset.StandardCharsets;
import java.util.UUID;
import lombok.experimental.UtilityClass;
import net.minecraft.core.UUIDUtil;

/**
 * Utility class with the purpose of creating distinct UUIDS for fakeplayers. Similar to
 * {@link UUIDUtil}.
 */
@UtilityClass
public class FakePlayerUuidUtil {

  /**
   * Will be used like ReallyFakePlayer:nickname in the creation of a UUID via bytes from the
   * string, since it uses set bytes, the UUID gotten is always the same as long as the nickname is
   * the same. It functions similar to minecraft's {@link UUIDUtil}, but gives different UUIDs.
   */
  public final String UUID_PREFIX_FAKE_PLAYER = "ReallyFakePlayer:";

  /**
   * Creates a UUID for a fake player. Different from existing minecraft offline/online UUIDs.
   *
   * @param nickname Name of a fake player
   * @return UUID for a fake player.
   */
  public UUID createFakePlayerUuid(String nickname) {
    return UUID.nameUUIDFromBytes((UUID_PREFIX_FAKE_PLAYER + nickname)
        .getBytes(StandardCharsets.UTF_8)
    );
  }
}
