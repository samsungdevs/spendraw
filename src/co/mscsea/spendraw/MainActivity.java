package co.mscsea.spendraw;

import java.io.IOException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import co.mscsea.spendraw.R;
import co.mscsea.util.PenUtils;
import co.mscsea.util.SpenHoverListenerExt;

import com.samsung.android.sdk.SsdkUnsupportedException;
import com.samsung.android.sdk.pen.Spen;
import com.samsung.android.sdk.pen.SpenSettingEraserInfo;
import com.samsung.android.sdk.pen.SpenSettingPenInfo;
import com.samsung.android.sdk.pen.document.SpenNoteDoc;
import com.samsung.android.sdk.pen.document.SpenPageDoc;
import com.samsung.android.sdk.pen.document.SpenPageDoc.HistoryListener;
import com.samsung.android.sdk.pen.document.SpenPageDoc.HistoryUpdateInfo;
import com.samsung.android.sdk.pen.engine.SpenColorPickerListener;
import com.samsung.android.sdk.pen.engine.SpenPenDetachmentListener;
import com.samsung.android.sdk.pen.engine.SpenSurfaceView;
import com.samsung.android.sdk.pen.settingui.SpenSettingEraserLayout;
import com.samsung.android.sdk.pen.settingui.SpenSettingPenLayout;

public class MainActivity extends Activity implements OnClickListener {

	private FrameLayout spenContainer;
	private RelativeLayout spenLayout;
	private ImageButton penButton;
	private ImageButton eraserButton;
	private ImageButton undoButton;
	private ImageButton redoButton;
	
	private boolean isSpenFeatureEnabled;
	private SpenSurfaceView spenSurfaceView;
	private SpenNoteDoc spenNoteDoc;
	private SpenPageDoc spenPageDoc;
	private SpenSettingPenLayout penSettingLayout;
	private SpenSettingEraserLayout eraserSettingLayout;
	
	private int toolType = SpenSurfaceView.TOOL_SPEN;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		spenContainer = (FrameLayout) findViewById(R.id.spenContainer);
		spenLayout = (RelativeLayout) findViewById(R.id.spenlayout);
		
		penButton = (ImageButton) findViewById(R.id.pen);
		penButton.setOnClickListener(this);
		
		eraserButton = (ImageButton) findViewById(R.id.eraser);
		eraserButton.setOnClickListener(this);
		
		undoButton = (ImageButton) findViewById(R.id.undo);
		undoButton.setOnClickListener(this);
		
		redoButton = (ImageButton) findViewById(R.id.redo);
		redoButton.setOnClickListener(this);

		// TODO: Initialize SPen
		//<task>
		Spen spen = new Spen();
		try {
			spen.initialize(this);
			isSpenFeatureEnabled = spen.isFeatureEnabled(Spen.DEVICE_PEN);
		} catch (SsdkUnsupportedException e) {
			if (PenUtils.processUnsupportedException(this, e)) {
				return;
			}
		} catch (Exception e1) {
			Toast.makeText(this, "Unable to initialize Spen", Toast.LENGTH_LONG).show();
			e1.printStackTrace();
			finish();
			return;
		}
		//</task>

		// TODO: Create surface view
		//<task>
		spenSurfaceView = new SpenSurfaceView(this);
		//</task>
		if (spenSurfaceView == null) {
			Toast.makeText(this, "Unable to create SpenSurfaceView", Toast.LENGTH_LONG).show();
			finish();
			return;
		}
		/*
		 * TODO: handle if device doesn't support s pen
		 */
		//<task>
		if (isSpenFeatureEnabled == false) {
			toolType = SpenSurfaceView.TOOL_FINGER;
			spenSurfaceView.setToolTypeAction(toolType, SpenSurfaceView.ACTION_STROKE);
			Toast.makeText(this, "SPen is not available, you can draw using your finger", Toast.LENGTH_LONG).show();
		}
		//</task>

