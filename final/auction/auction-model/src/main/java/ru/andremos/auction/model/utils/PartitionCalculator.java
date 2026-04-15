package ru.andremos.auction.model.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class PartitionCalculator {
    public static int calculateNumPartition(long key, int numPartitions) {
        long longKey = ((Number) key).longValue();
        return (int) (Math.abs(longKey) % numPartitions);
    }
}
