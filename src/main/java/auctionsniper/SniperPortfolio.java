package auctionsniper;

import auctionsniper.util.Announcer;

import java.util.ArrayList;
import java.util.List;

public class SniperPortfolio implements SniperCollector{
    private final List<AuctionSniper> snipers = new ArrayList<>();
    private final Announcer<PortfolioListener> listener = Announcer.to(PortfolioListener.class);

    @Override
    public void addSniper(AuctionSniper sniper) {
        snipers.add(sniper);
        listener.announce().sniperAdded(sniper);
    }

    public void addPortfolioListener(PortfolioListener portfolio) {
        listener.addListener(portfolio);
    }
}
