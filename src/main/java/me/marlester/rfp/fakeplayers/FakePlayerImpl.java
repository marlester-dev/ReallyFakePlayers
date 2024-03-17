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

import com.github.steveice10.mc.protocol.MinecraftProtocol;
import com.github.steveice10.mc.protocol.data.game.ResourcePackStatus;
import com.github.steveice10.mc.protocol.data.game.entity.player.HandPreference;
import com.github.steveice10.mc.protocol.data.game.setting.ChatVisibility;
import com.github.steveice10.mc.protocol.data.game.setting.SkinPart;
import com.github.steveice10.mc.protocol.packet.common.clientbound.ClientboundResourcePackPushPacket;
import com.github.steveice10.mc.protocol.packet.common.serverbound.ServerboundClientInformationPacket;
import com.github.steveice10.mc.protocol.packet.common.serverbound.ServerboundResourcePackPacket;
import com.github.steveice10.mc.protocol.packet.login.serverbound.ServerboundHelloPacket;
import com.github.steveice10.mc.protocol.packet.login.serverbound.ServerboundLoginAcknowledgedPacket;
import com.github.steveice10.packetlib.Session;
import com.github.steveice10.packetlib.event.session.DisconnectedEvent;
import com.github.steveice10.packetlib.event.session.PacketSendingEvent;
import com.github.steveice10.packetlib.event.session.SessionAdapter;
import com.github.steveice10.packetlib.packet.Packet;
import com.github.steveice10.packetlib.tcp.TcpClientSession;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import com.google.inject.name.Named;
import dev.dejvokep.boostedyaml.YamlDocument;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.marlester.rfp.ReallyFakePlayers;
import me.marlester.rfp.faketools.FakeLister;
import me.marlester.rfp.listener.fakeplayerjoinlistener.FakePlayerJoinListener;
import me.marlester.rfp.listener.fakeplayerjoinlistener.FakePlayerJoinListenerFactory;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@RequiredArgsConstructor(onConstructor_ = {@AssistedInject}, access = AccessLevel.PACKAGE)
class FakePlayerImpl implements FakePlayer {

  private final FakeLister fakeLister;
  @Named("config")
  private final YamlDocument config;
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
  private Session client;
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
    var protocol = new MinecraftProtocol(name);
    client = new TcpClientSession(host, port, protocol);
    client.addListener(new SessionAdapter() {
      @Override
      public void packetSending(PacketSendingEvent event) {
        var packet = event.getPacket();
        if (packet instanceof ServerboundHelloPacket helloPacket) {
          event.setPacket(helloPacket.withProfileId(key));
        }
      }

      // TODO move all of this outta here
      @Override
      public void packetReceived(Session session, Packet packet) {
        if (packet instanceof ClientboundResourcePackPushPacket rpPacket) {
          if (!isValidResourcePackUrl(rpPacket.getUrl())) {
            session.send(new ServerboundResourcePackPacket(rpPacket.getId(),
                ResourcePackStatus.INVALID_URL));
            return;
          }

          session.send(new ServerboundResourcePackPacket(rpPacket.getId(),
              ResourcePackStatus.ACCEPTED));
          session.send(new ServerboundResourcePackPacket(rpPacket.getId(),
              ResourcePackStatus.DOWNLOADED));
          session.send(new ServerboundResourcePackPacket(rpPacket.getId(),
              ResourcePackStatus.SUCCESSFULLY_LOADED));
        }
      }

      private boolean isValidResourcePackUrl(String url) {
        try {
          var protocol = URI.create(url).toURL().getProtocol();
          return "http".equals(protocol) || "https".equals(protocol);
        } catch (MalformedURLException var3) {
          return false;
        }
      }

      /* If you wonder why we don't remove the fake player here, it's because it's fundamentally
       * unstable to do so here, instead it happens in the FakePlayerQuitListener.java */
      @Override
      public void disconnected(DisconnectedEvent event) {
        var cause = event.getCause();
        if (cause != null) {
          logger.warn("Fake player " + name + " disconnected with an error!", cause);
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
