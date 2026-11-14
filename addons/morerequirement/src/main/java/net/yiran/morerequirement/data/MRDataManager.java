package net.yiran.morerequirement.data;

import com.google.gson.JsonElement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.yiran.morerequirement.MoreRequirement;
import net.yiran.morerequirement.requirements.grouprequirement.GroupRequirementStore;
import se.mickelus.mutil.data.DataDistributor;
import se.mickelus.mutil.data.DataStore;
import se.mickelus.tetra.blocks.scroll.ScrollData;
import se.mickelus.tetra.data.DataManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MRDataManager implements DataDistributor {
    public static MRDataManager instance = new MRDataManager();
    public DataStore<GroupRequirementStore> groupRequirement;
    public DataStore<ScrollData[]> scrolls;
    public List<DataStore<?>> dataStores = new ArrayList<>(10);

    public MRDataManager() {
        this.groupRequirement = new DataStore<>(DataManager.gson, "morerequirement", "group", GroupRequirementStore.class, this);
        this.scrolls = new DataStore<>(DataManager.gson, "morerequirement", "scrolls", ScrollData[].class, this);
        this.groupRequirement.onReload(() -> GroupRequirementStore.handler(this.groupRequirement.getData()));
        this.addDatastore(this.groupRequirement, this.scrolls);
    }

    public void addDatastore(DataStore<?>... datastore) {
        this.dataStores.addAll(List.of(datastore));
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void addReloadListener(AddReloadListenerEvent event) {
        this.dataStores.forEach(event::addListener);
    }

    @SubscribeEvent
    public void playerConnected(PlayerEvent.PlayerLoggedInEvent event) {
        this.dataStores.forEach(dataStore -> dataStore.sendToPlayer((ServerPlayer) event.getEntity()));
    }

    public void onDataReceived(String directory, Map<ResourceLocation, String> data) {
        this.dataStores.stream()
                .filter(dataStore -> dataStore.getDirectory().equals(directory))
                .forEach(dataStore -> dataStore.loadFromPacket(data));
    }

    @Override
    public void sendToAll(String directory, Map<ResourceLocation, JsonElement> data) {
        MoreRequirement.NETWORK.sendToAllPlayers(new MRUpdateDataPacket(directory, data));
    }

    @Override
    public void sendToPlayer(ServerPlayer player, String directory, Map<ResourceLocation, JsonElement> data) {
        MoreRequirement.NETWORK.sendTo(new MRUpdateDataPacket(directory, data), player);
    }
}
