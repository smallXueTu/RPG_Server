package cn.ltcraft.item.objs;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Angel、 on 2024/8/11 下午3:47
 */
@Data
public class StoneInstallResult {

    public static final StoneInstallResult fail = new StoneInstallResult(0, null, false);
    /**
     * 消耗数
     */
    private int consumption = 1;

    /**
     * 安装结果
     * 失败为null
     */
    private ItemStack resultItemStack;

    /**
     * 安装是否成功
     * 仅适于几率安装 是否真的成功请看{@link #resultItemStack}是否为null
     */
    private boolean success = true;

    /**
     * 安装成功消耗一个
     */
    public StoneInstallResult(ItemStack itemStack){
        resultItemStack = itemStack;
    }

    /**
     * 安装成功
     */
    public StoneInstallResult(int consumption, ItemStack itemStack){
        this.consumption = consumption;
        resultItemStack = itemStack;
    }

    /**
     * all args
     */
    public StoneInstallResult(int consumption, ItemStack resultItemStack, boolean success) {
        this.consumption = consumption;
        this.resultItemStack = resultItemStack;
        this.success = success;
    }
}
