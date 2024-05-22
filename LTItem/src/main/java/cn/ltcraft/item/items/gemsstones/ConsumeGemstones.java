package cn.ltcraft.item.items.gemsstones;

import cn.LTCraft.core.utils.MathUtils;

/**
 * 消耗宝石
 * 此宝石镶嵌到装备上不会占用装备槽位，会被装备直接给吸收
 * Created by Angel、 on 2024/2/22 23:52
 */
public interface ConsumeGemstones {
    /**
     * 是否安装成功
     */
    default boolean installSuccess(){
        return MathUtils.ifAdopt(getRate());
    }

    /**
     * 获取几率
     */
    default float getRate(){
        return 1;
    }
}
