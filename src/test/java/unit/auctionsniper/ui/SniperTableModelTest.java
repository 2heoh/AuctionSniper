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
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.samePropertyValuesAs;
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
        context.checking(new Expectations(){{
            oneOf(listener).tableChanged(with(aRowChangedEvent()));
        }});

        model.sniperStateChanged(new SniperSnapshot("item id", 555, 666, SniperState.BIDDING));

        assertColumnsEquals(Column.ITEM_IDENTIFIER, "item id");
        assertColumnsEquals(Column.LAST_PRICE, 555);
        assertColumnsEquals(Column.LAST_BID, 666);
        assertColumnsEquals(Column.SNIPER_STATE, textFor(SniperState.BIDDING));
    }

    @Test
    void setUpColumnHeadings() {
        for (Column column: Column.values()) {
            assertEquals(column.name, model.getColumnName(column.ordinal()));
        }
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