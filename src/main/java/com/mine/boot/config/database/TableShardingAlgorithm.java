//package com.mine.boot.config.database;
//
//import io.shardingjdbc.core.api.algorithm.sharding.PreciseShardingValue;
//import io.shardingjdbc.core.api.algorithm.sharding.standard.PreciseShardingAlgorithm;
//
//import java.util.Collection;
//
///**
// * TableShardingAlgorithm
// *
// * @author zhangsl
// * @date 2018/11/29 17:06
// */
//public class TableShardingAlgorithm implements PreciseShardingAlgorithm<Long> {
//
//    @Override
//    public String doSharding(Collection<String> availableTargetNames, PreciseShardingValue<Long> shardingValue) {
//        for (String targetName : availableTargetNames) {
//            if (targetName.endsWith(String.valueOf(Long.parseLong(shardingValue.getValue().toString()) % 2))) {
//                return targetName;
//            }
//        }
//        throw new IllegalArgumentException();
//    }
//}
