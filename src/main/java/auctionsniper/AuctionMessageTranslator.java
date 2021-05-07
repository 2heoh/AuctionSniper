package auctionsniper;

import auctionsniper.xmpp.XMPPFailureReporter;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.packet.Message;

public class AuctionMessageTranslator implements MessageListener {
    private final AuctionEventListener listener;
    private final String sniperId;
    private final XMPPFailureReporter failureReporter;

    public AuctionMessageTranslator(String sniperId, AuctionEventListener listener, XMPPFailureReporter failureReporter) {
        this.sniperId = sniperId;
        this.listener = listener;

        this.failureReporter = failureReporter;
    }

    public void processMessage(Chat chat, Message message) {
        final var messageBody = message.getBody();
        try {
            translate(messageBody);
        } catch (RuntimeException exception) {
            failureReporter.cannotTranslateMessage(sniperId, messageBody, exception);
            listener.auctionFailed();
        }
    }

    private void translate(String message) throws MissingValueException {
        AuctionEvent event = AuctionEvent.from(message);
        String eventType = event.type();
        if ("CLOSE".equals(eventType)) {
            listener.auctionClosed();
        } else if ("PRICE".equals(eventType)) {
            listener.currentPrice(
                    event.currentPrice(),
                    event.increment(),
                    event.isFrom(sniperId)
            );
        }
    }
}
