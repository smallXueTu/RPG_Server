package cn.LTCraft.core.game.more;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.material.MaterialData;

/**
 * Created by Angel、 on 2022/4/26 0:08
 */
public class FakeBlock extends Location {
    private final MaterialData type;
    public FakeBlock(Location location, Material material) {
        super(location.getWorld(), location.getX(), location.getY(), location.getZ());
        type = new MaterialData(material);
    }
    public FakeBlock(Location location, Material material, byte data) {
        super(location.getWorld(), location.getX(), location.getY(), location.getZ());
        type = new MaterialData(material, data);
    }
    public FakeBlock(Location location, MaterialData material) {
        super(location.getWorld(), location.getX(), location.getY(), location.getZ());
        type = material;
    }

    public Material getType() {
        return type.getItemType();
    }

    public MaterialData getTypeData() {
        return type;
    }

    public short getData() {
        return type.getData();
    }
}
