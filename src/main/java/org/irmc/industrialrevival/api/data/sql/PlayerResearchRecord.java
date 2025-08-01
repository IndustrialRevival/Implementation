package org.irmc.industrialrevival.api.data.sql;

import io.github.lijinhong11.mdatabase.serialization.annotations.Column;
import io.github.lijinhong11.mdatabase.serialization.annotations.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.NamespacedKey;
import org.irmc.industrialrevival.api.player.PlayerProfile;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author JWJUN233233
 */
@Getter
@Table(name = "player_researches")
@NoArgsConstructor
@AllArgsConstructor
@ApiStatus.Internal
public class PlayerResearchRecord {
    @Column
    private UUID playerUUID;

    @Column
    private String namespacedKey;

    @NotNull
    public static PlayerResearchRecord wrap(@NotNull PlayerProfile profile, NamespacedKey namespacedKey) {
        return new PlayerResearchRecord(profile.getPlayerUUID(), namespacedKey.asString());
    }

    @NotNull
    public static List<PlayerResearchRecord> wrapAll(@NotNull PlayerProfile profile) {
        List<PlayerResearchRecord> result = new ArrayList<>();

        for (Map.Entry<NamespacedKey, Boolean> entry : profile.getResearchStatus().entrySet()) {
            if (entry.getValue()) {
                result.add(wrap(profile, entry.getKey()));
            }
        }

        return result;
    }
}
