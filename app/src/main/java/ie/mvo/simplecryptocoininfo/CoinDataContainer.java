package ie.mvo.simplecryptocoininfo;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;

public class CoinDataContainer extends HashMap<String, String> implements Parcelable {

    // Constructor
    public CoinDataContainer() {
        super();
    }

    // Parcelable implementation
    protected CoinDataContainer(Parcel in) {
        int size = in.readInt();
        for (int i = 0; i < size; i++) {
            String key = in.readString();
            String value = in.readString();
            put(key, value);
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(size());
        for (String key : keySet()) {
            dest.writeString(key);
            dest.writeString(get(key));
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CoinDataContainer> CREATOR = new Creator<CoinDataContainer>() {
        @Override
        public CoinDataContainer createFromParcel(Parcel in) {
            return new CoinDataContainer(in);
        }

        @Override
        public CoinDataContainer[] newArray(int size) {
            return new CoinDataContainer[size];
        }
    };
}
