package auctionsniper.ui;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

public class MainWindow extends JFrame {
    public static final String SNIPER_STATUS_NAME = "sniper status";
    public static final String MAIN_WINDOW_NAME = "Auction Sniper";

    public static final String STATUS_LOST = "Lost";
    public static final String STATUS_WON = "Won";
    public static final String STATUS_WINNING = "Winning";
    public static final String STATUS_JOINING = "Joining";
    public static final String STATUS_BIDDING = "Bidding";
    private static final String SNIPERS_TABLE_NAME = "none";

    private final JLabel sniperStatus = createLabel(STATUS_JOINING);

    private final SniperTableModel snipers;

    public MainWindow(SniperTableModel snipers) {
        super("Auction Sniper");
        this.snipers = snipers;
        setName(MAIN_WINDOW_NAME);
        add(sniperStatus);
        fillContentPane(makeSnipersTable(snipers));
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void fillContentPane(JTable snipersTable) {
        final var contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
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

}
