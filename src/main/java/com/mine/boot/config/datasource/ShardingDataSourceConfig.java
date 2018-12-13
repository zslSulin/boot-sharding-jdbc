package com.mine.boot.config.datasource;

import com.google.common.collect.Maps;
import com.zaxxer.hikari.HikariDataSource;
import io.shardingsphere.core.api.MasterSlaveDataSourceFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * ShardingDataSourceConfig
 *
 * @author zhangsl
 * @date 2018/12/13 11:57
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(ShardingMasterSlaveConfig.class)
@ConditionalOnProperty({"sharding.jdbc.data-sources.ds_master.jdbc-url", "sharding.jdbc.data-sources.ds_slave_0.jdbc-url"})
public class ShardingDataSourceConfig {

    @Autowired(required = false)
    private ShardingMasterSlaveConfig shardingMasterSlaveConfig;

    @Bean("dataSource")
    public DataSource masterSlaveDataSource() throws SQLException {
        shardingMasterSlaveConfig.getDataSources().forEach((k, v) -> configDataSource(v));
        Map<String, DataSource> dataSourceMap = Maps.newHashMap();
        dataSourceMap.putAll(shardingMasterSlaveConfig.getDataSources());
        DataSource dataSource = MasterSlaveDataSourceFactory
                .createDataSource(dataSourceMap,
                        shardingMasterSlaveConfig.getMasterSlaveRule(),
                        new HashMap<String, Object>(),
                        new Properties());
        log.info("masterSlaveDataSource config complete");
        return dataSource;
    }

    private void configDataSource(HikariDataSource hikariDataSource) {
        hikariDataSource.setAutoCommit(true);
        hikariDataSource.setMaximumPoolSize(50);
        hikariDataSource.setMaxLifetime(1800000L);
        hikariDataSource.setIdleTimeout(600000L);
        hikariDataSource.setMinimumIdle(5);
        hikariDataSource.setConnectionTimeout(30000L);
        hikariDataSource.setConnectionTestQuery("SELECT 1");
    }
}
