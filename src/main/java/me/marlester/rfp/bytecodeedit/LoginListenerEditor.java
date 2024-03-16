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
import com.google.inject.name.Named;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.LoaderClassPath;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import me.marlester.rfp.util.RemapUtil;
import net.bytebuddy.agent.ByteBuddyAgent;
import net.minecraft.network.protocol.login.ServerboundHelloPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;

/**
 * Responsible for editing login listener behavior in PaperMC. This class
 * modifies the server's packet handling to allow for authentication and uuid change of fake
 * players using a unique approach.
 */
@RequiredArgsConstructor(onConstructor_ = {@Inject}, access = AccessLevel.PACKAGE)
@Singleton
public class LoginListenerEditor {

  private final SecretClasser secretClasser;
  @Named("pluginName")
  private final String pluginName;

  /**
   * Starts the editing process of the server's login listener. It modifies the
   * behavior of packet handling for player authentication, integrating checks
   * against fake players, and changing uuids. The method dynamically alters
   * the bytecode to implement the desired functionality.
   */
  @SneakyThrows
  public void startEditing() {
    Class<?> clazz = ServerLoginPacketListenerImpl.class;

    ClassPool pool = new ClassPool();
    pool.appendClassPath(new LoaderClassPath(clazz.getClassLoader()));
    CtClass ctClass = pool.get(clazz.getName());
    ctClass.defrost(); // as this class is already loaded, javassist tries to protect it.
    CtMethod handleHello = ctClass.getDeclaredMethod(RemapUtil.HANDLE_HELLO_METHOD_NAME,
        new CtClass[] {pool.get(ServerboundHelloPacket.class.getName())});
    CtMethod checkAuthMethod = pool.get(MinecraftServer.class.getName())
        .getDeclaredMethod(RemapUtil.USES_AUTH_METHOD_NAME);
    handleHello.instrument(new ExprEditor() {
      @SneakyThrows
      @Override
      public void edit(MethodCall methodCall) {
        if (methodCall.getMethod().equals(checkAuthMethod)) {
          /*
           * code executing checking authentication AND anti-fakeplayer check instead of just
           * checking authentication, we also use optional game profile id as a key. highly VDP
           */
          String code = String.format("""
              {
                try {
                  boolean isFakePlayer = false;
                  java.util.UUID key = packet.%4$s();
                  if (key != null) {
                    Class secretClass = Class.forName("%2$s");
                    java.util.Map fakePlayerUuidsByKey = (java.util.Map) secretClass
                        .getDeclaredField("%3$s").get(null);
                    java.util.UUID newUuid = (java.util.UUID) fakePlayerUuidsByKey.get(key);
                    if (newUuid != null) {
                      isFakePlayer = true;
                      this.%5$s.spoofedUUID = newUuid;
                      fakePlayerUuidsByKey.remove(key);
                    }
                  }
                  $_ = $proceed($$) && !isFakePlayer;
                } catch (Exception e) {
                  $_ = $proceed($$);
                  org.bukkit.Bukkit.getLogger().log(
                      java.util.logging.Level.SEVERE,
                      "[%1$s] Error while catching fakeplayer in loginlistener!",
                      e
                  );
                }
              }
              """,
              pluginName,
              secretClasser.getSecretClassName(),
              secretClasser.getUuidsByKeyFieldName(),
              RemapUtil.PROFILE_ID_METHOD_NAME,
              RemapUtil.CONNECTION_FIELD_NAME
          );
          methodCall.replace(code);
        }
      }
    });

    ClassDefinition classDefinition = new ClassDefinition(clazz, ctClass.toBytecode());
    ByteBuddyAgent.getInstrumentation().redefineClasses(classDefinition);
  }
}
