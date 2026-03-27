package com.blamejared;

import com.blamejared.gradle.mod.utils.GMUtils;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.dsl.DependencyHandler;
import org.gradle.api.artifacts.dsl.RepositoryHandler;
import org.gradle.api.plugins.BasePluginExtension;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.publish.PublishingExtension;
import org.gradle.api.publish.maven.MavenPublication;
import org.gradle.api.tasks.javadoc.Javadoc;
import org.gradle.external.javadoc.StandardJavadocDocletOptions;
import org.gradle.jvm.tasks.Jar;
import org.gradle.jvm.toolchain.JavaLanguageVersion;
import org.gradle.language.jvm.tasks.ProcessResources;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class CommonPlugin implements Plugin<Project> {
    
    @Override
    public void apply(@NotNull Project project) {
        
        project.getPlugins().apply("java-library");
        project.getPlugins().apply("maven-publish");
        project.getPlugins().apply("com.blamejared.gradle-mod-utils");
        
        base(project);
        project.setVersion(GMUtils.updatingVersion(Util.property(project, "mod_version")));
        java(project);
        dependencies(project);
        repositories(project);
        processResources(project);
        jar(project);
        javadoc(project);
        publishing(project);
    }
    
    void base(@NotNull Project project) {
        
        project.getExtensions()
                .getByType(BasePluginExtension.class)
                .getArchivesName()
                .set(Util.property(project, "mod_name") + "-" + project.getName() + "-" + Util.property(project, "minecraft"));
        
    }
    
    void java(@NotNull Project project) {
        
        final String javaVersion = Util.property(project, "java_version");
        final JavaPluginExtension java = project.getExtensions().getByType(JavaPluginExtension.class);
        java.getToolchain().getLanguageVersion().set(JavaLanguageVersion.of(javaVersion));
        java.withSourcesJar();
        java.withJavadocJar();
    }
    
    
    void dependencies(@NotNull Project project) {
        
        final DependencyHandler dependencies = project.getDependencies();
        dependencies.add("implementation", "com.google.code.findbugs:jsr305:3.0.2");
        dependencies.add("implementation", "org.jetbrains:annotations:24.0.1");
        dependencies.add("implementation", "com.google.auto.service:auto-service-annotations:1.0.1");
        dependencies.add("annotationProcessor", "com.google.auto.service:auto-service:1.0.1");
    }
    
    void repositories(@NotNull Project project) {
        final RepositoryHandler repositories = project.getRepositories();
        repositories.mavenCentral();
        repositories.maven(maven -> {
            maven.setName("BlameJared");
            maven.setUrl("https://maven.blamejared.com");
            maven.content(content -> {
                content.includeGroupAndSubgroups("com.blamejared");
                content.includeGroupAndSubgroups("mezz.jei");
                content.includeGroupAndSubgroups("com.faux");
                content.includeGroupAndSubgroups("org.openzen");
            });
        });
        
        repositories.maven(maven -> {
            maven.setName("Sponge");
            maven.setUrl("https://repo.spongepowered.org/repository/maven-public/");
            maven.content(content -> {
                content.includeGroupAndSubgroups("org.spongepowered");
            });
        });
    }
    
    void processResources(@NotNull Project project) {
        
        project.getTasks().named("processResources", ProcessResources.class).configure(task -> {
            final Map<String, String> expandProps = new HashMap<>();
            expandProps.put("VERSION", project.getVersion().toString());
            expandProps.put("MOD", Util.property(project, "mod_version"));
            expandProps.put("JAVA", Util.property(project, "java_version"));
            expandProps.put("MINECRAFT", Util.property(project, "minecraft"));
            expandProps.put("FABRIC_LOADER", Util.property(project, "fabric_loader"));
            expandProps.put("FABRIC", Util.property(project, "fabric"));
            expandProps.put("NEO_FORGE", Util.property(project, "neoforge"));
            expandProps.put("NEO_FORGE_LOADER", Util.property(project, "neoforge_loader"));
            expandProps.put("GROUP", project.getGroup().toString());
            expandProps.put("NAME", Util.property(project, "mod_name"));
            expandProps.put("AUTHOR", Util.property(project, "author"));
            expandProps.put("MODID", Util.property(project, "modid"));
            expandProps.put("AVATAR", Util.property(project, "avatar"));
            expandProps.put("CURSE_PROJECT_ID", Util.property(project, "curse_project_id"));
            expandProps.put("CURSE_HOMEPAGE", Util.property(project, "curse_homepage"));
            expandProps.put("MODRINTH_PROJECT_ID", Util.property(project, "modrinth_project_id"));
            expandProps.put("GIT_REPO", Util.property(project, "git_repo"));
            expandProps.put("DESCRIPTION", Util.property(project, "description"));
            expandProps.put("ITEM_ICON", Util.property(project, "item_icon"));
            expandProps.put("SEARCHABLES", Util.property(project, "searchables"));
            
            final Map<String, String> jsonExpandProps = expandProps.entrySet()
                    .stream()
                    .map(entry -> Map.entry(entry.getKey(), entry.getValue().replace("\n", "\\\\n")))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            
            task.filesMatching(Set.of("META-INF/mods.toml", "META-INF/neoforge.mods.toml"), it -> it.expand(expandProps));
            task.filesMatching(Set.of("pack.mcmeta", "fabric.mod.json", "*.mixins.json"), it -> it.expand(jsonExpandProps));
            
            task.getInputs().properties(expandProps);
        });
    }
    
    void jar(@NotNull Project project) {
        
        project.getTasks().named("jar", Jar.class).configure(task -> {
            task.from(project.getRootProject()
                    .file("LICENSE"), copySpec -> copySpec.rename(s -> s + "_" + Util.property(project, "mod_name")));
            
            task.manifest(manifest -> {
                
                manifest.attributes(Map.of("Specification-Title",
                        Util.property(project, "mod_name"),
                        "Specification-Vendor",
                        Util.property(project, "author"),
                        "Specification-Version",
                        task.getArchiveVersion(),
                        "Implementation-Title",
                        project.getName(),
                        "Implementation-Version",
                        task.getArchiveVersion(),
                        "Implementation-Vendor",
                        Util.property(project, "author"),
                        "Built-On-Java",
                        System.getProperty("java.vm.version") + " (" + System.getProperty("java.vm.vendor") + ")",
                        "Built-On-Minecraft",
                        Util.property(project, "minecraft")));
            });
        });
    }
    
    void javadoc(@NotNull Project project) {
        
        project.getTasks().named("javadoc", Javadoc.class).configure(task -> {
            if(task.getOptions() instanceof StandardJavadocDocletOptions opt) {
                opt.addStringOption("Xdoclint:none", "-quiet");
            }
        });
    }
    
    void publishing(@NotNull Project project) {
        
        final PublishingExtension publishing = project.getExtensions().getByType(PublishingExtension.class);
        publishing.publications(publications -> publications.register("mavenJava", MavenPublication.class)
                .configure(publication -> {
                    publication.setArtifactId(project.getExtensions()
                            .getByType(BasePluginExtension.class)
                            .getArchivesName()
                            .get());
                    publication.from(project.getComponents().named("java").get());
                }));
        publishing.repositories(repositories -> repositories.maven(maven -> maven.setUrl(System.getenv("local_maven_url"))));
    }
    
}
