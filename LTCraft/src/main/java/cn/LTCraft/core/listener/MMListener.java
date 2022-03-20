package cn.LTCraft.core.listener;

import cn.LTCraft.core.hook.MM.conditions.*;
import cn.LTCraft.core.hook.MM.mechanics.singletonSkill.*;
import cn.LTCraft.core.hook.MM.mechanics.*;
import cn.LTCraft.core.hook.MM.drop.*;
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicConditionLoadEvent;
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicDropLoadEvent;
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMechanicLoadEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;


public class MMListener implements Listener {
    @EventHandler
    public void onMythicConditionLoad(MythicConditionLoadEvent event){
        if (event.getConditionName().equalsIgnoreCase("WeaponDamage")){
            event.register(new WeaponDamage(event.getConfig().getLine(), event.getConfig()));
        }else if (event.getConditionName().equalsIgnoreCase("HasObjective")){
            event.register(new HasObjective(event.getConfig().getLine(), event.getConfig()));
        }else if (event.getConditionName().equalsIgnoreCase("HasItem")){
            event.register(new HasItem(event.getConfig().getLine(), event.getConfig()));
        }else if (event.getConditionName().equalsIgnoreCase("HasBQTag")){
            event.register(new HasBQTag(event.getConfig().getLine(), event.getConfig()));
        }else if (event.getConditionName().equalsIgnoreCase("IsSilence")){
            event.register(new IsSilence(event.getConfig().getLine(), event.getConfig()));
        }
    }
    @EventHandler
    public void onMythicMechanicLoad(MythicMechanicLoadEvent event) {
        if (event.getMechanicName().equalsIgnoreCase("modifySlotItem")){
            event.register(new ModifySlotItem(event.getContainer().getConfigLine(), event.getConfig()));
        }else if (event.getMechanicName().equalsIgnoreCase("DropCheckS")){
            event.register(new DropCheckS(event.getContainer().getConfigLine(), event.getConfig()));
        }else if (event.getMechanicName().equalsIgnoreCase("targetCommand")){
            event.register(new TargetCommand(event.getContainer().getConfigLine(), event.getConfig()));
        }else if (event.getMechanicName().equalsIgnoreCase("roundSummon")){
            event.register(new RoundSummon(event.getContainer().getConfigLine(), event.getConfig()));
        }else if (event.getMechanicName().equalsIgnoreCase("particleDoor")){
            event.register(new ParticleDoor(event.getContainer().getConfigLine(), event.getConfig()));
        }else if (event.getMechanicName().equalsIgnoreCase("airDoor")){
            event.register(new AirDoor(event.getContainer().getConfigLine(), event.getConfig()));
        }else if (event.getMechanicName().equalsIgnoreCase("sendAction")){
            event.register(new SendAction(event.getContainer().getConfigLine(), event.getConfig()));
        }else if (event.getMechanicName().equalsIgnoreCase("execBQEvent")){
            event.register(new ExecBQEvent(event.getContainer().getConfigLine(), event.getConfig()));
        }else if (event.getMechanicName().equalsIgnoreCase("addBQTag")){
            event.register(new AddBQTag(event.getContainer().getConfigLine(), event.getConfig()));
        }else if (event.getMechanicName().equalsIgnoreCase("startAnimation")){
            event.register(new StartAnimation(event.getContainer().getConfigLine(), event.getConfig()));
        }else if (event.getMechanicName().equalsIgnoreCase("disarm")){
            event.register(new Disarm(event.getContainer().getConfigLine(), event.getConfig()));
        }else if (event.getMechanicName().equalsIgnoreCase("dropGoldCoins")){
            event.register(new DropGoldCoins(event.getContainer().getConfigLine(), event.getConfig()));
        }
    }
    @EventHandler
    public void onMythicDropLoad(MythicDropLoadEvent event) {
        if (event.getDropName().equalsIgnoreCase("LTItem")){
            event.register(new LTItemDrop(event.getContainer().getConfigLine(), event.getConfig()));
        }else if (event.getDropName().equalsIgnoreCase("GoldCoinsDrop")){
            event.register(new GoldCoinsDrop(event.getContainer().getConfigLine(), event.getConfig()));
        }
    }
}