        // Make the spen draw area as big as the layout
		spenLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			
			@SuppressWarnings("deprecation")
			@SuppressLint("NewApi")
			@Override
			public void onGlobalLayout() {
				addSPenDoc();
				if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
					spenLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
				} else {
					spenLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
				}
			}
		});
		// TODO: add surfaceview on the layout
		//<task>
		spenLayout.addView(spenSurfaceView);
		//</task>
		
		createPenSettings();
        
        createEraserSettings();
		
		handleHoverEvent();
		
		handlePenDetachment();
	}
	
	private void addSPenDoc() {
		/*
		 * TODO: create note document
		 */
		//<task>
		try {
			spenNoteDoc = new SpenNoteDoc(this, spenLayout.getWidth(), spenLayout.getHeight());
		} catch (IOException e) {
			e.printStackTrace();
			finish();
			return;
		} catch (Exception e) {
			e.printStackTrace();
			finish();
			return;
		}
		//</task>
		
		/*
		 * TODO: create page
		 */
		//<task>
		spenPageDoc = spenNoteDoc.appendPage();
		spenPageDoc.setBackgroundColor(0xffd6e6f5);
		//</task>
		
		/*
		 * TODO: set the page on the surface view
		 */
		//<task>
		spenSurfaceView.setPageDoc(spenPageDoc, true);
		//</task>
		
		handleHistory();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		if (spenSurfaceView != null) {
			spenSurfaceView.close();
			spenSurfaceView = null;
		}
		
		if (spenNoteDoc != null) {
			try {
				spenNoteDoc.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			spenNoteDoc = null;
		}
	}
	
	private void handleHistory() {
		/*
		 * TODO: handle undo and redo
		 */
		//<task>
		spenPageDoc.setHistoryListener(new HistoryListener() {
			
			@Override
			public void onUndoable(SpenPageDoc page, boolean undoable) {
				undoButton.setEnabled(undoable);
			}
			
			@Override
			public void onRedoable(SpenPageDoc page, boolean redoable) {
				redoButton.setEnabled(redoable);
			}
			
			@Override
			public void onCommit(SpenPageDoc page) {
				
			}
		});
		spenPageDoc.clearHistory();
		undoButton.setEnabled(spenPageDoc.isUndoable());
		redoButton.setEnabled(spenPageDoc.isRedoable());
		//</task>
	}
	
	private void createPenSettings() {
		// TODO: Create pen setting layout
		//<task>
		penSettingLayout = new SpenSettingPenLayout(this, "", spenLayout);
		//</task>
		if (penSettingLayout == null) {
			Toast.makeText(this, "Can't create new PenSettingLayout.", Toast.LENGTH_LONG).show();
			return;
		}
		
		// TODO: Set pen settings layout's canvas view and add it to the container
		//<task>
		penSettingLayout.setCanvasView(spenSurfaceView);
        spenContainer.addView(penSettingLayout);
        //</task>
        
        // TODO: Handle color changed event
 		//<task>
 		spenSurfaceView.setColorPickerListener(new SpenColorPickerListener() {
 			
 			@Override
 			public void onChanged(int color, int x, int y) {
 				if (penSettingLayout != null) {
 					SpenSettingPenInfo info = penSettingLayout.getInfo();
 					info.color = color;
 				}
 			}
 		});
 		//</task>
 		
 		initPenSettingInfo();
	}
	
	private void createEraserSettings() {
		// TODO: Create eraser setting layout
        //<task>
 		eraserSettingLayout = new SpenSettingEraserLayout(this, "", spenLayout);
 		//</task>
 		if (eraserSettingLayout == null) {
 			Toast.makeText(this, "Can't create new EraserSettingLayout.", Toast.LENGTH_LONG).show();
 			finish();
 			return;
 		}
 		// TODO: Set eraser settings layout's canvas view and add it to the container
 		//<task>
 		eraserSettingLayout.setCanvasView(spenSurfaceView);
 		spenContainer.addView(eraserSettingLayout);
 		//</task>
 		
 		// TODO: Handle eraser settings clear all button
 		//<task>
 		eraserSettingLayout.setEraserListener(new SpenSettingEraserLayout.EventListener() {
 			
 			@Override
 			public void onClearAll() {
 				spenPageDoc.removeAllObject();
 				spenSurfaceView.update();
 			}
 		});
 		//</task>
 		
 		initEraserSettingInfo();
	}
	
	private void handleHoverEvent() {
		// TODO: Handle the hover and side button event
 		//<task>
		spenSurfaceView.setHoverListener(new SpenHoverListenerExt() {
			@Override
			public boolean onHoverExt(View view, MotionEvent event) {
				return false;
			}
			
			@Override
			public void onHoverButtonDown(View view, MotionEvent event) {
				
			}
			
			@Override
			public void onHoverButtonUp(View view, MotionEvent event) {
				togglePenSettings();
			}
		});
		//</task>
	}
	
	private void handlePenDetachment() {
		// TODO: Handle pen detachment event
		//<task>
		spenSurfaceView.setPenDetachmentListener(new SpenPenDetachmentListener() {
			
			@Override
			public void onDetached(boolean attached) {
				String msg = attached ? "S Pen has been attached" : "S Pen has been detached";
				Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show();
			}
		});
		//</task>
	}
	
	private void showPenSettings() {
		/*
		 * TODO:
		 * 1. Set view mode
		 * 2. Display the layout
		 * 3. Set tool type action
		 */
		//<task>
		if (penSettingLayout != null) {
			penSettingLayout.setViewMode(SpenSettingPenLayout.VIEW_MODE_EXTENSION);
	    	penSettingLayout.setVisibility(View.VISIBLE);
	    	
	    	spenSurfaceView.setToolTypeAction(toolType, SpenSurfaceView.ACTION_STROKE);
		}
    	//</task>
	}
	
	private void hidePenSettings() {
		/*
		 * TODO: hide the pen settings layout
		 */
		//<task>
		if (penSettingLayout != null) {
			penSettingLayout.setVisibility(View.GONE);
		}
		//</task>
	}

	private void togglePenSettings() {
		if (penSettingLayout == null) return;
		
		if (penSettingLayout.isShown()) {
			hidePenSettings();
        } else {
        	hideEraserSettings();
        	showPenSettings();
        }
	}
	
	private void showEraserSettings() {
		/*
		 * TODO:
		 * 1. Set view mode
		 * 2. Display the layout
		 * 3. Set tool type action
		 */
		//<task>
		if (eraserSettingLayout != null) {
			eraserSettingLayout.setViewMode(SpenSettingEraserLayout.VIEW_MODE_NORMAL);
			eraserSettingLayout.setVisibility(View.VISIBLE);
			
			spenSurfaceView.setToolTypeAction(toolType, SpenSurfaceView.ACTION_ERASER);
		}
		//</task>
	}
	
	private void hideEraserSettings() {
		/*
		 * TODO: hide the eraser settings layout
		 */
		//<task>
		if (eraserSettingLayout != null) {
			eraserSettingLayout.setVisibility(View.GONE);
		}
		//</task>
	}
	
	private void toggleEraserSettings() {
		if (eraserSettingLayout == null) return;
		
		if (eraserSettingLayout.isShown()) {
			hideEraserSettings();
		} else {
			hidePenSettings();
			showEraserSettings();
		}
	}
	
	private void undo() {
		if (spenPageDoc == null) return;
		
		/* 
		 * TODO:
		 * 1. Check if there's something that can be undo'ed
		 * 2. Undo the action
		 * 3. Update undo on the surface view
		 */
		//<task>
		if (spenPageDoc.isUndoable()) {
			HistoryUpdateInfo[] userData = spenPageDoc.undo();
			spenSurfaceView.updateUndo(userData);
		}
		//</task>
	}
	
	private void redo() {
		if (spenPageDoc == null) return;
		
		/*
		 * TODO:
		 * 1. Check if there's something that can be redo'ed
		 * 2. Redo the action
		 * 3. Update redo on the surface view
		 */
		//<task>
		if (spenPageDoc.isRedoable()) {
			HistoryUpdateInfo[] userData = spenPageDoc.redo();
			spenSurfaceView.updateRedo(userData);
		}
		//</task>
	}
	
	private void initPenSettingInfo() {
        SpenSettingPenInfo info = new SpenSettingPenInfo();
        info.color = Color.BLUE;
        info.size = 10;
        spenSurfaceView.setPenSettingInfo(info);
        penSettingLayout.setInfo(info);
    }
	
	private void initEraserSettingInfo() {
		SpenSettingEraserInfo info = new SpenSettingEraserInfo();
		info.size = 50;
		spenSurfaceView.setEraserSettingInfo(info);
		eraserSettingLayout.setInfo(info);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.pen: {
				togglePenSettings();
				break;
			}
			case R.id.eraser: {
				toggleEraserSettings();
				break;
			}
			case R.id.undo: {
				undo();
				break;
			}
			case R.id.redo: {
				redo();
				break;
			}
		}
	}
}
