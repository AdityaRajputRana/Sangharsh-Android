package com.adityarana.sangharsh.learning.sangharsh.Model;

public class PaymentDetails {
    Boolean isBankPayment;
    String paymentMethod;
    String upiId;
    String accountNumber;
    String holderName;
    String ifscCode;

    public PaymentDetails() {
    }

    public Boolean getBankPayment() {
        return isBankPayment;
    }

    public void setBankPayment(Boolean bankPayment) {
        isBankPayment = bankPayment;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getUpiId() {
        return upiId;
    }

    public void setUpiId(String upiId) {
        this.upiId = upiId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getHolderName() {
        return holderName;
    }

    public void setHolderName(String holderName) {
        this.holderName = holderName;
    }

    public String getIfscCode() {
        return ifscCode;
    }

    public void setIfscCode(String ifscCode) {
        this.ifscCode = ifscCode;
    }

    public PaymentDetails(Boolean isBankPayment, String paymentMethod, String upiId, String accountNumber, String holderName, String ifscCode) {
        this.isBankPayment = isBankPayment;
        this.paymentMethod = paymentMethod;
        this.upiId = upiId;
        this.accountNumber = accountNumber;
        this.holderName = holderName;
        this.ifscCode = ifscCode;
    }
}
