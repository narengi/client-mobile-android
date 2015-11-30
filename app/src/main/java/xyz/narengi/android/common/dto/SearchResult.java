package xyz.narengi.android.common.dto;

import java.util.List;

/**
 * @author Siavash Mahmoudpour
 */
public class SearchResult {

    private List<AroundLocation> aroundLocations;

    public List<AroundLocation> getAroundLocations() {
        return aroundLocations;
    }

    public void setAroundLocations(List<AroundLocation> aroundLocations) {
        this.aroundLocations = aroundLocations;
    }
}
