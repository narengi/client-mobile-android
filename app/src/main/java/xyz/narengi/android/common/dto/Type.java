package xyz.narengi.android.common.dto;

import java.io.Serializable;
import java.util.Locale;

import xyz.narengi.android.common.model.AroundLocation;
import xyz.narengi.android.service.WebServiceConstants;

/**
 * @author Siavash Mahmoudpour
 */
public class Type implements Serializable {

    private String title;
    private String key;
    private String description;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
}