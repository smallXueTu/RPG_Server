package cn.ltcraft.login.other;

import cn.ltcraft.login.Login;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;

public class InventoryPacketAdapter extends PacketAdapter {
    private static final int PLAYER_INVENTORY = 0;
    private static final int CRAFTING_SIZE = 5;
    private static final int ARMOR_SIZE = 4;
    private static final int MAIN_SIZE = 27;
    private static final int HOTBAR_SIZE = 9;

    public InventoryPacketAdapter(Login plugin) {
        super(plugin, PacketType.Play.Server.SET_SLOT, PacketType.Play.Server.WINDOW_ITEMS);
    }

    public void onPacketSending(PacketEvent packetEvent) {
        Player player = packetEvent.getPlayer();
        PacketContainer packet = packetEvent.getPacket();
        byte windowId = packet.getIntegers().read(0).byteValue();
        if ((windowId == 0 || windowId == -1) && Login.getInstance().playerStatus.get(player.getName()) != PlayerStatus.NORMAL) {
            packetEvent.setCancelled(true);
        }
    }

    public void onPacketReceiving(PacketEvent packetEvent) {
        Player player = packetEvent.getPlayer();
        PacketContainer packet = packetEvent.getPacket();
        byte windowId = packet.getIntegers().read(0).byteValue();
//        System.out.println(windowId);
        if ((windowId == 0 || windowId == -1) && Login.getInstance().playerStatus.get(player.getName()) != PlayerStatus.NORMAL) {
            packetEvent.setCancelled(true);
        }
    }

    public void register() {
        ProtocolLibrary.getProtocolManager().addPacketListener(this);
    }

    public void unregister() {
        ProtocolLibrary.getProtocolManager().removePacketListener(this);
    }

    public void sendBlankInventoryPacket(Player player) {
        PacketContainer inventoryPacket = Login.getInstance().getProtocolManager().createPacket(PacketType.Play.Server.WINDOW_ITEMS);
        inventoryPacket.getIntegers().write(0, 0);
        int inventorySize = 45;
        ItemStack[] blankInventory = new ItemStack[inventorySize];
        Arrays.fill(blankInventory, new ItemStack(Material.AIR));
        StructureModifier<ItemStack[]> itemArrayModifier = inventoryPacket.getItemArrayModifier();
        if (itemArrayModifier.size() > 0) {
            itemArrayModifier.write(0, blankInventory);
        } else {
            StructureModifier<List<ItemStack>> itemListModifier = inventoryPacket.getItemListModifier();
            itemListModifier.write(0, Arrays.asList(blankInventory));
        }

        try {
            Login.getInstance().getProtocolManager().sendServerPacket(player, inventoryPacket, false);
        } catch (InvocationTargetException e) {
            plugin.getLogger().warning("发送空白库存时出错");
        }

    }
}