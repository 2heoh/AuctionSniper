package auctionsniper;

import org.jivesoftware.smack.XMPPException;

public interface AuctionServer {
    void startSellingItem() throws Exception;

    void hasReceivedJoinRequestFrom(String sniperId) throws Exception;

    void announceClosed() throws Exception;

    void stop();

    String getItemId();

    void reportPrice(int price, int increment, String bidder) throws XMPPException;

    void hasReceivedBid(int bid, String sniperId) throws InterruptedException;
}
