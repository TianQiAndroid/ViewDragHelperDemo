package com.example.viewdraghelper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Point;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.widget.ViewDragHelper;
import android.support.v4.widget.ViewDragHelper.Callback;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

public class DragLayout extends LinearLayout {
	private final ViewDragHelper mDragHelper;
	
	private Point viewChildPosition = new Point();

	public DragLayout(Context context) {
		this(context, null);
	}

	public DragLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	@SuppressLint("NewApi")
	public DragLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// 其中1.0f是敏感度参数参数越大越敏感
		// 第一个参数必须为ViewGroup，它是ViewDragHelper的拖动处理对象
		mDragHelper = ViewDragHelper.create(this, 1.0f,
				new DragHelperCallback());
		// 允许边缘触控
		mDragHelper.setEdgeTrackingEnabled(ViewDragHelper.EDGE_LEFT);
	}

	/**
	 * 要让ViewDragHelper能够处理拖动需要将触摸事件传递给ViewDragHelper
	 */
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		final int action = MotionEventCompat.getActionMasked(ev);
		if (action == MotionEvent.ACTION_CANCEL
				|| action == MotionEvent.ACTION_UP) {
			mDragHelper.cancel();
			return false;
		}
		return mDragHelper.shouldInterceptTouchEvent(ev);
	}

	/**
	 * 要让ViewDragHelper能够处理拖动需要将触摸事件传递给ViewDragHelper
	 */
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		mDragHelper.processTouchEvent(ev);
		return true;
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		// 布局完成的时候就记录第一个子view的初始位置
		viewChildPosition.x = getChildAt(0).getLeft();
		viewChildPosition.y = getChildAt(0).getTop();
	}

	@Override
	public void computeScroll() {
		if (mDragHelper.continueSettling(true)) {
			invalidate();
		}
	}

	private class DragHelperCallback extends Callback {

		@Override
		public boolean tryCaptureView(View arg0, int arg1) {
			// 返回true代表可以拖动子view
			return true;
			// 第一个子view可以拖动
			// return arg0 == getChildAt(0);
		}

		/**
		 * 处理横向拖动
		 */
		@Override
		public int clampViewPositionHorizontal(View child, int left, int dx) {
			if (left < getPaddingLeft()) {
				left = getPaddingLeft();
			} else if (left > getWidth() - child.getWidth() - getPaddingRight()) {
				left = getWidth() - child.getWidth() - getPaddingRight();
			}
			return left;
		}

		/**
		 * 处理纵向拖动
		 */
		@Override
		public int clampViewPositionVertical(View child, int top, int dy) {
			if (top < getPaddingTop()) {
				top = getPaddingTop();
			} else if (top > getHeight() - child.getHeight()
					- getPaddingBottom()) {
				top = getHeight() - child.getHeight() - getPaddingBottom();
			}
			return top;
		}

		/**
		 * 边缘触控
		 */
		@Override
		public void onEdgeDragStarted(int edgeFlags, int pointerId) {
			mDragHelper.captureChildView(getChildAt(0), pointerId);
		}

		/**
		 * 手指释放的时候，调用
		 */
		@Override
		public void onViewReleased(View releasedChild, float xvel, float yvel) {
			// 松手的时候 判断如果是这个view 就让他回到起始位置
			if (releasedChild == getChildAt(0)) {
				// 这段代码最终调用的是startScroll这个方法，所以还要在computeScroll方法里刷新
				mDragHelper.settleCapturedViewAt(viewChildPosition.x,
						viewChildPosition.y);
				invalidate();
			}
		}

		/**
		 * 解决响应子view点击事件，无法滑动问题。 horizontalDragRange 和verticalDragRange 大于0的时候
		 * 对应的move事件才会捕获。否则就是丢弃直接丢给子view自己处理了
		 */
		@Override
		public int getViewHorizontalDragRange(View child) {
			return getMeasuredWidth() - child.getMeasuredWidth();
		}

		/**
		 * 解决响应子view点击事件，无法滑动问题。horizontalDragRange 和verticalDragRange 大于0的时候
		 * 对应的move事件才会捕获。否则就是丢弃直接丢给子view自己处理了
		 */
		@Override
		public int getViewVerticalDragRange(View child) {
			return getMeasuredHeight() - child.getMeasuredHeight();
		}

	}

}