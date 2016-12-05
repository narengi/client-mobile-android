package xyz.narengi.android.common.dto;

import java.io.Serializable;

public class UploadImage implements Serializable {

    private String url;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}