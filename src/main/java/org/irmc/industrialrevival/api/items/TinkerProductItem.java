package org.irmc.industrialrevival.api.items;

import lombok.Getter;
import org.bukkit.inventory.ItemStack;
import org.irmc.industrialrevival.api.elements.melt.MeltedType;
import org.irmc.industrialrevival.api.elements.tinker.TinkerType;
import org.irmc.industrialrevival.api.items.attributes.TinkerProduct;
import org.irmc.industrialrevival.dock.IRDock;
import org.jetbrains.annotations.NotNull;

/**
 * @author balugaq
 */
@Getter
public class TinkerProductItem extends IndustrialRevivalItem implements TinkerProduct {
    private MeltedType meltedType;
    private TinkerType tinkerType;

    public TinkerProductItem() {
        super();
    }

    public TinkerProductItem tinkerType(TinkerType tinkerType) {
        this.tinkerType = tinkerType;
        return this;
    }

    public TinkerProductItem meltedType(MeltedType meltedType) {
        this.meltedType = meltedType;
        return this;
    }

    @Override
    public void postRegister() {
        super.postRegister();
        if (this.tinkerType == null) {
            throw new IllegalArgumentException("TinkerType must be set for TinkerProductItem");
        }

        if (this.meltedType == null) {
            throw new IllegalArgumentException("MeltedType must be set for TinkerProductItem");
        }
        IRDock.getPlugin().getRegistry().registerTinkerRecipe(getMeltedType(), this);
    }

    @Override
    public @NotNull ItemStack getProduct() {
        return getItemStack();
    }

    @Override
    public @NotNull IndustrialRevivalItem getIRItem() {
        return this;
    }
}
