package android.content;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Set;

import dev.rikka.tools.refine.RefineAs;

@RefineAs(AttributionSource.class)
public final class AttributionSourceHidden implements Parcelable {
    public AttributionSourceHidden(int uid, @Nullable String packageName, @Nullable String attributionTag, @Nullable Set<String> renouncedPermissions, @Nullable AttributionSource next) {
        throw new RuntimeException("Stub!");
    }

    private AttributionSourceHidden(Parcel in) {
        throw new RuntimeException("Stub!");
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        throw new RuntimeException("Stub!");
    }

    @Override
    public int describeContents() {
        throw new RuntimeException("Stub!");
    }

    public static final Creator<AttributionSourceHidden> CREATOR = new Creator<>() {
        @Override
        public AttributionSourceHidden createFromParcel(Parcel in) {
            throw new RuntimeException("Stub!");
        }

        @Override
        public AttributionSourceHidden[] newArray(int size) {
            throw new RuntimeException("Stub!");
        }
    };
}
