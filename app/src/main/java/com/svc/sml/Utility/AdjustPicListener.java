package com.svc.sml.Utility;

import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * This class defines an OnTouchListener that you can attach to any view so you
 * can support panning and zooming.
 *
 * <p> This code has been adapted from the work described here:
 * "Java Pan/Zoom Listener" (http://code.cheesydesign.com/?p=723)
 *
 */
public class AdjustPicListener implements OnTouchListener,View.OnClickListener {

  private  TextView zoomRotateText = null;
  //added by ss
  private float startangle,angle=0;
  private static ImageButton rotateZoomButton;
  private static ImageButton rotateButton;
  private int zrmode;
  private boolean isOldMatrixSet;
  @Override
  public void onClick(View v) {
    if(v.getTag().equals("zoom")) {
      zrmode = ZOOM;
      //zrmode = ROTATE;
      //rotateZoomButton.setImageResource(R.drawable.selector_adjustpic_zoom);
      rotateZoomButton.setSelected(true);
      rotateButton.setSelected(false);
        zoomRotateText.setText("Zoom Mode");

    } else {
      //zrmode = ZOOM;
      zrmode = ROTATE;
      //rotateZoomButton.setImageResource(R.drawable.selector_adjustpic_rotate);
      rotateButton.setSelected(true);
        rotateZoomButton.setSelected(false);
        zoomRotateText.setText("Rotate Mode");
    }
  }

  public static class Anchor {

    public static final int CENTER = 0;
    public static final int TOPLEFT = 1;
  }
  private static final String TAG = "AdjustPicListener";
  // We can be in one of these 3 states
  static final int NONE = 0;
  static final int DRAG = 1;
  static final int ZOOM = 2;
  static final int ROTATE = 3;

  int mode = NONE;
  // Remember some things for zooming
  PointF start = new PointF();
  PointF mid = new PointF();
  float oldDist = 1f;

  PanZoomCalculator panZoomCalculator;

  public AdjustPicListener(FrameLayout container, View view, int anchor, ImageButton rotateZoomButton, TextView zoomRotateText, Matrix matrix) {
    panZoomCalculator = new PanZoomCalculator(container, view, anchor,matrix);
    AdjustPicListener.rotateZoomButton =rotateZoomButton;
    this.zoomRotateText=zoomRotateText;
    rotateZoomButton.setOnClickListener(this);
    zrmode=ZOOM;
    isOldMatrixSet=false;

  }

  public AdjustPicListener(FrameLayout container, View view, int anchor, ImageButton btnZoom, ImageButton btnRotate,TextView zoomRotateText, Matrix matrix) {
    panZoomCalculator = new PanZoomCalculator(container, view, anchor,matrix);
    rotateZoomButton=btnZoom;
      rotateZoomButton.setTag("zoom");
    rotateButton=btnRotate;
    rotateZoomButton.setOnClickListener(this);
    rotateButton.setOnClickListener(this);
      rotateButton.setTag("rotate");
      this.zoomRotateText=zoomRotateText;
      zrmode=ZOOM;
    isOldMatrixSet=false;

  }
  private float getAngle(MotionEvent event) {
    return (float) Math.toDegrees(Math.atan2( (event.getY(0) - event.getY(1)), (event.getX(0) - event.getX(1))) );
  }
  public boolean onTouch(View view, MotionEvent event) {

    // Handle touch events here...
    switch (event.getAction() & MotionEvent.ACTION_MASK) {
      case MotionEvent.ACTION_DOWN:
        start.set(event.getX(), event.getY());
        Log.d(TAG, "mode=DRAG");
        mode = DRAG;
        break;
      case MotionEvent.ACTION_POINTER_DOWN:

        oldDist = spacing(event);
        Log.d(TAG, "oldDist=" + oldDist);
        if (oldDist > 10f) {
          startangle=getAngle(event);
          midPoint(mid, event);
          mode = ZOOM;
          Log.d(TAG, "mode=ZOOM");
        }
        break;
      case MotionEvent.ACTION_UP:
      case MotionEvent.ACTION_POINTER_UP:
        panZoomCalculator.saveMatrix();
        mode = NONE;
        Log.d(TAG, "mode=NONE");
        break;
      case MotionEvent.ACTION_MOVE:
        if (mode == DRAG) {
          panZoomCalculator.doPan(event.getX() - start.x, event.getY() - start.y);
          start.set(event.getX(), event.getY());
        } else if (mode == ZOOM) {
          float newDist = spacing(event);
          Log.d(TAG, "newDist=" + newDist);
          if (newDist > 10f) {
            float scale = newDist / oldDist;
            oldDist = newDist;
            if(zrmode==ZOOM)
              panZoomCalculator.doZoom(scale, mid);
            else{
              angle=getAngle(event)-startangle;
              panZoomCalculator.doRotate(mid);
            }

            //midPoint(mid, event);
          }
        }
        break;
    }
    return true; // indicate event was handled
  }

//  private float getAngle(MotionEvent event) {
//    return (float) Math.atan2( (event.getY(0) - event.getY(1)), (event.getX(0) - event.getX(1)) );
//  }

  // Determine the space between the first two fingers
  private float spacing(MotionEvent event) {

    float x = event.getX(0) - event.getX(1);
    float y = event.getY(0) - event.getY(1);
    return (float)Math.sqrt(x * x + y * y);
  }

  // Calculate the mid point of the first two fingers
  private void midPoint(PointF point, MotionEvent event) {
    // ...
    float x = event.getX(0) + event.getX(1);
    float y = event.getY(0) + event.getY(1);
    point.set(x / 2, y / 2);
  }
  public class PanZoomCalculator {

    private static final float MINZOOM = 0.5f;
    /// The current pan position
    PointF currentPan;
    /// The current zoom position
    float currentZoom,oldZoom;
    /// The windows dimensions that we are zooming/panning in
    View window;
    View child;
    Matrix matrix,oldMatrix;

    // Pan jitter is a workaround to get the video view to update its layout properly when zoom is changed
    int panJitter = 0;
    int anchor;
    private PointF rotateCenter,zoomCenter;
    private float fitToWindow,xOffset,yOffset;

    public void saveMatrix()
    {
      if(angle!=0 || currentZoom !=1 || currentPan.x!=0 || currentPan.y!=0) {
        oldMatrix.set(matrix);
        ((ImageView) child).setImageMatrix(oldMatrix);
        angle = 0;
        currentZoom = 1;
        currentPan.set(0, 0);
      }
    }
    PanZoomCalculator(View container, View child, int anchor, Matrix matrix) {
      // Initialize class fields
      currentPan = new PointF(0, 0);
      currentZoom = 1f;
      rotateCenter = new PointF();
      rotateCenter.set(0,0);
      zoomCenter = new PointF();
      zoomCenter.set(0,0);
      this.window = container;
      this.child = child;
      this.matrix = new Matrix();
      oldMatrix = matrix;
      this.anchor = anchor;
      onPanZoomChanged();
      /* IS THIS COMPATIBLE WITH 2.3.3?
      this.child.addOnLayoutChangeListener(new OnLayoutChangeListener() {
        // This catches when the image bitmap changes, for some reason it doesn't recurse

        public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
          onPanZoomChanged();
        }
      });
      */
    }
    public void doRotate(PointF rotateCenter) {
      this.rotateCenter=rotateCenter;
      onPanZoomChanged();
    }
    public void doZoom(float scale, PointF zoomCenter) {

      currentZoom *= scale;
      this.zoomCenter=zoomCenter;
      onPanZoomChanged();
    }

    public void doPan(float panX, float panY) {
      currentPan.x += panX;
      currentPan.y += panY;
      onPanZoomChanged();
     // Log.d("SS","pan: "+currentPan.x+"  "+currentPan.y);
    }

    private float getMinimumZoom() {
      return 1f;
    }

    /// Call this to reset the Pan/Zoom state machine
    public void reset() {
      // Reset zoom and pan
      currentZoom = getMinimumZoom();
      currentPan = new PointF(0f, 0f);
      onPanZoomChanged();
    }

    public void onPanZoomChanged() {

      if (child instanceof ImageView && ((ImageView) child).getScaleType()== ImageView.ScaleType.MATRIX) {
        ImageView view = (ImageView) child;
        Drawable drawable = view.getDrawable();
        if (drawable != null) {

            matrix.set(oldMatrix);
            matrix.postScale(currentZoom , currentZoom ,zoomCenter.x,zoomCenter.y);
            matrix.postRotate(angle,rotateCenter.x,rotateCenter.y);
            matrix.postTranslate(currentPan.x, currentPan.y);

            ((ImageView) child).setImageMatrix(matrix);

        }
      } else {
        MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();

        lp.leftMargin = (int) currentPan.x + panJitter;
        lp.topMargin = (int) currentPan.y;
        lp.width = (int) (window.getWidth() * currentZoom);
        lp.height = (int) (window.getHeight() * currentZoom);
        panJitter ^= 1;

        child.setLayoutParams(lp);
      }
    }
  }
}

