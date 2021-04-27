package auctionsniper;

import auctionsniper.ui.SniperTableModel;

import javax.swing.*;

public class SwingThreadSniperListener implements SniperListener {
    private final SniperTableModel ui;

    public SwingThreadSniperListener(SniperTableModel ui) {
        this.ui = ui;
    }

    @Override
    public void sniperStateChanged(SniperSnapshot snapshot) {
        SwingUtilities.invokeLater(() -> {
            ui.sniperStateChanged(snapshot);
        });
    }

}
