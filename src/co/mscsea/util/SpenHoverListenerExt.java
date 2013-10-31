package co.mscsea.util;

import android.view.MotionEvent;
import android.view.View;

import com.samsung.android.sdk.pen.engine.SpenHoverListener;

public abstract class SpenHoverListenerExt implements SpenHoverListener {

	private boolean isButtonDown = false;

	public abstract void onHoverButtonDown(View view, MotionEvent event);
	public abstract void onHoverButtonUp(View view, MotionEvent event);
	public abstract boolean onHoverExt(View view, MotionEvent event);
	
	@Override
	public final boolean onHover(View view, MotionEvent event) {
		int action = event.getAction();
		if (action == MotionEvent.ACTION_HOVER_ENTER) {
			isButtonDown = false;
		} else if (action == MotionEvent.ACTION_HOVER_MOVE) {
			if (event.getButtonState() == MotionEvent.BUTTON_SECONDARY) {
				if (!isButtonDown) {
					isButtonDown = true;
					onHoverButtonDown(view, event);
				}
			} else {
				if (isButtonDown) {
					isButtonDown = false;
					onHoverButtonUp(view, event);
				}
			}
		} else if (action == MotionEvent.ACTION_HOVER_EXIT) {
			isButtonDown = false;
		}

		return onHoverExt(view, event);
	}
}
