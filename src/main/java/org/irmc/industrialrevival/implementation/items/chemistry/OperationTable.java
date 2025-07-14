package org.irmc.industrialrevival.implementation.items.chemistry;

import org.bukkit.Location;
import org.irmc.industrialrevival.api.elements.compounds.ChemicalFormula;
import org.irmc.industrialrevival.api.elements.reaction.ReactCondition;
import org.irmc.industrialrevival.api.machines.recipes.MachineRecipe;
import org.irmc.industrialrevival.api.menu.MatrixMenuDrawer;
import org.irmc.industrialrevival.api.recipes.RecipeType;
import org.irmc.industrialrevival.api.recipes.methods.ChemicalMethod;
import org.irmc.industrialrevival.dock.IRDock;
import org.irmc.industrialrevival.implementation.IndustrialRevival;
import org.irmc.industrialrevival.utils.MenuUtil;

import java.util.Set;

public class OperationTable extends Reactor {
    public OperationTable() {
        super();
        setAddon(IRDock.getPlugin());
    }

    @Override
    public MatrixMenuDrawer getMatrixMenuDrawer() {
        return new MatrixMenuDrawer(36)
                .addLine("AAAASAAAA")
                .addLine("iiiiiiiii")
                .addLine("iiiiiiiii")
                .addLine("iiiiiiiii")
                .addExplain("A", "Input and output border", MenuUtil.INPUT_AND_OUTPUT_BORDER)
                .addExplain("S", "Status", getBaseStatusIcon())
                .addExplain("i", "Item slot");
    }

    @Override
    public void loadRecipes() {
        for (var formula : IRDock.getRegistry().getChemicalFormulas().values()) {
            recipes.add(new MachineRecipe(0, 0, asRawItemLevel(formula.getInput()), asRawItemLevel(formula.getOutput())));
        }
    }

    // todo: for test ↓
    public static final Set<ReactCondition> CONDITIONS = Set.of(
            ReactCondition.ELECTROLYSIS,
            ReactCondition.HIGH_TEMPERATURE,
            ReactCondition.HEATING,
            ReactCondition.LIGHT
    );

    @Override
    public Set<ReactCondition> getReactConditions(Location location) {
        return CONDITIONS;
    }

    public static class OperationTableChemicalMethod extends ChemicalMethod {

        public OperationTableChemicalMethod(ChemicalFormula formula) {
            super(formula);
        }

        @Override
        public RecipeType getRecipeType() {
            return OperationTable.getRecipeType();
        }

        public static OperationTableChemicalMethod of(ChemicalFormula formula) {
            return new OperationTableChemicalMethod(formula);
        }
    }
}
