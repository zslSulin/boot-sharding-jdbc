package com.mine.boot.config.datasource;

import com.zaxxer.hikari.HikariDataSource;
import io.shardingsphere.core.yaml.masterslave.YamlMasterSlaveConfiguration;
import io.shardingsphere.shardingjdbc.api.yaml.YamlMasterSlaveDataSourceFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ResourceUtils;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

/**
 * ShardingDataSourceYmlConfig
 *
 * @author zhangsl
 * @date 2019/1/22 11:29
 */
@Slf4j
@Configuration
public class ShardingDataSourceYmlConfig {

    private static final String SHARDING_RULE_YAML_PATH = "classpath:sharding/shardingRule.yml";
    private static final String MASTER_SALVE_YAML_PATH = "classpath:sharding/masterSlave.yml";
    private static final String SHARDING_MASTER_SALVE_YAML_PATH = "classpath:sharding/shardingMasterSalve.yml";

    @Bean
    public DataSource getDataSource() throws IOException, SQLException {
        //读写分离配置
        File msYamlFile = ResourceUtils.getFile(MASTER_SALVE_YAML_PATH);
        YamlMasterSlaveConfiguration msConfig = YamlMasterSlaveConfiguration.unmarshal(msYamlFile);
        Map<String, DataSource> msDataSources = msConfig.getDataSources();
        msDataSources.forEach((k,v) -> {
            HikariDataSource dataSource = (HikariDataSource) v;
            configDataSource(dataSource);
        });
        //读写分离配置over
        return YamlMasterSlaveDataSourceFactory.createDataSource(msDataSources, msYamlFile);
    }

//    public DataSource getShardingDataSource() throws IOException, SQLException {
//        //分片配置
//        File srFile = ResourceUtils.getFile(SHARDING_RULE_YAML_PATH);
//        YamlShardingConfiguration srConfig = YamlShardingConfiguration.unmarshal(srFile);
//        Map<String, DataSource> srDataSources = srConfig.getDataSources();
//        srDataSources.forEach((k,v) -> {
//            HikariDataSource dataSource = (HikariDataSource) v;
//            configDataSource(dataSource);
//        });
//        //分片配置over
//        return YamlShardingDataSourceFactory.createDataSource(srDataSources, srFile);
//    }

//    private Map<String, Object> parseConfig() throws IOException {
//        Resource shardResource = new ClassPathResource(SHARDING_RULE_YAML_PATH);
//        Resource msResource = new ClassPathResource(MASTER_SALVE_YAML_PATH);
//        InputStreamReader shardInputStreamReader = new InputStreamReader(shardResource.getInputStream(), "UTF-8");
//        InputStreamReader msInputStreamReader = new InputStreamReader(msResource.getInputStream(), "UTF-8");
//        YamlShardingConfiguration shardingConfiguration = new Yaml(new Constructor(YamlShardingConfiguration.class)).loadAs(shardInputStreamReader, YamlShardingConfiguration.class);
//        YamlMasterSlaveConfiguration masterSlaveConfiguration = new Yaml(new Constructor(YamlMasterSlaveConfiguration.class)).loadAs(msInputStreamReader, YamlMasterSlaveConfiguration.class);
//        Map<String, Object> param = new HashMap<>(4);
//        param.put("simpleMasterSlave", masterSlaveConfiguration);
//        param.put("simpleSharding", shardingConfiguration);
//        return param;
//    }

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
