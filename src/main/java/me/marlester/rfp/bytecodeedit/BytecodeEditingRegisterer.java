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

package me.marlester.rfp.bytecodeedit;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import net.bytebuddy.agent.ByteBuddyAgent;

/**
 * Responsible for registering bytecode modifications.
 */
@RequiredArgsConstructor(onConstructor_ = {@Inject}, access = AccessLevel.PACKAGE)
@Singleton
public class BytecodeEditingRegisterer {

  private final SecretClasser secretClasser;
  private final LoginListenerEditor loginListenerEditor;

  /**
   * Initiates all bytecode modifications.
   */
  public void registerBytecodeEditions() {
    ByteBuddyAgent.install();
    secretClasser.createSecretClass();
    loginListenerEditor.startEditing();
  }
}
