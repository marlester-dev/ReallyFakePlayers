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
import java.util.Map;
import java.util.UUID;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.LoaderClassPath;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import net.minecraft.server.MinecraftServer;
import org.apache.commons.lang3.RandomStringUtils;

/**
 * Secret class used to hold some fake player data inside minecraft.
 */
@RequiredArgsConstructor(onConstructor_ = {@Inject}, access = AccessLevel.PACKAGE)
@Singleton
public class SecretClasser {

  @Named("pluginName")
  private final String pluginName;

  /**
   * The instance of a map in the secret class.
   */
  @Getter
  private Map<UUID, UUID> fakePlayerUuidsByKey;

  /**
   * The secret class's name.
   */
  @Getter
  private String secretClassName;

  /**
   * Name of the field of the map.
   */
  @Getter
  private String uuidsByKeyFieldName;

  /**
   * Creates the 'secret class' used to hold some fake player data inside minecraft.
   */
  @SuppressWarnings("unchecked")
  @SneakyThrows
  public void createSecretClass() {
    Class<?> neighborClass = MinecraftServer.class;
    String packageName = neighborClass.getPackageName();
    ClassLoader classLoader = neighborClass.getClassLoader();
    // Of course, this class is sneaky, so we need some level of protection,
    // let's protect ourselves by using a random string sequence in addition to it
    String randomAlphanumericString = RandomStringUtils.randomAlphanumeric(3);
    String secretClassSimpleName = "Secret" + pluginName + "DataClass" + randomAlphanumericString;
    ClassPool pool = new ClassPool();
    pool.appendClassPath(new LoaderClassPath(classLoader));
    CtClass newClass = pool.makeClass(packageName + "." + secretClassSimpleName);

    uuidsByKeyFieldName = "fakePlayerUuidsByKey" + randomAlphanumericString;
    // Since this field is going to be used from different async and sync contexts,
    // we need to make it concurrent
    newClass.addField(CtField.make("public static java.util.Map "
        + uuidsByKeyFieldName + " = new java.util.concurrent.ConcurrentHashMap();", newClass));
    Class<?> secretClass = newClass.toClass(neighborClass);
    secretClassName = secretClass.getName();
    fakePlayerUuidsByKey = (Map<UUID, UUID>) secretClass
        .getDeclaredField(uuidsByKeyFieldName).get(null);
  }

}
