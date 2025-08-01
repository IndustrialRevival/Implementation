package org.irmc.industrialrevival.implementation.groups;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.irmc.industrialrevival.dock.IRDock;
import org.irmc.industrialrevival.implementation.items.CustomItemStack;

public class GroupIcons {
    public static final ItemStack GROUP_DEBUG = new CustomItemStack(
            Material.BARRIER,
            IRDock.getLanguageManager().getGroupName("debug")).getBukkit();
    public static final ItemStack GROUP_ORE = new CustomItemStack(
            Material.GOLDEN_PICKAXE,
            IRDock.getLanguageManager().getGroupName("ore")).getBukkit();
    public static final ItemStack GROUP_MANUAL_MACHINES = new CustomItemStack(
            Material.BOOK,
            IRDock.getLanguageManager().getGroupName("manual_machines")).getBukkit();
    public static final ItemStack GROUP_MATERIALS = new CustomItemStack(
            Material.IRON_INGOT,
            IRDock.getLanguageManager().getGroupName("materials")).getBukkit();
    public static final ItemStack GROUP_SMELTING = new CustomItemStack(
            Material.GOLD_INGOT,
            IRDock.getLanguageManager().getGroupName("smelting")).getBukkit();
    public static final ItemStack GROUP_ELECTRIC_MACHINES = new CustomItemStack(
            Material.REDSTONE_BLOCK,
            IRDock.getLanguageManager().getGroupName("electric_machines")).getBukkit();
    public static final ItemStack GROUP_TOOLS = new CustomItemStack(
            Material.DIAMOND_PICKAXE,
            IRDock.getLanguageManager().getGroupName("tools")).getBukkit();
    public static final ItemStack GROUP_ARMORS = new CustomItemStack(
            Material.DIAMOND_CHESTPLATE,
            IRDock.getLanguageManager().getGroupName("armors")).getBukkit();
    public static final ItemStack GROUP_DEFENSE = new CustomItemStack(
            Material.IRON_SWORD,
            IRDock.getLanguageManager().getGroupName("defense")).getBukkit();
    public static final ItemStack GROUP_FOOD = new CustomItemStack(
            Material.BREAD,
            IRDock.getLanguageManager().getGroupName("food")).getBukkit();
    public static final ItemStack GROUP_MISC = new CustomItemStack(
            Material.PAPER,
            IRDock.getLanguageManager().getGroupName("misc")).getBukkit();
    public static final ItemStack GROUP_COMPONENTS = new CustomItemStack(
            Material.IRON_INGOT,
            IRDock.getLanguageManager().getGroupName("components")).getBukkit();
    public static final ItemStack GROUP_MULTIBLOCK = new CustomItemStack(
            Material.BRICK,
            IRDock.getLanguageManager().getGroupName("multiblock")).getBukkit();
}
