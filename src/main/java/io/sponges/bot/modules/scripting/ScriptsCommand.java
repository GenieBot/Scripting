package io.sponges.bot.modules.scripting;

import io.sponges.bot.api.cmd.Command;
import io.sponges.bot.api.cmd.CommandRequest;

import java.util.Iterator;
import java.util.List;

public class ScriptsCommand extends Command {

    private final Scripting scripting;

    public ScriptsCommand(Scripting scripting) {
        super("shows all currently loaded scripts", "scripts");
        this.scripting = scripting;
    }

    @Override
    public void onCommand(CommandRequest request, String[] args) {
        StringBuilder builder = new StringBuilder("Loaded scripts ");
        List<Script> scripts = scripting.getScripts();
        builder.append("(").append(scripts.size()).append("): ");
        Iterator<Script> iterator = scripts.iterator();
        while (iterator.hasNext()) {
            Script script = iterator.next();
            builder.append(script.getName());
            if (iterator.hasNext()) {
                builder.append(", ");
            }
        }
        request.reply(builder.toString());
    }
}
