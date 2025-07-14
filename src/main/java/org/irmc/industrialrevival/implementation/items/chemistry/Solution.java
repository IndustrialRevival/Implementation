package org.irmc.industrialrevival.implementation.items.chemistry;

import lombok.Data;
import org.irmc.industrialrevival.api.elements.compounds.ChemicalCompound;
import org.irmc.industrialrevival.api.items.IndustrialRevivalItem;
import org.irmc.industrialrevival.api.items.attributes.ChemicalCompoundContainer;

import java.util.Map;

public class Solution extends IndustrialRevivalItem implements ChemicalCompoundContainer {
    // todo: ↓
    @Data
    public static class CompoundStorable {
        public final Map<ChemicalCompound, CompoundMetadata> compounds;
    }

    // todo: ↓
    @Data
    public static class CompoundMetadata {
        public final double mass;
        public final Boolean isSediment;
        public final Boolean isGas;
        // Also means un-save
        public final Map<String, Object> unsafe;
    }
}
