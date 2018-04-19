/**
 * Created by EDWIN on 14/4/2018.
 */
package com.edwinbustamante.gruposcochalos.RenderizadorFotos;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

public class ResizableImageView extends ImageView {
    public ResizableImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heighMeasureSpec) {
        Drawable d = getDrawable();
        if (d == null) {
            super.setMeasuredDimension(widthMeasureSpec, heighMeasureSpec);
            return;
        }
        int imageHeight = d.getIntrinsicHeight();
        int imageWidth = d.getIntrinsicWidth();

        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heighMeasureSpec);

        float imageRatio = 0.0F;
        if (imageHeight > 0) {
            imageRatio = widthSize / heightSize;
        }
        float sizeRatio = 0.0F;
        if (heightSize > 0) {
            sizeRatio = widthSize / heightSize;
        }
        int width;
        int height;
        if (imageRatio>=sizeRatio){
            width=widthSize;
            height=width*imageHeight/imageWidth;
        }else{
            height=heightSize;
            width= height*imageWidth/imageHeight;
        }
        setMeasuredDimension(width,height);
    }
}
