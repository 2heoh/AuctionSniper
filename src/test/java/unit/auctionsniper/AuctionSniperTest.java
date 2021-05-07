package unit.auctionsniper;

import auctionsniper.*;
import auctionsniper.domain.Item;
import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import org.jmock.Expectations;
import org.jmock.Sequence;
import org.jmock.States;
import org.jmock.junit5.JUnit5Mockery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static auctionsniper.AuctionEventListener.PriceSource;
import static auctionsniper.SniperState.*;
import static org.hamcrest.Matchers.equalTo;

public class AuctionSniperTest {
    public static final Item ITEM = new Item("item-id", 1234);
    @RegisterExtension
    JUnit5Mockery context = new JUnit5Mockery();
    private final States sniperSnapshot = context.states("sniper");

    private final Auction auction = context.mock(Auction.class);
    private final SniperListener sniperListener = context.mock(SniperListener.class);
    private final AuctionSniper sniper = new AuctionSniper(ITEM, auction);

    @BeforeEach
    public void attachListener() {
        sniper.addSniperListener(sniperListener);
    }

    @Test
    void reportsLostIfAuctionClosesImmediately() {
        context.checking(new Expectations() {{
            atLeast(1).of(sniperListener).sniperStateChanged(with(aSniperThatIs(LOST)));
        }});

        sniper.auctionClosed();
    }

    @Test
    void reportLostIfAuctionClosesWhenBidding() {
        context.checking(new Expectations() {{
            ignoring(auction);
            allowing(sniperListener).sniperStateChanged(with(aSniperThatIs(BIDDING)));
            then(sniperSnapshot.is("bidding"));
            atLeast(1).of(sniperListener).sniperStateChanged(with(aSniperThatIs(LOST)));
            when(sniperSnapshot.is("bidding"));
        }});

        sniper.currentPrice(123, 45, PriceSource.FromOtherBidder);
        sniper.auctionClosed();
    }

    @Test
    void reportWonIfAuctionClosesWhenWinning() {
        context.checking(new Expectations() {{
            ignoring(auction);
            allowing(sniperListener).sniperStateChanged(with(aSniperThatIs(WINNING)));
            then(sniperSnapshot.is("winning"));
            atLeast(1).of(sniperListener).sniperStateChanged(with(aSniperThatIs(WON)));
            when(sniperSnapshot.is("winning"));
        }});

        sniper.currentPrice(123, 45, PriceSource.FromSniper);
        sniper.auctionClosed();
    }

    @Test
    void bidsHigherAndReportsBiddingWhenNewPriceArrives() {
        final int price = 1001;
        final int increment = 25;
        final int bid = price + increment;

        context.checking(new Expectations() {{
            oneOf(auction).bid(bid);
            atLeast(1).of(sniperListener).sniperStateChanged(new SniperSnapshot(ITEM.identifier, price, bid, BIDDING));
        }});

        sniper.currentPrice(price, increment, PriceSource.FromOtherBidder);
    }

    @Test
    void reportIsWinningWhenCurrentPriceComesFormSniper() {
        context.checking(new Expectations() {{
            ignoring(auction);
            allowing(sniperListener).sniperStateChanged(with(aSniperThatIs(BIDDING)));
            then(sniperSnapshot.is("bidding"));
            atLeast(1).of(sniperListener).sniperStateChanged(new SniperSnapshot(ITEM.identifier, 135, 135, WINNING));
            when(sniperSnapshot.is("bidding"));
        }});

        sniper.currentPrice(123, 12, PriceSource.FromOtherBidder);
        sniper.currentPrice(135, 45, PriceSource.FromSniper);
    }

    @Test
    void reportsLostWhenAuctionCloses() {
        context.checking(new Expectations() {{
            one(sniperListener).sniperStateChanged(with(aSniperThatIs(LOST)));
        }});

        sniper.auctionClosed();
    }

    @Test
    void doesNotBidAndReportsLosingIfSubsequentPriceIsAboveStopPrice() {
        allowingSniperBidding();
        context.checking(new Expectations(){{
            int bid = 123 + 45;
            allowing(auction).bid(bid);
            atLeast(1).of(sniperListener).sniperStateChanged(new SniperSnapshot(ITEM.identifier, 2345, bid, LOSING));
            when(sniperSnapshot.is("bidding"));
        }});

        sniper.currentPrice(123, 45, PriceSource.FromOtherBidder);
        sniper.currentPrice(2345, 25, PriceSource.FromOtherBidder);
    }

    @Test
    void reportsLostIfAuctionClosesWhenLosing() {
        context.checking(new Expectations() {{
            ignoring(auction);
            allowing(sniperListener).sniperStateChanged(with(aSniperThatIs(LOSING)));
            then(sniperSnapshot.is("losing"));

            atLeast(1).of(sniperListener).sniperStateChanged(with(aSniperThatIs(LOST)));
            when(sniperSnapshot.is("losing"));
        }});
        sniper.currentPrice(2345, 25, PriceSource.FromOtherBidder);
        sniper.auctionClosed();
    }

    @Test
    void continuesToBeLosingOnceStopPriceHasBeenReached() {
        final Sequence states = context.sequence("sniper states");
        context.checking(new Expectations(){{
            atLeast(1).of(sniperListener).sniperStateChanged(new SniperSnapshot(ITEM.identifier, 1234, 0, LOSING));
            inSequence(states);
            atLeast(1).of(sniperListener).sniperStateChanged(new SniperSnapshot(ITEM.identifier, 1235, 0, LOSING));
            inSequence(states);
        }});
        sniper.currentPrice(1234, 1, PriceSource.FromOtherBidder);
        sniper.currentPrice(1235, 1, PriceSource.FromOtherBidder);
    }

    @Test
    void doesNotBidAndReportsLosingIfPriceAfterWinningIsAboveStopPrice() {
        context.checking(new Expectations(){{
            ignoring(auction);
            allowing(sniperListener).sniperStateChanged(with(aSniperThatIs(SniperState.WINNING)));
            then(sniperSnapshot.is("winning"));
            atLeast(1).of(sniperListener).sniperStateChanged(new SniperSnapshot(ITEM.identifier, 1234, 0, LOSING));
            when(sniperSnapshot.is("winning"));
        }});

        sniper.currentPrice(123, 1, PriceSource.FromSniper);
        sniper.currentPrice(1234, 2, PriceSource.FromOtherBidder);
    }

    private void allowingSniperBidding() {
        context.checking(new Expectations(){{
            allowing(sniperListener).sniperStateChanged(with(aSniperThatIs(BIDDING)));
            then(sniperSnapshot.is("bidding"));
        }});
    }

    private Matcher<SniperSnapshot> aSniperThatIs(final SniperState state) {
        return new FeatureMatcher<>(equalTo(state), "sniper that is ", "was") {

            @Override
            protected SniperState featureValueOf(SniperSnapshot actual) {
                return actual.state;
            }
        };
    }
}
