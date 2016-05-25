package io.sponges.bot.modules.scripting;

import io.sponges.bot.api.module.Module;

import javax.script.Invocable;
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

    private final List<Invocable> scripts = new CopyOnWriteArrayList<>();
    private final ScriptEngineManager manager = new ScriptEngineManager();

    public Scripting() {
        super("Scripting", "1.0");
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
            Invocable invocable = (Invocable) engine;
            try {
                invocable.invokeFunction("onEnable");
            } catch (ScriptException | NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDisable() {
        for (Invocable script : scripts) {
            try {
                script.invokeFunction("onDisable");
            } catch (ScriptException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException ignored) {
                // onDisable method is optional
            }
        }
    }
}
