package com.adityarana.sangharsh.learning.sangharsh.Model;

public class OrderResponse {
        String id;
        String entity;
        String currency;
        String receipt;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getEntity() {
            return entity;
        }

        public void setEntity(String entity) {
            this.entity = entity;
        }

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }

        public String getReceipt() {
            return receipt;
        }

        public void setReceipt(String receipt) {
            this.receipt = receipt;
        }

        public String getOffer_id() {
            return offer_id;
        }

        public void setOffer_id(String offer_id) {
            this.offer_id = offer_id;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public int getAmount() {
            return amount;
        }

        public void setAmount(int amount) {
            this.amount = amount;
        }

        public int getAmount_paid() {
            return amount_paid;
        }

        public void setAmount_paid(int amount_paid) {
            this.amount_paid = amount_paid;
        }

        public int getAttempts() {
            return attempts;
        }

        public void setAttempts(int attempts) {
            this.attempts = attempts;
        }

        public int getCreated_at() {
            return created_at;
        }

        public void setCreated_at(int created_at) {
            this.created_at = created_at;
        }

        public String[] getNotes() {
            return notes;
        }

        public void setNotes(String[] notes) {
            this.notes = notes;
        }

        public OrderResponse(String id, String entity, String currency, String receipt, String offer_id, String status, int amount, int amount_paid, int attempts, int created_at, String[] notes) {
            this.id = id;
            this.entity = entity;
            this.currency = currency;
            this.receipt = receipt;
            this.offer_id = offer_id;
            this.status = status;
            this.amount = amount;
            this.amount_paid = amount_paid;
            this.attempts = attempts;
            this.created_at = created_at;
            this.notes = notes;
        }

        String offer_id;
        String status;
        int amount, amount_paid, attempts, created_at;
        String[] notes;

}
