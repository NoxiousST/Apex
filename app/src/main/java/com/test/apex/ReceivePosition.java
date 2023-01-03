package com.test.apex;

import android.content.Intent;

import com.test.apex.ui.Products.Product;

public interface ReceivePosition {
    public void receivePosition (int listPosition, int menu, Intent intent, Product product);

    public void receiveSubTotal (long subTotal);
}
