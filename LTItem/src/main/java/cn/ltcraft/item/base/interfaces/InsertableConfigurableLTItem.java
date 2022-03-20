package cn.ltcraft.item.base.interfaces;

public interface InsertableConfigurableLTItem extends ConfigurableLTItem, Attribute{
    /**
     * 获取最大镶嵌次数
     * @return 镶嵌次数
     */
    int getMaxSet();
}
