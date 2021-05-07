package e2e;

import org.apache.commons.io.FileUtils;
import org.hamcrest.Matcher;

import java.io.File;
import java.io.IOException;
import java.util.logging.LogManager;

import static org.hamcrest.MatcherAssert.assertThat;

public class AuctionLogDriver {
    private static final String LOG_FILE_NAME = "auction-sniper.log";
    private final File logFile = new File(LOG_FILE_NAME);

    public void hasEntry(Matcher<String> matcher) throws IOException {
        assertThat(FileUtils.readFileToString(logFile, "UTF-8"), matcher);
    }

    public void clear() {
        logFile.delete();
        LogManager.getLogManager().reset();
    }
}
