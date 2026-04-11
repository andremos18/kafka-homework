package ru.andremos.auction.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.andremos.auction.dto.statistic.AuctionBetStats;
import ru.andremos.auction.service.StatisticService;

@RestController
@RequestMapping(path = "/statistic")
@RequiredArgsConstructor
public class StatisticController {

    private final StatisticService statisticService;

    @GetMapping("/bets")
    public AuctionBetStats getUserAuction() {
        return statisticService.getAuctionBetStats();
    }
}
