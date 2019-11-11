package com.far.virtualmenu.Model;

import java.text.NumberFormat;
import java.util.Locale;

public class PriceModel {
    String description;
    double amount;
    public PriceModel(String description, double amount){
        this.description = description;
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAmount() {
        return NumberFormat.getCurrencyInstance(new Locale("en", "US"))
                .format(amount);
      //  return String.format("%10.2f", amount);
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
