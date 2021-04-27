package auctionsniper.ui;

import auctionsniper.SniperListener;
import auctionsniper.SniperSnapshot;
import auctionsniper.SniperState;

import javax.swing.table.AbstractTableModel;

public class SniperTableModel extends AbstractTableModel implements SniperListener {
    private final static SniperSnapshot STARTING_UP = new SniperSnapshot("-", 0, 0, SniperState.JOINING);
    private SniperSnapshot sniperSnapshot = STARTING_UP;

    private static String[] STATUS_TEXT = {
        "Joining",
        "Bidding",
        "Winning",
        "Lost",
        "Won",
    };

    @Override
    public void sniperStateChanged(SniperSnapshot newSniperSnapshot) {
        sniperSnapshot = newSniperSnapshot;
        fireTableRowsUpdated(0, 0);
    }

    public int getColumnCount() {
        return Column.values().length;
    }

    public int getRowCount() {
        return 1;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        return Column.at(columnIndex).valueIn(sniperSnapshot);
    }

    public static String textFor(SniperState state) {
        return STATUS_TEXT[state.ordinal()];
    }

    @Override
    public String getColumnName(int column) {
        return Column.at(column).name;
    }
}
