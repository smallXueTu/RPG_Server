package cn.ltcraft.login.other;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.reflect.StructureModifier;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;

public class Utils {
    private static final int PLAYER_INVENTORY = 0;
    private static final int CRAFTING_SIZE = 5;
    private static final int ARMOR_SIZE = 4;
    private static final int MAIN_SIZE = 27;
    private static final int HOTBAR_SIZE = 9;

    /**
     * Sends a blanked out packet to the given player in order to hide the inventory.
     *
     * @param player the player to send the blank inventory packet to
     */
    public static void sendBlankInventoryPacket(Player player) {
        ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
//        PacketContainer inventoryPacket = protocolManager.createPacket(PacketType.Play.Server.WINDOW_ITEMS);
//        inventoryPacket.getIntegers().write(0, 0);
//        int inventorySize = 45;
//        ItemStack[] blankInventory = new ItemStack[inventorySize];
//        Arrays.fill(blankInventory, new ItemStack(Material.AIR));
//        StructureModifier<ItemStack[]> itemArrayModifier = inventoryPacket.getItemArrayModifier();
//        if (itemArrayModifier.size() > 0) {
//            itemArrayModifier.write(0, blankInventory);
//        } else {
//            StructureModifier<List<ItemStack>> itemListModifier = inventoryPacket.getItemListModifier();
//            itemListModifier.write(0, Arrays.asList(blankInventory));
//        }
        PacketContainer fakeExplosion = new PacketContainer(PacketType.Play.Server.EXPLOSION);
        fakeExplosion.getDoubles().
                write(0, player.getLocation().getX()).
                write(1, player.getLocation().getY()).
                write(2, player.getLocation().getZ());
        fakeExplosion.getFloat().write(0, 3.0F);
        if (true)return;
        try {
            protocolManager.sendServerPacket(player, fakeExplosion);
        } catch (InvocationTargetException var8) {
        }
    }
}
