package net.yiran.extraholopage.compat;

import java.lang.reflect.Method;
import net.neoforged.fml.ModList;

public class JECHCompat {
    public static boolean JECHLoaded = ModList.get().isLoaded("jecharacters");
    private static Method matchContains;

    static {
        if (JECHLoaded) {
            try {
                Class<?> matchClass = Class.forName("me.towdium.jecharacters.utils.Match");
                matchContains = matchClass.getMethod("contains", String.class, CharSequence.class);
            } catch (ReflectiveOperationException | LinkageError e) {
                JECHLoaded = false;
                matchContains = null;
            }
        }
    }

    public static boolean contains(String chinese, String pinyin) {
        if (JECHLoaded && matchContains != null) {
            try {
                return (Boolean) matchContains.invoke(null, chinese, pinyin);
            } catch (ReflectiveOperationException | RuntimeException e) {
                return chinese.contains(pinyin);
            }
        }
        return chinese.contains(pinyin);
    }
}
