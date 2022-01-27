---
title: Pay with Card Custom Integration
keywords: 
contentType: docs
productStatus: current
apiVersion: TODO
sdkVersion: TODO
---

Follow these steps to add Card payments.

1. [Know before you code](#know-before-you-code)
1. [Add Card Payments](#add-card-payments)
1. [Test and go live](#test-and-go-live)

## Know before you code

You will need to set up authorization to use the PayPal Payments SDK. 
Follow the steps in [Get Started](https://developer.paypal.com/api/rest/#link-getstarted) to create a client ID and generate an access token. 

You will need a server integration to create an order and capture the funds using [PayPal Orders v2 API](https://developer.paypal.com/docs/api/orders/v2). 
For initial setup, the `curl` commands below can be used in place of a server SDK.

## Add Card Payments

### 1. Add the Payments SDK  to your app

In your `build.gradle` file, add the following dependency:

```groovy
dependencies {
   implementation "com.paypal.android:card:1.0.0"
}
```

### 2. Initiate the Payments SDK

Create a `CoreConfig` using your client ID from the PayPal Developer Portal:

```kotlin
val config = CoreConfig("<CLIENT_ID>", environment = Environment.SANDBOX)
```

Create a `CardClient` to approve an order with a Card payment method:

```kotlin
val cardClient = CardClient(config)
```

### 3. Create an order

When a user enters the payment flow, call `v2/checkout/orders` to create an order and obtain an order ID:

```bash
curl --location --request POST 'https://api.sandbox.paypal.com/v2/checkout/orders/' \
--header 'Content-Type: application/json' \
--header 'Authorization: Bearer <ACCESS_TOKEN>' \
--data-raw '{
    "intent": "CAPTURE",
    "purchase_units": [
        {
            "amount": {
                "currency_code": "USD",
                "value": "5.00"
            }
        }
    ]
}'
```

The `id` field of the response contains the order ID to pass to your client.

### 4. Create a request containing the card payment details

Create a `Card` object containing the user's card details.

```kotlin
val card = Card()
```

Attach the card to a `CardRequest`.


```kotlin
val cardRequest  = CardRequest(card)
```

### 5. Approve the order through the Payments SDK

Approve the order using your `CardClient`.

Call `CardClient#approveOrder` to approve the order, and then handle results:

```kotlin
cardClient.approveOrder(cardRequest) { result ->
    when (result) {
        is PayPalCheckoutResult.Success -> {
            // capture/authorize the order (see step 6)
        } 
        is PayPalCheckoutResult.Failure -> {
            // handle the error by accessing `result.error`
        } 
        is PayPalCheckoutResult.Cancellation -> {
            // the user canceled
        } 
    }
}
```

### 6. Capture/authorize the order

If you receive a successful result in the client-side flow, you can then capture or authorize the order. 

Call `authorize` to place funds on hold:

```bash
curl --location --request POST 'https://api.sandbox.paypal.com/v2/checkout/orders/<ORDER_ID>/authorize' \
--header 'Content-Type: application/json' \
--header 'Authorization: Bearer <ACCESS_TOKEN>' \
--data-raw ''
```

Call `capture` to capture funds immediately:

```bash
curl --location --request POST 'https://api.sandbox.paypal.com/v2/checkout/orders/<ORDER_ID>/capture' \
--header 'Content-Type: application/json' \
--header 'Authorization: Bearer <ACCESS_TOKEN>' \
--data-raw ''
```

## Testing and Go Live

### 1. Test the Card integratoin

TODO - Do we have test card numbers merchants can test this with?

### 2. Go live with your integration

Follow [these instructions](https://developer.paypal.com/api/rest/production/) to prepare your integration to go live.
