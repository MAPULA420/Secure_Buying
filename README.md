# Secure_Buying

Sellers and customers on TikTok and Facebook meet scams daily. A customer can pay and never get the item. A customer can say they didn't receive a product they did get, and ruin the seller's reputation. A seller can send the wrong item. And a customer can send fake proof of payment.

Secure_Buying protects both sides by holding the payment in the app until the deal is done.

## How It Works

1. Seller and customer register. The ID check is simulated, and ID numbers are never stored.
2. The seller photographs the exact item and the buyer confirms it's the right product.
3. The customer pays the app, not the seller. No payment means no order is sent.
4. If the product isn't delivered and confirmed within 7 days of payment, the customer is refunded automatically.
5. When the product arrives, the buyer or the courier confirms receipt. This starts a one-day window to check for damage.
6. If nothing is reported in that day, the seller gets paid.
7. The seller pays the delivery fee upfront and gets it back when they're paid.
8. If the product arrives damaged, the customer gets a full refund once the seller confirms the product is returned. "Wrong product" claims don't apply, because the buyer approved the photo before shipping.
9. A change of mind is not covered. That's the seller's business.

## Built With

Java and AWS. S3 stores the photos, and DynamoDB stores the users and transactions. This is a proof of concept, and payment is simulated.

## Where It Started

This project began as a Solidity smart contract, which is in the `contracts/` folder. I then rebuilt it on AWS for the Cloud Computing elective.

## Status

## Status

Done:

- S3 bucket with versioning on and public access blocked
- `PhotoUploader` uploads, hashes, and downloads photos
- DynamoDB `Transactions` table, with code that saves and loads orders
- DynamoDB `Users` table, with a `User` model and repository
- Order logic: photo confirmation, payment, delivery (confirmed by the buyer or the courier), damage window, and refunds
- 7-day refund sweeper (`RefundSweeper`): finds paid orders past the deadline and refunds them. It runs by hand, and `SweeperDemo` shows it working.
- Demo programs that run each part against real AWS (`OrderFlowDemo`, `UserDemo`, `TransactionDemo`, `SweeperDemo`)

Not built yet:

- Running the sweeper automatically on a schedule
- Login and an API
