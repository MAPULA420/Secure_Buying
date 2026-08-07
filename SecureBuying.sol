// SPDX-License-Identifier: MIT
pragma solidity 0.8.19;

// My fist code for  Secure_Buying —
// it stores the buyer and seller's addresses,
// and how much the buyer is paying.

contract Secure_Buying{
    address buyer;
    address seller;
    uint amount;
    
    function buyerAdress() public view returns(address){
        return buyer;
    }
    function sellerAdress() public view returns(address){
        return seller;
    }
    function amounT() public view returns(uint){
        return amount;
    }
}
