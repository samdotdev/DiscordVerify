package xyz.maquzo.dazumatwitch.bot.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import xyz.maquzo.dazumatwitch.DazumaTwitch;

import java.awt.*;
import java.io.IOException;
import java.time.Instant;
import java.util.Arrays;

public class MainCommand extends ListenerAdapter {

    private DazumaTwitch plugin;

    public MainCommand(DazumaTwitch plugin) {
        this.plugin = plugin;
    }

    public void onMessageReceived(MessageReceivedEvent event) {




        String[] args = event.getMessage().getContentRaw().split(" ");


        if (args.length != 1 ) return;
        if (args[0].equalsIgnoreCase("!help")) {
            try {
                event.getMessage().delete().queue();
                EmbedBuilder eb = new EmbedBuilder();
                eb.setTitle(":envelope_with_arrow: **| Sub-Verification.**");
                eb.setDescription("""
                                  > **1)** Joine auf den Server **dazuma.de** (1.18.1)
                                  > **2)** Verbinde dein **Discord Konto** mit deinem **Twitch Konto** ([Siehe hier](https://www.remote.tools/remote-work/link-twitch-to-discord))
                                  > **3)** Schreibe in den **#verify** Channel **!verify <MINECRAFT_NAME>**
                                  > **4)** Du solltest nun einen **Code** via Privat Nachricht erhalten haben.
                                  > **5)** Führe nun den command **/verify <CODE>** auf dem Minecraft Server aus.
                                  """);
                eb.setColor(new Color(100, 65, 165));
                eb.setThumbnail("https://seeklogo.com/images/T/twitch-tv-logo-51C922E0F0-seeklogo.com.png");
                eb.setTimestamp(Instant.now());

                event.getChannel().sendMessageEmbeds(eb.build()).queue();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
