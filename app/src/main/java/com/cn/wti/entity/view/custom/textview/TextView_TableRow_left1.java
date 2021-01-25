package com.cn.wti.entity.view.custom.textview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * lyf on 2016/06/27
 * 自定义TextView
 */
public class TextView_TableRow_left1 extends TextView {

	Paint paint = new Paint();
	private boolean top = true,buttom = true,left = true,right = true;

	public TextView_TableRow_left1(Context context) {
		super(context);
	}
	public TextView_TableRow_left1(Context context, AttributeSet attrs, int defStyle){
		super(context, attrs,defStyle);
	}

	public TextView_TableRow_left1(Context context, AttributeSet attrs) {
		super(context, attrs);
		int color = Color.parseColor("#aaaaaa");
		// 为边框设置颜色
		paint.setColor(color);
	}

	public TextView_TableRow_left1(Context context, boolean top, boolean buttom, boolean left, boolean right){
		super(context);
		this.top = top;
		this.buttom = buttom;
		this.left = left;
		this.right = right;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		// 画TextView的4个边
		if(top){
			canvas.drawLine(0, 0, this.getWidth() - 1, 0, paint);
		}

		if (buttom){
			canvas.drawLine(this.getWidth() - 1, 0, this.getWidth() - 1, this.getHeight() - 1, paint);
		}

		if (left){
			canvas.drawLine(0, 0, 0, this.getHeight() - 1, paint);
		}

		if (right){
			canvas.drawLine(0, this.getHeight() - 1, this.getWidth() - 1, this.getHeight() - 1, paint);
		}

	}
}