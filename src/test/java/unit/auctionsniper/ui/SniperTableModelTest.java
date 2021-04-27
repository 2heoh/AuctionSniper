package unit.auctionsniper.ui;

import auctionsniper.ui.Column;import auctionsniper.SniperSnapshot;
import auctionsniper.SniperState;
import auctionsniper.ui.SniperTableModel;
import org.hamcrest.Matcher;
import org.jmock.Expectations;
import org.jmock.junit5.JUnit5Mockery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import static auctionsniper.ui.SniperTableModel.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SniperTableModelTest {
    @RegisterExtension
    JUnit5Mockery context = new JUnit5Mockery();
    private SniperTableModel model = new SniperTableModel();
    private TableModelListener listener = context.mock(TableModelListener.class);

    @BeforeEach
    public void attachModeListener() {
        model.addTableModelListener(listener);
    }

    @Test
    void hasEnoughColumns() {
        assertThat(model.getColumnCount(), equalTo(Column.values().length));
    }

    @Test
    void setsSniperValuesInColumns() {
        final var joining = SniperSnapshot.joining("item id");
        final var bidding = joining.bidding(555, 666);

        context.checking(new Expectations(){{
            allowing(listener).tableChanged(with(anyInsertionEvent()));

            oneOf(listener).tableChanged(with(aChangedRow(0)));
        }});

        model.addSniper(joining);
        model.sniperStateChanged(bidding);

        assertRowMatchesSnapshot(0, bidding);
    }

    private Matcher<TableModelEvent> aChangedRow(int row) {
        return samePropertyValuesAs(new TableModelEvent(model, row));
    }

    private Matcher<TableModelEvent> anyInsertionEvent() {
        return hasProperty("type", equalTo(TableModelEvent.INSERT));
    }

    @Test
    void setUpColumnHeadings() {
        for (Column column: Column.values()) {
            assertEquals(column.name, model.getColumnName(column.ordinal()));
        }
    }

    @Test
    void notifiesListenersWhenAddingASniper() {
        final var joining = SniperSnapshot.joining("test-item");
        context.checking(new Expectations(){{
            oneOf(listener).tableChanged(with(anInsertionAtRow(0)));
        }});

        assertEquals(0, model.getRowCount());

        model.addSniper(joining);

        assertEquals(1, model.getRowCount());
        assertRowMatchesSnapshot(0, joining);
    }

    @Test
    void holdsSniperInAdditionOrder() {
        context.checking(new Expectations(){{
            ignoring(listener);
        }});

        model.addSniper(SniperSnapshot.joining("item 0"));
        model.addSniper(SniperSnapshot.joining("item 1"));

        assertEquals("item 0", cellValue(0, Column.ITEM_IDENTIFIER));
        assertEquals("item 1", cellValue(1, Column.ITEM_IDENTIFIER));
    }

    private Matcher<TableModelEvent> anInsertionAtRow(int rowNumber) {
        return samePropertyValuesAs(new TableModelEvent(model, rowNumber, rowNumber, TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT));
    }

    private void assertRowMatchesSnapshot(int row, SniperSnapshot snapshot) {
        assertEquals(snapshot.itemId, cellValue(row, Column.ITEM_IDENTIFIER));
        assertEquals(snapshot.lastPrice, cellValue(row, Column.LAST_PRICE));
        assertEquals(snapshot.lastBid, cellValue(row, Column.LAST_BID));
        assertEquals(SniperTableModel.textFor(snapshot.state), cellValue(row, Column.SNIPER_STATE));
    }

    private Object cellValue(int rowIndex, Column column) {
        return model.getValueAt(rowIndex, column.ordinal());
    }

    private Matcher<TableModelEvent> aRowChangedEvent() {
        return samePropertyValuesAs(new TableModelEvent(model, 0));
    }

    private void assertColumnsEquals(Column column, Object expected) {
        final int rowIndex = 0;
        final int columnIndex = column.ordinal();
        assertEquals(expected, model.getValueAt(rowIndex, columnIndex));
    }
}