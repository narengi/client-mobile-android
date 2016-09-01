package xyz.narengi.android.common.dto;

import java.io.Serializable;

/**
 * @author Siavash Mahmoudpour
 *
 * This class should be removed or renamed after the new object model for book request is created in back-end
 */
public class BookRequestDTO implements Serializable {

    private String houseTitle;
    private BookRequest[] bookRequests;


    public String getHouseTitle() {
        return houseTitle;
    }

    public void setHouseTitle(String houseTitle) {
        this.houseTitle = houseTitle;
    }

    public BookRequest[] getBookRequests() {
        return bookRequests;
    }

    public void setBookRequests(BookRequest[] bookRequests) {
        this.bookRequests = bookRequests;
    }
}
