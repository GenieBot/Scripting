package io.sponges.bot.modules.scripting.cmd;

import io.sponges.bot.api.cmd.Command;
import io.sponges.bot.api.cmd.CommandRequest;
import io.sponges.bot.api.entities.User;
import io.sponges.bot.modules.scripting.Scripting;

public class ReloadScriptsCommand extends Command {

    private final Scripting scripting;

    public ReloadScriptsCommand(Scripting scripting) {
        super("reloads all the currently loaded scripts", "reloadscripts");
        this.scripting = scripting;
    }

    @Override
    public void onCommand(CommandRequest request, String[] args) {
        User user = request.getUser();
        if (!user.isOp()) {
            request.reply("Only the bot owner can do that!");
            return;
        }
        request.reply("Disabling scripts...");
        scripting.onDisable();
        request.reply("Loading scripts...");
        scripting.loadScripts(scripting.getDirectory());
        request.reply("Reload complete!");
    }
}
