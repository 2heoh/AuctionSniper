package intergration.xmpp;

import auctionsniper.AuctionEventListener;
import auctionsniper.xmpp.XMPPAuctionHouse;
import e2e.ApplicationRunner;
import e2e.FakeAuctionServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static e2e.ApplicationRunner.SNIPER_ID;
import static e2e.ApplicationRunner.SNIPER_PASSWORD;
import static e2e.FakeAuctionServer.XMPP_HOSTNAME;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class XMPPAuctionHouseTest {
    private static final String itemId = "item-54321";
    private FakeAuctionServer auctionServer;

    private XMPPAuctionHouse auctionHouse;

    @BeforeEach
    void setUp() throws Exception {
        auctionServer = new FakeAuctionServer(itemId);
        auctionServer.startSellingItem();
    }

    @BeforeEach
    void setUpAuctionHouse() throws Exception {
        auctionHouse = XMPPAuctionHouse.connect(XMPP_HOSTNAME, SNIPER_ID, SNIPER_PASSWORD);
    }

    @Test
    void receivesEventsFromAuctionServerAfterJoining() throws Exception {
        final var auctionWasClosed = new CountDownLatch(1);

        final var auction = auctionHouse.auctionFor(auctionServer.getItemId());
        auction.addAuctionEventListener(auctionClosedListener(auctionWasClosed));

        auction.join();
        auctionServer.hasReceivedJoinRequestFrom(ApplicationRunner.SNIPER_XMPP_ID);
        auctionServer.announceClosed();

        assertTrue(auctionWasClosed.await(2, TimeUnit.SECONDS));
    }

    private AuctionEventListener auctionClosedListener(final CountDownLatch auctionWasClosed) {
        return new AuctionEventListener() {

            @Override
            public void auctionClosed() {
                auctionWasClosed.countDown();
            }

            @Override
            public void currentPrice(int price, int increment, PriceSource from) {

            }
        };
    }

    @AfterEach
    void tearDown() {
        auctionHouse.disconnect();
        auctionServer.stop();
    }
}
