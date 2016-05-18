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
		// ����1.0f�����жȲ�������Խ��Խ����
		// ��һ����������ΪViewGroup������ViewDragHelper���϶��������
		mDragHelper = ViewDragHelper.create(this, 1.0f,
				new DragHelperCallback());
		// �����Ե����
		mDragHelper.setEdgeTrackingEnabled(ViewDragHelper.EDGE_LEFT);
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

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		// ������ɵ�ʱ��ͼ�¼��һ����view�ĳ�ʼλ��
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
			// ����true��������϶���view
			return true;
			// ��һ����view�����϶�
			// return arg0 == getChildAt(0);
		}

		/**
		 * ��������϶�
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
		 * ���������϶�
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
		 * ��Ե����
		 */
		@Override
		public void onEdgeDragStarted(int edgeFlags, int pointerId) {
			mDragHelper.captureChildView(getChildAt(0), pointerId);
		}

		/**
		 * ��ָ�ͷŵ�ʱ�򣬵���
		 */
		@Override
		public void onViewReleased(View releasedChild, float xvel, float yvel) {
			// ���ֵ�ʱ�� �ж���������view �������ص���ʼλ��
			if (releasedChild == getChildAt(0)) {
				// ��δ������յ��õ���startScroll������������Ի�Ҫ��computeScroll������ˢ��
				mDragHelper.settleCapturedViewAt(viewChildPosition.x,
						viewChildPosition.y);
				invalidate();
			}
		}

		/**
		 * �����Ӧ��view����¼����޷��������⡣ horizontalDragRange ��verticalDragRange ����0��ʱ��
		 * ��Ӧ��move�¼��ŻᲶ�񡣷�����Ƕ���ֱ�Ӷ�����view�Լ�������
		 */
		@Override
		public int getViewHorizontalDragRange(View child) {
			return getMeasuredWidth() - child.getMeasuredWidth();
		}

		/**
		 * �����Ӧ��view����¼����޷��������⡣horizontalDragRange ��verticalDragRange ����0��ʱ��
		 * ��Ӧ��move�¼��ŻᲶ�񡣷�����Ƕ���ֱ�Ӷ�����view�Լ�������
		 */
		@Override
		public int getViewVerticalDragRange(View child) {
			return getMeasuredHeight() - child.getMeasuredHeight();
		}

	}

}