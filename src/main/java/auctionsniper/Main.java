package auctionsniper;

import auctionsniper.ui.MainWindow;
import auctionsniper.ui.SniperTableModel;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    private static final int HOSTNAME = 0;
    private static final int USERNAME = 1;
    private static final int PASSWORD = 2;
    private static final int ITEM_ID = 3;

    private static final String AUCTION_RESOURCE = "Auction";
    private static final String ITEM_ID_AS_LOGIN = "auction-%s";
    private static final String AUCTION_ID_FORMAT = ITEM_ID_AS_LOGIN + "@%s/" + AUCTION_RESOURCE;

    public static final String JOIN_COMMAND_FORMAT = "SOLVersion: 1.1; Command: JOIN;";
    public static final String BID_COMMAND_FORMAT = "SOLVersion: 1.1; Command: Bid; Price: %d;";

    private final SniperTableModel snipers = new SniperTableModel();

    private MainWindow ui;
    @SuppressWarnings("unused")
    private List<Chat> notToBeGCd = new ArrayList<>();

    public Main() throws InterruptedException, InvocationTargetException {
        SwingUtilities.invokeAndWait(() -> ui = new MainWindow(snipers));
    }

    public static void main(String... args) throws Exception {
        Main main = new Main();
        final var connection = connection(args[HOSTNAME], args[USERNAME], args[PASSWORD]);
        main.disconnectWhenUICloses(connection);
        main.addUserRequestListenerFor(connection);
    }

    private void addUserRequestListenerFor(final XMPPConnection connection) {
        ui.addUserRequestListener(new UserRequestListener() {
            @Override
            public void joinAuction(String itemId) {
                snipers.addSniper(SniperSnapshot.joining(itemId));
                final var chat = connection.getChatManager().createChat(auctionId(itemId, connection), null);
                notToBeGCd.add(chat);
                final var auction = new XMPPAuction(chat);
                chat.addMessageListener(
                    new AuctionMessageTranslator(
                        connection.getUser(),
                        new AuctionSniper(itemId, auction, new SwingThreadSniperListener(snipers))
                    )
                );
                auction.join();
            }
        });
    }

    private void joinAuction(XMPPConnection connection, String itemId) throws Exception {
        safelyAddItemToModel(itemId);
        disconnectWhenUICloses(connection);
        final var chat = connection.getChatManager().createChat(auctionId(itemId, connection), null);
        notToBeGCd.add(chat);

        Auction auction = new XMPPAuction(chat);
        chat.addMessageListener(
            new AuctionMessageTranslator(
                connection.getUser(),
                new AuctionSniper(itemId, auction, new SwingThreadSniperListener(snipers))
            )
        );

        auction.join();
    }

    private void safelyAddItemToModel(String itemId) throws InterruptedException, InvocationTargetException {
        SwingUtilities.invokeAndWait(() -> snipers.addSniper(SniperSnapshot.joining(itemId)));
    }

    private void disconnectWhenUICloses(XMPPConnection connection) {
        ui.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                connection.disconnect();
            }
        });
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

}
