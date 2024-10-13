package me.marlester.rfp.listener;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import me.marlester.rfp.faketools.FakeLister;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

@RequiredArgsConstructor(onConstructor_ = {@Inject}, access = AccessLevel.PACKAGE)
@Singleton
public class FakePlayerRespawnListener implements Listener {

  private final FakeLister fakeLister;
  private final FakePlayerJoinListener joinListener;

  @EventHandler
  public void onRespawn(PlayerRespawnEvent e) {
    var player = e.getPlayer();
    var fakePlayer = fakeLister.getRawFakePlayersByUuid().get(player.getUniqueId());
    if (fakePlayer == null) {
      return;
    }
    joinListener.onSpawn(player, true);
  }
}
