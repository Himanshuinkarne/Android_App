package com.svc.sml.Graphics;

import static java.lang.Math.sqrt;

/**
 * Created by Sarbartha on 16-11-2015.
 */
public class Vec3d {
    private float x;
    private float y;
    private float z;
    Vec3d()
    {
        x=y=z=0.0f;
    }
    Vec3d(float _x, float _y, float _z)
    {
        x=_x;
        y=_y;
        z=_z;
    }
    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getZ() {
        return z;
    }

    public void setZ(float z) {
        this.z = z;
    }

    public float[] getArray()
    {
        float[] tmp = new float[3];
        tmp[0]=x;
        tmp[1]=y;
        tmp[2]=z;
        return tmp;
    }
    public float getDistance(float _x, float _y, float _z)
    {
        float dist = (float)sqrt(((x-_x)*(x-_x))+((y-_y)*(y-_y))+((z-_z)*(z-_z)));
        return dist;
    }
}
