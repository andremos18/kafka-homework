package ru.andremos.auction.dto.statistic;

import lombok.Data;


@Data
public class AuctionBetStat {
    /**
     * Идентификатор аукциона
     */
    private Integer auctionId;
    /**
     * Наименование аукциона
     */
    private String auctionName;
    /**
     * время в сек от создания аукциона до последней ставки
     */
    public Long totalDuration;
    /**
     * количество ставок за время totalDuration
     */
    public Long count;

    /**
     * среднее время между ставками
     */
    public Double avgTime;
}
