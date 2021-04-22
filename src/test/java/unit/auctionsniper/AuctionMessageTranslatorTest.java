package unit.auctionsniper;

import auctionsniper.AuctionEventListener;
import auctionsniper.AuctionMessageTranslator;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.packet.Message;
import org.jmock.Expectations;
import org.jmock.junit5.JUnit5Mockery;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;


public class AuctionMessageTranslatorTest{
    private static final Chat UNUSED_CHAT = null;

    @RegisterExtension JUnit5Mockery context = new JUnit5Mockery();
    private final AuctionEventListener listener = context.mock(AuctionEventListener.class);

    private AuctionMessageTranslator translator = new AuctionMessageTranslator(listener);

    @Test
    void notifiesAuctionClosedWhenCloseMessageReceivedFrom() {

        context.checking(new Expectations(){{ oneOf(listener).auctionClosed(); }});

        final var message = new Message();
        message.setBody("SOLVersion: 1.1; Event: CLOSE;");

        translator.processMessage(UNUSED_CHAT, message);
    }

    @Test
    void notifiesBidDetailsWhenCurrentPriceMessageReceivedOtherBidder() {
        context.checking(new Expectations() {{
            exactly(1).of(listener).currentPrice(192, 7);
        }});

        final var message = new Message();
        message.setBody("SOLVersion: 1.1; Event: PRICE; CurrentPrice: 192; Increment: 7; Bidder: Someone else;");

        translator.processMessage(UNUSED_CHAT, message);
    }

//    @Test
//    void notifiesBidDetailsWhenCurrentPriceMessageReceivedFormSniper() {
//        context.checking(new Expectations(){{
//            exactly(1).of(listener).currentPrice(234, 5);
//        }});
//
//        final var message = new Message();
//        message.setBody("SOLVersion: 1.1; Event: PRICE; CurrentPrice: 234; Increment: 5; Bidder: "+SNIPER_ID+";");
//        translator.processMessage(UNUSED_CHAT, message);
//    }
}
