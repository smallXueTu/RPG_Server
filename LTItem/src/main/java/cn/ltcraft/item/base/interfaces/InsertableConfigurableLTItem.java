package cn.ltcraft.item.base.interfaces;

/**
 * 可镶嵌可配置拥有属性物品
 */
public interface InsertableConfigurableLTItem extends ConfigurableAttLTItem{
    /**
     * 获取最大镶嵌次数
     * @return 镶嵌次数
     */
    int getMaxSet();
}
