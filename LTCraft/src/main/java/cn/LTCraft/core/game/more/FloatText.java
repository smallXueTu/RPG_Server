package cn.LTCraft.core.game.more;

import cn.LTCraft.core.Main;
import cn.LTCraft.core.game.Game;
import cn.LTCraft.core.game.more.tickEntity.TickEntity;
import cn.LTCraft.core.task.GlobalRefresh;
import cn.ltcraft.item.LTItemSystem;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.nms.interfaces.entity.NMSEntityBase;
import com.gmail.filoghost.holographicdisplays.object.CraftHologram;
import com.gmail.filoghost.holographicdisplays.object.line.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;


/**
 * 悬浮字
 * Created by Angel、 on 2022/5/12 18:30
 */
public class FloatText implements TickEntity {
    private final Location location;
    private final String text;
    private final long durationTick;
    private Vector motion = null;
    private Hologram hologram;
    public FloatText(Location location, String text, int durationTick){
        this.location = location;
        this.text = text;
        this.durationTick = durationTick + GlobalRefresh.getTick();
        init();
        //防止并发修改异常
        Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> GlobalRefresh.addTickEntity(this), 0);
    }
    public FloatText(Location location, String text, int durationTick, Vector motion){
        this(location, text, durationTick);
        this.motion = motion;
    }
    public void init(){
        hologram = HologramsAPI.createHologram(LTItemSystem.getInstance(), location);
        hologram.appendTextLine(text);
    }
    @Override
    public boolean doTick(long tick) {
        if (tick > durationTick){
            close();
            return false;
        }
        if (motion != null){
            hologram.teleport(hologram.getLocation().add(motion));
        }
        return true;
    }
    public void close(){
        hologram.delete();
    }
    @Override
    public int getTickRate() {
        return 1;
    }

    @Override
    public boolean isAsync() {
        return true;
    }
}
