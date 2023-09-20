package com.shepherdmoney.interviewproject.controller;

import com.shepherdmoney.interviewproject.vo.request.AddCreditCardToUserPayload;
import com.shepherdmoney.interviewproject.vo.request.UpdateBalancePayload;
import com.shepherdmoney.interviewproject.vo.response.CreditCardView;
import com.shepherdmoney.interviewproject.repository.CreditCardRepository;
import com.shepherdmoney.interviewproject.repository.UserRepository;
import com.shepherdmoney.interviewproject.model.User;
import com.shepherdmoney.interviewproject.model.BalanceHistory;
import com.shepherdmoney.interviewproject.model.CreditCard;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.time.Instant;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.*;


@RestController
public class CreditCardController {

    // TODO: wire in CreditCard repository here (~1 line)
    private final CreditCardRepository creditCardRepository;
    private final UserRepository userRepository;

    //Construct CreditCardController object using repositories
    public CreditCardController(CreditCardRepository creditCardRepository, UserRepository userRepository) {
        this.creditCardRepository = creditCardRepository;
        this.userRepository = userRepository;
    }

    @PostMapping("/credit-card")
    public ResponseEntity<Integer> addCreditCardToUser(@RequestBody AddCreditCardToUserPayload payload) {
        // TODO: Create a credit card entity, and then associate that credit card with user with given userId
        //       Return 200 OK with the credit card id if the user exists and credit card is successfully associated with the user
        //       Return other appropriate response code for other exception cases
        //       Do not worry about validating the card number, assume card number could be any arbitrary format and length
        Optional<User> realUser = userRepository.findById(payload.getUserId());
        if (realUser.isPresent()) {
            User user = realUser.get();
            CreditCard creditCard = new CreditCard(payload.getIssuanceBank(), payload.getNumber());
            creditCard.setOwner(user);
            creditCardRepository.save(creditCard);
            return ResponseEntity.ok().body(200);
        }
        else {
            return ResponseEntity.badRequest().body(400);
        }
    }

    @GetMapping("/credit-card:all")
    public ResponseEntity<List<CreditCardView>> getAllCardOfUser(@RequestParam int userId) {
        // TODO: return a list of all credit card associated with the given userId, using CreditCardView class
        //       if the user has no credit card, return empty list, never return null
        Optional<User> realUser = userRepository.findById(userId);
        if (realUser.isPresent()) {
            List<CreditCardView> creditCardViews = new ArrayList<>();
            User user = realUser.get();

            for (CreditCard creditCard : user.getCreditCards()) {
                creditCardViews.add(new CreditCardView(creditCard.getIssuanceBank(), creditCard.getNumber()));
            }
            return ResponseEntity.ok().body(creditCardViews);
        }
        else {
            return ResponseEntity.badRequest().body(new ArrayList<>());
        }
    }

