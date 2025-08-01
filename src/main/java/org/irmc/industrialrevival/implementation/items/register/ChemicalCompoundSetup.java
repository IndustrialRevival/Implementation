package org.irmc.industrialrevival.implementation.items.register;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.irmc.industrialrevival.api.elements.registry.ChemicalCompounds;
import org.irmc.industrialrevival.api.elements.registry.ChemicalFormulas;

import org.irmc.industrialrevival.implementation.IndustrialRevival;
import org.irmc.industrialrevival.implementation.items.chemistry.OperationTable;
import org.irmc.industrialrevival.implementation.items.chemistry.Solution;
import org.irmc.industrialrevival.implementation.items.CustomItemStack;

import java.util.HashMap;
import java.util.Map;

public class ChemicalCompoundSetup {
    public static final Map<String, Solution> solutions = new HashMap<>();

    public static void setup() {
        ChemicalCompounds.onLoad(() -> {
            IndustrialRevival.getInstance().getFoliaLibImpl().getScheduler().runAsync(_ -> {
                IndustrialRevival.getInstance().getRegistry().getChemicalCompounds().values().forEach(chemicalCompound -> {
                    var item = new Solution()
                            .addon(IndustrialRevival.getInstance())
                            .id("CHEMICAL_COMPOUND_" + chemicalCompound.asKey())
                            .icon(new CustomItemStack(
                                    Material.GLASS_BOTTLE,
                                    Component.empty().append(Component.translatable("chemistry.solution." + chemicalCompound.getName()).append(
                                            Component.translatable("chemistry.solution.bottle")))
                            ))
                            .cast(Solution.class);
                    item.register();
                    solutions.put(chemicalCompound.getName(), item);
                });

                IndustrialRevival.getInstance().getRegistry().getChemicalFormulas().values().forEach(formula ->
                        formula.getOutput().keySet().forEach(compound ->
                                solutions.get(compound.getName()).recipe(OperationTable.OperationTableChemicalMethod.of(formula))
                        )
                );
            });
        });
        ChemicalCompounds.onLoad(ChemicalFormulas::setup);

        ChemicalCompounds.load();
    }
}
