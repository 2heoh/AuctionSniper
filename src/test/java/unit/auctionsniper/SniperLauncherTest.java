package unit.auctionsniper;

import auctionsniper.*;
import auctionsniper.ui.SniperTableModel;
import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import org.jmock.Expectations;
import org.jmock.States;
import org.jmock.junit5.JUnit5Mockery;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static org.hamcrest.Matchers.equalTo;

public class SniperLauncherTest {

    @RegisterExtension
    JUnit5Mockery context = new JUnit5Mockery();
    private final Auction auction = context.mock(Auction.class);
    private final AuctionHouse auctionHouse = context.mock(AuctionHouse.class);
    private final States auctionState = context.states("auction state").startsAs("not joined");


    private final SniperCollector sniperCollector = context.mock(SniperCollector.class);
    private final SniperLauncher launcher = new SniperLauncher(auctionHouse, sniperCollector);

    @Test
    void addsNewSniperToCollectorAnThenJoinsAuction() {
        final String itemId = "item 123";
        context.checking(new Expectations(){{
            allowing(auctionHouse).auctionFor(itemId);
            will(returnValue(auction));

            oneOf(auction).addAuctionEventListener(with(sniperForItem(itemId)));
            when(auctionState.is("not joined"));
            oneOf(sniperCollector).addSniper(with(sniperForItem(itemId)));
            when(auctionState.is("not joined"));

            oneOf(auction).join();
            then(auctionState.is("joined"));
        }});

        launcher.joinAuction(itemId);
    }

    protected Matcher<AuctionSniper>sniperForItem(String itemId) {
        return new FeatureMatcher<AuctionSniper, String>(equalTo(itemId), "sniper with item id", "item") {
            protected String featureValueOf(AuctionSniper actual) {
                return actual.getSnapshot().itemId;
            }
        };
    }
}
