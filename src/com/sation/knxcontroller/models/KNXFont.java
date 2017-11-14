package com.sation.knxcontroller.models;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.TextPaint;

import com.sation.knxcontroller.util.Log;

import org.w3c.dom.Text;

import java.io.Serializable;

import static android.graphics.Paint.ANTI_ALIAS_FLAG;
import static android.graphics.Paint.DEV_KERN_TEXT_FLAG;
import static android.graphics.Paint.DITHER_FLAG;
import static android.graphics.Paint.FILTER_BITMAP_FLAG;
import static android.graphics.Paint.LINEAR_TEXT_FLAG;
import static android.graphics.Paint.SUBPIXEL_TEXT_FLAG;

/**
 * Created by wangchunfeng on 2017/6/2.
 */

public class KNXFont implements Serializable {
    private static final String TAG = "KNXFont";

    private String Color;
    public int getColor() {
        return android.graphics.Color.parseColor(this.Color);
    }

    private int Size;
    public int getSize(){
        return this.Size;
    }

    private boolean Bold;
    private boolean Italic;
    private boolean Strikeout;
    private boolean Underline;

    public int getTextStyle() {
        /* 字体样式，粗体、斜体 */
        int style = Typeface.NORMAL;
        if(this.Bold) {
            style |= Typeface.BOLD;
        }
        if (this.Italic) {
            style |= Typeface.ITALIC;
        }

        return  style;
    }

//    public Typeface getTypeface() {
//        int style =getTextStyle();
//
//       return  Typeface.create("宋体", style);
//    }

    /**
     *  中心对齐、亚像素显示、线性文本标识
     */
    public TextPaint getTextPaint()
    {
        TextPaint txtPaint = new TextPaint(ANTI_ALIAS_FLAG|DITHER_FLAG|FILTER_BITMAP_FLAG|DEV_KERN_TEXT_FLAG|LINEAR_TEXT_FLAG|SUBPIXEL_TEXT_FLAG);
        txtPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        configTextPaint(txtPaint);

        txtPaint.setTextAlign(Paint.Align.CENTER);

        return txtPaint;
    }

    public void configTextPaint(TextPaint paint) {

        /* 字体样式，粗体、斜体 */
        int style = Typeface.NORMAL;
        if(this.Bold) {
            style |= Typeface.BOLD;
            paint.setFakeBoldText(true);
        }
        if (this.Italic) {
            style |= Typeface.ITALIC;
            paint.setTextSkewX(-0.25f);
        }

        paint.setTypeface(Typeface.create("宋体", style));



        paint.setTextSize(this.Size); // 字体大小
        int color = this.getColor();
        paint.setColor(color); // 字体颜色

        paint.setStrikeThruText(this.Strikeout); // 删除线

        paint.setUnderlineText(this.Underline); // 下划线

        paint.setSubpixelText(true);
        paint.setLinearText(true);
//        paint.setTextAlign(Paint.Align.CENTER);
    }
}
