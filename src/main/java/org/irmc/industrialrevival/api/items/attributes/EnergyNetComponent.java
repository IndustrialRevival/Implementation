package org.irmc.industrialrevival.api.items.attributes;

import org.irmc.industrialrevival.api.machines.EnergyNetComponentType;

/**
 * This interface defines a machine can be connected to an energy network.<br>
 * <br>
 * <b>Note: </b> use {@link EnergyNetProvider} if the machine provides energy to the network.
 *
 * @author lijinhong11
 */
public interface EnergyNetComponent extends ItemAttribute {
    long getCapacity();

    EnergyNetComponentType getComponentType();
}
