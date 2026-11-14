package ovo.yiran.universalscroll.client;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.Nullable;

/**
 * 客户端入口。仅在 {@code level.isClientSide} 分支被调用，避免服务端加载客户端类。
 */
public final class ClientHooks {
    private ClientHooks() {
    }

    public static void openSelection(@Nullable BlockPos workbenchPos) {
        Minecraft.getInstance().setScreen(new SchematicSelectionScreen(workbenchPos));
    }
}
