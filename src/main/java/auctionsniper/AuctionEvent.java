package auctionsniper;

import java.util.HashMap;
import java.util.Map;

public class AuctionEvent {
    private final Map<String, String> fields = new HashMap<>();

    public String type() {
        return get("Event");
    }

    public int currentPrice() {
        return getInt("CurrentPrice");
    }

    public int increment() {
        return getInt("Increment");
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

    private int getInt(String fieldName) {
        return Integer.parseInt(get(fieldName));
    }

    private String get(String fieldName) {
        return fields.get(fieldName);
    }

}
