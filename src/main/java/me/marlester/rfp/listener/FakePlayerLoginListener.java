package me.marlester.rfp.listener;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import me.marlester.rfp.config.RfpYamlDoc;
import me.marlester.rfp.faketools.FakeLister;
import me.marlester.rfp.vault.VaultIntegration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

@RequiredArgsConstructor(onConstructor_ = {@Inject}, access = AccessLevel.PACKAGE)
@Singleton
public class FakePlayerLoginListener implements Listener {

  private final FakeLister fakeLister;
  private final VaultIntegration vaultIntegration;

  @EventHandler(priority = EventPriority.LOW)
  public void onLogin(PlayerLoginEvent e) {
    if (vaultIntegration.isActive() == false) {
      return;
    }
    var player = e.getPlayer();
    var uuid = player.getUniqueId();
    var fakePlayer = fakeLister.getRawFakePlayersByUuid().get(uuid);
    if (fakePlayer == null) {
      return;
    }
    vaultIntegration.giveGroup(player);
    vaultIntegration.givePermissions(player);
  }
}
