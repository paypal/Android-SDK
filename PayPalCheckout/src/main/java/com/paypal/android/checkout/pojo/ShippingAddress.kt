package com.paypal.android.checkout.pojo

data class ShippingAddress(
    val firstName: String? = null,
    val lastName: String? = null,
    val line1: String? = null,
    val line2: String? = null,
    val city: String? = null,
    val state: String? = null,
    val postalCode: String? = null,
    val country: String? = null,
    val isFullAddress: Boolean? = null,
    val isStoreAddress: Boolean? = null
) {
    internal constructor(cartShippingAddress: com.paypal.pyplcheckout.pojo.CartShippingAddress?) : this(
        firstName = cartShippingAddress?.firstName,
        lastName = cartShippingAddress?.lastName,
        line1 = cartShippingAddress?.line1,
        line2 = cartShippingAddress?.line2,
        city = cartShippingAddress?.city,
        state = cartShippingAddress?.state,
        postalCode = cartShippingAddress?.postalCode,
        country = cartShippingAddress?.country,
        isFullAddress = cartShippingAddress?.isFullAddress,
        isStoreAddress = cartShippingAddress?.isStoreAddress
    )
}