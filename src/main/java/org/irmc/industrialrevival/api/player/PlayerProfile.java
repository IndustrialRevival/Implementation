package org.irmc.industrialrevival.api.player;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.irmc.industrialrevival.api.data.sql.PlayerResearchRecord;
import org.irmc.industrialrevival.core.guide.GuideHistory;
import org.irmc.industrialrevival.core.guide.GuideSettings;

import org.irmc.industrialrevival.implementation.IndustrialRevival;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Contains information related to {@link Player} and Industrial Revival.
 *
 * @author lijinhong11
 * @since 1.0
 */
@Getter
public class PlayerProfile {
    private final String playerName;

    private final UUID playerUUID;

    private final GuideHistory guideHistory;

    private final GuideSettings guideSettings;

    private final Map<NamespacedKey, Boolean> researchStatus;

    protected PlayerProfile(
            String playerName,
            UUID playerUUID,
            GuideSettings guideSettings,
            Map<NamespacedKey, Boolean> researchStatus) {
        this.playerName = playerName;
        this.playerUUID = playerUUID;
        this.guideHistory = new GuideHistory(playerName);
        this.guideSettings = Objects.requireNonNullElse(guideSettings, GuideSettings.DEFAULT_SETTINGS);
        this.researchStatus = researchStatus;
    }

    @Nullable
    public static PlayerProfile getProfile(Player player) {
        return getProfile(player.getName());
    }

    @Nullable
    public static PlayerProfile getProfile(String playerName) {
        return IndustrialRevival.getInstance().getDataManager().getPlayerProfiles().get(playerName);
    }

    @NotNull
    @CanIgnoreReturnValue
    public static PlayerProfile getOrRequestProfile(String name) {
        if (IndustrialRevival.getInstance().getDataManager().getPlayerProfiles().containsKey(name)) {
            return IndustrialRevival.getInstance()
                    .getDataManager()
                    .getPlayerProfiles()
                    .get(name);
        }

        OfflinePlayer player = Bukkit.getOfflinePlayer(name);

        UUID playerUUID = player.getUniqueId();

        GuideSettings guideSettings = GuideSettings.DEFAULT_SETTINGS;
        //IndustrialRevival.getInstance().getDataManager().getGuideSettings(name);

        Map<NamespacedKey, Boolean> researchStatus = new HashMap<>();
        //IndustrialRevival.getInstance().getDataManager().getResearchStatus(name);

        List<PlayerResearchRecord> researchRecords = IndustrialRevival.getInstance().getSQLDataManager().getPlayerResearchRecord(playerUUID);

        for (PlayerResearchRecord researchRecord : researchRecords) {
            researchStatus.put(NamespacedKey.fromString(researchRecord.getNamespacedKey()), true);
        }

        PlayerProfile profile = new PlayerProfile(name, playerUUID, guideSettings, researchStatus);
        IndustrialRevival.getInstance().getDataManager().getPlayerProfiles().put(name, profile);

        return profile;
    }

    public void save() {
        IndustrialRevival.getInstance().getSQLDataManager().deletePlayerResearchRecord(playerUUID);

        for (PlayerResearchRecord playerResearchRecord : PlayerResearchRecord.wrapAll(this)) {
            IndustrialRevival.getInstance().getSQLDataManager().savePlayerResearchRecord(playerResearchRecord);
        }
    }

    /*
    public void research(NamespacedKey key) {
        if (hasResearched(key)) {
            return;
        }

        Research research = Research.getResearch(key);
        if (research == null) {
            return;
        }

        if (player.getExpToLevel() < research.getRequiredExpLevel()) {
            IndustrialRevival.getInstance().getLanguageManager().sendMessage(player, "research.not_enough_exp");
            return;
        }

        player.giveExpLevels(-research.getRequiredExpLevel());
        researchStatus.put(key, true);

        IndustrialRevival.getInstance()
                .getLanguageManager()
                .sendMessage(player, "research.success", new MessageReplacement("%name%", research.getName()));
    }
     */

    public <T> T getGuideSettings(GuideSetting<T> clazz) {
        return getGuideSettings().getPlayerSettings(clazz).get();
    }

    public boolean hasResearched(NamespacedKey key) {
        return researchStatus.getOrDefault(key, false);
    }
}
