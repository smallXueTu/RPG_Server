package cn.LTCraft.core.dataBase;

import cn.LTCraft.core.Config;
import cn.LTCraft.core.Main;
import org.apache.ibatis.datasource.pooled.PooledDataSource;
import org.apache.ibatis.datasource.pooled.PooledDataSourceFactory;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.bukkit.configuration.file.YamlConfiguration;
import javax.sql.DataSource;
import java.util.Properties;

public class SQLServer {
    private final Main plugin;
    private boolean shutdown = false;
    private SqlSessionFactory sqlSessionFactory = null;
    private Configuration configuration = null;
    public SQLServer(Main plugin){
        this.plugin = plugin;
        initSqlSessionFactory();
    }

    /**
     * 初始化 SqlSessionFactory
     */
    public void initSqlSessionFactory(){
        Properties properties = new Properties();
        YamlConfiguration mySQLInfoYaml = Config.getInstance().getMySQLInfoYaml();
        properties.setProperty("driver", "com.mysql.jdbc.Driver");
        properties.setProperty("url", "jdbc:mysql://" + mySQLInfoYaml.getString("localhost") + ":" + mySQLInfoYaml.getString("port") + "/" + mySQLInfoYaml.getString("database") + "?useSSL=false&autoReconnect=true");
        properties.setProperty("username", mySQLInfoYaml.getString("username"));
        properties.setProperty("password", mySQLInfoYaml.getString("password"));
        PooledDataSourceFactory pooledDataSourceFactory = new PooledDataSourceFactory();
        pooledDataSourceFactory.setProperties(properties);
        ((PooledDataSource) pooledDataSourceFactory.getDataSource()).setPoolMaximumIdleConnections(10);
        ((PooledDataSource) pooledDataSourceFactory.getDataSource()).setPoolMaximumActiveConnections(20);
        ((PooledDataSource) pooledDataSourceFactory.getDataSource()).setPoolPingQuery("SELECT NOW()");
        ((PooledDataSource) pooledDataSourceFactory.getDataSource()).setPoolPingEnabled(true);
        DataSource dataSource = pooledDataSourceFactory.getDataSource();
        TransactionFactory transactionFactory = new JdbcTransactionFactory();
        Environment environment = new Environment("development", transactionFactory, dataSource);
        configuration = new Configuration(environment);
        sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);
    }

    /**
     * 增加Mapper
     * @param tClass
     * @param <T>
     */
    public <T> void addMapper(Class<T> tClass){
        configuration.addMapper(tClass);
    }

    /**
     * 关闭
     */
    public void setShutdown() {
        this.shutdown = true;
    }

    /**
     * @return 如果关闭返回true
     */
    public boolean getShutdown(){
        return shutdown;
    }

    /**
     * 获取SqlSessionFactory
     * @return SqlSessionFactory
     */
    public SqlSessionFactory getSqlSessionFactory() {
        return sqlSessionFactory;
    }

    /**
     * 获取配置
     * 用于addMapper
     * @return 配置
     */
    public Configuration getConfiguration() {
        return configuration;
    }

    /**
     * 获取数据库连接
     * @return 配置文件
     */
    public YamlConfiguration getConfig(){
        return plugin.getMySQLInfo();
    }
}
