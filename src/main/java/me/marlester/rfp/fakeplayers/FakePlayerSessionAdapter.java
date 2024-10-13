package me.marlester.rfp.fakeplayers;

import static org.geysermc.mcprotocollib.protocol.data.game.ClientCommand.RESPAWN;

import java.util.UUID;
import org.geysermc.mcprotocollib.network.Session;
import org.geysermc.mcprotocollib.network.event.session.PacketSendingEvent;
import org.geysermc.mcprotocollib.network.event.session.SessionAdapter;
import org.geysermc.mcprotocollib.network.packet.Packet;
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.entity.player.ClientboundSetHealthPacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.serverbound.ServerboundClientCommandPacket;
import org.geysermc.mcprotocollib.protocol.packet.login.serverbound.ServerboundHelloPacket;

public class FakePlayerSessionAdapter extends SessionAdapter {

  private final FakePlayer fakePlayer;
  private final UUID key;

  FakePlayerSessionAdapter(FakePlayer fakePlayer, UUID key) {
    this.fakePlayer = fakePlayer;
    this.key = key;
  }

  @Override
  public void packetSending(PacketSendingEvent event) {
    var packet = event.getPacket();
    if (packet instanceof ServerboundHelloPacket helloPacket) {
      event.setPacket(helloPacket.withProfileId(key));
    }
  }

  @Override
  public void packetReceived(Session session, Packet packet) {
    if (packet instanceof ClientboundSetHealthPacket healthPacket) {
      if (healthPacket.getHealth() < 1) {
        // respawning
        session.send(new ServerboundClientCommandPacket(RESPAWN));
      }
    }
  }
}
