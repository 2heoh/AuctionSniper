package intergration.ui;

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
        mainWindow = new MainWindow(new SniperTableModel());
    }

    @Test
    void makesUserRequestWhenJoinButtonClicked() {
        final var buttonProbe = new ValueMatcherProbe<String>(equalTo("item-id"), "join request");

        mainWindow.addUserRequestListener(itemId -> buttonProbe.setReceivedValue(itemId));

        driver.startBiddingFor("item-id");
        driver.check(buttonProbe);
    }

}
