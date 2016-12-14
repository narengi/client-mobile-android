package xyz.narengi.android.common.dto;

import java.io.Serializable;

public class UploadImage implements Serializable {

    private ImageUploadResult result;

	public ImageUploadResult getResult() {
		return result;
	}

	public void setResult(ImageUploadResult result) {
		this.result = result;
	}
}