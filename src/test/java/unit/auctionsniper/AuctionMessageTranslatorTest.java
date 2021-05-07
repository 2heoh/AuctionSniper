package unit.auctionsniper;

import auctionsniper.AuctionEventListener;
import auctionsniper.AuctionMessageTranslator;
import auctionsniper.xmpp.XMPPFailureReporter;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.packet.Message;
import org.jmock.Expectations;
import org.jmock.junit5.JUnit5Mockery;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static e2e.ApplicationRunner.SNIPER_ID;


public class AuctionMessageTranslatorTest{
    private static final Chat UNUSED_CHAT = null;

    @RegisterExtension JUnit5Mockery context = new JUnit5Mockery();
    private final AuctionEventListener listener = context.mock(AuctionEventListener.class);
    private final XMPPFailureReporter failureReporter = context.mock(XMPPFailureReporter.class);

    private AuctionMessageTranslator translator = new AuctionMessageTranslator(SNIPER_ID, listener, failureReporter);

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
            exactly(1).of(listener).currentPrice(192, 7, AuctionEventListener.PriceSource.FromOtherBidder);
        }});

        final var message = new Message();
        message.setBody("SOLVersion: 1.1; Event: PRICE; CurrentPrice: 192; Increment: 7; Bidder: Someone else;");

        translator.processMessage(UNUSED_CHAT, message);
    }

    @Test
    void notifiesBidDetailsWhenCurrentPriceMessageReceivedFormSniper() {
        context.checking(new Expectations(){{
            exactly(1).of(listener).currentPrice(234, 5, AuctionEventListener.PriceSource.FromSniper);
        }});

        final var message = new Message();
        message.setBody("SOLVersion: 1.1; Event: PRICE; CurrentPrice: 234; Increment: 5; Bidder: "+SNIPER_ID+";");
        translator.processMessage(UNUSED_CHAT, message);
    }

    @Test
    void notifiesAuctionFailedWhenBadMessageReceived() {
        final var aBadMessage = "a bad message";
        expectFailureWithMessage(aBadMessage);
        translator.processMessage(UNUSED_CHAT, message(aBadMessage));
    }

    private Message message(String aBadMessage) {
        Message message = new Message();
        message.setBody(aBadMessage);
        return message;
    }

    private void expectFailureWithMessage(final String aBadMessage) {
        context.checking(new Expectations() {{
            oneOf(listener).auctionFailed();
            oneOf(failureReporter).cannotTranslateMessage(with(SNIPER_ID), with(aBadMessage), with(any(Exception.class)));
        }});
    }

    @Test
    void notifiesAuctionFailedWhenEventTypeMissing() {
        context.checking(new Expectations() {{
            allowing(failureReporter);
            exactly(1).of(listener).auctionFailed();
        }});

        final var message = new Message();
        message.setBody("SOLVersion: 1.1; CurrentPrice: ; Increment: 5; Bidder: "+SNIPER_ID+";");

        translator.processMessage(UNUSED_CHAT, message);
    }
}
