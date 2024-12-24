package me.marlester.rfp.listener;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import me.marlester.rfp.faketools.FakeLister;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;

/**
 * Prevents original player from being kicked wehn fakeplayer under the same name joins.
 */
@RequiredArgsConstructor(onConstructor_ = {@Inject}, access = AccessLevel.PACKAGE)
@Singleton
public class PlayerKickListener implements Listener {

  private final FakeLister fakeLister;

  @EventHandler
  public void onPlayerKick(PlayerKickEvent e) {
    if (!e.isCancelled() && e.getCause() == PlayerKickEvent.Cause.DUPLICATE_LOGIN
          && fakeLister.getRawFakePlayersByName().containsKey(e.getPlayer().getName())) {
      e.setCancelled(true);
    }
  }
}
