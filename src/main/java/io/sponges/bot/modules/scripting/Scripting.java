package io.sponges.bot.modules.scripting;

import io.sponges.bot.api.Logger;
import io.sponges.bot.api.cmd.Command;
import io.sponges.bot.api.cmd.CommandManager;
import io.sponges.bot.api.module.Module;
import io.sponges.bot.modules.scripting.cmd.ReloadScriptsCommand;
import io.sponges.bot.modules.scripting.cmd.ScriptsCommand;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Scripting extends Module {

    private final ScriptEngineManager manager = new ScriptEngineManager();
    private final List<Script> scripts = new CopyOnWriteArrayList<>();
    private final File directory = new File("scripts");

    public Scripting() {
        super("Scripting", "1.01");
    }

    @SuppressWarnings({"ResultOfMethodCallIgnored", "ConstantConditions"})
    @Override
    public void onEnable() {
        loadScripts(directory);
        CommandManager commandManager = getCommandManager();
        commandManager.registerCommand(this, new ScriptsCommand(this));
        commandManager.registerCommand(this, new ReloadScriptsCommand(this));
    }

    @Override
    public void onDisable() {
        for (Script script : scripts) {
            try {
                script.onDisable();
            } catch (ScriptException e) {
                e.printStackTrace();
            }
        }
        CommandManager commandManager = getCommandManager();
        for (Command command : commandManager.getCommands(this)) {
            String primaryName = command.getNames()[0];
            if (primaryName.equals("scripts") || primaryName.endsWith("reloadscripts")) continue;
            commandManager.unregisterCommand(command);
        }
    }

    @SuppressWarnings("ConstantConditions")
    public void loadScripts(File directory) {
        if (!scripts.isEmpty()) {
            scripts.clear();
        }
        if (!directory.exists()) {
            getLogger().log(Logger.Type.DEBUG, "\"scripts\" directory does not exist! Creating the directory...");
            try {
                directory.createNewFile();
                getLogger().log(Logger.Type.INFO, "Created the \"scripts\" directory. Please populate it with scripts!");
            } catch (IOException e) {
                getLogger().log(Logger.Type.WARNING, "Could not create the \"scripts\" directory: " + e.getMessage());
            }
            return;
        }
        if (!directory.isDirectory()) {
            getLogger().log(Logger.Type.WARNING, "\"scripts\" file is not a directory!");
            return;
        }
        for (File file : directory.listFiles()) {
            getLogger().log(Logger.Type.INFO, "Loading \"" + file.getName() + "\"...");
            ScriptEngine engine = manager.getEngineByName("nashorn");
            engine.put("module", this);
            try {
                engine.eval(new FileReader(file));
            } catch (ScriptException | FileNotFoundException e) {
                e.printStackTrace();
            }
            Script script = new Script(engine, file.getName());
            scripts.add(script);
            try {
                script.onEnable();
            } catch (ScriptException | NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
    }

    public List<Script> getScripts() {
        return scripts;
    }

    public File getDirectory() {
        return directory;
    }
}
