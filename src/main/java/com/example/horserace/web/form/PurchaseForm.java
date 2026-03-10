package com.example.horserace.web.form;

import jakarta.validation.constraints.Min;

public class PurchaseForm {

    @Min(value = 1, message = "Debes comprar minimo un paquete.")
    private int packageCount = 1;

    public int getPackageCount() {
        return packageCount;
    }

    public void setPackageCount(int packageCount) {
        this.packageCount = packageCount;
    }
}
