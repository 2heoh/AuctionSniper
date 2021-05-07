package intergration.ui;

import auctionsniper.SniperPortfolio;
import auctionsniper.UserRequestListener;
import auctionsniper.domain.Item;
import auctionsniper.ui.MainWindow;
import auctionsniper.ui.SniperTableModel;
import com.objogate.wl.swing.probe.ValueMatcherProbe;
import e2e.AuctionSniperDriver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Properties;

import static org.hamcrest.Matchers.equalTo;

public class MainWindowTest {
    private static final Properties props = System.getProperties();;
    private AuctionSniperDriver driver;
    private MainWindow mainWindow;

    @BeforeEach
    void setUp() {
        props.setProperty("com.objogate.wl.keyboard", "Mac-GB");
        driver = new AuctionSniperDriver(1000);
        mainWindow = new MainWindow(new SniperPortfolio());
    }

    @Test
    void makesUserRequestWhenJoinButtonClicked() {
        final ValueMatcherProbe<Item> itemProbe =
                new ValueMatcherProbe<>(equalTo(new Item("an item-id", 789)), "item request");

        mainWindow.addUserRequestListener(item -> itemProbe.setReceivedValue(item));
        driver.startBiddingFor("an item-id", 789);
        driver.check(itemProbe);
    }

}
