package org.irmc.industrialrevival.implementation.items.chemistry;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.irmc.industrialrevival.api.elements.ElementType;
import org.irmc.industrialrevival.api.items.ElementItem;
import org.irmc.pigeonlib.pdc.PersistentDataAPI;

public class OreDust extends ElementItem  {
    public static final NamespacedKey ELEMENT_TYPE_KEY = NamespacedKey.minecraft("element_type");

    public ElementType getElementType(ItemStack itemStack) {
        return ElementType.valueOf(PersistentDataAPI.get(itemStack.getItemMeta(), ELEMENT_TYPE_KEY, PersistentDataType.STRING));
    }

    public void setElementType(ItemStack itemStack, ElementType data) {
        itemStack.editMeta(meta -> PersistentDataAPI.set(meta, ELEMENT_TYPE_KEY, PersistentDataType.STRING, data.name()));
    }
}
