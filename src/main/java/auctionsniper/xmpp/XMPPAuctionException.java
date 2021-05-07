package auctionsniper.xmpp;

public class XMPPAuctionException extends Throwable {
    public XMPPAuctionException(String message, Exception exception) {
        super(message, exception);
    }
}
