package auctionsniper;

import auctionsniper.domain.Item;

import java.util.EventListener;

public interface UserRequestListener extends EventListener {
//    @Deprecated
//    void joinAuction(String itemId);

    void joinAuction(Item item);
}
