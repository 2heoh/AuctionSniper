package e2e;

import auctionsniper.*;
import auctionsniper.ui.MainWindow;

import static auctionsniper.ui.SniperTableModel.*;

public class ApplicationRunner {
    public static final String SNIPER_ID = "sniper";
    public static final String SNIPER_PASSWORD = "sniper";
    private static final String XMPP_HOSTNAME = "104.248.47.45";
    public static final String SNIPER_XMPP_ID = SNIPER_ID + "@" + XMPP_HOSTNAME+"/Auction";
    private AuctionSniperDriver driver;

    public void startBiddingIn(final AuctionServer... auctions) {

        Thread thread = new Thread("Test Application") {
            @Override
            public void run() {
                try {
                    Main.main(arguments(auctions));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        thread.setDaemon(true);
        thread.start();
        driver = new AuctionSniperDriver(1000);
        driver.hasTitle(MainWindow.MAIN_WINDOW_NAME);
        driver.hasColumnTitles();
        for (AuctionServer auction : auctions) {
            driver.showsSniperStatus("-", 0,0,textFor(SniperState.JOINING));
        }
    }

    private static String[] arguments(AuctionServer... auctions) {
        String[] arguments = new String[auctions.length + 3];
        arguments[0] = XMPP_HOSTNAME;
        arguments[1] = SNIPER_ID;
        arguments[2] = SNIPER_PASSWORD;
        for( int i =0; i < auctions.length; i++) {
            arguments[i+3] = auctions[i].getItemId();
        }
        return arguments;
    }


    public void showsSniperHasLostAuction() {
        driver.showsSniperStatus(textFor(SniperState.LOST));
    }

    public void stop() {
        driver.dispose();
    }

    public void hasShownSniperIsBidding(AuctionServer auction, int lastPrice, int lastBid) {
        driver.showsSniperStatus(auction.getItemId(), lastPrice, lastBid, MainWindow.STATUS_BIDDING);
    }

    public void hasShownSniperIsWinning(AuctionServer auction, int winningBid) {
        driver.showsSniperStatus(auction.getItemId(), winningBid, winningBid, MainWindow.STATUS_WINNING);
    }

    public void showsSniperHasWonAuction(AuctionServer auction, int lastPrice) {
        driver.showsSniperStatus(auction.getItemId(), lastPrice, lastPrice, MainWindow.STATUS_WON);
    }
}
