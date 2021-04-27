package auctionsniper.ui;

import auctionsniper.UserRequestListener;
import auctionsniper.util.Announcer;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainWindow extends JFrame {
    public static final String SNIPER_STATUS_NAME = "sniper status";
    public static final String MAIN_WINDOW_NAME = "Auction Sniper";

    public static final String STATUS_WON = "Won";
    public static final String STATUS_WINNING = "Winning";
    public static final String STATUS_JOINING = "Joining";
    public static final String STATUS_BIDDING = "Bidding";
    public static final String NEW_ITEM_ID_NAME = "item id";
    public static final String JOIN_BUTTON_NAME = "Join Auction";

    private static final String SNIPERS_TABLE_NAME = "none";

    private final JLabel sniperStatus = createLabel(STATUS_JOINING);

//    private final SniperTableModel snipers;

    private final Announcer<UserRequestListener> userRequests =  Announcer.to(UserRequestListener.class);

    public MainWindow(SniperTableModel snipers) {
        super("Auction Sniper");
//        this.snipers = snipers;
        setName(MAIN_WINDOW_NAME);
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

        final var joinAuctionButton = new JButton("Join Auction");
        joinAuctionButton.setName(JOIN_BUTTON_NAME);
        joinAuctionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                userRequests.announce().joinAuction(itemIdField.getText());
            }
        });
        controls.add(joinAuctionButton);

        return controls;
    }

    private void fillContentPane(JTable snipersTable, JPanel controls) {
        final var contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(controls, BorderLayout.NORTH);
        contentPane.add(new JScrollPane(snipersTable), BorderLayout.CENTER);
    }

    private JTable makeSnipersTable(SniperTableModel snipers) {
        final var snipersTable = new JTable(snipers);
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
