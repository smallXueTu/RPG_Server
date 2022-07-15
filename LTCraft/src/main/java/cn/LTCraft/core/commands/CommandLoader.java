package cn.LTCraft.core.commands;

public class CommandLoader {
    public CommandLoader(){
        registerCommands();
    }
    public void registerCommands(){
        new LTCoreCommand();
        new MMSkillsCommand();
        new PrefixCommand();
        new SignCommand();
        new LTTSCommand();
        new LTCSCommand();
        new LTGCommand();
        new RTPCommand();
        new CInfoCommand();
        new LTECommand();
        new ClearCommand();
        new VIPCommand();
        new LTCCommand();
    }
}
