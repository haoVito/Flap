package me.yifeiyuan.flap.internal;

import android.support.annotation.NonNull;
import android.support.annotation.RestrictTo;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import me.yifeiyuan.flap.FlapComponent;

/**
 * DefaultFlapItem is a build-in FlapItem that would be used when something went wrong .
 *
 * So that Flap won't crash your App.
 *
 * Flap Github: <a>https://github.com/AlanCheen/Flap</a>
 *
 * @author 程序亦非猿 [Follow me](<a> https://github.com/AlanCheen</a>)
 * @version 1.0
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
public final class DefaultComponent extends FlapComponent {

    private static final int DEFAULT_ITEM_TYPE = -66666;

    public DefaultComponent(final View itemView) {
        super(itemView);
    }

    @Override
    protected void onBind(@NonNull final Object model) {

    }

    public static class Factory implements ComponentProxy {

        @NonNull
        @Override
        public FlapComponent createComponent(@NonNull final LayoutInflater inflater, @NonNull final ViewGroup parent, final int viewType) {
            View view = new View(parent.getContext());
            RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);
            view.setLayoutParams(layoutParams);
            return new DefaultComponent(view);
        }

        @Override
        public int getItemViewType(final Object model) {
            return DEFAULT_ITEM_TYPE;
        }

        @Override
        public Class getComponentModelClass() {
            return Object.class;
        }
    }
}
