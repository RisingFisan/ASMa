package Classes;

import java.io.Serializable;

public class Purchase implements Serializable {
    public String productName;
    public int quantity;

    public Purchase(String productName, int quantity) {
        this.productName = productName;
        this.quantity = quantity;
    }
}
