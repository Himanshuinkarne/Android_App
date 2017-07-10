package com.svc.sml.Graphics;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;

import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

/**
 * Created by Sarbartha on 07/04/16.
 */
public class CameraMotion {
    private static final float INITANGLE = (float)PI;
    private static final int MAXSCRIPTS = 2;

    private int MAXSTATES[];

    private final Context context;
    private float ang[][],zoom[][],X[][],Y[][],Z[][],CY[][];
    private int stateSteps[][];

    public CameraMotion(Context context) {
        this.context=context;
        MAXSTATES= new int[MAXSCRIPTS];
        stateSteps= new int[MAXSCRIPTS][];
        ang= new float[MAXSCRIPTS][];
        zoom= new float[MAXSCRIPTS][];
        CY= new float[MAXSCRIPTS][];
        X= new float[MAXSCRIPTS][];
        Y= new float[MAXSCRIPTS][];
        Z= new float[MAXSCRIPTS][];
    }
    public int getnoOfFrames(int s)
    {
        int frame=0;
        for(int i=0;i<stateSteps[s].length;i++)
        {
            frame+=stateSteps[s][i];
        }
        return frame;
    }
    public boolean readCamScript(int s,int file)
    {
        InputStream in = null;
        in = this.context.getResources().openRawResource(file);
        int id=0;
        String data="";
        int tempI = 0;
        try {
            byte[] buffer= new byte[in.available()];
            while (in.read(buffer) != -1)
            {
                data += new String(buffer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        String lines[] =  data.split("\n");
        MAXSTATES[s] = Integer.parseInt(lines[0].trim());
        stateSteps[s] = new int[MAXSTATES[s]];
        ang[s] = new float[MAXSTATES[s]+1];
        zoom[s] = new float[MAXSTATES[s]+1];
        CY[s] = new float[MAXSTATES[s]+1];
        X[s] = new float[MAXSTATES[s]+1];
        Y[s] = new float[MAXSTATES[s]+1];
        Z[s] = new float[MAXSTATES[s]+1];

        for(int i=1;i<lines.length;i++) {
            String[] elems = lines[i].split(" ");
            if(elems[0].equalsIgnoreCase("s"))
            {
                int v = Integer.parseInt(elems[1].trim());
                int val = Integer.parseInt(elems[2].trim());
                stateSteps[s][v]=val;
            }
            if(elems[0].equalsIgnoreCase("c"))
            {
                int v = Integer.parseInt(elems[1].trim());
                float ang = Float.parseFloat(elems[2].trim());
                float zoom = Float.parseFloat(elems[3].trim());
                float CY = Float.parseFloat(elems[4].trim());
                float X = Float.parseFloat(elems[5].trim());
                float Y = Float.parseFloat(elems[6].trim());
                float Z = Float.parseFloat(elems[7].trim());
                this.ang[s][v]=ang;
                this.zoom[s][v]=zoom;
                this.CY[s][v]=CY;
                this.X[s][v]=X;
                this.Y[s][v]=Y;
                this.Z[s][v]=Z;
            }

        }

        return true;
    }
    public int getMaxStates(int s)
    {
        return MAXSTATES[s];
    }

    public float getCamPos(int s,int state, int t, Vec3d cam, Vec3d eye)
    {
        float zm = zoom[s][state]+((float)t/stateSteps[s][state])*(zoom[s][state+1]-zoom[s][state]);
        float an = ang[s][state]+((float)t/stateSteps[s][state])*(ang[s][state+1]-ang[s][state]);

        eye.setX(X[s][state] + ((float) t / stateSteps[s][state]) * (X[s][state + 1] - X[s][state]));
        eye.setY(Y[s][state] + ((float) t / stateSteps[s][state]) * (Y[s][state + 1] - Y[s][state]));
        eye.setZ(Z[s][state] + ((float) t / stateSteps[s][state]) * (Z[s][state + 1] - Z[s][state]));

        cam.setX((float)((zm)*sin(an+INITANGLE))+eye.getX());
        cam.setY(CY[s][state] + ((float) t / stateSteps[s][state]) * (CY[s][state + 1] - CY[s][state]));
        cam.setZ((float) ((zm) * cos(an + INITANGLE))+eye.getZ());

        return an;
    }
    public int getStateSteps(int s,int state)
    {
        return stateSteps[s][state];
    }
    public int getMaxScript()
    {
        return MAXSCRIPTS;
    }
}
