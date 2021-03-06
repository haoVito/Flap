package me.yifeiyuan.flapdev.components.simpleimage;

import androidx.annotation.NonNull;
import android.view.View;

import me.yifeiyuan.flap.Component;
import me.yifeiyuan.flap.annotations.Proxy;
import me.yifeiyuan.flapdev.R;

/**
 * Created by 程序亦非猿 on 2018/12/4.
 */
//@Proxy(layoutId = R.layout.flap_item_simple_image)
@Proxy(layoutName = "flap_item_simple_image")
public class SimpleImageComponent extends Component<SimpleImageModel> {

    public SimpleImageComponent(final View itemView) {
        super(itemView);
    }

    @Override
    protected void onBind(@NonNull final SimpleImageModel model) {

    }
}
