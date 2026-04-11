package ru.andremos.auction.statistic;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BetDurationStat {
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
    private Long totalDuration;
    /**
     * количество ставок за время totalDuration
     */
    private Long count;
}
