package xyz.maquzo.dazumatwitch.bot.commands;

import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.permission.IPermissionUser;

import java.awt.Color;
import java.io.IOException;
import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.requests.RestAction;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;
import xyz.maquzo.dazumatwitch.DazumaTwitch;
import xyz.maquzo.dazumatwitch.bot.commands.types.SlashCommand;

public class VerifyCommand implements SlashCommand, Listener, CommandExecutor {
    private DazumaTwitch plugin;

    public VerifyCommand(DazumaTwitch plugin) {
        this.plugin = plugin;
    }

    public void performCommand(SlashCommandEvent event, Member member, TextChannel channel) {
        if (event.getTextChannel().getId().equals("949332562654928957")) {
            User user = member.getUser();
            EmbedBuilder failed = new EmbedBuilder();
            failed.setTitle(":x: **| Verifikation fehlgeschlagen.**");
            failed.setDescription(" > Die **Verifikation** ist fehlgeschlagen.\n > Probiere es in **einer** Minute erneut.\n");
            failed.setColor(Color.RED);
            failed.setTimestamp(Instant.now());
            String randomcode = Long.toHexString(Double.doubleToLongBits(Math.random()));
            Player target = Bukkit.getPlayer(((OptionMapping) Objects.<OptionMapping>requireNonNull(event.getOption("name"))).getAsString());
            if (target == null) {
                event.reply("Der Spieler ist nicht auf dem Server!").setEphemeral(true).queue();
                sendMessage(user, failed);
                return;
            }
            EmbedBuilder success = new EmbedBuilder();
            success.setTitle(":white_check_mark:  **| Verifikation erfolgreich.**");
            success.setDescription("> Die **Verifaktion** war erfolgreich. \n > Dein **Code** lautet `" + randomcode + "`");
            success.setColor(Color.GREEN);
            success.setTimestamp(Instant.now());
            success.setThumbnail("https://mc-heads.net/head/" + target.getName());
            if (isSub(target)) {
                event.reply("Du bist bereits ein Sub!").setEphemeral(true).queue();
                sendMessage(user, failed);
                return;
            }
            if (this.plugin.uuidIdMap.values().contains(member.getId())) {
                event.reply("Du besitzt bereits einen Code!").setEphemeral(true).queue();
                sendMessage(user, failed);
                return;
            }
            this.plugin.uuidCodeMap.put(target.getUniqueId(), randomcode);
            this.plugin.uuidIdMap.put(target.getUniqueId(), member.getId());
            event.reply("Du solltest einen Code via DM bekommen haben!").queue();
            sendMessage(user, success);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        if (this.plugin.playerData.contains("Data." + e.getPlayer().getUniqueId().toString()))
            this.plugin.verifiedMembers.add(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        this.plugin.verifiedMembers.remove(e.getPlayer().getUniqueId());
        this.plugin.uuidCodeMap.remove(e.getPlayer().getUniqueId());
        this.plugin.uuidIdMap.remove(e.getPlayer().getUniqueId());
    }

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) return true;
        Player player = (Player) sender;
        if (isSub(player)) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&5TWITCH&8] &cDu besitzt bereits den Sub rang."));
            return true;
        }
        if (!this.plugin.uuidCodeMap.containsKey(player.getUniqueId())) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&5TWITCH&8] &cDu besitzt keinen Code."));
            return true;
        }
        if (args.length != 1) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&5TWITCH&8] &cBenutze /verify <CODE>"));
            return true;
        }
        String actualcode = (String) this.plugin.uuidCodeMap.get(player.getUniqueId());
        if (!actualcode.equals(args[0])) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&5TWITCH&8] &cFalscher Code!"));
            return true;
        }
        String discordid = (String) this.plugin.uuidIdMap.get(player.getUniqueId());
        User user = this.plugin.getDiscordBot().getUserById(discordid);
        this.plugin.playerData.set("Data." + player.getUniqueId().toString(), discordid);
        try {
            this.plugin.playerData.save(this.plugin.data);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.plugin.uuidCodeMap.remove(player.getUniqueId());
        this.plugin.uuidIdMap.remove(player.getUniqueId());
        this.plugin.verifiedMembers.add(player.getUniqueId());
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&5TWITCH&8] &aDu besitzt nun deinen Sub rang für 30 Tage!"));
        addSoubOne(player);
        return true;
    }

    public void sendMessage(User user, EmbedBuilder embedBuilder) {
        user.openPrivateChannel().flatMap(channel -> channel.sendMessageEmbeds(embedBuilder.build(), new net.dv8tion.jda.api.entities.MessageEmbed[0])).queue();
    }

    public void addSoubOne(Player player) {
        CloudNetDriver.getInstance().getPermissionManagement().modifyUser(player.getUniqueId(), permissionUser -> permissionUser.addGroup("Sub", 30L, TimeUnit.DAYS));
    }

    public void addSoubTwo(Player player) {
        CloudNetDriver.getInstance().getPermissionManagement().modifyUser(player.getUniqueId(), permissionUser -> permissionUser.addGroup("Sub-II", 30L, TimeUnit.DAYS));
    }

    public void addSoubThree(Player player) {
        CloudNetDriver.getInstance().getPermissionManagement().modifyUser(player.getUniqueId(), permissionUser -> permissionUser.addGroup("Sub-III", 30L, TimeUnit.DAYS));
    }

    public boolean isSub(Player player) {
        IPermissionUser user = CloudNetDriver.getInstance().getPermissionManagement().getUser(player.getUniqueId());
        return CloudNetDriver.getInstance().getPermissionManagement().getUsersByGroup("Sub").contains(user);
    }
}
