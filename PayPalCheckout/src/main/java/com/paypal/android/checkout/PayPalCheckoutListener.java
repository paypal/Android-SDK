package com.paypal.android.checkout;

import androidx.annotation.NonNull;

public interface PayPalCheckoutListener {
    void onPayPalCheckoutResult(@NonNull PayPalCheckoutResult result);
}