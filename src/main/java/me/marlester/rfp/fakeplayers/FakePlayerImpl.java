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

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import com.google.inject.name.Named;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.marlester.mcbotlib.BotSession;
import me.marlester.mcbotlib.BotSettings;
import me.marlester.rfp.ReallyFakePlayers;
import me.marlester.rfp.config.RfpYamlDoc;
import me.marlester.rfp.faketools.FakeLister;
import me.marlester.rfp.listener.fakeplayerjoinlistener.FakePlayerJoinListener;
import me.marlester.rfp.listener.fakeplayerjoinlistener.FakePlayerJoinListenerFactory;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.geysermc.mcprotocollib.network.Session;
import org.geysermc.mcprotocollib.network.event.session.PacketSendingEvent;
import org.geysermc.mcprotocollib.network.event.session.SessionAdapter;
import org.geysermc.mcprotocollib.protocol.packet.login.serverbound.ServerboundHelloPacket;

@RequiredArgsConstructor(onConstructor_ = {@AssistedInject}, access = AccessLevel.PACKAGE)
class FakePlayerImpl implements FakePlayer {

  private final FakeLister fakeLister;
  @Named("config")
  private final RfpYamlDoc config;
  private final ReallyFakePlayers pl;
  private final FakePlayerJoinListenerFactory joinListenerFactory;
  private final ComponentLogger logger;

  @Getter
  @Assisted
  private final String name;
  @Getter
  private UUID uuid;
  @Getter
  private UUID key;
  @Getter
  private BotSession client;
  @Getter
  @Setter
  private Player player;
  @Getter
  private boolean removed;

  @Setter
  private FakePlayerJoinListener joinListener;

  public void join() {
    if (removed) {
      return;
    }
    // LoginListenerAsm.java takes part in applying this uuid further
    uuid = FakePlayerUuidUtil.createFakePlayerUuid(name);
    fakeLister.getRawFakePlayers().add(this);
    fakeLister.getRawFakePlayersByName().put(name, this);
    fakeLister.getRawFakePlayersByUuid().put(uuid, this);
    var fakePlayerUuidsByKey = fakeLister.getFakePlayerUuidsByKey();
    do {
      // Ain't no way they can crack this key
      key = UUID.randomUUID();
    } while (fakePlayerUuidsByKey.containsKey(key));
    fakePlayerUuidsByKey.put(key, uuid);
    String host = config.getOptionalString("join-ip").orElse("localhost");
    int port = config.getOptionalInt("join-port").orElse(Bukkit.getPort());
    client = new BotSession(name, host, port, BotSettings.builder()
        .renderDistance(config.getInt("render-distance"))
        .build());
    client.addListener(new SessionAdapter() {
      @Override
      public void packetSending(PacketSendingEvent event) {
        var packet = event.getPacket();
        if (packet instanceof ServerboundHelloPacket helloPacket) {
          event.setPacket(helloPacket.withProfileId(key));
        }
      }
    });
    client.connect();
    joinListener = joinListenerFactory.create(this);
    joinListener.startListening();
    Bukkit.getScheduler().runTaskLater(pl, () -> {
      if (!removed && player == null) {
        logger.error("Fake player " + name
            + " doesn't seem to show any life signs after ~30 seconds,"
            + " meaning he didn't join/quit correctly! Aborting!");
        remove();
      }
    }, 30 * 20);
  }

  public void remove() {
    if (removed) {
      return;
    }
    if (joinListener != null) {
      joinListener.stopListening();
      joinListener = null;
    }
    if (!Bukkit.getServer().isStopping()) {
      client.disconnect("Removed");
    }
    fakeLister.removeFakePlayerFromAllMaps(this);
    removed = true;
  }
}
