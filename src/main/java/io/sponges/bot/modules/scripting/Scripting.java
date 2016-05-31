package io.sponges.bot.modules.scripting;

import io.sponges.bot.api.module.Module;

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

    public Scripting() {
        super("Scripting", "1.01");
    }

    @SuppressWarnings({"ResultOfMethodCallIgnored", "ConstantConditions"})
    @Override
    public void onEnable() {
        File directory = new File("scripts");
        if (!directory.exists()) {
            getLogger().log("\"scripts\" directory does not exist! Creating the directory...");
            try {
                directory.createNewFile();
                getLogger().log("Created the \"scripts\" directory. Please populate it with scripts!");
            } catch (IOException e) {
                getLogger().log("Could not create the \"scripts\" directory: " + e.getMessage());
            }
            return;
        }
        if (!directory.isDirectory()) {
            getLogger().log("\"scripts\" file is not a directory!");
            return;
        }
        for (File file : directory.listFiles()) {
            getLogger().log("Loading \"" + file.getName() + "\"...");
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
        getCommandManager().registerCommand(this, new ScriptsCommand(this));
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
    }

    public List<Script> getScripts() {
        return scripts;
    }
}
