package auctionsniper;

public class SniperLauncher implements UserRequestListener {
//    private final List<Auction> notToBeGCd = new ArrayList<>();
    private final AuctionHouse auctionHouse;
    private final SniperCollector collector;

    public SniperLauncher(AuctionHouse auctionHouse, SniperCollector collector) {
        this.auctionHouse = auctionHouse;
        this.collector = collector;
    }

    @Override
    public void joinAuction(String itemId) {
//        snipers.addSniper(SniperSnapshot.joining(itemId));
        Auction auction = auctionHouse.auctionFor(itemId);
        AuctionSniper sniper = new AuctionSniper(itemId, auction);
        auction.addAuctionEventListener(sniper);
        collector.addSniper(sniper);
//        notToBeGCd.add(auction);
//        AuctionSniper sniper = new AuctionSniper(itemId, auction, new SwingThreadSniperListener(snipers));
        auction.join();
    }
}
