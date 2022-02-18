package com.paypal.android.card

import com.paypal.android.core.OrderStatus

/**
 * A result returned by [CardClient] when an order was successfully approved with a [Card].
 */
data class CardResult(val orderID: String, val status: OrderStatus)
