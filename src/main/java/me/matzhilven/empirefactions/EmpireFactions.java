package me.matzhilven.empirefactions;

import me.matzhilven.empirefactions.commands.empire.EmpireBaseCommand;
import me.matzhilven.empirefactions.commands.empire.subcommands.CreateSubCommand;
import me.matzhilven.empirefactions.commands.empire.subcommands.InfoSubCommand;
import me.matzhilven.empirefactions.commands.empire.subcommands.SetDescriptionSubCommand;
import me.matzhilven.empirefactions.empire.EmpireManager;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public final class EmpireFactions extends JavaPlugin {

    private FileConfiguration messages;
    private EmpireManager empireManager;

    @Override
    public void onEnable() {
        createFiles();

        empireManager = new EmpireManager(this);

        registerCommands();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private void registerCommands() {
        EmpireBaseCommand empireCommand = new EmpireBaseCommand(this);
        empireCommand.registerSubCommand(new CreateSubCommand(this));
        empireCommand.registerSubCommand(new InfoSubCommand(this));
        empireCommand.registerSubCommand(new SetDescriptionSubCommand(this));
    }

    private void createFiles() {
        saveDefaultConfig();

        File messagesFile = new File(getDataFolder(), "messages.yml");

        if (!messagesFile.exists()) {
            messagesFile.getParentFile().mkdir();
            saveResource("messages.yml", false);
        }

        messages = new YamlConfiguration();

        try {
            messages.load(messagesFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public EmpireManager getEmpireManager() {
        return empireManager;
    }

    public FileConfiguration getMessages() {
        return messages;
    }
}
