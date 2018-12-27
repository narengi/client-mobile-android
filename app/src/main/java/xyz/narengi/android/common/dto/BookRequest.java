package xyz.narengi.android.common.dto;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Siavash Mahmoudpour
 */
public class BookRequest implements Serializable {

    private HostProfile Host;
    private String GuestUrl;
    private House House;
    private String RequestDate;
    private int Guests;
    private HouseExtraService[] extraServices;
    private String StartDate;
    private String EndDate;
    private TotalCost TotalCost;
    private BookingConversation[] Conversation;
    private String URL;
    private boolean Paid;
    private String Status;


    public HostProfile getHost() {
        return Host;
    }

    public void setHost(HostProfile host) {
        Host = host;
    }

    public String getGuestUrl() {
        return GuestUrl;
    }

    public void setGuestUrl(String guestUrl) {
        GuestUrl = guestUrl;
    }

    public xyz.narengi.android.common.dto.House getHouse() {
        return House;
    }

    public void setHouse(xyz.narengi.android.common.dto.House house) {
        House = house;
    }

    public String getRequestDate() {
        return RequestDate;
    }

    public void setRequestDate(String requestDate) {
        RequestDate = requestDate;
    }

    public int getGuests() {
        return Guests;
    }

    public void setGuests(int guests) {
        Guests = guests;
    }

    public HouseExtraService[] getExtraServices() {
        return extraServices;
    }

    public void setExtraServices(HouseExtraService[] extraServices) {
        this.extraServices = extraServices;
    }

    public String getStartDate() {
        return StartDate;
    }

    public void setStartDate(String startDate) {
        StartDate = startDate;
    }

    public String getEndDate() {
        return EndDate;
    }

    public void setEndDate(String endDate) {
        EndDate = endDate;
    }

    public BookRequest.TotalCost getTotalCost() {
        return TotalCost;
    }

    public void setTotalCost(BookRequest.TotalCost totalCost) {
        TotalCost = totalCost;
    }

    public BookingConversation[] getConversation() {
        return Conversation;
    }

    public void setConversation(BookingConversation[] conversation) {
        Conversation = conversation;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public boolean isPaid() {
        return Paid;
    }

    public void setPaid(boolean paid) {
        Paid = paid;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public enum BookingStatus {
        PENDING,
        APPROVED,
        REJECTED,
        CANCELED
    }

    public class TotalCost implements Serializable {
        private double cost;
        private String currency;


        public double getCost() {
            return cost;
        }

        public void setCost(double cost) {
            this.cost = cost;
        }

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }
    }
}
