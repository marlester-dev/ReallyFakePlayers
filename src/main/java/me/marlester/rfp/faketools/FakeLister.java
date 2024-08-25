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

package me.marlester.rfp.faketools;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;
import lombok.Getter;
import me.marlester.rfp.bytecodeedit.SecretClasser;
import me.marlester.rfp.fakeplayers.FakePlayer;

/**
 * This class contains all maps, lists, etc. containing info of fakeplayers.
 */
@Singleton
@Getter
public class FakeLister {

  @Inject
  FakeLister(SecretClasser secretClasser) {
    fakePlayerUuidsByKey = secretClasser.getFakePlayerUuidsByKey();
  }

  /**
   * <h3>What's the difference between the raw and usual fake player?</h3>
   * - Usual fake players are fully initialized. - Raw fake players are not fully initialized, they
   * lack in-game appearance and some nullable fields are not available in their object.<br> Raw
   * fake players are also added IMMEDIATELY to raw fake player maps on their creation. Generally,
   * for all (Craft)Player object related stuff use usual, otherwise think.
   * <br>
   * <br>
   * A simple raw fake players list.<br> ⚠ Fakeplayer (data) is added here immediately on fakeplayer
   * creation, you likely don't want it. ⚠<br>
   */
  private final LinkedList<FakePlayer> rawFakePlayers = new LinkedList<>();

  /**
   * <h3>What's the difference between the raw and usual fake player?</h3>
   * - Usual fake players are fully initialized. - Raw fake players are not fully initialized, they
   * lack in-game appearance and some nullable fields are not available in their object.<br> Raw
   * fake players are also added IMMEDIATELY to raw fake player maps on their creation. Generally,
   * for all (Craft)Player object related stuff use usual, otherwise think.
   * <br>
   * <br>
   * Key - fakeplayer's name, a string.<br> Value - fakeplayer's FakePlayer object.<br>
   * <br>
   * ⚠ Fakeplayer (data) is added here immediately on fakeplayer creation, you likely don't want it.
   * ⚠<br>
   */
  private final Map<String, FakePlayer> rawFakePlayersByName = new HashMap<>();

  /**
   * <h3>What's the difference between the raw and usual fake player?</h3>
   * - Usual fake players are fully initialized. - Raw fake players are not fully initialized, they
   * lack in-game appearance and some nullable fields are not available in their object.<br> Raw
   * fake players are also added IMMEDIATELY to raw fake player maps on their creation. Generally,
   * for all (Craft)Player object related stuff use usual, otherwise think.
   * <br>
   * <br>
   * Key - fakeplayer's uuid, a UUID object.<br> Value - fakeplayer's FakePlayer object.<br>
   * <br>
   * ⚠ Fakeplayer (data) is added here immediately on fakeplayer creation, you likely don't want it.
   * ⚠<br>
   */
  private final Map<UUID, FakePlayer> rawFakePlayersByUuid = new HashMap<>();

  /**
   * A simple fake player list.
   * <br><br>
   * Fakeplayer (data) is added here when they are fully initialized, so with delay.<br>
   */
  private final LinkedList<FakePlayer> fakePlayers = new LinkedList<>();

  /**
   * Key - fakeplayer's name, a string.<br> Value - fakeplayer's FakePlayer object.<br>
   * <br>
   * Fakeplayer (data) is added here when they are fully initialized, so with delay.<br>
   */
  private final Map<String, FakePlayer> fakePlayersByName = new HashMap<>();

  /**
   * Key - fakeplayer's uuid, a UUID object.<br> Value - fakeplayer's FakePlayer object.<br>
   * <br>
   * Fakeplayer (data) is added here when they are fully initialized, so with delay.<br>
   */
  private final Map<UUID, FakePlayer> fakePlayersByUuid = new HashMap<>();

  /**
   * Key - fakeplayer's key, a UUID object.<br> Value - fakeplayer's uuid, a UUID object.<br>
   * <br>
   * ⚠ Managed by the secret class ⚠<br> ⚠ Fake players' data gets added and deleted from here
   * constantly ⚠<br> ⚠ Fakeplayer (data) is added here immediately on fakeplayer creation, you
   * likely don't want it. ⚠<br>
   */
  private final Map<UUID, UUID> fakePlayerUuidsByKey;

  /**
   * Removes fakeplayer from all maps, lists, etc.
   *
   * @param fakePlayer fakeplayer that needs to be removed.
   */
  public void removeFakePlayerFromAllMaps(FakePlayer fakePlayer) {
    String name = fakePlayer.getName();
    var uuid = fakePlayer.getUuid();

    rawFakePlayers.remove(fakePlayer);
    rawFakePlayersByName.remove(name);
    rawFakePlayersByUuid.remove(uuid);
    fakePlayers.remove(fakePlayer);
    fakePlayersByName.remove(name);
    fakePlayersByUuid.remove(uuid);
    fakePlayerUuidsByKey.remove(fakePlayer.getKey());
  }

  /**
   * ⚠ FAKE PLAYERS MAY BE NOT FULLY INITIALIZED AT THE MOMENT OF A CHECK ⚠<br> Checks if online
   * fake player has a given uuid.
   *
   * @param uuid uuid you want to try,
   * @return true if found online fake player with that name, false if not.
   */
  public boolean isFakePlayer(UUID uuid) {
    return rawFakePlayersByUuid.containsKey(uuid);
  }
}
