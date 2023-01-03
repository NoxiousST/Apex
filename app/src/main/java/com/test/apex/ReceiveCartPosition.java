package com.test.apex;

public interface ReceiveCartPosition {
    public void receivePosition (int listPosition, Cart cart);

    public void receiveSubTotal (long subTotal);
}
