package cn.LTCraft.core.listener;

import cn.LTCraft.core.hook.MM.conditions.*;
import cn.LTCraft.core.hook.MM.mechanics.singletonSkill.*;
import cn.LTCraft.core.hook.MM.mechanics.*;
import cn.LTCraft.core.hook.MM.drop.*;
import io.lumine.xikage.mythicmobs.api.bukkit.events.*;
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
        }else if (event.getConditionName().equalsIgnoreCase("LTWearing")){
            event.register(new LTWearingCondition(event.getConfig().getLine(), event.getConfig()));
        }else if (event.getConditionName().equalsIgnoreCase("MainLinePositionDistance")){
            event.register(new MainLinePositionDistance(event.getConfig().getLine(), event.getConfig()));
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
        }else if (event.getMechanicName().equalsIgnoreCase("summonTheEnemy")){
            event.register(new SummonForMob(event.getContainer().getConfigLine(), event.getConfig()));
        }else if (event.getMechanicName().equalsIgnoreCase("escort")){
            event.register(new Escort(event.getContainer().getConfigLine(), event.getConfig()));
        }else if (event.getMechanicName().equalsIgnoreCase("callVigilant")){
            event.register(new CallForHelp(event.getContainer().getConfigLine(), event.getConfig()));
        }else if (event.getMechanicName().equalsIgnoreCase("randomLightning")){
            event.register(new RandomLightning(event.getContainer().getConfigLine(), event.getConfig()));
        }
    }

    /**
     * TODO: 重写MythicMobs掉落系统，保护带掉落物仅能被击杀者捡起
     * {@link cn.LTCraft.core.other.Temp#protectItem(org.bukkit.entity.Player, org.bukkit.entity.Item)}
     * @param event 事件
     */
    @EventHandler
    public void onMythicDropLoad(MythicDropLoadEvent event) {
        if (event.getDropName().equalsIgnoreCase("LTItem")){
            event.register(new LTItemDrop(event.getContainer().getConfigLine(), event.getConfig()));
        }else if (event.getDropName().equalsIgnoreCase("GoldCoinsDrop")){
            event.register(new GoldCoinsDrop(event.getContainer().getConfigLine(), event.getConfig()));
        }else if (event.getDropName().equalsIgnoreCase("GoldCoins")){
            event.register(new GoldCoins(event.getContainer().getConfigLine(), event.getConfig()));
        }else if (event.getDropName().equalsIgnoreCase("PseudorandomDrop")){
            event.register(new PseudorandomDrop(event.getContainer().getConfigLine(), event.getConfig()));
        }
    }
    @EventHandler
    public void onMythicMobDeath(MythicMobDeathEvent event){

    }
}
