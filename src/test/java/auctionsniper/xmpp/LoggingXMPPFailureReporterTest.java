package auctionsniper.xmpp;

import org.jmock.Expectations;
import org.jmock.junit5.JUnit5Mockery;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.jmock.lib.legacy.ClassImposteriser;

import java.util.logging.Logger;

class LoggingXMPPFailureReporterTest {
    @RegisterExtension
    JUnit5Mockery context = new JUnit5Mockery() {{
        setImposteriser(ClassImposteriser.INSTANCE);
    }};
    private final Logger logger = context.mock(Logger.class);
    private final LoggingXMPPFailureReporter reporter = new LoggingXMPPFailureReporter(logger);

    @Test
    void writesMessageTranslationFailureToLog() {
        context.checking(new Expectations(){{
            oneOf(logger).severe("<auction id> Could not translate message \"bad message\" " +
                    "because \"java.lang.Exception: bad\"");
        }});

        reporter.cannotTranslateMessage("auction id", "bad message", new Exception("bad"));
    }
}