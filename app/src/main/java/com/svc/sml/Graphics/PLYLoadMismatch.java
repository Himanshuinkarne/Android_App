package com.svc.sml.Graphics;

import android.util.Log;

/**
 * Created by Sarbartha on 23/06/16.
 */
public class PLYLoadMismatch extends Exception {
    public PLYLoadMismatch(int vertN, int vertLoop, int faceN, int faceLoop) {
        Log.e("PLYLoadException","Verts: "+vertN+" out of "+vertLoop);
        Log.e("PLYLoadException","Verts: "+faceN+" out of "+faceLoop);
    }
}
