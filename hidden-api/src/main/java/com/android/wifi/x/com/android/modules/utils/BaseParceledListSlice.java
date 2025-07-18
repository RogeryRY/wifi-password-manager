package com.android.wifi.x.com.android.modules.utils;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.List;

abstract class BaseParceledListSlice<T> implements Parcelable {

    public BaseParceledListSlice(List<T> list) {
        throw new RuntimeException("Stub!");
    }

    BaseParceledListSlice(Parcel p, ClassLoader loader) {
        throw new RuntimeException("Stub!");
    }

    public List<T> getList() {
        throw new RuntimeException("Stub!");
    }

    public void setInlineCountLimit(int maxCount) {
        throw new RuntimeException("Stub!");
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        throw new RuntimeException("Stub!");
    }

    protected abstract void writeElement(T parcelable, Parcel reply, int callFlags);

    protected abstract void writeParcelableCreator(T parcelable, Parcel dest);

    protected abstract Creator<?> readParcelableCreator(Parcel from, ClassLoader loader);

}