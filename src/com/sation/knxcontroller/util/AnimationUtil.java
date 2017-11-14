package com.sation.knxcontroller.util;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.sation.knxcontroller.R;

/**
 * Created by wangchunfeng on 2017/3/29.
 */

public class AnimationUtil {
    // action down按下动画
    private static Animation downAnimation;
    // action up动画
    private static Animation upAnimation;
    // 前一个元素
    private static int tempChildViewId = -1;
    // 按下时候的元素，设置为公有方便之后查看
    public static int downChildViewId = -1;
    // 抬起时候的元素，设置为公有方便之后查看
    public static int upChildViewId = -2;

    private static boolean isLastView = true;

    /**
     * 点击每个gridView元素将执行动画
     *
     * @param v
     * @param event
     */
    public static void AnimationAlgorithm(View v, MotionEvent event,
                                          Context context) {
        // 判断是不是GridView的v
//        if (!(v instanceof GridView))
//            return;
//        GridView parent = ((GridView) v);
        LinearLayout parent = ((LinearLayout) v);
        int count = parent.getChildCount();
        // 没有元素不做动画
        if (count == 0)
            return;
        // 获得每个元素的大小。这里每个gridView的元素都是相同大小的，取第一个为例。
        int childWidth = parent.getChildAt(0).getWidth() + 10;
        int childHeight = parent.getChildAt(0).getHeight() + 2;
//        Log.d("count", "==" + count);
        // 进行事件监听
        switch (event.getAction()) {
            // 按下的时候，获得当前元素的id，由于我一行是3个,所以我的y方向上就必须乘以3
            // 例如我按下的是第3个（从第0个开始，第三个为第二行第一列），那么第三个=点击x的大小/子元素的x的大小+(点击y的大小/子元素的y的大小)*3=0+3=3、
            case MotionEvent.ACTION_DOWN: {
                // 重置
                tempChildViewId = -1;
                downChildViewId = -1;
                upChildViewId = -2;
                isLastView = true;
                // 三目运算符
                int currentChildViewId = ((int) event.getX() / childWidth + (int) event
                        .getY() / childHeight * 3) < count ? ((int) event.getX()
                        / childWidth + (int) event.getY() / childHeight * 3) : -1;
                // 开始按没按在存在的元素中的时候这个动画不做
                if (currentChildViewId == -1)
                    return;
                downAnimation = AnimationUtils.loadAnimation(context,
                        R.anim.background_animation_down);
                parent.getChildAt(currentChildViewId).startAnimation(downAnimation);
                tempChildViewId = currentChildViewId;
                downChildViewId = currentChildViewId;
                break;
            }
            // 通过位置判断是哪个item，做对应的动画
            case MotionEvent.ACTION_MOVE: {
                // 计算出当前chidView的位于gridView中的位置
                int currentChildViewId = ((int) event.getX() / childWidth + (int) event
                        .getY() / childHeight * 3) < count ? ((int) event.getX()
                        / childWidth + (int) event.getY() / childHeight * 3) : -1;
                // 当之前一个元素存在，而移动到不存在元素的区域的时候，需要立即将之前图片抬起
                //由于这个方法只让他走一次。设置了一个isLastView的boolean参数
                if (tempChildViewId != -1 && currentChildViewId == -1 && isLastView) {
//                    Log.d("movemove", "movemove");
                    isLastView = false;
                    upAnimation = AnimationUtils.loadAnimation(context,
                            R.anim.background_animation_up);
                    parent.getChildAt(tempChildViewId).startAnimation(upAnimation);
                    return;
                }
                if (currentChildViewId == -1)
                    return;
                // 当前元素与之前元素相同，不执行变化操作。
                if (currentChildViewId != tempChildViewId) {
                    // 表示从不存在的元素移动到存在的元素的时候。只需要做按下操作即可
                    if (tempChildViewId == -1) {
                        downAnimation = AnimationUtils.loadAnimation(context,
                                R.anim.background_animation_down);
                        parent.getChildAt(currentChildViewId).startAnimation(
                                downAnimation);
                    } else {
                        // 表示从存在的元素移动到另外一个存在的元素的时候。只需要做按下操作即可
                        // 原来的动画变成弹起。之后的那个执行新的动画
                        upAnimation = AnimationUtils.loadAnimation(context,
                                R.anim.background_animation_up);
                        parent.getChildAt(tempChildViewId).startAnimation(
                                upAnimation);
                        downAnimation = AnimationUtils.loadAnimation(context,
                                R.anim.background_animation_down);
                        parent.getChildAt(currentChildViewId).startAnimation(
                                downAnimation);
                    }
                    // 改变前一个元素的位置。
                    tempChildViewId = currentChildViewId;
                }
                break;
            }
            // 抬起，松手的时候
            case MotionEvent.ACTION_UP: {
                int currentChildViewId = ((int) event.getX() / childWidth + (int) event
                        .getY() / childHeight * 3) < count ? ((int) event.getX()
                        / childWidth + (int) event.getY() / childHeight * 3) : -1;
//                Log.d("currentChildViewId", currentChildViewId + "");
                // 按下和抬起都在无效位置的话，do nothing
                if (currentChildViewId == -1)
                    return;
                // 其他情况下，需要收起当前的动画
                upAnimation = AnimationUtils.loadAnimation(context,
                        R.anim.background_animation_up);
                parent.getChildAt(currentChildViewId).startAnimation(
                        upAnimation);
                upChildViewId = currentChildViewId;
            }
            default:
                break;
        }
    }
}


