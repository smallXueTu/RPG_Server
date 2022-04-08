package cn.LTCraft.core.hook.MM.mechanics;

import io.lumine.utils.tasks.Scheduler;
import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.adapters.AbstractEntity;
import io.lumine.xikage.mythicmobs.adapters.AbstractLocation;
import io.lumine.xikage.mythicmobs.adapters.bukkit.BukkitAdapter;
import io.lumine.xikage.mythicmobs.io.MythicLineConfig;
import io.lumine.xikage.mythicmobs.logging.MythicLogger;
import io.lumine.xikage.mythicmobs.mobs.ActiveMob;
import io.lumine.xikage.mythicmobs.mobs.MobManager;
import io.lumine.xikage.mythicmobs.mobs.MythicMob;
import io.lumine.xikage.mythicmobs.mobs.entities.MythicEntity;
import io.lumine.xikage.mythicmobs.skills.ITargetedEntitySkill;
import io.lumine.xikage.mythicmobs.skills.ITargetedLocationSkill;
import io.lumine.xikage.mythicmobs.skills.SkillMechanic;
import io.lumine.xikage.mythicmobs.skills.SkillMetadata;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;

public class RoundSummon extends SkillMechanic implements ITargetedEntitySkill, ITargetedLocationSkill {
    protected MythicMob mm;
    protected MythicEntity me;
    protected String strType;
    protected int amount;
    protected int radius;
    protected int angle;
    protected boolean summonerIsOwner;
    protected boolean summonerIsParent;
    protected boolean inheritThreatTable;
    protected boolean copyThreatTable;

    public RoundSummon(String skill, MythicLineConfig mlc) {
        super(skill, mlc);
        this.ASYNC_SAFE = false;
        this.strType = mlc.getString(new String[]{"type", "t", "mob", "m"}, "SKELETON");
        this.amount = mlc.getInteger(new String[]{"amount", "a"}, 1);//数量
        this.radius = mlc.getInteger(new String[]{"radius", "r"}, 0);//半径
        this.angle = mlc.getInteger(new String[]{"angle", "an"}, 0);//角度
        this.copyThreatTable = mlc.getBoolean(new String[]{"copythreattable", "ctt"}, false);//所生成实体是否继承其它实体对施法者的威胁
        this.inheritThreatTable = mlc.getBoolean(new String[]{"inheritfaction", "if"}, true);//所生成实体是否与施法者处在同一阵营
        this.inheritThreatTable = mlc.getBoolean(new String[]{"inheritthreattable", "itt"}, false);//所生成实体是否与施法者共享其它实体对于它的威胁度
        this.summonerIsOwner = mlc.getBoolean(new String[]{"summonerisowner", "sio"}, true);//施法者是否作为所生成实体的主人
        this.summonerIsParent = mlc.getBoolean(new String[]{"summonerisparent", "sip"}, true);//施法者是否作为所生成实体的父系实体
        if (this.amount <= 0) {
            MythicLogger.errorMechanicConfig(this, mlc, "The 'amount' attribute cannot be less than 1");
            this.amount = 1;
        }

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
        AbstractLocation l;
        ActiveMob ams;
        for(int i = 1; i <= this.amount; ++i) {
            getPlugin().getMobManager();
            float leftYaw = target.getYaw() + 90 % 360;
            float rightYaw = target.getYaw() - 90 % 360;
            double x = 0,z = 0, y = radius;
            if (angle == 0){
                x = -Math.sin(leftYaw / 180 * Math.PI) * radius;//计算X
                z = Math.cos(leftYaw / 180 * Math.PI) * radius;//计算Z
                y -= Math.sqrt(y);
            }else if(angle == 2){
                x = -Math.sin(rightYaw / 180 * Math.PI) * radius;//计算X
                z = Math.cos(rightYaw / 180 * Math.PI) * radius;//计算Z
                y -= Math.sqrt(y);
            }
            l = new AbstractLocation(target.getWorld(), target.getX() + x, target.getY() + y, target.getZ() + z, target.getYaw(), target.getPitch());
            if (this.mm != null) {
                ams = this.mm.spawn(l, data.getCaster().getLevel());
                if (ams != null) {
                    getPlugin().getEntityManager().registerMob(ams.getEntity().getWorld(), ams.getEntity());
//                       ((CraftEntity)ams.getEntity().getBukkitEntity()).getHandle().yaw = target.getYaw();
//                       ((CraftEntity)ams.getEntity().getBukkitEntity()).getHandle().pitch = target.getPitch();
                    if (data.getCaster() instanceof ActiveMob) {
                        ActiveMob am = (ActiveMob) data.getCaster();
                        if (this.summonerIsOwner) {
                            ams.setParent(am);
                            ams.setFaction(am.getFaction());
                        }
                        if (this.copyThreatTable) {
                            try {
                                ams.importThreatTable(am.getThreatTable().clone());
                                ams.getThreatTable().targetHighestThreat();
                            } catch (CloneNotSupportedException e) {
                                e.printStackTrace();
                            }
                        } else if (this.inheritThreatTable) {
                            ams.importThreatTable(am.getThreatTable());
                            ams.getThreatTable().targetHighestThreat();
                        }
                    } else if (this.summonerIsOwner) {
                        ams.setOwner(data.getCaster().getEntity().getUniqueId());
                    }
                }
            }else {
                this.me.spawn(BukkitAdapter.adapt(l));
            }
        }
        return true;
    }

    public boolean castAtEntity(SkillMetadata data, AbstractEntity target) {
        this.castAtLocation(data, target.getLocation());
        return true;
    }
}
