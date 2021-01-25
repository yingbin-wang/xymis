package com.cn.wti.entity.view.pulltorefresh;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.widget.ListView;

public class PullableRecyclerView extends RecyclerView implements Pullable
{

	private int lastVisible,firstVisible;

	public PullableRecyclerView(Context context)
	{
		super(context);
	}

	public PullableRecyclerView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	public PullableRecyclerView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	}

	@Override
	public boolean canPullDown()
	{
		if(getLayoutManager() instanceof LinearLayoutManager){
			lastVisible = ((LinearLayoutManager) getLayoutManager()).findLastVisibleItemPosition();
			firstVisible = ((LinearLayoutManager) getLayoutManager()).findFirstVisibleItemPosition();
		}

		if (getAdapter() == null){return  false;}

		if (getAdapter().getItemCount() == 0){
			// 没有item的时候也可以下拉刷新
			return true;
		} else if (firstVisible == 0 && getChildAt(0).getTop() >= 0){
			// 滑到ListView的顶部了
			return true;
		} else
			return false;
	}

	@Override
	public boolean canPullUp()
	{

		if (getAdapter() == null){return  false;}

		if (getAdapter().getItemCount() == 0)
		{
			// 没有item的时候也可以上拉加载
			return true;
		} else if (lastVisible == (getAdapter().getItemCount() - 1))
		{
			// 滑到底部了
			if (getChildAt(lastVisible - firstVisible) != null
					&& getChildAt(lastVisible - firstVisible).getBottom() <= getMeasuredHeight())
				return true;
		}
		return false;
	}
}
