package hu.smartcampus.templates;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class MyTextView extends TextView {

	private static int width;
	private static int height;
	
	public MyTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	public MyTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
	}
	
	public MyTextView(Context context) {
		super(context);
	}

	public int getCurrentWidth() {
		return width;
	}

	public int getCurrentHeight() {
		return height;
	}
	
	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		width = right - left;
        height = bottom - top;
	}

}