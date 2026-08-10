// SPDX-License-Identifier: MIT
pragma solidity 0.8.19;


contract Secure_Buying{
  struct User { //when a user is registering on the app they need to comfirm that they ae using their id
    address userAddress;
    uint userIdNumber;
    bool validId;
    string fullNames;
}

struct Transaction {//when the buyer and seller are actually exchanging parcels
    address buyer;
    address seller;
    uint amount;
    uint deliveryfee;
    uint paymentTimestamp;
    bool itemPhotoConfirmed;
    uint deliveryDateConfirmation;
    bool damageReport;
    bool returnedParcel;
    bool sellerPayment;
    bool buyerRefund;
}
mapping(address => User) public users; 
mapping(uint => Transaction) public transactions;
}
