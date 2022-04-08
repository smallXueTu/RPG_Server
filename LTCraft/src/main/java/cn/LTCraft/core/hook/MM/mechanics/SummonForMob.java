package cn.LTCraft.core.hook.MM.mechanics;

import cn.LTCraft.core.game.TargetOnlyMobsManager;
import io.lumine.utils.tasks.Scheduler;
import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.adapters.AbstractEntity;
import io.lumine.xikage.mythicmobs.adapters.AbstractLocation;
import io.lumine.xikage.mythicmobs.io.MythicLineConfig;
import io.lumine.xikage.mythicmobs.logging.MythicLogger;
import io.lumine.xikage.mythicmobs.mobs.ActiveMob;
import io.lumine.xikage.mythicmobs.mobs.MythicMob;
import io.lumine.xikage.mythicmobs.mobs.entities.MythicEntity;
import io.lumine.xikage.mythicmobs.skills.ITargetedEntitySkill;
import io.lumine.xikage.mythicmobs.skills.ITargetedLocationSkill;
import io.lumine.xikage.mythicmobs.skills.SkillMechanic;
import io.lumine.xikage.mythicmobs.skills.SkillMetadata;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

/**
 * Created by Angel、 on 2022/4/8 10:03
 */
public class SummonForMob extends SkillMechanic implements ITargetedEntitySkill, ITargetedLocationSkill {
    protected MythicMob mm;
    protected MythicEntity me;
    protected String strType;
    protected int amount;
    protected boolean immediately;
    public SummonForMob(String skill, MythicLineConfig mlc) {
        super(skill, mlc);
        this.ASYNC_SAFE = false;
        this.strType = mlc.getString(new String[]{"type", "t", "mob", "m"}, "SKELETON");
        this.amount = mlc.getInteger(new String[]{"amount", "a"}, 1);//数量
        this.immediately = mlc.getBoolean(new String[]{"immediately", "im", "i"}, false);//数量
        Scheduler.runLaterSync(() -> {
            this.mm = MythicMobs.inst().getMobManager().getMythicMob(this.strType);
            if (this.mm == null) {
                this.me = MythicEntity.getMythicEntity(this.strType);
                if (this.me == null) {
                    MythicLogger.errorMechanicConfig(this, mlc, "The 'type' attribute must be a valid MythicMob or MythicEntity type.");
                }
            }
        }, 1L);
    }

    public boolean castAtLocation(SkillMetadata data, AbstractLocation target) {
        ActiveMob ams;
        if (this.mm != null) {
            for(int i = 1; i <= this.amount; ++i) {
                getPlugin().getMobManager();
                ams = this.mm.spawn(target, data.getCaster().getLevel());
                if (ams != null) {
                    getPlugin().getEntityManager().registerMob(ams.getEntity().getWorld(), ams.getEntity());
                    if (immediately) {
                        ams.getThreatTable().Taunt(data.getCaster().getEntity());
                    }
                    Entity bukkitEntity = data.getCaster().getEntity().getBukkitEntity();
                    if (bukkitEntity instanceof Player) {
                        TargetOnlyMobsManager.getInstance().add(ams, (Player) bukkitEntity);
                    }
                }
            }
            return true;
        }
        return false;
    }

    public boolean castAtEntity(SkillMetadata data, AbstractEntity target) {
        this.castAtLocation(data, target.getLocation());
        return true;
    }
}
