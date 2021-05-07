package auctionsniper.ui;

import auctionsniper.SniperPortfolio;
import auctionsniper.SniperState;
import auctionsniper.UserRequestListener;
import auctionsniper.domain.Item;
import auctionsniper.util.Announcer;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

public class MainWindow extends JFrame {
    public static final String SNIPER_STATUS_NAME = "sniper status";
    public static final String MAIN_WINDOW_NAME = "Auction Sniper";

    public static final String STATUS_WON = "Won";
    public static final String STATUS_WINNING = "Winning";
    public static final String STATUS_JOINING = "Joining";
    public static final String STATUS_BIDDING = "Bidding";
    public static final String NEW_ITEM_ID_NAME = "item id";
    public static final String NEW_ITEM_STOP_PRICE_NAME = "stop price";
    public static final String JOIN_BUTTON_NAME = "Join Auction";
    public static final String STATUS_LOSING = "Losing";
    public static final String STATUS_FAILED = "Failed";

    private static final String SNIPERS_TABLE_NAME = "none";

    private final Announcer<UserRequestListener> userRequests =  Announcer.to(UserRequestListener.class);

    public MainWindow(SniperPortfolio snipers) {
        super("Auction Sniper");
        setName(MAIN_WINDOW_NAME);
        JLabel sniperStatus = createLabel(STATUS_JOINING);
        add(sniperStatus);
        fillContentPane(makeSnipersTable(snipers), makeControls());
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private JPanel makeControls() {
        final var controls = new JPanel(new FlowLayout());
        final var itemIdField = new JTextField();
        itemIdField.setColumns(25);
        itemIdField.setName(NEW_ITEM_ID_NAME);
        controls.add(itemIdField);

        final var stopPriceField = new JTextField();
        stopPriceField.setColumns(15);
        stopPriceField.setName(NEW_ITEM_STOP_PRICE_NAME);
        controls.add(stopPriceField);

        final var joinAuctionButton = new JButton("Join Auction");
        joinAuctionButton.setName(JOIN_BUTTON_NAME);
        joinAuctionButton.addActionListener(
            e -> userRequests.announce().joinAuction(new Item(itemIdField.getText(), Integer.parseInt(stopPriceField.getText())))
        );

        controls.add(joinAuctionButton);

        return controls;
    }

    private void fillContentPane(JTable snipersTable, JPanel controls) {
        final var contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(controls, BorderLayout.NORTH);
        contentPane.add(new JScrollPane(snipersTable), BorderLayout.CENTER);
    }

    private JTable makeSnipersTable(SniperPortfolio portfolio) {
        SniperTableModel model = new SniperTableModel();
        portfolio.addPortfolioListener(model);
        final var snipersTable = new JTable(model);
        snipersTable.setName(SNIPERS_TABLE_NAME);
        return snipersTable;
    }

    public static JLabel createLabel(String initialText) {
        JLabel result = new JLabel(initialText);
        result.setName(SNIPER_STATUS_NAME);
        result.setBorder(new LineBorder(Color.BLACK));
        return result;
    }

    public void addUserRequestListener(UserRequestListener userRequestListener) {
        userRequests.addListener(userRequestListener);
    }
}
