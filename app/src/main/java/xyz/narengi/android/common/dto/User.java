package xyz.narengi.android.common.dto;

import java.util.List;

/**
 * @author Siavash Mahmoudpour
 */
public class User {

    private Long id;
    private String name;
    private String cellNumber;
    private String emailAddress;
    private List<BookRequest> bookRequests;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCellNumber() {
        return cellNumber;
    }

    public void setCellNumber(String cellNumber) {
        this.cellNumber = cellNumber;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public List<BookRequest> getBookRequests() {
        return bookRequests;
    }

    public void setBookRequests(List<BookRequest> bookRequests) {
        this.bookRequests = bookRequests;
    }
}
