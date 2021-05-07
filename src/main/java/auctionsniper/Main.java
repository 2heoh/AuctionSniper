package auctionsniper;

import auctionsniper.ui.MainWindow;
import auctionsniper.ui.SniperTableModel;
import auctionsniper.xmpp.XMPPAuctionException;
import auctionsniper.xmpp.XMPPAuctionHouse;

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

    private final SniperPortfolio portfolio = new SniperPortfolio();
    private MainWindow ui;
    @SuppressWarnings("unused")
    private final List<Auction> notToBeGCd = new ArrayList<>();

    public Main() throws InterruptedException, InvocationTargetException {
        SwingUtilities.invokeAndWait(() -> ui = new MainWindow(portfolio));
    }

    public static void main(String... args) throws Exception, XMPPAuctionException {
        Main main = new Main();
        XMPPAuctionHouse auctionHouse = XMPPAuctionHouse.connect(args[HOSTNAME], args[USERNAME], args[PASSWORD]);
        main.disconnectWhenUICloses(auctionHouse);
        main.addUserRequestListenerFor(auctionHouse);
    }

    private void addUserRequestListenerFor(final AuctionHouse auctionHouse) {
        ui.addUserRequestListener(new SniperLauncher(auctionHouse, portfolio));
    }

    private void disconnectWhenUICloses(final AuctionHouse auctionHouse) {
        ui.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                auctionHouse.disconnect();
            }
        });
    }

}
