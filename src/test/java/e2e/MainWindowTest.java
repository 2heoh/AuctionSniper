package e2e;

import auctionsniper.UserRequestListener;
import auctionsniper.ui.MainWindow;
import auctionsniper.ui.SniperTableModel;
import com.objogate.wl.swing.probe.ValueMatcherProbe;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.equalTo;

public class MainWindowTest {

    private AuctionSniperDriver driver = new AuctionSniperDriver(1000);
    private MainWindow mainWindow = new MainWindow(new SniperTableModel());

    @Test
    void makesUserRequestWhenJoinButtonClicked() {
        final var buttonProbe = new ValueMatcherProbe<String>(equalTo("item id"), "join request");

        mainWindow.addUserRequestListener(new UserRequestListener(){
            @Override
            public void joinAuction(String itemId) {
                buttonProbe.setReceivedValue(itemId);
            }
        });

        driver.startBiddingFor("item id");
        driver.check(buttonProbe);
    }
}
