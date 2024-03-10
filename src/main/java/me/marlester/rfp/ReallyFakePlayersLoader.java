package me.marlester.rfp;

import io.papermc.paper.plugin.loader.PluginClasspathBuilder;
import io.papermc.paper.plugin.loader.PluginLoader;
import io.papermc.paper.plugin.loader.library.impl.MavenLibraryResolver;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.repository.RemoteRepository;
import org.jetbrains.annotations.NotNull;

/**
 * The loader class for this plugin.
 * Its main goal now is to load up libraries of the plugin.
 */
public class ReallyFakePlayersLoader implements PluginLoader {

  @Override
  public void classloader(@NotNull PluginClasspathBuilder classpathBuilder) {
    MavenLibraryResolver resolver = new MavenLibraryResolver();

    // Add repositories
    resolver.addRepository(new RemoteRepository.Builder("central", "default",
        "https://repo1.maven.org/maven2").build());
    resolver.addRepository(new RemoteRepository.Builder("opencollab", "default",
        "https://repo.opencollab.dev/maven-releases").build());
    resolver.addRepository(new RemoteRepository.Builder("jitpack", "default",
        "https://jitpack.io").build());

    // Add dependencies
    resolver.addDependency(new Dependency(new DefaultArtifact(
        "com.google.inject:guice:7.0.0"), null));
    resolver.addDependency(new Dependency(new DefaultArtifact(
        "com.google.inject.extensions:guice-assistedinject:7.0.0"), null));
    resolver.addDependency(new Dependency(new DefaultArtifact(
        "org.javassist:javassist:3.30.2-GA"), null));
    resolver.addDependency(new Dependency(new DefaultArtifact(
        "net.bytebuddy:byte-buddy-agent:1.14.12"), null));
    resolver.addDependency(new Dependency(new DefaultArtifact(
        "com.github.steveice10:mcprotocollib:1.20.4-1"), null));
    resolver.addDependency(new Dependency(new DefaultArtifact(
        "dev.dejvokep:boosted-yaml-spigot:1.4"), null));
    resolver.addDependency(new Dependency(new DefaultArtifact(
        "xyz.jpenilla:reflection-remapper:0.1.0"), null));

    classpathBuilder.addLibrary(resolver);
  }
}
