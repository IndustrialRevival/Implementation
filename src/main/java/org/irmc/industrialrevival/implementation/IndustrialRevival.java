package org.irmc.industrialrevival.implementation;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.tcoded.folialib.FoliaLib;
import com.tcoded.folialib.wrapper.task.WrappedTask;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.irmc.industrialrevival.api.IndustrialRevivalAddon;
import org.irmc.industrialrevival.core.listeners.*;
import org.irmc.industrialrevival.core.managers.ListenerManager;
import org.irmc.industrialrevival.core.services.IGitHubService;
import org.irmc.industrialrevival.core.services.IIRDataManager;
import org.irmc.industrialrevival.core.services.IIRRegistry;
import org.irmc.industrialrevival.core.services.IItemDataService;
import org.irmc.industrialrevival.core.services.IItemSettings;
import org.irmc.industrialrevival.core.services.IListenerManager;
import org.irmc.industrialrevival.core.services.IMinecraftRecipeService;
import org.irmc.industrialrevival.core.services.IRunningProfilerService;
import org.irmc.industrialrevival.core.services.ISQLDataManager;

import org.irmc.industrialrevival.implementation.services.GitHubService;
import org.irmc.industrialrevival.implementation.services.IRDataManager;
import org.irmc.industrialrevival.implementation.services.SQLDataManager;
import org.irmc.industrialrevival.implementation.services.IRRegistry;
import org.irmc.industrialrevival.implementation.services.ItemDataService;
import org.irmc.industrialrevival.implementation.services.ItemSettings;
import org.irmc.industrialrevival.implementation.services.LanguageTextService;
import org.irmc.industrialrevival.implementation.services.RunningProfilerService;
import org.irmc.industrialrevival.core.task.AnitEnderDragonTask;
import org.irmc.industrialrevival.core.task.ArmorCheckTask;
import org.irmc.industrialrevival.core.task.PostSetupTask;
import org.irmc.industrialrevival.core.world.populators.ElementOreGenerator;
import org.irmc.industrialrevival.api.IIndustrialRevivalPlugin;
import org.irmc.industrialrevival.implementation.command.IRCommandGenerator;
import org.irmc.industrialrevival.implementation.groups.IRItemGroups;
import org.irmc.industrialrevival.implementation.items.IndustrialRevivalItemSetup;
import org.irmc.industrialrevival.utils.Constants;
import org.irmc.industrialrevival.utils.WorldUtil;
import org.irmc.industrialrevival.api.enums.Language;
import org.irmc.industrialrevival.utils.ConfigFileUtil;
import org.irmc.industrialrevival.api.language.LanguageManager;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public final class IndustrialRevival extends JavaPlugin implements IIndustrialRevivalPlugin {
    private @Getter static IndustrialRevival instance;

    private @Getter LanguageManager languageManager;
    private @Getter LanguageTextService languageTextService;

    private @Getter IIRRegistry registry;
    private @Getter ISQLDataManager sqlDataManager;
    private @Getter IIRDataManager irDataManager;
    private @Getter IItemDataService itemDataService;
    private @Getter IRunningProfilerService runningProfilerService;
    private @Getter FoliaLib foliaLibImpl;
    private @Getter IItemSettings itemSettings;
    private @Getter ElementOreGenerator elementOreGenerator;
    private @Getter IGitHubService githubService;
    private @Getter ProtocolManager protocolManager;
    private @Getter IListenerManager listenerManager;
    private @Getter IMinecraftRecipeService minecraftRecipeService;

    @Override
    public void runSync(@NotNull Runnable runnable) {
        IndustrialRevival.getInstance().getFoliaLibImpl().getScheduler().runNextTick(_ -> runnable.run());
    }
    
    @Override
    public void runSync(@NotNull Consumer<WrappedTask> consumer) {
        IndustrialRevival.getInstance().getFoliaLibImpl().getScheduler().runNextTick(consumer);
    }

    @Override
    public @NotNull ISQLDataManager getSQLDataManager() {
        return sqlDataManager;
    }

    @Override
    public @NotNull IIRDataManager getDataManager() {
        return irDataManager;
    }

    @Override
    public @NotNull IGitHubService getGitHubService() {
        return githubService;
    }

    @Override
    public void runAsync(@NotNull Runnable runnable) {
        IndustrialRevival.getInstance().getFoliaLibImpl().getScheduler().runAsync(_ -> runnable.run());
    }
    
    @Override
    public void runAsync(@NotNull Consumer<WrappedTask> consumer) {
        IndustrialRevival.getInstance().getFoliaLibImpl().getScheduler().runAsync(consumer);
    }

    public @NotNull List<IndustrialRevivalAddon> getAddons() {
        String pluginName = instance.getName();

        return Arrays.stream(instance.getServer().getPluginManager().getPlugins())
                .filter(plugin -> plugin instanceof IndustrialRevivalAddon)
                .map(plugin -> (IndustrialRevivalAddon) plugin)
                .toList();
    }

    @Override
    public @NotNull IListenerManager getListenerManager() {
        return listenerManager;
    }

    @Override
    public void onLoad() {
        instance = this;

        CommandAPI.onLoad(new CommandAPIBukkitConfig(this));
        IRCommandGenerator.registerCommand(this);

        foliaLibImpl = new FoliaLib(this);

        completeFiles();

        itemSettings = new ItemSettings(YamlConfiguration.loadConfiguration(Constants.Files.ITEM_SETTINGS_FILE));

        setupProtocolLib();

        System.setProperty("org.jooq.no-logo", "true");
        System.setProperty("org.jooq.no-tips", "true");
    }

    @Override
    public void onEnable() {
        getLogger().info("IndustrialRevival is being enabled!");

        getLogger().info("Setting up data manager...");
        setupDataManager();

        languageManager = new LanguageManager(this, Language.ZH_CN);
        registry = new IRRegistry();

        getLogger().info("Setting up items...");
        setupIndustrialRevivalItems();

        getLogger().info("Setting up listeners...");
        listenerManager = new ListenerManager();
        listenerManager.registerListener(new BulkDensityListener());
        listenerManager.registerListener(new DefaultHandler());
        listenerManager.registerListener(new DropListener());
        listenerManager.registerListener(new EventCreator());
        listenerManager.registerListener(new EventPrechecker());
        listenerManager.registerListener(new GuideListener());
        listenerManager.registerListener(new HandlerCaller());
        listenerManager.registerListener(new LimitedItemListener());
        listenerManager.registerListener(new MachineMenuListener());
        listenerManager.registerListener(new MultiBlockListener());
        listenerManager.registerListener(new NotPlaceableListener());
        listenerManager.registerListener(new PlayerJoinListener());
        listenerManager.registerListener(new RespondTimingListener());
        listenerManager.registerListener(new UnusableItemListener());
        listenerManager.registerListener(new MultiblockTicker());

        getLogger().info("Setting up services...");
        setupServices();

        getLogger().info("Setting up tasks...");
        setupTasks();

        getLogger().info("Adding block populators...");
        this.elementOreGenerator = new ElementOreGenerator();
        World overworld = Bukkit.getWorld("world");
        if (overworld != null) {
            WorldUtil.addPopulatorTo(overworld, elementOreGenerator);
        }

        getComponentLogger().info(LanguageManager.parseToComponent("<green>Industrial Revival has been enabled!"));
    }

    private void completeFiles() {
        ConfigFileUtil.completeFile(this, "config.yml");
        //ConfigFileUtil.completeLangFile(this, "language/en-US.yml");
        ConfigFileUtil.completeLangFile(this, "language/zh-CN.yml");

        if (!Constants.Files.ITEM_SETTINGS_FILE.exists()) {
            saveResource("items-settings.yml", false);
        }
    }

    private void setupIndustrialRevivalItems() {
        IRItemGroups.setup();
        IndustrialRevivalItemSetup.setup();
    }

    private void setupServices() {
        irDataManager = new IRDataManager();
        itemDataService = new ItemDataService();
        runningProfilerService = new RunningProfilerService();
        languageTextService = new LanguageTextService();
        githubService = new GitHubService();
    }

    private void setupDataManager() {
        if (!Constants.Files.STORAGE_FOLDER.exists()) {
            Constants.Files.STORAGE_FOLDER.mkdirs();
        }

        sqlDataManager = new SQLDataManager(this);
        sqlDataManager.init();
    }

    private void setupTasks() {
        int checkInterval = getConfig().getInt("options.armor-check-interval", 1);
        foliaLibImpl.getScheduler().runTimerAsync(new ArmorCheckTask(checkInterval), checkInterval, checkInterval);
        foliaLibImpl.getScheduler().runTimerAsync(IndustrialRevival.getInstance().getRunningProfilerService().getTask(), checkInterval, checkInterval);
        int deEnderDragonCheckInterval = getConfig().getInt("options.anti-ender-dragon-check.interval", 20);
        int deEnderDragonCheckRadius = getConfig().getInt("options.anti-ender-dragon-check.radius", 20);
        foliaLibImpl.getScheduler().runTimerAsync(new AnitEnderDragonTask(deEnderDragonCheckRadius), deEnderDragonCheckInterval, deEnderDragonCheckInterval);
        foliaLibImpl.getScheduler().runAsync(new PostSetupTask());
    }

    private void setupProtocolLib() {
        this.protocolManager = ProtocolLibrary.getProtocolManager();
    }

    @Override
    public void onDisable() {
        itemSettings.saveSettings();

        if (irDataManager != null) {
            irDataManager.saveAllData();
        }

        if (sqlDataManager != null) {
            sqlDataManager.close();
        }

        getLogger().info("IndustrialRevival has been disabled!");
    }

    @Override
    public @NotNull JavaPlugin getPlugin() {
        return this;
    }

    @Override
    public String getIssueTrackerURL() {
        return "https://github.com/IndustrialRevival/IndustrialRevival/issues";
    }

    @Override
    public void reloadConfig() {
        super.reloadConfig();

        languageManager = new LanguageManager(this);
    }
}
