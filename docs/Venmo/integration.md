---
title: Pay with Venmo Custom Integration
keywords: 
contentType: docs
productStatus: current
apiVersion: TODO
sdkVersion: TODO
---
# Pay with Venmo Custom Integration

Follow these steps to add Venmo (//TODO: button):

1. [Know before you code](#know-before-you-code)
2. [Add PayPal Payments](#add-paypal-payments)
3. [Test and go live](#test-and-go-live)

## Know before you code

You will need to set up authorization to use the PayPal Payments SDK. 
Follow the steps in [Get Started](https://developer.paypal.com/api/rest/#link-getstarted) to create a client ID and generate an access token. 

You will need a server integration to create an order to capture funds using the [PayPal Orders v2 API](https://developer.paypal.com/docs/api/orders/v2).
For initial setup, the `curl` commands below can be used as a reference for making server-side RESTful API calls.

## Add PayPal Payments

### 1. Add the Payments SDK to your app

In your `build.gradle` file, add the following dependency

```groovy
dependencies {
    implementation "com.paypal.android:venmo:1.0.0"
}
```

### 2. Create a Venmo button 

Add a `VenmoButton` to your layout XML:

```xml
<com.paypal.android.checkout.paymentbutton.VenmoButton
    android:id="@+id/venmo_button"
    android:layout_width="match_parent"
    android:layout_height="wrap_content" />
```

### 3. Initiate the Payments SDK

Create a `CoreConfig` using your client ID from the PayPal Developer Portal:

```kotlin
val config = CoreConfig("<CLIENT_ID>", environment = Environment.SANDBOX)
```

Create a `VenmoLauncher` to make a payment using Venmo:

```kotlin
val venmoLauncher = VenmoLauncher(
    context = requireContext(),
    lifecycle = lifecycle,
    coreConfig = config //Config created in step 3
)
```

Call `payWithVenmo` method to handle the results:

```kotlin
venmoLauncher.payWithVenmo(orderId = orderId,
    onSuccess = {
        Log.d("Tag", "venmo success")
    }, onFailure = {
        Log.d("Tag", "venmo failure")
    })
```

## Testing and go live

### 1. Test the PayPal integration

Pay with Venmo is a mobile experience, so be sure to have the Venmo iOS or Android app installed and test on an iOS Safari or Android Chrome browser.

You can find more information [here](https://developer.paypal.com/docs/checkout/pay-with-venmo/integrate/)
