package net.tetra.modify.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonWriter;
import net.neoforged.fml.loading.FMLPaths;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public class MixinConfig {
    public static int nowVersion = 3;
    public static Gson GSON = new GsonBuilder().create();
    public static Path configPath = FMLPaths.CONFIGDIR.get().resolve("TetraModifyCoreMixinConfig.json");
    private static MixinConfig instance;
    public int version = nowVersion;
    public String thread_desc = "\u8bbe\u5b9astatBar\u591a\u7ebf\u7a0b\u66f4\u65b0\u7684\u7ebf\u7a0b\u6570,\u8bbe\u7f6e\u4e3a0\u5173\u95ed\u591a\u7ebf\u7a0b\u4f18\u5316";
    public int thread = 0;
    public String bowModify_desc = "\u662f\u5426\u5f00\u542f\u5bf9\u5f13\u5f29\u7684\u4fee\u6539";
    public boolean bowModify = true;
    public String alwaysNeedsWrapTooltip_desc = "\u9ed8\u8ba4\u542f\u7528tooltip\u6e32\u67d3\u7684\u6362\u884c";
    public boolean alwaysNeedsWrapTooltip = true;

    public static MixinConfig getInstance() {
        if (instance == null) {
            instance = MixinConfig.buildInstance();
        }
        return instance;
    }

    private static MixinConfig buildInstance() {
        if (Files.exists(configPath)) {
            MixinConfig config;
            try (BufferedReader reader = Files.newBufferedReader(configPath)) {
                config = GSON.fromJson(reader, MixinConfig.class);
                if (config.version != nowVersion) {
                    config.version = nowVersion;
                    MixinConfig.writeConfig(config);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return config;
        }
        MixinConfig config = new MixinConfig();
        MixinConfig.writeConfig(config);
        return config;
    }

    private static void writeConfig(MixinConfig config) {
        try (BufferedWriter writer = Files.newBufferedWriter(configPath)) {
            JsonWriter jsonWriter = new JsonWriter(writer);
            jsonWriter.setIndent("\t");
            jsonWriter.setSerializeNulls(true);
            jsonWriter.setLenient(true);
            Streams.write(GSON.toJsonTree(config, MixinConfig.class), jsonWriter);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
