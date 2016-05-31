package io.sponges.bot.modules.scripting;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

public class Script {

    private final ScriptEngine engine;
    private final String name;
    private final Invocable invocable;

    protected Script(ScriptEngine engine, String name) {
        this.engine = engine;
        this.name = name;
        this.invocable = (Invocable) engine;
    }

    protected void onEnable() throws ScriptException, NoSuchMethodException {
        invocable.invokeFunction("onEnable");
    }

    protected void onDisable() throws ScriptException {
        try {
            invocable.invokeFunction("onDisable");
        } catch (NoSuchMethodException ignored) {
            // function is optional
        }
    }

    public String getName() {
        return name;
    }
}
