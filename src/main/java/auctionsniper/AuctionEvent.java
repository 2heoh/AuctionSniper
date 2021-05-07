package auctionsniper;

import java.util.HashMap;
import java.util.Map;

import static auctionsniper.AuctionEventListener.PriceSource.FromOtherBidder;
import static auctionsniper.AuctionEventListener.PriceSource.FromSniper;

public class AuctionEvent {
    private final Map<String, String> fields = new HashMap<>();

    public String type() throws MissingValueException {
        return get("Event");
    }

    public int currentPrice() throws MissingValueException {
        return getInt("CurrentPrice");
    }

    public int increment() throws MissingValueException {
        return getInt("Increment");
    }

    private String bidder() throws MissingValueException {
        return get("Bidder");
    }

    public AuctionEventListener.PriceSource isFrom(String sniperId) throws MissingValueException {
        return sniperId.equals(bidder()) ? FromSniper : FromOtherBidder;
    }

    static AuctionEvent from(String messageBody) {
        AuctionEvent event = new AuctionEvent();
        for (String field : fieldsIn(messageBody)) {
            event.addField(field);
        }
        return event;
    }

    private void addField(String field) {
        final var pair = field.split(":");
        fields.put(pair[0].trim(), pair[1].trim());
    }

    private static String[] fieldsIn(String messageBody) {
        return messageBody.split(";");
    }

    private int getInt(String fieldName) throws MissingValueException {
        return Integer.parseInt(get(fieldName));
    }

    private String get(String fieldName) throws MissingValueException {
        final var value = fields.get(fieldName);
        if (null == value) {
            throw new MissingValueException(fieldName);
        }
        return value;
    }

}
