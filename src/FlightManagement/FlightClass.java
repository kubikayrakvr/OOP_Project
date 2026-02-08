package FlightManagement;

public enum FlightClass {

    BUSINESS(0.0),
    ECONOMY(0.0);

    private double pricing;

    FlightClass(double pricing) {
        this.pricing = pricing;
    }

    public double getPricing() {
        return pricing;
    }

    public void setPricing(double pricing) {
        this.pricing = pricing;
    }
}
