package xyz.narengi.android.common.dto;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;
import org.parceler.ParcelPropertyConverter;
import org.parceler.Parcels;
import org.parceler.converter.NullableParcelConverter;

import java.io.Serializable;

/**
 * @author Siavash Mahmoudpour
 */
@Parcel
public class AroundLocation<T> implements Serializable {

    @SerializedName("Type")
    private String Type;
    @SerializedName("Data")
    @ParcelPropertyConverter(ParcelsWrapperConverter.class)
    private T Data;


    public String getType() {
        return Type;
    }

    public void setType(String type) {
        this.Type = type;
    }

    public T getData() {
        return Data;
    }

    public void setData(T data) {
        this.Data = data;
    }
}

class ParcelsWrapperConverter extends NullableParcelConverter<Object> {

    @Override
    public void nullSafeToParcel(Object input, android.os.Parcel parcel) {
        parcel.writeParcelable(Parcels.wrap(input), 0);
    }

    @Override
    public Object nullSafeFromParcel(android.os.Parcel parcel) {
        return Parcels.unwrap(parcel.readParcelable(ParcelsWrapperConverter.class.getClassLoader()));
    }
}
