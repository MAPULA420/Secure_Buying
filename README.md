# Secure_Buying

Sellers and customers on TikTok and Facebook meet scams daily. A customer can pay and never get the item. A customer can say they didn't receive a product they did get, and ruin the seller's reputation. A seller can send the wrong item. And a customer can send fake proof of payment.

## How the App Solves It

Secure_Buying protects both sides by holding the payment in the app until the deal is done. The buyer and seller move their deal from TikTok or Facebook into the app, where both prove who they are with an ID and a face scan. The seller photographs the exact item, so the buyer can confirm that it is the right product, so a "wrong product" claim can't happen later. The buyer then pays the app, not the seller, and both can see that the money is held.

When the seller ships, her ID is scanned at the courier and the order is marked as shipped, so both sides can track it. At handover, the courier scans the buyer's ID, and the app checks that it belongs to the person who bought the item. This starts a one-day window to report a fault. If nothing is reported, the seller is paid. If the item doesn't work or isn't as shown, the buyer sends it back and is refunded once the seller confirms she received it. If nothing is delivered within 7 days of payment, the buyer is refunded automatically. A change of mind is not covered, because that is the seller's business.

This is a proof of concept built with Java and AWS (S3 for photos, DynamoDB for orders and users). Payment, ID checks and face scans are simulated, and there are no app screens yet. The demo programs run the order logic against real AWS.

## Future Features

- Chat inside the app, with photos sent through it
- Real ID and face checks, for example with AWS Textract and Rekognition
- A login, an API and app screens, so buyer and seller can track an order themselves
- Running the refund sweeper automatically on a schedule
- Connecting the app to the Solidity contract in `contracts/`, with Chainlink Automation, so the money is held on a blockchain

## How to Run

You need Java 17 or newer, Maven, and an AWS account with the AWS CLI set up (`aws configure`, region `us-east-1`). Your AWS user needs access to S3 and DynamoDB.

1. Create an S3 bucket for the photos (the name must be unique), block public access, and turn on versioning:

```
aws s3api create-bucket --bucket YOUR-BUCKET-NAME --region us-east-1
aws s3api put-public-access-block --bucket YOUR-BUCKET-NAME --public-access-block-configuration BlockPublicAcls=true,IgnorePublicAcls=true,BlockPublicPolicy=true,RestrictPublicBuckets=true
aws s3api put-bucket-versioning --bucket YOUR-BUCKET-NAME --versioning-configuration Status=Enabled
```

2. Create the two DynamoDB tables:

```
aws dynamodb create-table --table-name Transactions --attribute-definitions AttributeName=transactionId,AttributeType=S --key-schema AttributeName=transactionId,KeyType=HASH --provisioned-throughput ReadCapacityUnits=5,WriteCapacityUnits=5 --region us-east-1
aws dynamodb create-table --table-name Users --attribute-definitions AttributeName=userId,AttributeType=S --key-schema AttributeName=userId,KeyType=HASH --provisioned-throughput ReadCapacityUnits=5,WriteCapacityUnits=5 --region us-east-1
```

3. Put a JPG called `test.jpg` in the `cloud` folder, and tell the program your bucket name:

```
$env:PHOTO_BUCKET = "YOUR-BUCKET-NAME"     # PowerShell
export PHOTO_BUCKET=YOUR-BUCKET-NAME       # Linux or Mac
```

4. Build and run the demos from the `cloud` folder (in PowerShell, keep the quotes around the `-D` option):

```
cd cloud
mvn clean compile
mvn exec:java
mvn exec:java "-Dmain.class=UserDemo"
mvn exec:java "-Dmain.class=OrderFlowDemo"
mvn exec:java "-Dmain.class=SweeperDemo"
```

- `mvn exec:java` uploads `test.jpg` to S3, prints its SHA-256 hash, and downloads it back as `downloaded.jpg`.
- `UserDemo` registers a buyer, a seller and a courier.
- `OrderFlowDemo` runs three order scenarios (happy path, never delivered, damaged or faulty item) with a fake clock, so seven days pass instantly.
- `SweeperDemo` shows the 7-day refund sweeper refunding overdue orders.
