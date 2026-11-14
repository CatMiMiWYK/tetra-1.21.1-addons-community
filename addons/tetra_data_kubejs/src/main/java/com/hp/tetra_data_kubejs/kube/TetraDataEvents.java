/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.latvian.mods.kubejs.event.EventGroup
 *  dev.latvian.mods.kubejs.event.EventHandler
 */
package com.hp.tetra_data_kubejs.kube;

import com.hp.tetra_data_kubejs.kube.event.TetraDataRegistryEventJS;
import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;

public final class TetraDataEvents {
    public static final String KUBEJS_NAME = "hp_TetraDataEvents";
    public static final EventGroup GROUP = EventGroup.of((String)"hp_TetraDataEvents");
    public static final EventHandler REGISTER = GROUP.startup("register", () -> TetraDataRegistryEventJS.class);
    public static final EventHandler QUEUE = GROUP.server("queue", () -> TetraDataRegistryEventJS.class);

    private TetraDataEvents() {
    }
}

