// SPDX-License-Identifier: MIT
pragma solidity 0.8.19;

contract Secure_Buying{
    uint transactionCount; 

  struct User { //when a user is registering on the app they need to comfirm that they ae using their id
    address userAddress;
    uint userIdNumber;
    bool validId;
    string fullNames;
}

struct Transaction {//when the buyer and seller are actually exchanging parcels
    uint transactionId;
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

mapping(address => User) public users; //this mapping enables me to find a user by their address
mapping(uint => Transaction) public transactions; //this mapping enables me to find a transaction by its id number

function registerUser(address _userAddress,uint _userIdNumber,bool _validId,string memory _fullNames)public{ //when a new user signs up their details get saved here
  users[_userAddress]=User(_userAddress,_userIdNumber,_validId,_fullNames);
}

function getUser(address _userAddress)public view returns(User memory){ //this function enables me to find a user's details using their address
  return users[_userAddress];
}

function createTransaction(address _buyer,address _seller,uint _amount,uint _deliveryFee)public{ //when a buyer and seller start a new deal
  transactions[transactionCount]=Transaction(transactionCount,_buyer,_seller,_amount,_deliveryFee,0,false,0,false,false,false,false);
  transactionCount++; //the next transaction stored gets a new unused number
}

function getTransaction(uint _transactionId)public view returns(Transaction memory){ //this function enables me to find a transaction's details using its id number
  return transactions[_transactionId];
}
}
