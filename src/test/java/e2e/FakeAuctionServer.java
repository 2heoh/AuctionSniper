package e2e;

import auctionsniper.AuctionServer;
import auctionsniper.Main;
import org.hamcrest.Matcher;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class FakeAuctionServer implements AuctionServer {
    private static final String ITEM_AS_LOGIN = "auction-%s";
    private static final String AUCTION_PASSWORD = "auction";
    private static final String AUCTION_RESOURCE = "Auction";
    private static final String XMPP_HOSTNAME = "104.248.47.45";

    private Chat currentChat;

    private final String itemId;
    private final XMPPConnection connection;

    public FakeAuctionServer(String itemId) {
        this.itemId = itemId;
        this.connection = new XMPPConnection(XMPP_HOSTNAME);
    }

    private final SingleMessageListener messageListener = new SingleMessageListener();

    public void startSellingItem() throws XMPPException {
        connection.connect();
        SASLAuthentication.unregisterSASLMechanism("DIGEST-MD5");
        final var username = String.format(ITEM_AS_LOGIN, itemId);
        connection.login(username, AUCTION_PASSWORD, AUCTION_RESOURCE);
        connection.getChatManager().addChatListener((chat, b) -> {
            currentChat = chat;
            chat.addMessageListener(messageListener);
        });
    }

    public String getItemId() {
        return itemId;
    }

    @Override
    public void reportPrice(int price, int increment, String bidder) throws XMPPException {
        currentChat.sendMessage(String.format("SOLVersion: 1.1; Event: PRICE; CurrentPrice: %d; Increment: %d; Bidder: %s", price, increment, bidder));
    }

    public void hasReceivedJoinRequestFrom(String sniperId) throws InterruptedException {
        receivesAMessageMatching(sniperId, equalTo(Main.JOIN_COMMAND_FORMAT));
    }

    private void receivesAMessageMatching(String sniperId, Matcher<? super String> messageMatcher) throws InterruptedException {
        messageListener.receivesAMessage(messageMatcher);
        assertThat(currentChat.getParticipant(), equalTo(sniperId));
    }

    public void hasReceivedJoinRequestFromSniper() throws InterruptedException {
        messageListener.receivesAMessage();
    }

    @Override
    public void hasReceivedBid(int bid, String sniperId) throws InterruptedException {
        receivesAMessageMatching(sniperId, equalTo(String.format(Main.BID_COMMAND_FORMAT, bid)));
    }

    public void announceClosed() throws XMPPException {
        final var message = new Message();
        message.setBody("SOLVersion: 1.1; Event: CLOSE;");
        currentChat.sendMessage(message);
    }

    public void stop() {
        connection.disconnect();
    }
}
