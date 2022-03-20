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
//        new UseItemCommand();
        new RtpCommand();
        new CInfoCommand();
        new LTECommand();
        new ClearCommand();
    }
}
