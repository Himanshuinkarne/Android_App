package com.svc.sml.Helper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpManager {

    public static String getData(String uri) {
        BufferedReader reader = null;
        try{
            URL url = new URL(uri);
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            StringBuilder sb = new StringBuilder();
            reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null){
                sb.append(line + "\n");
            }
            return sb.toString();
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }finally {
            if(reader !=null){
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

//
//    public void readPLYObject(String object, Bitmap tex) throws PLYLoadMismatch {
//        isObjLoaded = false;
//        int faceN =0;
//        int vertN=0;
//        String line=null;
//        //boolean loadTexture=false;
//        maxX=-999999;maxY=-999999;maxZ=-999999;minX=999999;minY=999999;minZ=999999;
//        //boolean isSet=false;
//        //FloatBuffer vertexBuffer=normalBuffer=textureBuffer=null;
//        //IntBuffer indexBuffer=null;
//        /*
//        faceN=0;
//        vertN=0;
//        String line=null;
//        loadTexture=false;
//        maxX=-999999;maxY=-999999;maxZ=-999999;minX=999999;minY=999999;minZ=999999;
//        isSet=false;
//        vertexBuffer=normalBuffer=textureBuffer=null;
//        indexBuffer=null;
//        */
//
//        Vector verts = null,textures = null,normals =null ,facePly = null ;
//
//        AssetManager am = context.getAssets();
//        File file = new File(object);
//        InputStream in = null;
//        int id=0;
//        String data="";
//        int tempI = 0;
//        try {
//            if (file == null || !file.isFile()) {
//                try {
//                    in = am.open(object);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//            else {
//                in = new FileInputStream(file);
//            }
//            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
//
//            //  Log.d("line","I am here");
//
////            verts = new Vector<Vec3d>();
////            textures = new Vector<Vec3d>();
////            normals = new Vector<Vec3d>();
////            facePly = new Vector<int[]>();
//
//            verts = new Vector<Vec3d>();
//            textures = new Vector<Vec3d>();
//            normals = new Vector<Vec3d>();
//            facePly = new Vector<int[]>();
//
//            boolean header=true;
//            int vertLoop=0,faceLoop=0,property=0,px=0,py=0,pz=0,pnx=0,pny=0,pnz=0,ps=0,pt=0;
//            //  Log.d("line",""+lines.length);
//            line = bufferedReader.readLine();
//            while (line != null)
//            {
//                String[] elems = line.split(" ");
//
//                if(header)
//                {
//                    if(elems[0].equalsIgnoreCase("element"))
//                    {
//                        //   Log.d("elems",elems[2]);
//                        if(elems[1].equalsIgnoreCase("vertex"))
//                            vertLoop=Integer.parseInt(elems[2].trim());
//                        else
//                            faceLoop=Integer.parseInt(elems[2].trim());
//                    }
//                    if(elems[0].equalsIgnoreCase("property"))
//                    {
//                        if(elems[2].equalsIgnoreCase("x"))
//                            px=property;
//                        if(elems[2].equalsIgnoreCase("y"))
//                            py=property;
//                        if(elems[2].equalsIgnoreCase("z"))
//                            pz=property;
//
//                        if(elems[2].equalsIgnoreCase("nx"))
//                            pnx=property;
//                        if(elems[2].equalsIgnoreCase("ny"))
//                            pny=property;
//                        if(elems[2].equalsIgnoreCase("nz"))
//                            pnz=property;
//
//                        if(elems[2].equalsIgnoreCase("s"))
//                            ps=property;
//                        if(elems[2].equalsIgnoreCase("t"))
//                            pt=property;
//
//
//                        property++;
//                    }
//                    if(elems[0].equalsIgnoreCase("end_header"))
//                    {
//                        header=false;
//                    }
//                }
//                else
//                {
//                    //   Log.d("vertLoop",""+vertLoop);
//                    // Log.d("verts",""+elems.length);
//                    if(elems.length==8) {
//                        float X = Float.parseFloat(elems[px]);
//                        float Y = Float.parseFloat(elems[py]);
//                        float Z = Float.parseFloat(elems[pz]);
//                        verts.add(new Vec3d(X, Y, Z));   // changing axis only for display
//                        vertN++;
//                        if (Y > maxX)
//                            maxX = X;
//                        if (Y > maxZ)
//                            maxZ = Y;
//                        if (Z > maxY)
//                            maxY = Z;
//
//                        if (Y < minX)
//                            minX = X;
//                        if (Y < minZ)
//                            minZ = Y;
//                        if (Z < minY)
//                            minY = Z;
//
//                        float nx = Float.parseFloat(elems[pnx]);
//                        float ny = Float.parseFloat(elems[pny]);
//                        float nz = Float.parseFloat(elems[pnz]);
//                        normals.add(new Vec3d(nx, ny, nz));//normal changed for display
//                        float s = Float.parseFloat(elems[ps]);
//                        float t = 1.0f - Float.parseFloat(elems[pt]);
//                        textures.add(new Vec3d(s, t, 0));
//                    }
//                    //   Log.d("ply",lines[i]+" "+i);
//                    //   Log.d("plyelems",elems[0]+" "+elems.length);
//                    if(elems.length==4) {
//                        int f[] = new int[3];
//                        f[0] = Integer.parseInt(elems[1]);
//                        f[1] = Integer.parseInt(elems[2]);
//                        f[2] = Integer.parseInt(elems[3]);
//                        facePly.add(f);
//                        faceN++;
//                    }
//
//                }
//                elems = null;
//                line = bufferedReader.readLine();
//            }
//            if(vertN!=vertLoop || faceN!=faceLoop)
//            {
//                throw new PLYLoadMismatch(vertN,vertLoop,faceN,faceLoop);
//
//            }
////              Log.d(TAG,"vert: "+vertN+" face: "+faceN);
////              Log.d(TAG,"verts: "+verts.size());
//            // Log.d("PLY",""+px+"  "+py+"  "+pz+"  "+pnx+"  "+pny+"  "+pnz+"  "+ps+"  "+pt);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        this.faceN = faceN;
//        this.vertN= vertN;
//        loadTexture=false;
//        maxX=-999999;maxY=-999999;maxZ=-999999;minX=999999;minY=999999;minZ=999999;
//        isSet=false;
//
//        this.verts = verts;
//        this.textures = textures;
//        this.normals = normals;
//        this.facePly = facePly;
//
//        float[] vertCoord = new float[vertN*3];
//        float[] texCoord = new float[vertN*2];
//
//        float[] normCoord = new float[vertN*3];
//
//        int[] indexCoord = new int[faceN*3*3];
//        for(int i=0;i<vertN;i++)
//        {
//            vertCoord[(i*3)]=this.verts.get(i).getX();
//            vertCoord[(i*3+1)]=this.verts.get(i).getY();
//            vertCoord[(i*3+2)]=this.verts.get(i).getZ();
//            float val= (float) sqrt(this.normals.get(i).getX()*this.normals.get(i).getX()+this.normals.get(i).getY()*this.normals.get(i).getY()+this.normals.get(i).getZ()*this.normals.get(i).getZ());
//            normCoord[(i*3)]=this.normals.get(i).getX()/val;
//            normCoord[(i*3+1)]=this.normals.get(i).getY()/val;
//            normCoord[(i*3+2)]=this.normals.get(i).getZ()/val;
//            texCoord[(i*2)]=this.textures.get(i).getX();
//            texCoord[(i*2+1)]=this.textures.get(i).getY();
//
//
//        }
//        for(int i=0;i<faceN;i++)
//        {
//            for (int j=0;j<3;j++) {
//                indexCoord[(i * 3 + j)] = this.facePly.get(i)[j];
//            }
//        }
//
//        // Initialize the buffers.
//        vertexBuffer = ByteBuffer.allocateDirect(vertCoord.length * mBytesPerFloat)
//                .order(ByteOrder.nativeOrder()).asFloatBuffer();
//        vertexBuffer.put(vertCoord).position(0);
//
//        normalBuffer = ByteBuffer.allocateDirect(normCoord.length * mBytesPerFloat)
//                .order(ByteOrder.nativeOrder()).asFloatBuffer();
//        normalBuffer.put(normCoord).position(0);
//
//        textureBuffer = ByteBuffer.allocateDirect(texCoord.length * mBytesPerFloat)
//                .order(ByteOrder.nativeOrder()).asFloatBuffer();
//        textureBuffer.put(texCoord).position(0);
//
//        indexBuffer = ByteBuffer.allocateDirect(indexCoord.length * mBytesPerInt)
//                .order(ByteOrder.nativeOrder()).asIntBuffer();
//        indexBuffer.put(indexCoord).position(0);
//
//        // Load the texture
//        if(tex!=null) {
//            this.tex=tex;
//            mShouldLoadTexture=true;
//        }
//        isSet=true;
//        isObjLoaded = true;
//    }
