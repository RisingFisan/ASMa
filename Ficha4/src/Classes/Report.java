package Classes;

import java.io.Serializable;

public class Report implements Serializable {
    public int totalProfit;
    public int averageProfit;
    public String mostSoldProduct;
    public int mostSoldQt;

    public Report(int totalProfit, int averageProfit, String mostSoldProduct, int mostSoldQt) {
        this.totalProfit = totalProfit;
        this.averageProfit = averageProfit;
        this.mostSoldProduct = mostSoldProduct;
        this.mostSoldQt = mostSoldQt;
    }

    @Override
    public String toString() {
        return "Report{" +
                "totalProfit=" + totalProfit +
                ", averageProfit=" + averageProfit +
                ", mostSoldProduct='" + mostSoldProduct + '\'' +
                ", mostSoldQt=" + mostSoldQt +
                '}';
    }
}
