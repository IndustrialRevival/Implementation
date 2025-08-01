package org.irmc.industrialrevival.core.guide;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.irmc.industrialrevival.api.items.groups.ItemGroup;
import org.irmc.industrialrevival.utils.GuideUtil;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * @author balugaq
 */
public abstract class IRGuide implements GuideImplementation {
    @Override
    @ParametersAreNonnullByDefault
    public void openMainPage(Player player, int page) {
        GuideUtil.openMainMenu(player, this, page);
    }

    @Override
    @ParametersAreNonnullByDefault
    public void openItemGroup(Player player, ItemGroup itemGroup, int page) {
        GuideUtil.openGroupMenu(player, itemGroup, page);
    }

    @Override
    @ParametersAreNonnullByDefault
    public void displayItem(Player player, ItemStack item, int page) {
        GuideUtil.lookupItem(player, item, page);
    }
}
