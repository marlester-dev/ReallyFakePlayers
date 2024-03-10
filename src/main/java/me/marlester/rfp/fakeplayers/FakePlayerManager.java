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

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import dev.dejvokep.boostedyaml.YamlDocument;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import me.marlester.rfp.faketools.FakeLister;
import me.marlester.rfp.faketools.FakeNamer;
import org.jetbrains.annotations.ApiStatus;

/**
 * Class for managing those pesky fake players. It's like playing god, but with less thunder.
 * This baby handles everything from adding to removing those virtual wannabes.
 * Don't get too excited, it's not rocket science, just some fake players in a digital playground.
 */
@RequiredArgsConstructor(onConstructor_ = {@Inject}, access = AccessLevel.PACKAGE)
@Singleton
public class FakePlayerManager {

  @Named("config")
  private final YamlDocument config;
  private final FakePlayerFactory fakePlayerFactory;
  private final FakeLister fakeLister;
  private final FakeNamer fakeNamer;

  /**
   * Adds a fake player. It's like inviting an imaginary friend to your party.
   *
   * @param name The name of the fake player. It must meet the Minecraft Java profile name
   *             requirements. Stick to their rules or no dice.
   */
  public void add(String name) {
    int maximum = config.getInt("max-fake-players");
    if (fakeLister.getRawFakePlayers().size() >= maximum) {
      return;
    }
    fakePlayerFactory.create(name).join();
  }

  /**
   * Adds a number of fake players. Because the more, the merrier, right?
   * Their names are being acquired via the grand {@link FakeNamer}.
   *
   * @param number How many fake friends you want to add. Keep it positive, and
   *               within the bounds of sanity.
   * @see FakeNamer#getRandomName() FakeNamer's name getting procedure.
   */
  public void addNumber(int number) {
    for (int i = 0; i < number; i++) {
      String name = fakeNamer.getRandomName();
      add(name);
    }
  }

  /**
   * Why keep a fake player around when you can zap 'em out of existence, right?
   * This little method does exactly that. Poof! Gone! Just like my sense of responsibility
   * on a Friday night.
   *
   * @param fakePlayer The wannabe player you're itching to get rid of. Don't get sentimental,
   *                   it's just a bunch of code.
   * @see FakePlayer#remove()
   */
  @ApiStatus.Obsolete
  public void remove(FakePlayer fakePlayer) {
    fakePlayer.remove();
  }

  /**
   * It's like wielding a Death Note for fake players. Write a name, and poof! They're gone.
   *
   * @param name The name of the fake player you want to send into the oblivion. Say goodbye,
   *             or don't. It's just a fake player after all!
   * @see FakePlayer#remove()
   */
  public void remove(String name) {
    remove(fakeLister.getRawFakePlayersByName().get(name));
  }

  /**
   * Initiates a methodical purge of all fake players. Think of it as a systematic extermination,
   * but less grim. Starting from the oldest (who probably remember the dial-up era) and moving
   * to the newest, this method wipes them out one by one. It's like a reverse chronology of
   * annihilation.
   *
   * @see FakePlayer#remove()
   */
  public void removeAll() {
    var fakePlayers = fakeLister.getRawFakePlayers();
    while (!fakePlayers.isEmpty()) {
      fakePlayers.getFirst().remove();
    }
  }

  /**
   * Obliterates a specified number of the oldest fake players. It's like a time machine of
   * destruction, starting from the oldest and moving forward.
   *
   * @param number The number of ancient fake players you want to send to the digital beyond.
   *               Keep it positive, and within the realm of reason.
   * @see FakePlayer#remove()
   */
  public void removeNumber(int number) {
    var fakePlayers = fakeLister.getRawFakePlayers();
    int numToRemove = Math.min(number, fakePlayers.size());
    if (0 >= numToRemove) {
      return;
    }
    for (int i = 0; i < numToRemove; i++) {
      fakePlayers.getFirst().remove();
    }
  }
}
