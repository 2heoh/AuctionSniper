import org.jivesoftware.smack.*;
import org.jivesoftware.smack.packet.Message;

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

    public void hasReceivedJoinRequestFromSniper() throws InterruptedException {
        messageListener.receivesAMessage();
    }

    public void announceClosed() throws XMPPException {
        currentChat.sendMessage(new Message());
    }

    public void stop() {
        connection.disconnect();
    }
}
