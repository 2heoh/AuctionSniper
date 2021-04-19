public interface AuctionServer {
    void startSellingItem() throws Exception;

    void hasReceivedJoinRequestFromSniper() throws Exception;

    void announceClosed() throws Exception;

    void stop();

    String getItemId();
}
