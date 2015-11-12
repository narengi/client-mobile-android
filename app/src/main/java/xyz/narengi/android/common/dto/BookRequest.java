package xyz.narengi.android.common.dto;

import java.util.Date;

/**
 * @author Siavash Mahmoudpour
 */
public class BookRequest {

    private Long id;
    private Host host;
    private Guest guest;
    private Date bookDate;
    private String message;
    private RequestStatus requestStatus;

    public enum RequestStatus {
        PENDING,
        APPROVED,
        REJECTED
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Host getHost() {
        return host;
    }

    public void setHost(Host host) {
        this.host = host;
    }

    public Guest getGuest() {
        return guest;
    }

    public void setGuest(Guest guest) {
        this.guest = guest;
    }

    public Date getBookDate() {
        return bookDate;
    }

    public void setBookDate(Date bookDate) {
        this.bookDate = bookDate;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public RequestStatus getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(RequestStatus requestStatus) {
        this.requestStatus = requestStatus;
    }
}