    @GetMapping("/credit-card:user-id")
    public ResponseEntity<Integer> getUserIdForCreditCard(@RequestParam String creditCardNumber) {
        // TODO: Given a credit card number, efficiently find whether there is a user associated with the credit card
        //       If so, return the user id in a 200 OK response. If no such user exists, return 400 Bad Request
        Optional<CreditCard> realCreditCard = creditCardRepository.findByNumber(creditCardNumber);
        if (realCreditCard.isPresent()) {
            CreditCard creditCard = realCreditCard.get();
            return ResponseEntity.ok().body(creditCard.getOwner().getId());
        }
        else {
            return ResponseEntity.badRequest().body(null);
        }
    }


@PostMapping("/credit-card:update-balance")
public ResponseEntity<String> updateBalanceHistory(@RequestBody UpdateBalancePayload[] payload) {
    //TODO: Given a list of transactions, update credit cards' balance history.
        //      For example: if today is 4/12, a credit card's balanceHistory is [{date: 4/12, balance: 110}, {date: 4/10, balance: 100}],
        //      Given a transaction of {date: 4/10, amount: 10}, the new balanceHistory is
        //      [{date: 4/12, balance: 120}, {date: 4/11, balance: 110}, {date: 4/10, balance: 110}]
        //      Return 200 OK if update is done and successful, 400 Bad Request if the given card number
        //        is not associated with a card.    
    //loop over all transactions in the array
    for (int i = 0; i < payload.length; i++) {

        //read in the transaction amount and the credit card number for the transaction
        double transactionAmount = payload[i].getTransactionAmount();
        String creditCardNumber = payload[i].getCreditCardNumber();

        //retrieve the correct credit card for the number passed in
        Optional<CreditCard> realCreditCard = creditCardRepository.findByNumberWithBalanceHistory(creditCardNumber);
        
        //check if the card exists
        if (realCreditCard.isPresent()) {
            //unwrap the card from the optional class
            CreditCard creditCard = realCreditCard.get();
            // Get the balance history list or initialize if null
            List<BalanceHistory> balanceHistoryList = creditCard.getBalanceHistory();
            for (int u = 0; u < creditCard.getBalanceHistory().size(); u++) {
                System.out.println("initial iteration " + u + " " + balanceHistoryList.get(u).getDate() + " and " + balanceHistoryList.get(u).getBalance());
            }
            if (balanceHistoryList.isEmpty()) {
                BalanceHistory newEntry = new BalanceHistory(payload[i].getTransactionTime(), transactionAmount);
                balanceHistoryList.add(newEntry);
                creditCard.setBalanceHistory(balanceHistoryList);
                newEntry.setCreditCard(creditCard);
                creditCardRepository.save(creditCard);
                continue;
            }
            
            // transaction time and current balance
            Instant transactionTime = payload[i].getTransactionTime();
            double currentBalance = creditCard.getCurrentBalance();
            Instant entryDate = transactionTime;

            int j;
            for (j = 0; j < balanceHistoryList.size(); j++) {
                BalanceHistory balanceHistory = balanceHistoryList.get(j);
                entryDate = balanceHistory.getDate();

                if (entryDate.equals(transactionTime)) {
                    balanceHistory.setBalance(balanceHistory.getBalance() + transactionAmount);
                    int k = j + 1;
                    for (k = k; k < balanceHistoryList.size(); k++) {
                        balanceHistoryList.get(k).setBalance(balanceHistoryList.get(k).getBalance() + transactionAmount);
                    }
                    break;
                } else if (entryDate.isAfter(transactionTime)) {
                    BalanceHistory newEntry = new BalanceHistory();
                    newEntry.setDate(transactionTime);
                    if (j == 0) {
                        newEntry.setBalance(transactionAmount);
                    }
                    else {
                        newEntry.setBalance(balanceHistoryList.get(j-1).getBalance() + transactionAmount);
                    }
                    newEntry.setCreditCard(creditCard);
                    balanceHistoryList.add(j, newEntry);
                    int k = j + 1;
                    for (k = k; k < balanceHistoryList.size(); k++) {
                        balanceHistoryList.get(k).setBalance(balanceHistoryList.get(k).getBalance() + transactionAmount);
                    }
                    break;
                }
            }

            double key = j != 0 ? balanceHistoryList.get(j-1).getBalance() : transactionAmount;
            
                System.out.println("the value of j is " + j);
                Instant lastDate = j > 0 ? balanceHistoryList.get(j-1).getDate() : transactionTime;
                if (j == 0) {
                    j++;
                }
                //iterate through to find missing days
                Instant dateToFill = lastDate.plusSeconds(86400); 
                while (dateToFill.isBefore(entryDate)) {
                    BalanceHistory newEntry = new BalanceHistory();
                    newEntry.setDate(dateToFill);
                    newEntry.setBalance(key);
                    newEntry.setCreditCard(creditCard);
                    balanceHistoryList.add(j, newEntry);
                    dateToFill = dateToFill.plusSeconds(86400); 
                    j++;
                }
            

            if (j == balanceHistoryList.size()) {
                BalanceHistory newEntry = new BalanceHistory();
                newEntry.setDate(transactionTime);
                System.out.println("inside");
                newEntry.setBalance(key + transactionAmount);
                newEntry.setCreditCard(creditCard);
                balanceHistoryList.add(newEntry);
            }

            creditCard.setCurrentBalance(currentBalance + transactionAmount);

            //save the updated card with the new balance list
            creditCard.setBalanceHistory(balanceHistoryList);
            creditCardRepository.save(creditCard);

            for (int y = 0; y < balanceHistoryList.size(); y++) {
                System.out.println("iteration " + y + " " + balanceHistoryList.get(y).getDate() + " and " + balanceHistoryList.get(y).getBalance());
            }

            for (int y = 0; y < creditCard.getBalanceHistory().size(); y++) {
                System.out.println(" credit iteration " + y + " " + creditCard.getBalanceHistory().get(y).getDate() + " and " + creditCard.getBalanceHistory().get(y).getBalance());
            }

        } else {
            return ResponseEntity.badRequest().body("The given card number is not associated with a card");
        }
    }
    return ResponseEntity.ok().body("Update is done and successful");
}

}
