//package com.mine.boot.config.datasource;
//
//import com.zaxxer.hikari.HikariDataSource;
//import io.shardingsphere.core.api.config.MasterSlaveRuleConfiguration;
//import lombok.Getter;
//import lombok.Setter;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * ShardingMasterSlaveConfig
// *
// * @author zhangsl
// * @date 2018/12/13 11:58
// */
//@Getter
//@Setter
//@ConfigurationProperties(prefix = "sharding.jdbc")
//public class ShardingMasterSlaveConfig {
//
//    private Map<String, HikariDataSource> dataSources = new HashMap<>();
//
//    private MasterSlaveRuleConfiguration masterSlaveRule;
//}
