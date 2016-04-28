package com.x91tec.appshelf.v7;

import android.support.annotation.NonNull;

import java.util.List;

/**
 * Created by oeager on 16-4-13.
 */
public interface ViewTypeGenerator<T> {

    int getItemViewType(@NonNull List<T> items, int position);
}
