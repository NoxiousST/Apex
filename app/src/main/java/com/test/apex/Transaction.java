package com.test.apex;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Transaction {

    private String transactionId, invoiceNumber, transactionStatus, transactionAddress, invoiceDate, paymentDate, userId, products;
    private Long transactionAmount;

    Transaction() {

    }

    public String getTransactionId () {return transactionId ;}
    public void setTransactionId (String transactionId ) {this.transactionId  = transactionId ;}

    public String getInvoiceNumber() {return invoiceNumber;}
    public void setInvoiceNumber(String invoiceNumber) {this.invoiceNumber = invoiceNumber;}

    public String getTransactionStatus() {return transactionStatus;}
    public void setTransactionStatus(String transactionStatus) {this.transactionStatus = transactionStatus;}

    public Long getTransactionAmount() {return transactionAmount;}
    public void setTransactionAmount(Long transactionAmount) {this.transactionAmount = transactionAmount;}

    public String gettransactionAddress() {return transactionAddress;}
    public void settransactionAddress(String transactionAddress) {this.transactionAddress = transactionAddress;}

    public String getinvoiceDate() {return invoiceDate;}
    public void setinvoiceDate(String invoiceDate) {this.invoiceDate = invoiceDate;}

    public String getpaymentDate() {return paymentDate;}
    public void setpaymentDate(String paymentDate) {this.paymentDate = paymentDate;}

    public String getuserId() {return userId;}
    public void setuserId(String userId) {this.userId = userId;}

    public String getproducts() {return products;}
    public void setproducts(String products) {this.products = products;}

    public Transaction(String transactionId, String invoiceNumber, String transactionStatus, Long transactionAmount, String transactionAddress, String invoiceDate, String paymentDate, String userId, String products) {
        this.transactionId = transactionId;
        this.invoiceNumber = invoiceNumber;
        this.transactionStatus = transactionStatus;
        this.transactionAmount= transactionAmount;
        this.transactionAddress = transactionAddress;
        this.invoiceDate = invoiceDate;
        this.paymentDate = paymentDate;
        this.userId = userId;
        this.products = products;
    }

    @Exclude
    public Map<String, String> toMap() {
        HashMap<String, String> result = new HashMap<>();
        result.put("transactionId", transactionId);
        result.put("invoiceNumber", invoiceNumber);
        result.put("transactionStatus", transactionStatus);
        result.put("transactionAmount", String.valueOf(transactionAmount));
        result.put("transactionAddress", transactionAddress);
        result.put("invoiceDate", invoiceDate);
        result.put("paymentDate", paymentDate);
        result.put("userId", userId);
        result.put("products", products);
        return result;
    }
}
