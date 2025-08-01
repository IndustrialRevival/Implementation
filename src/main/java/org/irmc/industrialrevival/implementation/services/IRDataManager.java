package org.irmc.industrialrevival.implementation.services;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.irmc.industrialrevival.api.menu.MachineMenu;
import org.irmc.industrialrevival.api.menu.MachineMenuPreset;
import org.irmc.industrialrevival.api.data.runtime.IRBlockData;
import org.irmc.industrialrevival.api.data.sql.BlockRecord;
import org.irmc.industrialrevival.api.player.PlayerProfile;
import org.irmc.industrialrevival.core.services.IIRDataManager;
import org.irmc.industrialrevival.dock.IRDock;
import org.irmc.industrialrevival.utils.Debug;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class IRDataManager implements IIRDataManager {
    private final Map<Location, IRBlockData> blockDataMap;
    private final Map<String, PlayerProfile> playerProfileMap; // id -> profile

    public IRDataManager() {
        this.blockDataMap = new HashMap<>();
        this.playerProfileMap = new HashMap<>();

        loadData();
    }

    private void loadData() {
        List<BlockRecord> records =
                IRDock.getPlugin().getSQLDataManager().getAllBlockRecords();
        Debug.log("List<BlockRecord> records: " + records.size());
        for (BlockRecord record : records) {
            Location loc = record.getLocation();

            blockDataMap.put(loc, IRBlockData.warp(record));
        }
    }

    public IRBlockData getBlockData(Location location) {
        return blockDataMap.get(location);
    }

    public void placeBlock(Location loc, NamespacedKey machineId) {
        Debug.log("handleBlockPlacing");
        YamlConfiguration configuration = new YamlConfiguration();
        MachineMenuPreset preset = IRDock.getPlugin().getRegistry().getMenuPresets().get(machineId);

        MachineMenu menu = null;
        if (preset != null) {
            menu = new MachineMenu(loc, preset);
            preset.newInstance(loc.getBlock(), menu);
        }

        IRBlockData blockData = new IRBlockData(machineId, loc, configuration, menu);
        blockDataMap.put(loc, blockData);
    }

    @CanIgnoreReturnValue
    public IRBlockData breakBlock(Location loc) {
        return blockDataMap.remove(loc);
    }

    public void saveAllData() {
        Debug.log("blockDataMap: " + blockDataMap.size());
        for (IRBlockData data : blockDataMap.values()) {
            BlockRecord blockRecord = BlockRecord.warp(data);

            IRDock.getPlugin().getSQLDataManager().saveBlockRecord(blockRecord);
        }
        blockDataMap.clear();
    }

    @Override
    public void saveBlock(@NotNull Location location) {
        IRBlockData data = blockDataMap.get(location);
        if (data == null) return;
        BlockRecord blockRecord = BlockRecord.warp(data);

        IRDock.getPlugin().getSQLDataManager().saveBlockRecord(blockRecord);
    }

    public Map<Location, IRBlockData> getBlockDataMap() {
        return new HashMap<>(blockDataMap);
    }

    @Override
    public @NotNull Map<String, PlayerProfile> getPlayerProfiles() {
        return new HashMap<>(playerProfileMap);
    }

    @Override
    public @NotNull Collection<PlayerProfile> getAllPlayerProfiles() {
        return playerProfileMap.values();
    }

    @Override
    public @Nullable PlayerProfile getPlayerProfile(@NotNull String playerName) {
        return playerProfileMap.get(playerName);
    }

    @Override
    public @NotNull PlayerProfile getPlayerProfile(@NotNull Player player) {
        return Objects.requireNonNull(getPlayerProfile(player.getName()));
    }

    @Override
    public @NotNull PlayerProfile getPlayerProfile(@NotNull UUID playerUUID) {
        return getPlayerProfile(Objects.requireNonNull(Bukkit.getPlayer(playerUUID)));
    }

    @Override
    public void savePlayerProfile(@NotNull PlayerProfile profile) {
        profile.save();
    }

    @Override
    public void savePlayerProfile(@NotNull Player player) {
        requestPlayerProfile(player).save();
    }

    @Override
    public PlayerProfile requestPlayerProfile(@NotNull Player player) {
        return PlayerProfile.getOrRequestProfile(player.getName());
    }
}
