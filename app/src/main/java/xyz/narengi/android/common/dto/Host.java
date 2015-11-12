package xyz.narengi.android.common.dto;

import java.util.List;

/**
 * @author Siavash Mahmoudpour
 */
public class Host {

    private Long id;
    private User user;
    private List<House> houses;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<House> getHouses() {
        return houses;
    }

    public void setHouses(List<House> houses) {
        this.houses = houses;
    }
}
