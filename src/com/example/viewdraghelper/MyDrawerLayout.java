package com.example.viewdraghelper;

import android.content.Context;
import android.graphics.Point;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.widget.ViewDragHelper;
import android.support.v4.widget.ViewDragHelper.Callback;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

public class MyDrawerLayout extends FrameLayout {

	private ViewDragHelper mDragHelper;

	private int menuMarginRight = 100;

	private Point startPoint = new Point();
	private Point endPoint = new Point();

	// 保存菜单打开状态，初始菜单关闭
	private boolean isMenuOpen = false;

	private DragHelperCallback callback;

	public MyDrawerLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public MyDrawerLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public MyDrawerLayout(Context context) {
		super(context);
		init();
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		getChildAt(1).setClickable(true);
		// ((FrameLayout.LayoutParams)getChildAt(1).getLayoutParams()).setMargins(0,
		// 0, dip2px(menuMarginRight), 0);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int measureWidth = MeasureSpec.getSize(widthMeasureSpec);
		int measureHeigth = MeasureSpec.getSize(heightMeasureSpec);
		setMeasuredDimension(measureWidth, measureHeigth);

		for (int i = 0; i < getChildCount(); i++) {
			View childView = getChildAt(i);
			int widthSpec = 0;
			int heightSpec = 0;
			FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) childView
					.getLayoutParams();
			if (params.width > 0) {
				widthSpec = MeasureSpec.makeMeasureSpec(params.width,
						MeasureSpec.EXACTLY);
			} else if (params.width == FrameLayout.LayoutParams.MATCH_PARENT) {
				if (i == 1) {
					measureWidth = measureWidth - dip2px(menuMarginRight);
				}
				widthSpec = MeasureSpec.makeMeasureSpec(measureWidth,
						MeasureSpec.EXACTLY);
			} else if (params.width == FrameLayout.LayoutParams.WRAP_CONTENT) {
				widthSpec = MeasureSpec.makeMeasureSpec(measureWidth,
						MeasureSpec.AT_MOST);
			}

			if (params.height > 0) {
				// 具体值
				heightSpec = MeasureSpec.makeMeasureSpec(params.height,
						MeasureSpec.EXACTLY);
			} else if (params.height == FrameLayout.LayoutParams.MATCH_PARENT) {
				//
				heightSpec = MeasureSpec.makeMeasureSpec(measureHeigth,
						MeasureSpec.EXACTLY);
			} else if (params.height == FrameLayout.LayoutParams.WRAP_CONTENT) {
				heightSpec = MeasureSpec.makeMeasureSpec(measureWidth,
						MeasureSpec.AT_MOST);
			}
			childView.measure(widthSpec, heightSpec);

		}
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		getChildAt(1).layout(
				left - getChildAt(1).getMeasuredWidth(),
				top,
				right - getChildAt(1).getMeasuredWidth()
						- dip2px(menuMarginRight), bottom);

		startPoint.x = getChildAt(1).getLeft();
		startPoint.y = getChildAt(1).getTop();
		endPoint.x = startPoint.x + getChildAt(1).getMeasuredWidth();
		endPoint.y = getChildAt(1).getTop();
	}

	@Override
	public void computeScroll() {
		if (mDragHelper.continueSettling(true)) {
			invalidate();
		}
	}

	private void init() {
		// 其中1.0f是敏感度参数参数越大越敏感
		// 第一个参数必须为ViewGroup，它是ViewDragHelper的拖动处理对象
		callback = new DragHelperCallback();
		mDragHelper = ViewDragHelper.create(this, 1.0f, callback);
		// 允许边缘触控
		mDragHelper.setEdgeTrackingEnabled(ViewDragHelper.EDGE_LEFT);

	}

	private class DragHelperCallback extends Callback {

		@Override
		public boolean tryCaptureView(View arg0, int arg1) {
			// 菜单允许滑动
			return arg0 == getChildAt(1);
		}

		/**
		 * 解决响应子view点击事件，无法滑动问题。 horizontalDragRange 和verticalDragRange 大于0的时候
		 * 对应的move事件才会捕获。否则就是丢弃直接丢给子view自己处理了
		 */
		@Override
		public int getViewHorizontalDragRange(View child) {
			return endPoint.x - startPoint.x;
		}

		/**
		 * 处理横向拖动
		 */
		@Override
		public int clampViewPositionHorizontal(View child, int left, int dx) {
			// 指定menu的横向拖动范围
			if (left < startPoint.x) {
				left = startPoint.x;
			} else if (left > endPoint.x) {
				left = endPoint.x;

			}
			return left;
		}

		/**
		 * 边缘触控
		 */
		@Override
		public void onEdgeDragStarted(int edgeFlags, int pointerId) {
			super.onEdgeDragStarted(edgeFlags, pointerId);
			mDragHelper.captureChildView(getChildAt(1), pointerId);
		}

		/**
		 * 手指释放的时候，调用
		 */
		@Override
		public void onViewReleased(View releasedChild, float xvel, float yvel) {
			// 松手的时候 判断如果是这个view 就让他回到起始位置
			if (releasedChild == getChildAt(1)) {
				// 这段代码最终调用的是startScroll这个方法，所以还要在computeScroll方法里刷新
				if (isMenuOpen) {
					mDragHelper.settleCapturedViewAt(endPoint.x, endPoint.y);
				} else {
					mDragHelper
							.settleCapturedViewAt(startPoint.x, startPoint.y);

				}
				invalidate();
			}
		}

		@Override
		public void onViewPositionChanged(View changedView, int left, int top,
				int dx, int dy) {
			if (left >= -getChildAt(1).getWidth() / 2) {
				isMenuOpen = true;
			} else {
				isMenuOpen = false;
			}
			// super.onViewPositionChanged(changedView, left, top, dx, dy);
			// 子view初始不可见的时候，必须设置以下操作，在滑动的过程中，子view才可见
			changedView.setVisibility(View.VISIBLE);
			invalidate();
			//
		}

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

	/**
	 * dip转px
	 * 
	 * @param dpValue
	 * @return
	 */
	private int dip2px(float dpValue) {
		float scale = getContext().getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * 菜单是否打开
	 * 
	 * @return
	 */
	public boolean isMenuOpen() {
		return isMenuOpen;
	}

	/**
	 * 关闭菜单
	 */
	public void close() {
		if (isMenuOpen) {
			mDragHelper.smoothSlideViewTo(getChildAt(1), startPoint.x,
					startPoint.y);
			invalidate();
		}
	}

	/**
	 * 打开菜单
	 */
	public void open() {
		if (!isMenuOpen) {
			mDragHelper
					.smoothSlideViewTo(getChildAt(1), endPoint.x, endPoint.y);
			invalidate();
		}
	}

}
