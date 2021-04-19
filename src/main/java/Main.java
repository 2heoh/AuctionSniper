import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;

public class Main {
    private static final int HOSTNAME = 0;
    private static final int USERNAME = 1;
    private static final int PASSWORD = 2;

    private static final int ITEM_ID = 3;

    private static final String AUCTION_RESOURCE = "Auction";
    private static final String ITEM_ID_AS_LOGIN = "auction-%s";
    private static final String AUCTION_ID_FORMAT = ITEM_ID_AS_LOGIN + "@%s/" + AUCTION_RESOURCE;

    private MainWindow ui;
    @SuppressWarnings("unused") private Chat notToBeGCd;

    public Main() throws InterruptedException, InvocationTargetException {
        startUserInterface();
    }

    public static void main(String... args) throws Exception {
        Main main = new Main();
        main.joinAuction(connection(args[HOSTNAME], args[USERNAME], args[PASSWORD]), args[ITEM_ID]);
    }

    private void joinAuction(XMPPConnection connection, String itemId) throws XMPPException {
        final var chat = connection.getChatManager().createChat(auctionId(itemId, connection),
                (chat1, message) -> SwingUtilities.invokeLater(() -> {
                    ui.showStatus(MainWindow.STATUS_LOST);
                }));

        this.notToBeGCd = chat;

        chat.sendMessage(new Message());
    }

    private static XMPPConnection connection(String hostname, String username, String password) throws XMPPException {
        final var connection = new XMPPConnection(hostname);
        connection.connect();
        connection.login(username, password, AUCTION_RESOURCE);
        return connection;
    }

    private static String auctionId(String itemId, XMPPConnection connection) {
        return String.format(AUCTION_ID_FORMAT, itemId, connection.getServiceName());
    }

    private void startUserInterface() throws InterruptedException, InvocationTargetException {
        SwingUtilities.invokeAndWait(() -> ui = new MainWindow());
    }
}
