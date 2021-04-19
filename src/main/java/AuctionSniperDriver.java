import com.objogate.wl.swing.AWTEventQueueProber;
import com.objogate.wl.swing.driver.JFrameDriver;
import com.objogate.wl.swing.driver.JLabelDriver;
import com.objogate.wl.swing.gesture.GesturePerformer;

import static org.hamcrest.Matchers.equalTo;

public class AuctionSniperDriver extends JFrameDriver {

    public AuctionSniperDriver(int timeout) {
        super(new GesturePerformer(), JFrameDriver.topLevelFrame(named(MainWindow.MAIN_WINDOW_NAME), showingOnScreen()), new AWTEventQueueProber(timeout, 100));
    }

    public void showsSniperStatus(String statusText) {
        new JLabelDriver(this, named(MainWindow.SNIPER_STATUS_NAME)).hasText(equalTo(statusText));
    }

}
