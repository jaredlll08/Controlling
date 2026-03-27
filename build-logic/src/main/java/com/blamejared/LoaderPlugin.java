package com.blamejared;

import com.blamejared.gradle.mod.utils.extensions.VersionTrackerExtension;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.artifacts.ModuleDependency;
import org.gradle.api.artifacts.dsl.DependencyHandler;
import org.gradle.api.attributes.Attribute;
import org.gradle.api.tasks.TaskContainer;
import org.gradle.api.tasks.compile.JavaCompile;
import org.gradle.api.tasks.javadoc.Javadoc;
import org.gradle.jvm.tasks.Jar;
import org.gradle.language.jvm.tasks.ProcessResources;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class LoaderPlugin implements Plugin<Project> {
    
    @Override
    public void apply(@NotNull Project project) {
        
        project.getPlugins().apply("com.blamejared.common-plugin");
        project.getPlugins().apply("com.blamejared.gradle-mod-utils");
        project.getPlugins().apply("net.darkhax.curseforgegradle");
        project.getPlugins().apply("com.modrinth.minotaur");
        
        configurations(project);
        dependencies(project);
        tasks(project);
        versionTracker(project);
    }
    
    void configurations(@NotNull Project project) {
        
        final ConfigurationContainer configurations = project.getConfigurations();
        configurations.register("commonJava", files -> files.setCanBeResolved(true));
        configurations.register("commonResources", files -> files.setCanBeResolved(true));
    }
    
    void dependencies(@NotNull Project project) {
        
        final Attribute<String> loaderAttribute = Attribute.of("io.github.mcgradleconventions.loader", String.class);
        final DependencyHandler dependencies = project.getDependencies();
        final ModuleDependency commonDep = (ModuleDependency) dependencies.add("compileOnly", project.project(":common"));
        commonDep.attributes(attributes -> attributes.attribute(loaderAttribute, "common"));
        dependencies.add("commonJava", dependencies.project(Map.of("path", ":common", "configuration", "commonJava")));
        dependencies.add("commonResources", dependencies.project(Map.of("path", ":common", "configuration", "commonResources")));
    }
    
    void tasks(@NotNull Project project) {
        
        final TaskContainer tasks = project.getTasks();
        tasks.named("compileJava", JavaCompile.class).configure(it -> {
            it.dependsOn(project.getConfigurations().named("commonJava"));
            it.source(project.getConfigurations().named("commonJava"));
        });
        tasks.named("processResources", ProcessResources.class).configure(it -> {
            it.dependsOn(project.getConfigurations().named("commonResources"));
            it.from(project.getConfigurations().named("commonResources"));
        });
        tasks.named("javadoc", Javadoc.class).configure(it -> {
            it.dependsOn(project.getConfigurations().named("commonJava"));
            it.source(project.getConfigurations().named("commonJava"));
        });
        tasks.named("sourcesJar", Jar.class).configure(it -> {
            it.dependsOn(project.getConfigurations().named("commonJava"));
            it.from(project.getConfigurations().named("commonJava"));
            it.dependsOn(project.getConfigurations().named("commonResources"));
            it.from(project.getConfigurations().named("commonResources"));
        });
    }
    
    void versionTracker(@NotNull Project project) {
        
        final VersionTrackerExtension tracker = project.getExtensions().getByType(VersionTrackerExtension.class);
        tracker.getMcVersion().set(Util.property(project, "minecraft"));
        tracker.getHomepage().set(Util.property(project, "curse_homepage"));
        tracker.getAuthor().set(Util.property(project, "author"));
        tracker.getProjectName().set(Util.property(project, "mod_name"));
    }
    
}
