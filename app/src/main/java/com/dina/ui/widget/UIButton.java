package com.dina.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wticn.wyb.wtiapp.R;

public class UIButton extends LinearLayout {

	private LayoutInflater mInflater;
	private LinearLayout mButtonContainer;
	private ClickListener mClickListener;
	private CharSequence mTitle;
	private CharSequence mSubtitle;
	private int mImage;
	
	public UIButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.setClickable(true);
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mButtonContainer = (LinearLayout) mInflater.inflate(R.layout.list_item_single, null);
		LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);
		
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.UIButton, 0, 0);
		mTitle = a.getString(R.styleable.UIButton_title);
		mSubtitle = a.getString(R.styleable.UIButton_subtitle);
		mImage = a.getResourceId(R.styleable.UIButton_image, -1);
		
		if(mTitle != null) {
			((TextView) mButtonContainer.findViewById(R.id.title)).setText(mTitle.toString());
		} else {
			((TextView) mButtonContainer.findViewById(R.id.title)).setText("subtitle");
		}
		
		if(mSubtitle != null) {
			((TextView) mButtonContainer.findViewById(R.id.subtitle)).setText(mSubtitle.toString());
		} else {
			((TextView) mButtonContainer.findViewById(R.id.subtitle)).setVisibility(View.GONE);
		}
		
		if(mImage > -1) {
			((ImageView) mButtonContainer.findViewById(R.id.image)).setImageResource(mImage);
		}
		
		mButtonContainer.setOnClickListener( new OnClickListener() {

			@Override
			public void onClick(View view) {
				if(mClickListener != null)
					mClickListener.onClick(UIButton.this);
			}
			
		});
		
		addView(mButtonContainer, params);
	}	
	
	public interface ClickListener {		
		void onClick(View view);		
	}
	
	/**
	 * 
	 * @param listener
	 */
	public void addClickListener(ClickListener listener) {
		this.mClickListener = listener;
	}
	
	/**
	 * 
	 */
	public void removeClickListener() {
		this.mClickListener = null;
	}

}
