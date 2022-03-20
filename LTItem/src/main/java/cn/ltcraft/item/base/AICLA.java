package cn.ltcraft.item.base;

import cn.ltcraft.item.base.interfaces.InsertableConfigurableLTItem;

/**
 * 抽象可配置宝石 拥有属性的LT物品
 * AbstractInsertableConfigurableLTItemsAttribute
 */
public abstract class AICLA extends AbstractAttribute implements InsertableConfigurableLTItem {
    @Override
    public AICLA clone() {
        return (AICLA) super.clone();
    }
}
