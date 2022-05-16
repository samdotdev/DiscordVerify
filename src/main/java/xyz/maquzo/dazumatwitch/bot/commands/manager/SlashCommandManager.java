package xyz.maquzo.dazumatwitch.bot.commands.manager;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import org.jetbrains.annotations.NotNull;
import xyz.maquzo.dazumatwitch.DazumaTwitch;
import xyz.maquzo.dazumatwitch.bot.commands.VerifyCommand;
import xyz.maquzo.dazumatwitch.bot.commands.types.SlashCommand;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SlashCommandManager extends ListenerAdapter {

    private Map<String, SlashCommand> commandsMap;

    private DazumaTwitch plugin;

    public SlashCommandManager(DazumaTwitch plugin) {
        commandsMap = new ConcurrentHashMap<>();


        commandsMap.put("verify", new VerifyCommand(plugin));

        CommandListUpdateAction commands = plugin.discordBot.updateCommands();


        commands.addCommands(new CommandData("verify", "verify with your Minecraft Username")
                .addOptions(new OptionData(OptionType.STRING, "name", "Your Minecraft Name").setRequired(true))).queue();

    }

    @Override
    public void onSlashCommand(@NotNull SlashCommandEvent event) {
        String commandName = event.getName();

        SlashCommand command;

        if ((command = commandsMap.get(commandName)) != null) {
            command.performCommand(event, event.getMember(), event.getTextChannel());
        }
    }
}
