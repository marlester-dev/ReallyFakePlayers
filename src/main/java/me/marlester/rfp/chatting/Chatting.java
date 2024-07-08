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

package me.marlester.rfp.chatting;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import dev.dejvokep.boostedyaml.YamlDocument;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import me.marlester.rfp.ReallyFakePlayers;
import me.marlester.rfp.fakeplayers.FakePlayer;
import me.marlester.rfp.minimessage.MiniMsgAsst;
import org.bukkit.Bukkit;

/**
 * With this class fake players can automatically chat.
 */
@RequiredArgsConstructor(onConstructor_ = {@Inject}, access = AccessLevel.PACKAGE)
@Singleton
public class Chatting {

  @Named("config")
  private final YamlDocument config;
  private final ReallyFakePlayers pl;
  private final MiniMsgAsst miniMsgAsst;

  private final List<FakePlayer> chatters = new ArrayList<>();

  /**
   * Forces the fake player to continuously chat if configured so.
   *
   * @param fakePlayer fake player that you want to force to chat
   */
  public void startChatting(FakePlayer fakePlayer) {
    if (!config.getBoolean("chatting.enable")) {
      return;
    }
    if (chatters.size() >= config.getInt("chatting.max-chatters")) {
      return;
    }
    AtomicBoolean firstRun = new AtomicBoolean(true);
    int firstDelay = 20 * ThreadLocalRandom.current().nextInt(
        config.getInt("chatting.first-delay.min"),
        config.getInt("chatting.first-delay.max") + 1
    );
    var scheduler = Bukkit.getScheduler();
    scheduler.runTaskLater(pl, new Runnable() {
      @Override
      public void run() {
        if (fakePlayer.isRemoved()) {
          return;
        }
        chatRandomMessage(fakePlayer, firstRun.get());
        chatters.add(fakePlayer);
        scheduler.runTaskLater(pl, () -> {
          chatters.remove(fakePlayer);
        }, 20L * config.getInt("chatting.period"));
        int delay = 20 * ThreadLocalRandom.current().nextInt(
            config.getInt("chatting.delay.min"),
            config.getInt("chatting.delay.max") + 1
        );

        firstRun.compareAndSet(true, false);
        // Schedule the next execution(s)
        scheduler.runTaskLater(pl, this, delay);
      }
    }, firstDelay);
  }

  private void chatRandomMessage(FakePlayer fakePlayer, boolean firstMessage) {
    List<String> messages;

    var firstMessages = config.getOptionalStringList("chatting.first-messages");
    if (firstMessage && firstMessages.isPresent()) {
      messages = firstMessages.get();
    } else {
      messages = config.getStringList("chatting.messages");
    }

    String message = messages.get(ThreadLocalRandom.current().nextInt(messages.size()));
    var player = fakePlayer.getPlayer();
    message = miniMsgAsst.deserializeAsPlainText(message, player);
    player.chat(message);
  }
}
