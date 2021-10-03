package me.matzhilven.empirefactions;

import me.matzhilven.empirefactions.commands.empire.EmpireBaseCommand;
import me.matzhilven.empirefactions.commands.empire.subcommands.*;
import me.matzhilven.empirefactions.commands.faction.FactionBaseCommand;
import me.matzhilven.empirefactions.commands.faction.subcommands.*;
import me.matzhilven.empirefactions.data.Database;
import me.matzhilven.empirefactions.data.mysql.MySQL;
import me.matzhilven.empirefactions.empire.EmpireManager;
import me.matzhilven.empirefactions.listeners.InventoryListeners;
import me.matzhilven.empirefactions.listeners.PlayerListeners;
import me.matzhilven.empirefactions.utils.Logger;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public final class EmpireFactions extends JavaPlugin {

    private FileConfiguration messages;
    private FileConfiguration guiConfig;
    private EmpireManager empireManager;
    private Database db;

    @Override
    public void onEnable() {
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

        Logger.log("Registering commands...");
        registerCommands();
        Logger.log("Successfully registered commands");

        Logger.log("Registering listeners...");
        new InventoryListeners(this);
        new PlayerListeners(this);
        Logger.log("Successfully registered listeners");

        Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> this.getDb().saveEmpires(), 20L * 60L * 5L,
                20L * 60L * 5L);
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

        FactionBaseCommand factionCommand = new FactionBaseCommand(this);
        factionCommand.registerSubCommand(new FactionCreateSubCommand(this));
        factionCommand.registerSubCommand(new FactionInfoSubCommand(this));
        factionCommand.registerSubCommand(new FactionSetDescriptionSubCommand(this));
        factionCommand.registerSubCommand(new FactionDisbandSubCommand(this));
        factionCommand.registerSubCommand(new FactionInviteSubCommand(this));
        factionCommand.registerSubCommand(new FactionAcceptSubCommand(this));
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

    public Database getDb() {
        return db;
    }

    public EmpireManager getEmpireManager() {
        return empireManager;
    }

    public FileConfiguration getMessages() {
        return messages;
    }

    public FileConfiguration getGuiConfig() {
        return guiConfig;
    }
}
