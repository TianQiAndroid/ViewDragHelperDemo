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

	// ����˵���״̬����ʼ�˵��ر�
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
				// ����ֵ
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
		// ����1.0f�����жȲ�������Խ��Խ����
		// ��һ����������ΪViewGroup������ViewDragHelper���϶��������
		callback = new DragHelperCallback();
		mDragHelper = ViewDragHelper.create(this, 1.0f, callback);
		// �����Ե����
		mDragHelper.setEdgeTrackingEnabled(ViewDragHelper.EDGE_LEFT);

	}

	private class DragHelperCallback extends Callback {

		@Override
		public boolean tryCaptureView(View arg0, int arg1) {
			// �˵�������
			return arg0 == getChildAt(1);
		}

		/**
		 * �����Ӧ��view����¼����޷��������⡣ horizontalDragRange ��verticalDragRange ����0��ʱ��
		 * ��Ӧ��move�¼��ŻᲶ�񡣷�����Ƕ���ֱ�Ӷ�����view�Լ�������
		 */
		@Override
		public int getViewHorizontalDragRange(View child) {
			return endPoint.x - startPoint.x;
		}

		/**
		 * ��������϶�
		 */
		@Override
		public int clampViewPositionHorizontal(View child, int left, int dx) {
			// ָ��menu�ĺ����϶���Χ
			if (left < startPoint.x) {
				left = startPoint.x;
			} else if (left > endPoint.x) {
				left = endPoint.x;

			}
			return left;
		}

		/**
		 * ��Ե����
		 */
		@Override
		public void onEdgeDragStarted(int edgeFlags, int pointerId) {
			super.onEdgeDragStarted(edgeFlags, pointerId);
			mDragHelper.captureChildView(getChildAt(1), pointerId);
		}

		/**
		 * ��ָ�ͷŵ�ʱ�򣬵���
		 */
		@Override
		public void onViewReleased(View releasedChild, float xvel, float yvel) {
			// ���ֵ�ʱ�� �ж���������view �������ص���ʼλ��
			if (releasedChild == getChildAt(1)) {
				// ��δ������յ��õ���startScroll������������Ի�Ҫ��computeScroll������ˢ��
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
			// ��view��ʼ���ɼ���ʱ�򣬱����������²������ڻ����Ĺ����У���view�ſɼ�
			changedView.setVisibility(View.VISIBLE);
			invalidate();
			//
		}

	}

	/**
	 * Ҫ��ViewDragHelper�ܹ������϶���Ҫ�������¼����ݸ�ViewDragHelper
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
	 * Ҫ��ViewDragHelper�ܹ������϶���Ҫ�������¼����ݸ�ViewDragHelper
	 */
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		mDragHelper.processTouchEvent(ev);
		return true;
	}

	/**
	 * dipתpx
	 * 
	 * @param dpValue
	 * @return
	 */
	private int dip2px(float dpValue) {
		float scale = getContext().getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * �˵��Ƿ��
	 * 
	 * @return
	 */
	public boolean isMenuOpen() {
		return isMenuOpen;
	}

	/**
	 * �رղ˵�
	 */
	public void close() {
		if (isMenuOpen) {
			mDragHelper.smoothSlideViewTo(getChildAt(1), startPoint.x,
					startPoint.y);
			invalidate();
		}
	}

	/**
	 * �򿪲˵�
	 */
	public void open() {
		if (!isMenuOpen) {
			mDragHelper
					.smoothSlideViewTo(getChildAt(1), endPoint.x, endPoint.y);
			invalidate();
		}
	}

}
