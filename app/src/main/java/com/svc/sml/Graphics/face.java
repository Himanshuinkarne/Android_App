package com.svc.sml.Graphics;

/**
 * Created by Sarbartha on 16-11-2015.
 */
public class face {
    private int[] v,t,n;
    private Vec3d center;
    private float dist;

    public float getDist() {
        return dist;
    }

    public void setDist(float x, float y, float z) {
        dist=center.getDistance(x,y,z);
    }

    face()
    {
        v = new int[3];
        t = new int[3];
        n = new int[3];
        center=new Vec3d(0,0,0);
        dist=0;
    }
    public void addFace(int i, int _v,int _t,int _n)
    {
        v[i]=_v;
        t[i]=_t;
        n[i]=_n;
    }
    public void addCenter(float x,float y,float z)
    {
        center.setX(x);
        center.setY(y);
        center.setZ(z);
    }
    public Vec3d getCenter()
    {
        return center;
    }
    public int[] getV() {
        return v;
    }

    public void setV(int[] v) {
        this.v = v;
    }

    public int[] getT() {
        return t;
    }

    public void setT(int[] t) {
        this.t = t;
    }

    public int[] getN() {
        return n;
    }

    public void setN(int[] n) {
        this.n = n;
    }

    public short[] getShortArrayV() {
        short tmp[] = new short[3];
        for(int i=0;i<3;i++)
            tmp[i]=(short)v[i];

        return tmp;
    }
}
