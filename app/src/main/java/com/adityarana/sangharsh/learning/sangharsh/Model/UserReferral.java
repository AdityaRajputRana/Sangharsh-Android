package com.adityarana.sangharsh.learning.sangharsh.Model;

import java.util.HashMap;

public class UserReferral {
    String uid;
    String referralId;
    Boolean exists;
    HashMap<String, Object> referred;
    PaymentDetails paymentDetails;

    public PaymentDetails getPaymentDetails() {
        return paymentDetails;
    }

    public void setPaymentDetails(PaymentDetails paymentDetails) {
        this.paymentDetails = paymentDetails;
    }

    public UserReferral(String uid, String referralId, Boolean exists, HashMap<String, Object> referred, PaymentDetails paymentDetails) {
        this.uid = uid;
        this.referralId = referralId;
        this.exists = exists;
        this.referred = referred;
        this.paymentDetails = paymentDetails;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getReferralId() {
        return referralId;
    }

    public void setReferralId(String referralId) {
        this.referralId = referralId;
    }

    public Boolean getExists() {
        return exists;
    }

    public void setExists(Boolean exists) {
        this.exists = exists;
    }

    public HashMap<String, Object> getReferred() {
        return referred;
    }

    public void setReferred(HashMap<String, Object> referred) {
        this.referred = referred;
    }

    public UserReferral() {
    }

    public UserReferral(String uid, String referralId, Boolean exists, HashMap<String, Object> referred) {
        this.uid = uid;
        this.referralId = referralId;
        this.exists = exists;
        this.referred = referred;
    }
}
