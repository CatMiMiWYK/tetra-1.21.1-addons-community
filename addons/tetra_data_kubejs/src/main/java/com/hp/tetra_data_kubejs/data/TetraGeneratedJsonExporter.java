/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.JsonElement
 *  dev.latvian.mods.kubejs.KubeJSPaths
 *  net.minecraft.resources.ResourceLocation
 */
package com.hp.tetra_data_kubejs.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.hp.tetra_data_kubejs.TetraDataKubejs;
import com.hp.tetra_data_kubejs.data.ScriptedTetraEntry;
import dev.latvian.mods.kubejs.KubeJSPaths;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;
import net.minecraft.resources.ResourceLocation;

public final class TetraGeneratedJsonExporter {
    private static final Gson PRETTY_GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path ROOT = KubeJSPaths.DIRECTORY.resolve("server").resolve("generated").resolve("tetra_data_kubejs");
    private static final Map<String, Path> EXPORTED_DIRECTORIES = new LinkedHashMap<String, Path>();

    private TetraGeneratedJsonExporter() {
    }

    public static Path root() {
        return ROOT;
    }

    public static void prepareExports(Collection<ScriptedTetraEntry> entries) {
        if (entries == null || entries.isEmpty()) {
            return;
        }
        for (Map.Entry<String, Path> exportEntry : EXPORTED_DIRECTORIES.entrySet()) {
            if (entries.stream().noneMatch(entry -> ((String)exportEntry.getKey()).equals(entry.getDirectory()))) continue;
            try {
                TetraGeneratedJsonExporter.clearDirectory(exportEntry.getValue());
                Files.createDirectories(exportEntry.getValue(), new FileAttribute[0]);
            }
            catch (IOException exception) {
                TetraDataKubejs.LOGGER.warn("Failed to prepare generated {} json export directory", (Object)exportEntry.getKey(), (Object)exception);
            }
        }
    }

    public static void writeEntry(ScriptedTetraEntry entry, JsonElement json) {
        if (entry == null || json == null || json.isJsonNull()) {
            return;
        }
        Path exportDirectory = EXPORTED_DIRECTORIES.get(entry.getDirectory());
        if (exportDirectory == null) {
            return;
        }
        ResourceLocation resourceId = entry.getResourceId();
        if (resourceId == null) {
            return;
        }
        Path output = ROOT.resolve(resourceId.getNamespace()).resolve(resourceId.getPath());
        try {
            Files.createDirectories(output.getParent(), new FileAttribute[0]);
            String contents = PRETTY_GSON.toJson(json) + System.lineSeparator();
            if (!Files.exists(output, new LinkOption[0]) || !Files.readString(output, StandardCharsets.UTF_8).equals(contents)) {
                Files.writeString(output, (CharSequence)contents, StandardCharsets.UTF_8, new OpenOption[0]);
            }
        }
        catch (IOException exception) {
            TetraDataKubejs.LOGGER.warn("Failed to write generated {} json {}", new Object[]{entry.getDirectory(), output, exception});
        }
    }

    private static void clearDirectory(Path directory) throws IOException {
        if (!Files.exists(directory, new LinkOption[0])) {
            return;
        }
        try (Stream<Path> paths = Files.walk(directory, new FileVisitOption[0]);){
            paths.sorted(Comparator.reverseOrder()).forEach(path -> {
                try {
                    Files.deleteIfExists(path);
                }
                catch (IOException exception) {
                    throw new RuntimeException(exception);
                }
            });
        }
        catch (RuntimeException exception) {
            Throwable throwable = exception.getCause();
            if (throwable instanceof IOException) {
                IOException ioException = (IOException)throwable;
                throw ioException;
            }
            throw exception;
        }
    }

    static {
        EXPORTED_DIRECTORIES.put("materials", ROOT.resolve("tetra").resolve("materials"));
        EXPORTED_DIRECTORIES.put("improvements", ROOT.resolve("tetra").resolve("improvements"));
        EXPORTED_DIRECTORIES.put("modules", ROOT.resolve("tetra").resolve("modules"));
        EXPORTED_DIRECTORIES.put("schematics", ROOT.resolve("tetra").resolve("schematics"));
    }
}

