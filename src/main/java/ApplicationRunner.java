
public class ApplicationRunner {
    public static final String SNIPER_ID = "sniper";
    public static final String SNIPER_PASSWORD = "sniper";
    private static final String XMPP_HOSTNAME = "104.248.47.45";
    private AuctionSniperDriver driver;


    public void startBiddingIn(AuctionServer auction) {
        Thread thread = new Thread("Test Application") {
            @Override
            public void run() {
                try{
                    System.out.println(auction.getItemId());
                    Main.main(XMPP_HOSTNAME, SNIPER_ID, SNIPER_PASSWORD, auction.getItemId());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        thread.setDaemon(true);
        thread.start();
        driver = new AuctionSniperDriver(10000);
        driver.showsSniperStatus(MainWindow.STATUS_JOINING);
    }


    public void showsSniperHasLostAuction() {
        driver.showsSniperStatus(MainWindow.STATUS_LOST);
    }

    public void stop() {
        driver.dispose();
    }
}
