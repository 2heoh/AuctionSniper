package auctionsniper.ui;

import auctionsniper.*;
import com.objogate.exception.Defect;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class SniperTableModel extends AbstractTableModel implements SniperListener, SniperCollector {
    private final static SniperSnapshot STARTING_UP = new SniperSnapshot("-", 0, 0, SniperState.JOINING);
    private final List<SniperSnapshot> sniperSnapshots;
    private final List<AuctionSniper> notToBeGCd = new ArrayList<AuctionSniper>();

    private static final String[] STATUS_TEXT = {
            "Joining",
            "Bidding",
            "Winning",
            "Lost",
            "Won",
    };

    public SniperTableModel() {
        this.sniperSnapshots = new ArrayList<>();
    }

    @Override
    public void sniperStateChanged(SniperSnapshot newSniperSnapshot) {
        int row = matchRow(newSniperSnapshot);
        sniperSnapshots.set(row, newSniperSnapshot);
        fireTableRowsUpdated(row, row);
    }

    private int matchRow(SniperSnapshot snapshot) {
        for (int i = 0; i < sniperSnapshots.size(); i++) {
            if (snapshot.itemId.equals(sniperSnapshots.get(i).itemId)) {
                return i;
            }
        }
        throw new Defect("Cannot find match for " + snapshot);
    }

    @Override
    public void addSniper(SniperSnapshot snapshot) {
        sniperSnapshots.add(snapshot);
        fireTableRowsInserted(0, 0);
    }

    public int getColumnCount() {
        return Column.values().length;
    }

    public int getRowCount() {
        return sniperSnapshots.size();
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        return Column.at(columnIndex).valueIn(sniperSnapshots.get(rowIndex));
    }

    public static String textFor(SniperState state) {
        return STATUS_TEXT[state.ordinal()];
    }

    @Override
    public String getColumnName(int column) {
        return Column.at(column).name;
    }

    @Override
    public void addSniper(AuctionSniper sniper) {
        notToBeGCd.add(sniper);
        addSniperSnapshot(sniper.getSnapshot());
        sniper.addSniperListener(new SwingThreadSniperListener(this));
    }

    private void addSniperSnapshot(SniperSnapshot snapshot) {
        sniperSnapshots.add(snapshot);
        int row = sniperSnapshots.size() - 1;
        fireTableRowsInserted(row, row);
    }
}
