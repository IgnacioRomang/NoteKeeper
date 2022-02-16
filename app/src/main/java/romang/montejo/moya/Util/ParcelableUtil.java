package romang.montejo.moya.Util;


import android.os.Parcel;
import android.os.Parcelable;

//CREDITOS A : https://stackoverflow.com/questions/18000093/how-to-marshall-and-unmarshall-a-parcelable-to-a-byte-array-with-help-of-parcel
// USER : https://stackoverflow.com/users/194894/flow
public class ParcelableUtil {
    public static byte[] marshall(Parcelable parceable) {
        Parcel parcel = Parcel.obtain();
        parceable.writeToParcel(parcel, 0);
        byte[] bytes = parcel.marshall();
        parcel.recycle();
        return bytes;
    }

    public static Parcel unmarshall(byte[] bytes) {
        Parcel parcel = Parcel.obtain();
        parcel.unmarshall(bytes, 0, bytes.length);
        parcel.setDataPosition(0);
        return parcel;
    }

    public static <T> T unmarshall(byte[] bytes, Parcelable.Creator<T> creator) {
        Parcel parcel = unmarshall(bytes);
        T result = creator.createFromParcel(parcel);
        parcel.recycle();
        return result;
    }
}
