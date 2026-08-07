# Secure_Buying

## Problem Statement
Sellers and customers on TikTok and Facebook meet scams daily. A customer can pay for a good and that good never gets delivered. A seller can sell and the customer claims they didn't receive the product while they did, ruining the seller's reputation. A seller can send the wrong good and get away with it. And a customer can send fake proof of payment.

So my system is designed to help both sides using a smart contract.

## How It Works

There will be an app where when a seller and customer want to exchange goods, they both register with their ID. Before shipping, the seller takes a photo of the exact item and the buyer confirms it's the right product. Instead of the seller getting money directly from the customer, the customer transfers money to the app directly. Once the payment is confirmed by the app, the seller can proceed with sending the delivery. If the customer hasn't paid, no order is sent at all.

If the seller doesn't get the product delivered and confirmed within 7 days of payment, the contract automatically refunds the customer — no one needs to report anything, the system enforces this on its own.

As soon as the delivery guy hands over the product, the buyer scans their ID on the spot to notify the app that the product has been received. This starts a one-day window for the buyer to check for any damage. If nothing is reported within that day, the seller gets paid.

Normally a customer covers the delivery fee, but since the money sits in the app, the seller has to use their own money to deliver, and they get it back once they're paid.

If the product arrives damaged, the customer won't have to pay for anything and gets a full refund of their money, but only after the damaged product has been returned and the seller confirms they've received it back. Since the buyer already confirmed the item's photo before it shipped, a claim of "wrong product" doesn't apply — only genuine damage that happened after that confirmation.

If it's just the customer changing their mind, that's not covered — that's the seller's business, not a fault in the product.

## Why Blockchain
I designed this so that neither side has to just trust the other person, or trust me as the person who built the app. To come up with this idea, I used a principle of smart contracts called trust-minimized — where the money sits in the contract, not with either person, and it only moves when the conditions are actually met. I was also inspired by Chainlink Automation, where actions only happen once certain requirements are met, without anyone needing to trigger it manually. Since this is a decentralized application, both the buyer and the seller can see the transaction — no one is kept in the dark or has to just take the other person's word for it.

## Future Features
- Chainlink Automation, so the payment release after the one-day window and the delivery deadline happens on its own, without anyone needing to trigger it manually.
- A stronger version of delivery confirmation using real ID verification, not just a manual confirmation.

