package org.irmc.industrialrevival.implementation.items.chemistry;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.irmc.industrialrevival.api.items.ElementItem;
import org.irmc.industrialrevival.api.items.attributes.ChemicalCompoundContainer;
import org.irmc.industrialrevival.api.items.attributes.GasStorage;
import org.irmc.industrialrevival.utils.KeyUtil;
import org.irmc.pigeonlib.pdc.PersistentDataAPI;
import org.jetbrains.annotations.NotNull;

public class GasJar extends ElementItem implements GasStorage {
    private static final NamespacedKey ITEM_KEY = KeyUtil.customKey("gas_jar");

    @Override
    public void insertGas(ItemStack item, ChemicalCompoundContainer gas) {
        PersistentDataAPI.set(item.getItemMeta(), ChemicalCompoundContainer.CHEMICAL_COMPOUND_KEY, PersistentDataType.STRING, gas.getKey().toString());
    }


    @Override
    public @NotNull NamespacedKey getKey() {
        return ITEM_KEY;
    }
}
