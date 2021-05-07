package auctionsniper;

import auctionsniper.domain.Item;

public class SniperLauncher implements UserRequestListener {
    private final AuctionHouse auctionHouse;
    private final SniperCollector collector;

    public SniperLauncher(AuctionHouse auctionHouse, SniperCollector collector) {
        this.auctionHouse = auctionHouse;
        this.collector = collector;
    }

//    @Override
//    public void joinAuction(String itemId) {
//        Auction auction = auctionHouse.auctionFor(itemId);
//        AuctionSniper sniper = new AuctionSniper(itemId, auction);
//        auction.addAuctionEventListener(sniper);
//        collector.addSniper(sniper);
//        auction.join();
//    }

    @Override
    public void joinAuction(Item item) {
        Auction auction = auctionHouse.auctionFor(item.identifier);
        AuctionSniper sniper = new AuctionSniper(item, auction);
        auction.addAuctionEventListener(sniper);
        collector.addSniper(sniper);
        auction.join();
    }
}
