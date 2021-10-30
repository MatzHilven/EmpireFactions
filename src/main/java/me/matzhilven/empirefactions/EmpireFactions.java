package me.matzhilven.empirefactions;

import me.matzhilven.empirefactions.commands.empire.EmpireBaseCommand;
import me.matzhilven.empirefactions.commands.empire.subcommands.*;
import me.matzhilven.empirefactions.commands.faction.FactionBaseCommand;
import me.matzhilven.empirefactions.commands.faction.subcommands.*;
import me.matzhilven.empirefactions.data.Database;
import me.matzhilven.empirefactions.data.PlayerData;
import me.matzhilven.empirefactions.data.mysql.MySQL;
import me.matzhilven.empirefactions.empire.EmpireManager;
import me.matzhilven.empirefactions.empire.chunk.ChunkManager;
import me.matzhilven.empirefactions.hooks.PlaceHolderAPIHook;
import me.matzhilven.empirefactions.listeners.InventoryListeners;
import me.matzhilven.empirefactions.listeners.PlayerListeners;
import me.matzhilven.empirefactions.managers.AdminManager;
import me.matzhilven.empirefactions.utils.Logger;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public final class EmpireFactions extends JavaPlugin {

    private FileConfiguration messages;
    private FileConfiguration guiConfig;
    private EmpireManager empireManager;
    private ChunkManager chunkManager;
    private AdminManager adminManager;
    private Database db;
    private Economy econ;

    @Override
    public void onEnable() {

        if (!setupEconomy() ) {
            Logger.severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        Logger.log("Creating files...");
        createFiles();
        Logger.log("Successfully created files");

        Logger.log("Loading database...");
        db = new MySQL(this);
        db.load();
        Logger.log("Successfully loaded database");

        Logger.log("Loading EmpireManager...");
        empireManager = new EmpireManager(this);
        Logger.log("Successfully loaded EmpireManager");

        Logger.log("Loading ChunkManager...");
        chunkManager = new ChunkManager(this);
        Logger.log("Successfully loaded ChunkManager");

        Logger.log("Loading AdminManager...");
        adminManager = new AdminManager();
        Logger.log("Successfully loaded AdminManager");

        Logger.log("Registering commands...");
        registerCommands();
        Logger.log("Successfully registered commands");

        Logger.log("Registering listeners...");
        new InventoryListeners(this);
        new PlayerListeners(this);
        Logger.log("Successfully registered listeners");

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PlaceHolderAPIHook(this).register();
            Logger.log("Registered Placeholders");
        }

        Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
            int power = this.getConfig().getInt("power-per-hour");
            Bukkit.getOnlinePlayers().forEach(player -> PlayerData.get(player.getUniqueId()).addPower(power));
        }, 20L * 60L, 20L * 60L * 60L);
    }

    @Override
    public void onDisable() {
        getDb().saveEmpires();
    }

    private void registerCommands() {
        EmpireBaseCommand empireCommand = new EmpireBaseCommand(this);
        empireCommand.registerSubCommand(new CreateSubCommand(this));
        empireCommand.registerSubCommand(new InfoSubCommand(this));
        empireCommand.registerSubCommand(new SetDescriptionSubCommand(this));
        empireCommand.registerSubCommand(new SetColorSubCommand(this));
        empireCommand.registerSubCommand(new PromoteSubCommand(this));
        empireCommand.registerSubCommand(new DemoteSubCommand(this));
        empireCommand.registerSubCommand(new GetCoreSubCommand(this));
        empireCommand.registerSubCommand(new TransferSubCommand(this));
        empireCommand.registerSubCommand(new SetCenterSubCommand(this));
        empireCommand.registerSubCommand(new RenameSubCommand(this));
        empireCommand.registerSubCommand(new PowerSubCommand(this));

        FactionBaseCommand factionCommand = new FactionBaseCommand(this);
        factionCommand.registerSubCommand(new FactionCreateSubCommand(this));
        factionCommand.registerSubCommand(new FactionDisbandSubCommand(this));
        factionCommand.registerSubCommand(new FactionTransferSubCommand(this));
        factionCommand.registerSubCommand(new FactionInfoSubCommand(this));
        factionCommand.registerSubCommand(new FactionRenameSubCommand(this));
        factionCommand.registerSubCommand(new FactionSetDescriptionSubCommand(this));
        factionCommand.registerSubCommand(new FactionSetTitleSubCommand(this));
        factionCommand.registerSubCommand(new FactionSetTagSubCommand(this));
        factionCommand.registerSubCommand(new FactionSetHomeSubCommand(this));

        factionCommand.registerSubCommand(new FactionOpenSubCommand(this));
        factionCommand.registerSubCommand(new FactionInviteSubCommand(this));
        factionCommand.registerSubCommand(new FactionAcceptSubCommand(this));
        factionCommand.registerSubCommand(new FactionKickSubCommand(this));
        factionCommand.registerSubCommand(new FactionJoinSubCommand(this));

        factionCommand.registerSubCommand(new FactionMapSubCommand(this));

        factionCommand.registerSubCommand(new FactionClaimSubCommand(this));
        factionCommand.registerSubCommand(new FactionUnClaimSubCommand(this));

        factionCommand.registerSubCommand(new FactionAllySubCommand(this));
        factionCommand.registerSubCommand(new FactionNeutralSubCommand(this));
        factionCommand.registerSubCommand(new FactionAllowAllySubCommand(this));

        factionCommand.registerSubCommand(new FactionChatSubCommand(this));

        factionCommand.registerSubCommand(new FactionMuteSubCommand(this));
        factionCommand.registerSubCommand(new FactionUnMuteSubCommand(this));

        factionCommand.registerSubCommand(new FactionBalanceSubCommand(this));
        factionCommand.registerSubCommand(new FactionDepositSubCommand(this));
        factionCommand.registerSubCommand(new FactionWithdrawSubCommand(this));

        factionCommand.registerSubCommand(new FactionHomeSubCommand(this));

        factionCommand.registerSubCommand(new FactionPlayerSubCommand(this));
        factionCommand.registerSubCommand(new FactionPowerSubCommand(this));

        factionCommand.registerSubCommand(new FactionAdminSubCommand(this));
        factionCommand.registerSubCommand(new FactionReloadSubCommand(this));
        factionCommand.registerSubCommand(new FactionSetPowerSubCommand(this));
        factionCommand.registerSubCommand(new FactionChatSpySubCommand(this));
    }

    private void createFiles() {
        saveDefaultConfig();

        File messagesFile = new File(getDataFolder(), "messages.yml");
        File guiFile = new File(getDataFolder(), "gui.yml");

        if (!messagesFile.exists()) {
            messagesFile.getParentFile().mkdir();
            saveResource("messages.yml", false);
        }

        if (!guiFile.exists()) {
            guiFile.getParentFile().mkdir();
            saveResource("gui.yml", false);
        }

        messages = new YamlConfiguration();
        guiConfig = new YamlConfiguration();

        try {
            messages.load(messagesFile);
            guiConfig.load(guiFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public void reloadFiles() {
        reloadConfig();

        File messagesFile = new File(getDataFolder(), "messages.yml");
        File guiFile = new File(getDataFolder(), "gui.yml");

        messages = new YamlConfiguration();
        guiConfig = new YamlConfiguration();

        try {
            messages.load(messagesFile);
            guiConfig.load(guiFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    public Database getDb() {
        return db;
    }

    public EmpireManager getEmpireManager() {
        return empireManager;
    }

    public ChunkManager getChunkManager() {
        return chunkManager;
    }

    public AdminManager getAdminManager() {
        return adminManager;
    }

    public FileConfiguration getMessages() {
        return messages;
    }

    public FileConfiguration getGuiConfig() {
        return guiConfig;
    }

    public Economy getEcon() {
        return econ;
    }
}
