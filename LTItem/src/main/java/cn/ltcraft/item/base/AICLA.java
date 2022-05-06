package cn.ltcraft.item.base;

import cn.ltcraft.item.base.interfaces.InsertableConfigurableLTItem;

/**
 * 抽象可配置可镶嵌宝石 拥有属性的LT物品
 * @see cn.ltcraft.item.items.BaseWeapon
 * @see cn.ltcraft.item.items.Armor
 * 以上类都会继承此类
 * AbstractInsertableConfigurableLTItemsAttribute
 * Abstract Insertable Configurable LTItems Attribute
 */
public abstract class AICLA extends AbstractAttribute implements InsertableConfigurableLTItem {
    @Override
    public AICLA clone() {
        return (AICLA) super.clone();
    }
}
