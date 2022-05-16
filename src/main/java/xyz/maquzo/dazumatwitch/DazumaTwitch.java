package xyz.maquzo.dazumatwitch;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import javax.security.auth.login.LoginException;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.maquzo.dazumatwitch.bot.commands.MainCommand;
import xyz.maquzo.dazumatwitch.bot.commands.VerifyCommand;
import xyz.maquzo.dazumatwitch.bot.commands.manager.SlashCommandManager;

public final class DazumaTwitch extends JavaPlugin {
    public JDA discordBot;

    public FileConfiguration playerData;

    public File data;

    public HashMap<UUID, String> uuidCodeMap;

    public HashMap<UUID, String> uuidIdMap;

    public List<UUID> verifiedMembers;

    public Guild guild;

    public static SlashCommandManager slashCommandManager;

    public DazumaTwitch plugin;

    public void onEnable() {
        this.plugin = this.plugin;
        this.discordBot = this.discordBot;
        saveDefaultConfig();
        initializeDiscord();
        createConfig();
        PluginManager pluginManager = Bukkit.getPluginManager();
        getCommand("verify").setExecutor((CommandExecutor) new VerifyCommand(this));
        this.uuidIdMap = new HashMap<>();
        this.uuidCodeMap = new HashMap<>();
        this.verifiedMembers = new ArrayList<>();
    }

    public void onDisable() {
    }

    public void initializeDiscord() {
        String discordToken = getConfig().getString("DISCORD_TOKEN");
        if (discordToken == null) {
            getServer().getPluginManager().disablePlugin((Plugin) this);
            getLogger().severe("Bitte gebe einen gültigen DISCORD_TOKEN in der config.yml an!");
            return;
        }
        try {
            this.discordBot = JDABuilder.createDefault(discordToken).build();
            this.discordBot.getPresence().setActivity(Activity.streaming(" ", "https://www.twitch.tv/dazuma"));
            this.discordBot.getPresence().setStatus(OnlineStatus.DO_NOT_DISTURB);
            this.discordBot.addEventListener(new Object[]{new MainCommand(this)});
            this.discordBot.addEventListener(new Object[]{slashCommandManager = new SlashCommandManager(this)});
            this.discordBot.awaitReady();
            this.discordBot.getGuildById("295687391367135234");
            this.guild = this.discordBot.getGuildById("295687391367135234");
        } catch (LoginException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public JDA getDiscordBot() {
        return this.discordBot;
    }

    private void createConfig() {
        this.data = new File("" + getDataFolder() + getDataFolder() + "data.yml");
        if (!this.data.exists())
            saveResource("data.yml", false);
        this.playerData = (FileConfiguration) new YamlConfiguration();
        try {
            this.playerData.load(this.data);
        } catch (IOException | org.bukkit.configuration.InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public HashMap<UUID, String> getUuidCodeMap() {
        return this.uuidCodeMap;
    }

    public HashMap<UUID, String> getUuidIdMap() {
        return this.uuidIdMap;
    }

    public void setUuidCodeMap(HashMap<UUID, String> uuidCodeMap) {
        this.uuidCodeMap = uuidCodeMap;
    }

    public void setUuidIdMap(HashMap<UUID, String> uuidIdMap) {
        this.uuidIdMap = uuidIdMap;
    }

    public void setVerifiedMembers(List<UUID> verifiedMembers) {
        this.verifiedMembers = verifiedMembers;
    }

    public List<UUID> getVerifiedMembers() {
        return this.verifiedMembers;
    }

    public Guild getGuild() {
        return this.guild;
    }

    public DazumaTwitch getPlugin() {
        return this.plugin;
    }
}
