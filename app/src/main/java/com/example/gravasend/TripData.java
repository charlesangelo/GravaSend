public class TripData {
    private String firstMaintenanceItem;
    private String firstMaintenanceDate;
    private String secondMaintenanceItem;
    private String secondMaintenanceDate;

    public TripData() {
        // Default constructor required for calls to DataSnapshot.getValue(TripData.class)
    }

    public String getFirstMaintenanceItem() {
        return firstMaintenanceItem;
    }

    public void setFirstMaintenanceItem(String firstMaintenanceItem) {
        this.firstMaintenanceItem = firstMaintenanceItem;
    }

    public String getFirstMaintenanceDate() {
        return firstMaintenanceDate;
    }

    public void setFirstMaintenanceDate(String firstMaintenanceDate) {
        this.firstMaintenanceDate = firstMaintenanceDate;
    }

    public String getSecondMaintenanceItem() {
        return secondMaintenanceItem;
    }

    public void setSecondMaintenanceItem(String secondMaintenanceItem) {
        this.secondMaintenanceItem = secondMaintenanceItem;
    }

    public String getSecondMaintenanceDate() {
        return secondMaintenanceDate;
    }

    public void setSecondMaintenanceDate(String secondMaintenanceDate) {
        this.secondMaintenanceDate = secondMaintenanceDate;
    }
}
