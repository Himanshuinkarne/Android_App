package com.svc.sml.Helper;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.svc.sml.Database.ComboData;
import com.svc.sml.Database.ComboDataReconcile;
import com.svc.sml.Database.ComboDataWrapper;
import com.svc.sml.Database.InkarneDataSource;
import com.svc.sml.Database.User;
import com.svc.sml.InkarneAppContext;
import com.svc.sml.Model.AppUpdateItem;
import com.svc.sml.Model.BaseAccessoryItem;
import com.svc.sml.Model.BaseItem;
import com.svc.sml.Model.FaceItem;
import com.svc.sml.Model.HairContent;
import com.svc.sml.Model.LookBoardItem;
import com.svc.sml.Model.SpecsContent;
import com.svc.sml.Utility.ConstantsFunctional;
import com.svc.sml.Utility.ConstantsUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by himanshu on 1/14/16.
 */
public class DataManager {
    public static final String LOGTAG = "DataManager";
    public static final String message_Json_Parse_error = "JSon parse error occurred";
    public static final String ERROR_CODE_KEY = "Error_Code";
    public static final String message_server_error = "error occurred";
    public static final int CODE_DATA_MANAGER_NETWORK_ERROR = 0;
    public static final int CODE_DATA_MANAGER_PARSE_ERROR = 1;
    public static final int CODE_DATA_MANAGER_UNKNOWN_ERROR = 2;

    public HashMap<String,Long> hashMapRefreshTime =  new HashMap<>();

    public interface OnResponseHandlerInterface {
        // TODO: Update argument type and name
        void onResponse(Object obj);

        void onResponseError(String errorMessage, int errorCode);
    }

    private static DataManager sharedInstance;

    public InkarneDataSource getDataSource() {
        //dataSource.open();
        return dataSource;
    }

    public void setDataSource(InkarneDataSource dataSource) {
        this.dataSource = dataSource;
    }

    private InkarneDataSource dataSource;

    public static DataManager getInstance() {
        if (sharedInstance == null) {
            sharedInstance = new DataManager();
        }
        return sharedInstance;
    }

    public DataManager() {
        dataSource = InkarneDataSource.getInstance(InkarneAppContext.getAppContext());
        dataSource.open();
    }

    private void printError(Object obj, String url) {
        //Toast.makeText(InkarneAppContext.getAppContext(),"Please check your network connection",Toast.LENGTH_SHORT).show();
        if (obj == null)
            Log.e(LOGTAG, "ERROR OCCURRED (E NULL) URL: " + url);
        if (obj instanceof JSONException) {
            JSONException e = (JSONException) obj;
            e.printStackTrace();
            if (e.getMessage() != null)
                Log.e(LOGTAG, "Json Error :" + e.getMessage());
            else
                Log.e(LOGTAG, message_Json_Parse_error);
            e.printStackTrace();
        } else if (obj instanceof VolleyError) {
            VolleyError error = (VolleyError) obj;
            if (error != null) {
                error.printStackTrace();
                if (error.getMessage() != null) {
                    Log.e(LOGTAG, error.getMessage() + " URI :" + url);
                }
            } else {
                Log.e(LOGTAG, "Volley Error occurred in requestComboDataByCount URI: " + url);
            }
        } else {
            Error error = (Error) obj;
            if (error != null) {
                if (error.getMessage() != null) {
                    Log.e(LOGTAG, "Generic Error " + error.getMessage() + " URI :" + url);
                } else
                    Log.e(LOGTAG, "Generic Error1 URI :" + url);
            } else {
                Log.e(LOGTAG, "Generic Error2 URI :" + url);
            }
        }
    }
    public void requestCodeVersion(final String uri, final OnResponseHandlerInterface responseListener) {
        Log.d(LOGTAG, "uri :" + uri);
        //get from database
        final JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, uri, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(LOGTAG, "Response: " + response.toString());

                JSONObject jsonObj = null;
                try {
                    jsonObj = response.getJSONObject("CheckForceVersionUpdateResult");
                    int error_code = (int) jsonObj.get("Error_Code");
                    if(error_code ==0) {
                        AppUpdateItem a = new AppUpdateItem();
                        a.setAppVersionCode(jsonObj.getInt("App_Version"));
                        a.setDownloadLink(jsonObj.getString("Download_Link"));
                        a.setIsForceUpdate(jsonObj.getBoolean("Force_Update_Flag"));
                        responseListener.onResponse(a);
                    }
                    else{
                        responseListener.onResponseError(message_Json_Parse_error, CODE_DATA_MANAGER_PARSE_ERROR);
                    }
                } catch (JSONException e) {
                    responseListener.onResponseError(message_Json_Parse_error, CODE_DATA_MANAGER_PARSE_ERROR);
                    printError(e, uri);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                responseListener.onResponseError(message_server_error, 0);
                printError(error, uri);
            }
        });
        VolleyHelper.getInstance(InkarneAppContext.getAppContext()).addToRequestQueue(jsObjRequest);
    }


    public void requestCreateUser(final String uri, final OnResponseHandlerInterface responseListener) {
        Log.d(LOGTAG, "uri :" + uri);
        //get from database
        final JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, uri, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(LOGTAG, "Response: " + response.toString());

                JSONObject jsonObj = null;
                try {
                    if(response.has("CreateUserv4Result"))
                    jsonObj = response.getJSONObject("CreateUserv4Result");
                    if(response.has("UpgradeUserv4Result"))
                        jsonObj = response.getJSONObject("UpgradeUserv4Result");
                    int error_code = (int) jsonObj.get("Error_Code");
                    if(error_code ==0)
                    responseListener.onResponse(jsonObj.get("User_ID"));
                    else{
                        responseListener.onResponseError(message_Json_Parse_error, CODE_DATA_MANAGER_PARSE_ERROR);
                    }
                } catch (JSONException e) {
                    responseListener.onResponseError(message_Json_Parse_error, CODE_DATA_MANAGER_PARSE_ERROR);
                    printError(e, uri);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                responseListener.onResponseError(message_server_error, 0);
                printError(error, uri);
            }
        });
        VolleyHelper.getInstance(InkarneAppContext.getAppContext()).addToRequestQueue(jsObjRequest);
    }

    public void requestReconcileComboData(final String uri,final OnResponseHandlerInterface responseListener) {
        //String uri = ConstantsUtil.URL_BASEPATH + ConstantsUtil.URL_METHOD_RECONCILE + User.getInstance().getmUserId() + "/" + User.getInstance().getPBId();
        Log.d(LOGTAG, uri);
        final GsonRequest gsonRequest = new GsonRequest(uri, ComboDataWrapper.Reconcile.class, null, new Response.Listener<ComboDataWrapper.Reconcile>() {
            @Override
            public void onResponse(ComboDataWrapper.Reconcile comboReconcile) {
                final List<ComboDataReconcile> arrayListComboReconcile = comboReconcile.getComboDataReconcile();
                if(arrayListComboReconcile != null) {
                    final HandlerThread handlerThread = new HandlerThread("BackgroundHandler");
                    handlerThread.start();
                    final Handler backgroundHandler = new Handler(handlerThread.getLooper());
                    backgroundHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            long dateReconcileInMilli = System.currentTimeMillis();
                            for (ComboDataReconcile reComboData : arrayListComboReconcile) {
                                if (reComboData.getCombo_ID() != null) {
                                    Log.d(LOGTAG, "requestReconcileComboData - comboId =" + reComboData.getCombo_ID().toString());
                                    dataSource.createReconcile(reComboData,dateReconcileInMilli);
                                }
                            }
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    responseListener.onResponse(arrayListComboReconcile);
                                }
                            });
                        }
                    });

//                    long dateReconcileInMilli = System.currentTimeMillis();
//                    for (ComboDataReconcile reComboData : arrayListComboReconcile) {
//                        if (reComboData.getCombo_ID() != null) {
//                            Log.d(LOGTAG, "requestReconcileComboData - comboId =" + reComboData.getCombo_ID().toString());
//                            dataSource.createReconcile(reComboData,dateReconcileInMilli);
//                        }
//                    }
//                    responseListener.onResponse(arrayListComboReconcile);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                responseListener.onResponseError(message_server_error, 0);
                printError(volleyError, uri);
            }
        });
        VolleyHelper.getInstance(InkarneAppContext.getAppContext()).addToRequestQueue(gsonRequest);
    }

    public void requestComboDataByCount(int count, String faceId, OnResponseHandlerInterface responseListener) {
        String userID = User.getInstance().getmUserId();
        String url = ConstantsUtil.URL_BASEPATH + ConstantsUtil.URL_METHOD_GETCOMBOSKUDATA + userID + "/" + faceId + "/" + count;
        requestComboDataByCount(url, responseListener);
    }

    public void requestComboDataByCount(final String uri, final OnResponseHandlerInterface responseListener) {
        Log.d(LOGTAG, "uri :" + uri);
        final JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, uri, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(LOGTAG, "Response: " + response.toString());
                try {
                    JSONArray combodataDetails = response.getJSONArray("GetComboSKUDataResult");
                    parseComboResponse(combodataDetails, responseListener);
                } catch (JSONException e) {
                    responseListener.onResponseError(message_Json_Parse_error, CODE_DATA_MANAGER_PARSE_ERROR);
                    printError(e, uri);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                responseListener.onResponseError(message_server_error, 0);
                printError(error, uri);
            }
        });
        VolleyHelper.getInstance(InkarneAppContext.getAppContext()).addToRequestQueue(jsObjRequest);
    }

    public void requestComboDataInfoById(String comboID, String faceId, OnResponseHandlerInterface responseListener) {
        User user = User.getInstance();
        String url = ConstantsUtil.URL_BASEPATH_V2 + ConstantsUtil.URL_METHOD_GETCOMBOSKUINFO_BY_ID + user.getmUserId() + "/" + user.getPBId() + "/" + faceId + "/" + comboID;
        requestComboDataInfo(url, responseListener);
    }

    public void requestComboDataInfo(final String uri, final OnResponseHandlerInterface responseListener) {
        Log.d(LOGTAG, "uri :" + uri);
        final JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, uri, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(LOGTAG, "Response: " + response.toString());
                        try {
                            //JSONArray combodataDetails = response.getJSONArray("GetComboSKUInfoResult");
                            JSONArray combodataDetails = response.getJSONArray("GetComboSKUInfoV2Result");

                            parseComboResponse(combodataDetails, responseListener);
                        } catch (JSONException e) {
                            responseListener.onResponseError(message_Json_Parse_error, CODE_DATA_MANAGER_PARSE_ERROR);
                            printError(e, uri);
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error != null && error.getMessage() != null && !error.getMessage().contains("java.net.unknownHostException")) {
                            responseListener.onResponseError(message_server_error, 0);
                        } else {
                            responseListener.onResponseError(message_server_error, 0);
                        }
                        printError(error, uri);
                    }
                });
        VolleyHelper.getInstance(InkarneAppContext.getAppContext()).addToRequestQueue(jsObjRequest);
    }

    public void parseComboResponse(JSONArray responseArray, final OnResponseHandlerInterface responseListener) {
        ArrayList<ComboData> arrayListCombo = new ArrayList<ComboData>();
        String A1_Category = null,
                A2_Category = null,
                A3_Category = null,
                A4_Category = null,
                A5_Category = null,
                A6_Category = null,
                A7_Category = null,
                A8_Category = null,
                A9_Category = null,
                Combo_ID = null,
                SKU_ID1 = null,
                SKU_ID2 = null,
                SKU_ID3 = null,
                SKU_ID4 = null,
                SKU_ID5 = null,
                SKU_ID6 = null,
                SKU_ID7 = null,
                SKU_ID8 = null,
                SKU_ID9 = null;

        String Combo_PIC_Png_Key_Name = null,
                A1_Obj_Key_Name = null, A1_PIC_Png_Key_Name = null, A1_Png_Key_Name = null,
                A2_Obj_Key_Name = null, A2_PIC_Png_Key_Name = null, A2_Png_Key_Name = null,
                A3_Obj_Key_Name = null, A3_PIC_Png_Key_Name = null, A3_Png_Key_Name = null,
                A4_Obj_Key_Name = null, A4_PIC_Png_Key_Name = null, A4_Png_Key_Name = null,
                A5_Obj_Key_Name = null, A5_PIC_Png_Key_Name = null, A5_Png_Key_Name = null,
                A6_Obj_Key_Name = null, A6_PIC_Png_Key_Name = null, A6_Png_Key_Name = null,
                A7_Obj_Key_Name = null, A7_PIC_Png_Key_Name = null, A7_Png_Key_Name = null,
                A8_Obj_Key_Name = null, A8_PIC_Png_Key_Name = null, A8_Png_Key_Name = null,
                A9_Obj_Key_Name = null, A9_PIC_Png_Key_Name = null, A9_Png_Key_Name = null;

        JSONArray combodataDetails = null;
        try {
            combodataDetails = responseArray;

            Log.d(LOGTAG, "Read: Combo no : =" + combodataDetails.length());
            for (int i = 0; i < combodataDetails.length(); i++) {
                ComboData combodata = new ComboData();
                JSONObject obj = combodataDetails.getJSONObject(i);
                A1_Category = obj.getString("A1_Category");
                A1_Obj_Key_Name = obj.getString("A1_Obj_Key_Name");
                A1_PIC_Png_Key_Name = obj.getString("A1_PIC_Png_Key_Name");
                A1_Png_Key_Name = obj.getString("A1_Png_Key_Name");

                A2_Category = obj.getString("A2_Category");
                A2_Obj_Key_Name = obj.getString("A2_Obj_Key_Name");
                A2_PIC_Png_Key_Name = obj.getString("A2_PIC_Png_Key_Name");
                A2_Png_Key_Name = obj.getString("A2_Png_Key_Name");

                A3_Category = obj.getString("A3_Category");
                A3_Obj_Key_Name = obj.getString("A3_Obj_Key_Name");
                A3_PIC_Png_Key_Name = obj.getString("A3_PIC_Png_Key_Name");
                A3_Png_Key_Name = obj.getString("A3_Png_Key_Name");

                A4_Category = obj.getString("A4_Category");
                A4_Obj_Key_Name = obj.getString("A4_Obj_Key_Name");
                A4_PIC_Png_Key_Name = obj.getString("A4_PIC_Png_Key_Name");
                A4_Png_Key_Name = obj.getString("A4_Png_Key_Name");

                A5_Category = obj.getString("A5_Category");
                A5_Obj_Key_Name = obj.getString("A5_Obj_Key_Name");
                A5_PIC_Png_Key_Name = obj.getString("A5_PIC_Png_Key_Name");
                A5_Png_Key_Name = obj.getString("A5_Png_Key_Name");

                A6_Category = obj.getString("A6_Category");
                A6_Obj_Key_Name = obj.getString("A6_Obj_Key_Name");
                A6_PIC_Png_Key_Name = obj.getString("A6_PIC_Png_Key_Name");
                A6_Png_Key_Name = obj.getString("A6_Png_Key_Name");

                A7_Category = obj.getString("A7_Category");
                A7_Obj_Key_Name = obj.getString("A7_Obj_Key_Name");
                A7_PIC_Png_Key_Name = obj.getString("A7_PIC_Png_Key_Name");
                A7_Png_Key_Name = obj.getString("A7_Png_Key_Name");

                A8_Category = obj.getString("A8_Category");
                A8_Obj_Key_Name = obj.getString("A8_Obj_Key_Name");
                A8_PIC_Png_Key_Name = obj.getString("A8_PIC_Png_Key_Name");
                A8_Png_Key_Name = obj.getString("A8_Png_Key_Name");

                A9_Category = obj.getString("A9_Category");
                A9_Obj_Key_Name = obj.getString("A9_Obj_Key_Name");
                A9_PIC_Png_Key_Name = obj.getString("A9_PIC_Png_Key_Name");
                A9_Png_Key_Name = obj.getString("A9_Png_Key_Name");

                SKU_ID1 = obj.getString("SKU_ID1");
                SKU_ID2 = obj.getString("SKU_ID2");
                SKU_ID3 = obj.getString("SKU_ID3");
                SKU_ID4 = obj.getString("SKU_ID4");
                SKU_ID5 = obj.getString("SKU_ID5");
                SKU_ID6 = obj.getString("SKU_ID6");
                SKU_ID7 = obj.getString("SKU_ID7");
                SKU_ID8 = obj.getString("SKU_ID8");
                SKU_ID9 = obj.getString("SKU_ID9");

                //combodata.setmStyle_Rating(Style_Rating);
                // Combo_Gender = obj.getString("Combo_Gender");
                Combo_ID = obj.getString("Combo_ID");
                //Combo_PIC_Png_File_Name = obj.getString("Combo_PIC_Png_File_Name");
                //Combo_PIC_Png_Key_Name = obj.getString("Combo_PIC_Png_Key_Name");
                //Combo_Style_Category = obj.getString("Combo_Style_Category");
                //Combo_Title = obj.getString("Combo_Title");
                //Likes_Count = obj.getString("Likes_Count");
                //Style_Rating = obj.getString("Style_Rating");

                combodata.setmA1_Category(A1_Category);
                if (obj.has("A1_Color")) {
                    combodata.setmA1_Color(obj.getString("A1_Color"));
                }
//                combodata.setmA1_Obj_File_Name(A1_Obj_File_Name);
//                combodata.setmA1_PIC_Png_File_Name(A1_PIC_Png_File_Name);
//                combodata.setmA1_Png_File_Name(A1_Png_File_Name);

                combodata.setmA1_Obj_Key_Name(A1_Obj_Key_Name);
                combodata.setmA1_PIC_Png_Key_Name(A1_PIC_Png_Key_Name);
                combodata.setmA1_Png_Key_Name(A1_Png_Key_Name);

                combodata.setmA2_Category(A2_Category);
                if (obj.has("A2_Color")) {
                    combodata.setmA2_Color(obj.getString("A2_Color"));
                }
                combodata.setmA2_Obj_Key_Name(A2_Obj_Key_Name);
                combodata.setmA2_PIC_Png_Key_Name(A2_PIC_Png_Key_Name);
                combodata.setmA2_Png_Key_Name(A2_Png_Key_Name);

                combodata.setmA3_Category(A3_Category);
                if (obj.has("A3_Color")) {
                    combodata.setmA3_Color(obj.getString("A3_Color"));
                }
                combodata.setmA3_Obj_Key_Name(A3_Obj_Key_Name);
                combodata.setmA3_PIC_Png_Key_Name(A3_PIC_Png_Key_Name);
                combodata.setmA3_Png_Key_Name(A3_Png_Key_Name);

                combodata.setmA4_Category(A4_Category);
                if (obj.has("A4_Color")) {
                    combodata.setmA4_Color(obj.getString("A4_Color"));
                }
                combodata.setmA4_Obj_Key_Name(A4_Obj_Key_Name);
                combodata.setmA4_PIC_Png_Key_Name(A4_PIC_Png_Key_Name);
                combodata.setmA4_Png_Key_Name(A4_Png_Key_Name);

                combodata.setmA5_Category(A5_Category);
                combodata.setmA5_Obj_Key_Name(A5_Obj_Key_Name);
                combodata.setmA5_PIC_Png_Key_Name(A5_PIC_Png_Key_Name);
                combodata.setmA5_Png_Key_Name(A5_Png_Key_Name);

                combodata.setmA6_Category(A6_Category);
                combodata.setmA6_Obj_Key_Name(A6_Obj_Key_Name);
                combodata.setmA6_PIC_Png_Key_Name(A6_PIC_Png_Key_Name);
                combodata.setmA6_Png_Key_Name(A6_Png_Key_Name);

                combodata.setmA7_Category(A7_Category);
                combodata.setmA7_Obj_Key_Name(A7_Obj_Key_Name);
                combodata.setmA7_PIC_Png_Key_Name(A7_PIC_Png_Key_Name);
                combodata.setmA7_Png_Key_Name(A7_Png_Key_Name);

                combodata.setmA8_Category(A8_Category);
                combodata.setmA8_Obj_Key_Name(A8_Obj_Key_Name);
                combodata.setmA8_PIC_Png_Key_Name(A8_PIC_Png_Key_Name);
                combodata.setmA8_Png_Key_Name(A8_Png_Key_Name);

                combodata.setmA9_Category(A9_Category);
                combodata.setmA9_Obj_Key_Name(A9_Obj_Key_Name);
                combodata.setmA9_PIC_Png_Key_Name(A9_PIC_Png_Key_Name);
                combodata.setmA9_Png_Key_Name(A9_Png_Key_Name);

                combodata.setmA10_Category(obj.getString("A10_Category"));
                combodata.setmA10_Obj_Key_Name(obj.getString("A10_Obj_Key_Name"));
                combodata.setmA10_PIC_Png_Key_Name(obj.getString("A10_PIC_Png_Key_Name"));
                combodata.setmA10_Png_Key_Name(obj.getString("A10_Png_Key_Name"));

                combodata.setCombo_ID(Combo_ID);
                combodata.setmSKU_ID1(SKU_ID1);
                combodata.setmSKU_ID2(SKU_ID2);
                combodata.setmSKU_ID3(SKU_ID3);
                combodata.setmSKU_ID4(SKU_ID4);
                combodata.setmSKU_ID5(SKU_ID5);
                combodata.setmSKU_ID6(SKU_ID6);
                combodata.setmSKU_ID7(SKU_ID7);
                combodata.setmSKU_ID8(SKU_ID8);
                combodata.setmSKU_ID9(SKU_ID9);
                combodata.setmSKU_ID10(obj.getString("SKU_ID10"));
//                if (obj.has("Leg_ID")) {
//                    combodata.setLegId(obj.getString("Leg_ID"));
//                    Log.d(LOGTAG, "comboData :" + combodata.getCombo_ID() + "  LegId :" + combodata.getLegId());
//                    BaseAccessoryItem legItem = dataSource.getAccessory(ConstantsUtil.EAccessoryType.eAccTypeLegs.toString(), combodata.getLegId());
//                    if (legItem != null) {
//                        if (legItem.getObjAwsKey() != null)
//                            combodata.setLegObjKeyName(legItem.getObjAwsKey());
//                        if (legItem.getTextureAwsKey() != null)
//                            combodata.setLegTextureKeyName(legItem.getTextureAwsKey());
//                    } else {
//                        Log.e(LOGTAG, "could not get Leg Object LegId: " + combodata.getLegId());
//                    }
//                }

                if (obj.has("Leg_ID")) {//TODO to be removed
                    combodata.setLegId(obj.getString("Leg_ID"));
                    Log.d(LOGTAG, "comboData :" + combodata.getCombo_ID() + "  LegId :" + combodata.getLegId());
                    if(!combodata.getLegId().equals("NA")) {
                        BaseAccessoryItem legItem = dataSource.getAccessory(ConstantsUtil.EAccessoryType.eAccTypeLegs.toString(), combodata.getLegId());
                        if (legItem != null) {
                            combodata.setLegItem(legItem);
                        } else {
                            Log.e(LOGTAG, "could not get Leg Object LegId: " + combodata.getLegId());
                        }
                    }
                }

                if (obj.has("PB_ID")) {
                    combodata.setPbId(obj.getString("PB_ID"));
                } else {
                    combodata.setPbId(User.getInstance().getPBId());
                }

                if(User.getInstance().getDefaultFaceId() != null)
                combodata.setFaceId(User.getInstance().getDefaultFaceId());
                combodata = dataSource.create(combodata);
                arrayListCombo.add(combodata);
                Log.d(LOGTAG, "combodata :" + combodata.getCombo_ID() + "  Obj :" + combodata.toString());
            }
            responseListener.onResponse(arrayListCombo);
        } catch (JSONException e) {
            responseListener.onResponseError(message_Json_Parse_error, CODE_DATA_MANAGER_PARSE_ERROR);
            printError(e, " Parsing ComboData");
        }
    }


    //faceobj and png
    public void requestFaceObj(final String uri, final OnResponseHandlerInterface responseListener) {
        Log.d(LOGTAG, "requestFaceObj uri : " + uri);
        final JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, uri, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(LOGTAG, "Response: " + response.toString());
                try {
                    JSONObject jsonObj = null;
                    FaceItem faceItem = null;
                    int errorCode = -1;
                        jsonObj = response.getJSONObject("CreateFaceAndHairstyleV2Result");
                        errorCode = jsonObj.getInt("Error_Code");
                        if(errorCode != 300) {
                            faceItem = new FaceItem();
                            faceItem.setFaceId(jsonObj.getString(FaceItem.reskeyFaceId));
                            faceItem.setHairstyleId(jsonObj.getString(FaceItem.reskeyHairId));
                            faceItem.setFacePngkey(jsonObj.getString(FaceItem.reskeyFacePng));
                            faceItem.setFaceObjkey(jsonObj.getString(FaceItem.reskeyFaceObj));
                            faceItem.setHairPngKey(jsonObj.getString(FaceItem.reskeyHairPng));
                            faceItem.setHairObjkey(jsonObj.getString(FaceItem.reskeyHairObj));
                        }
                    responseListener.onResponse(faceItem);
                } catch (JSONException e) {
                    responseListener.onResponseError(message_Json_Parse_error, CODE_DATA_MANAGER_PARSE_ERROR);
                    printError(e, uri);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                responseListener.onResponseError(message_server_error, 0);
                printError(error, uri);
            }
        });
        VolleyHelper.getInstance(InkarneAppContext.getAppContext()).addToRequestQueue(jsObjRequest);
    }

    //faceobj and png
    public void requestCreateAccessory(final String uri, final BaseAccessoryItem baseObj, boolean shouldRefresh, final OnResponseHandlerInterface responseListener) {
        Log.d(LOGTAG, "uri :" + uri);
        //get from database

        final JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, uri, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(LOGTAG, "Response: " + response.toString());

                JSONObject jsonObj = null;
                try {
                    if (baseObj.getAccessoryType().equals(ConstantsUtil.EAccessoryType.eAccTypeSpecs.toString())) {
                        jsonObj = response.getJSONObject("CreateSpecsV2Result");
                    } else if (baseObj.getAccessoryType().equals(ConstantsUtil.EAccessoryType.eAccTypeHair.toString())) {
                        if (response.has("CreateHairstyleV2Result"))
                            jsonObj = response.getJSONObject("CreateHairstyleV2Result");
                    } else if (baseObj.getAccessoryType().equals(ConstantsUtil.EAccessoryType.eAccTypeEarrings.toString())) {

                        jsonObj = response.getJSONObject("CreateEarringsV2Result");
                    } else if (baseObj.getAccessoryType().equals(ConstantsUtil.EAccessoryType.eAccTypeSunglasses.toString())) {

                        jsonObj = response.getJSONObject("CreateSunglassesV2Result");
                    }
                    else if (baseObj.getAccessoryType().equals(ConstantsUtil.EAccessoryType.eAccTypeShoes.toString())) {

                        jsonObj = response.getJSONObject("CreateShoesV2Result");
                    }
                    /*
                    else if (baseObj.getAccessoryType().equals(ConstantsUtil.EAccessoryType.eAccTypeShoes.toString())) {
                        jsonObj = response.getJSONObject("CreateShoesResult");

                    } else if (baseObj.getAccessoryType().equals(ConstantsUtil.EAccessoryType.eAccTypeClutches.toString())) {
                        jsonObj = response.getJSONObject("CreateClutchesResult");
                    }
                    */

                    baseObj.setObjAwsKey(jsonObj.getString(BaseItem.keyNameObj));
                    baseObj.setTextureAwsKey(jsonObj.getString(BaseItem.keyNameTexture));
                    responseListener.onResponse(baseObj);
                } catch (JSONException e) {
                    responseListener.onResponseError(message_Json_Parse_error, CODE_DATA_MANAGER_PARSE_ERROR);
                    printError(e, uri);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                responseListener.onResponseError(message_server_error, 0);
                printError(error, uri);
            }
        });
        VolleyHelper.getInstance(InkarneAppContext.getAppContext()).addToRequestQueue(jsObjRequest);
    }

    public void requestCreateBody(final String uri, final OnResponseHandlerInterface responseListener) {
        Log.d(LOGTAG, "uri :" + uri);
        //get from database

        final JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, uri, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(LOGTAG, "Response: " + response.toString());

                JSONObject jsonObj = null;
                try {
                    //jsonObj = response.getJSONObject("CreateBodyV2Result");
                    jsonObj = response.getJSONObject("CreateBodyV2Result");
                    int errorcode = jsonObj.getInt("Error_Code");
                    if (errorcode == 300) {
                        responseListener.onResponseError("Error occurred Error_code: " + errorcode, 0);
                        return;
                    }

                    BaseItem item = new BaseItem();

                    item.setObjAwsKey(jsonObj.getString(BaseItem.keyNameObj));
                    item.setTextureAwsKey(jsonObj.getString(BaseItem.keyNameTexture));
                    item.setObjId(jsonObj.getString("PB_ID"));
                    if(jsonObj.has("Leg_Details")) {
                        JSONArray jsonArray = jsonObj.getJSONArray("Leg_Details");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            BaseAccessoryItem legItem = new BaseAccessoryItem();
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            legItem.setObjAwsKey(jsonObject.getString(BaseItem.keyNameObj));
                            legItem.setTextureAwsKey(jsonObject.getString(BaseItem.keyNameTexture));
                            legItem.setObjId(jsonObject.getString("Leg_ID"));
                            legItem.setAccessoryType(ConstantsUtil.EAccessoryType.eAccTypeLegs.toString());
                            legItem.setPbID(item.getObjId());
                            if(User.getInstance() != null && User.getInstance().getDefaultFaceItem() != null && User.getInstance().getDefaultFaceItem().getFaceId() != null && !User.getInstance().getDefaultFaceItem().getFaceId().isEmpty())
                            legItem.setFaceID(User.getInstance().getDefaultFaceItem().getFaceId());
                            else{
                                legItem.setFaceID("1");
                            }
                            Log.w(LOGTAG,"create body legid:"+ item.getObjId()+ "  faceid:"+legItem.getFaceID()+"  pbid:"+item.getObjId());
                            InkarneAppContext.getDataSource().create(legItem);
                        }
                    }
                    responseListener.onResponse(item);
                } catch (JSONException e) {
                    responseListener.onResponseError(message_Json_Parse_error, CODE_DATA_MANAGER_PARSE_ERROR);
                    printError(e, uri);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                responseListener.onResponseError(message_server_error, 0);
                printError(error, uri);
            }
        });
        VolleyHelper.getInstance(InkarneAppContext.getAppContext()).addToRequestQueue(jsObjRequest);
    }

    public void requestCreateLegs(final String uri, final OnResponseHandlerInterface responseListener) {
        Log.d(LOGTAG, "uri :" + uri);
        //get from database

        final JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, uri, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(LOGTAG, "Response: " + response.toString());
                ArrayList<BaseAccessoryItem> arrayBaseItem = new ArrayList<>();
                JSONArray jsonArray = null;
                try {
                    //jsonArray = response.getJSONArray("CreateLegsV2Result");
                    jsonArray = response.getJSONArray("CreateLegsV2Result");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        BaseAccessoryItem item = new BaseAccessoryItem();
                        JSONObject jsonObj = jsonArray.getJSONObject(i);
                        item.setObjAwsKey(jsonObj.getString(BaseItem.keyNameObj));
                        item.setTextureAwsKey(jsonObj.getString(BaseItem.keyNameTexture));
                        item.setObjId(jsonObj.getString("Leg_ID"));
                        item.setAccessoryType(ConstantsUtil.EAccessoryType.eAccTypeLegs.toString());
                        item.setPbID(User.getInstance().getPBId());
                        item.setFaceID(User.getInstance().getDefaultFaceId());
                        InkarneAppContext.getDataSource().create(item);
                        arrayBaseItem.add(item);
                    }
                    responseListener.onResponse(arrayBaseItem);
                } catch (JSONException e) {
                    responseListener.onResponseError(message_Json_Parse_error, CODE_DATA_MANAGER_PARSE_ERROR);
                    printError(e, uri);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                responseListener.onResponseError(message_server_error, CODE_DATA_MANAGER_PARSE_ERROR);
                printError(error, uri);
            }
        });
        VolleyHelper.getInstance(InkarneAppContext.getAppContext()).addToRequestQueue(jsObjRequest);
    }


    public void requestUserFedPoints(final String uri, final OnResponseHandlerInterface responseListener) {
        Log.d(LOGTAG, "uri :" + uri);
        //get from database

        final JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, uri, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(LOGTAG, "Response: " + response.toString());
                try {
                    JSONObject fDetails = response.getJSONObject("GetUserFidicualsResult");
                    responseListener.onResponse(fDetails);
                } catch (JSONException e) {
                    responseListener.onResponseError(message_Json_Parse_error, CODE_DATA_MANAGER_PARSE_ERROR);
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                responseListener.onResponseError(message_server_error, 0);
                printError(error, uri);
            }
        });
        VolleyHelper.getInstance(InkarneAppContext.getAppContext()).addToRequestQueue(jsObjRequest);
    }

    private ArrayList<BaseAccessoryItem> populateAccessoryItem(JSONArray jsonArray, String accessoryType) {
        ArrayList<BaseAccessoryItem> items = new ArrayList<>();
        try {
            long dateReconcileInMilli = System.currentTimeMillis();
            for (int i = 0; i < jsonArray.length(); i++) {
                BaseAccessoryItem item = new BaseAccessoryItem();
                JSONObject jsonObj = jsonArray.getJSONObject(i);
                item.setObjId(jsonObj.getString("Accessory_ID"));
                item.setAccessoryType(accessoryType);
                item.setThumbnailAwsKey(jsonObj.getString(BaseItem.keyNamePng));
                if (jsonObj.has(BaseItem.keyNameObj)) {
                    item.setObjAwsKey(jsonObj.getString(BaseItem.keyNameObj));
                }
                if (jsonObj.has(BaseItem.keyNameTexture)) {
                    item.setTextureAwsKey(jsonObj.getString(BaseItem.keyNameTexture));
                }

                if (ConstantsUtil.isAccessoryIndependent(accessoryType)) {

                } else if (ConstantsUtil.isAccessoryBodyRelated(accessoryType)) {
                    item.setPbID(User.getInstance().getPBId());
                } else if (ConstantsUtil.isAccessoryFaceRelated(accessoryType)) {
                    item.setFaceID(User.getInstance().getDefaultFaceItem().getFaceId());
                } else if (ConstantsUtil.isAccessoryBodyFaceRelated(accessoryType)) {
                    item.setPbID(User.getInstance().getPBId());
                    item.setFaceID(User.getInstance().getDefaultFaceItem().getFaceId());
                }
                if (accessoryType.equals(ConstantsUtil.EAccessoryType.eAccTypeShoes.toString())) {
                    item.setObjId2(jsonObj.getString(BaseItem.keyNameObjId2));
                    item.setAccessoryType2(ConstantsUtil.EAccessoryType.eAccTypeLegs.toString());
                }

                item = dataSource.create(item,dateReconcileInMilli);
                items.add(item);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return items;
    }

    public void requestCreateVideo(final String uri, final OnResponseHandlerInterface responseListener) {
        Log.d(LOGTAG, "uri :" + uri);

        final JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, uri, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(LOGTAG, "Response: " + response.toString());

                JSONObject jsonObj = null;
                try {
//                    "Error_Code": 0,
//                            "Video_ID": "32",
//                            "Video_Key_Name":"inkarne/users/789/videos/MyLook32.mp4"
                    jsonObj = response.getJSONObject("CreateVideoResult");
                    int errorCode = jsonObj.getInt(ERROR_CODE_KEY);

                    if(errorCode != 300){
                        String videoCode = jsonObj.getString("Video_Key_Name");
                        responseListener.onResponse(videoCode);
                    }
                    else
                    responseListener.onResponseError(message_server_error,errorCode);
                } catch (JSONException e) {
                    responseListener.onResponseError(message_Json_Parse_error, CODE_DATA_MANAGER_PARSE_ERROR);
                    printError(e, uri);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                responseListener.onResponseError(message_server_error, CODE_DATA_MANAGER_UNKNOWN_ERROR);
                printError(error, uri);
            }
        });
        VolleyHelper.getInstance(InkarneAppContext.getAppContext()).addToRequestQueue(jsObjRequest);
    }


    public ArrayList<BaseAccessoryItem> getAccessories(final String accessoryType, boolean shouldRefresh, final OnResponseHandlerInterface responseListener) {
        ArrayList<BaseAccessoryItem> arrayAccessories = null;
        if (!shouldRefresh) {
            if (!isTimeForRefresh(accessoryType)) {
                arrayAccessories = getAccessoryFromDB(accessoryType);
            }
        }
        if (arrayAccessories == null || arrayAccessories.size() == 0) {
            final String[] arrayValue = (String[]) ConstantsUtil.HASH_MAP_ACCESSORY.get(accessoryType);
            String urlMethod = arrayValue[0];
            final String uri = ConstantsUtil.URL_BASEPATH_V2 + urlMethod + User.getInstance().getmUserId() + "/" + User.getInstance().getmGender();
            Log.d(LOGTAG, "uri :" + uri);
            final JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, uri, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    Log.d(LOGTAG, "Response: " + response.toString());
                    try {

                        JSONArray jsonArray = response.getJSONArray(arrayValue[1]);
                        ArrayList<BaseAccessoryItem> list = populateAccessoryItem(jsonArray, accessoryType);
                       //if(list != null && list.size() != 0)
                       {
                           long time = System.currentTimeMillis()/1000;
                           hashMapRefreshTime.put(accessoryType,Long.valueOf(time));
                       }
                        list = getAccessoryFromDB(accessoryType);
                        responseListener.onResponse(list);
                    } catch (JSONException e) {
                        //getAccessoryFromDB(accessoryType,responseListener );
                        printError(e, uri);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    getAccessoryFromDB(accessoryType, responseListener);
                    printError(error, uri);
                }
            });
            VolleyHelper.getInstance(InkarneAppContext.getAppContext()).addToRequestQueue(jsObjRequest);
        } else {
            responseListener.onResponse(arrayAccessories);
        }
        return arrayAccessories;
    }

    public ArrayList<BaseAccessoryItem> getAccessoryFromDB(String accessoryType) {
        ArrayList<BaseAccessoryItem> arrayAccessories = (ArrayList<BaseAccessoryItem>) dataSource.getAccessories(accessoryType);
        return arrayAccessories;
    }

    public void getAccessoryFromDB(String accessoryType, final OnResponseHandlerInterface responseListener) {
        ArrayList<BaseAccessoryItem> arrayAccessories = (ArrayList<BaseAccessoryItem>) dataSource.getAccessories(accessoryType);
        if (arrayAccessories != null && arrayAccessories.size() != 0) {
            responseListener.onResponse(arrayAccessories);
        } else {
            responseListener.onResponseError(" Data not available or failed to get data from DB", 0);
        }
    }

    public boolean isTimeForRefresh(String accessoryType) {
        Long lastFetched = hashMapRefreshTime.get(accessoryType);
        //hashMapRefreshTime.put(accessoryType,Long.valueOf(time));
        return lastFetched == null || System.currentTimeMillis() / 1000 - lastFetched > ConstantsFunctional.TIME_REFRESH_INTERVAL_ACCESSORY_INSEC;
    }

    public boolean isTimeForRefresh() {
        return true;
    }


    /*update functionality*/

    public void deleteFace(final String uri, final OnResponseHandlerInterface responseListener) {
        Log.d(LOGTAG, "uri :" + uri);
        //get from database
        final JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, uri, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(LOGTAG, "Response: " + response.toString());
                JSONObject jsonObj = null;
                responseListener.onResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                responseListener.onResponseError(message_server_error, 0);
                printError(error, uri);
            }
        });
        VolleyHelper.getInstance(InkarneAppContext.getAppContext()).addToRequestQueue(jsObjRequest);
    }

    public void deleteObjOnServer(final String uri, final OnResponseHandlerInterface responseListener) {
        Log.d(LOGTAG, "uri :" + uri);
        //get from database
        final JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, uri, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(LOGTAG, "Response: " + response.toString());
                JSONObject jsonObj = null;
                responseListener.onResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                responseListener.onResponseError(message_server_error, 0);
                printError(error, uri);
            }
        });
        VolleyHelper.getInstance(InkarneAppContext.getAppContext()).addToRequestQueue(jsObjRequest);
    }

    public void updateDefaultFace(final String uri, final OnResponseHandlerInterface responseListener) {
        Log.d(LOGTAG, "uri :" + uri);
        //get from database
        final JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, uri, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(LOGTAG, "Response: " + response.toString());
                JSONObject jsonObj = null;
                responseListener.onResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                responseListener.onResponseError(message_server_error, 0);
                printError(error, uri);
            }
        });
        VolleyHelper.getInstance(InkarneAppContext.getAppContext()).addToRequestQueue(jsObjRequest);
    }


    public void updateMethodToServer(final String url, final String updateType, final OnResponseHandlerInterface responseListener) {
        Log.d(LOGTAG, "uri :" + url);
        final JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(LOGTAG, "Response: " + response.toString());
                        try {
                            String[] strArray = ConstantsUtil.getHASH_MAP_UPDATE_TYPE(updateType);//TODO
                            if(strArray != null && strArray.length > 1) {
                                String jsonKey = ConstantsUtil.getHASH_MAP_UPDATE_TYPE(updateType)[1];
                                if (jsonKey != null) {
                                    int result = response.getInt(jsonKey);
                                    if (result != 300) {
                                        responseListener.onResponse(result);
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            printError(e, url);
                            responseListener.onResponseError(message_Json_Parse_error, CODE_DATA_MANAGER_PARSE_ERROR);
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        printError(error, url);
                        responseListener.onResponseError(message_server_error, 0);
                    }
                });
        VolleyHelper.getInstance(InkarneAppContext.getAppContext()).addToRequestQueue(jsObjRequest);
    }

    //                final ArrayList<LookBoardItem> arrayBaseItem = new ArrayList<>();
//                //final long reconcileDateInMilli = System.currentTimeMillis();
//                    new Handler(Looper.myLooper()).post(new Runnable() {
//                        @Override
//                        public void run() {
//
//                        }
//                    });


    public void requestLookBoard(final String uri, final OnResponseHandlerInterface responseListener) {
        Log.d(LOGTAG, "uri :" + uri);
        //get from database
        final JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, uri, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                final JSONArray jsonArray = response.getJSONArray("ReconcileCampaignDatav3Result");
                Log.d(LOGTAG, "Response: " + jsonArray.toString());
                    final HandlerThread handlerThread = new HandlerThread("BackgroundHandler");
                    handlerThread.start();
                    final Handler backgroundHandler = new Handler(handlerThread.getLooper());
                    backgroundHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                          final ArrayList<LookBoardItem> arrayBaseItem = new ArrayList<>();
                          final long reconcileDateInMilli = System.currentTimeMillis();
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    LookBoardItem item = new LookBoardItem();
                                    JSONObject jsonObj = jsonArray.getJSONObject(i);
                                    item.setCampaign_ID(jsonObj.getString("Campaign_ID"));
                                    item.setCombo_ID(jsonObj.getString("Combo_ID"));
                                    item.setImage_Key_Name(jsonObj.getString("Image_Key_Name"));
                                    item.setStatus(jsonObj.getString("Status"));
                                    item.setCategory(jsonObj.getString("Category"));
                                    item.setDescription(jsonObj.getString("Description"));
                                    item.setRank(Integer.parseInt(jsonObj.getString("Rank")));

                                    dataSource.createReconcileLookBoard(item,reconcileDateInMilli);
                                    arrayBaseItem.add(item);
                                }
                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        responseListener.onResponse(arrayBaseItem);
                                    }
                                });

                            }catch (final Exception e){
                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        responseListener.onResponseError(message_Json_Parse_error, CODE_DATA_MANAGER_PARSE_ERROR);
                                        printError(e, uri);
                                    }
                                });
                            }
                        }
                    });


//                    for (int i = 0; i < jsonArray.length(); i++) {
//                        LookBoardItem item = new LookBoardItem();
//                        JSONObject jsonObj = jsonArray.getJSONObject(i);
//                        item.setCampaign_ID(jsonObj.getString("Campaign_ID"));
//                        item.setCombo_ID(jsonObj.getString("Combo_ID"));
//                        item.setImage_Key_Name(jsonObj.getString("Image_Key_Name"));
//                        item.setStatus(jsonObj.getString("Status"));
//                        item.setCategory(jsonObj.getString("Category"));
//                        item.setDescription(jsonObj.getString("Description"));
//                        item.setRank(Integer.parseInt(jsonObj.getString("Rank")));
//
//                        dataSource.createReconcileLookBoard(item,reconcileDateInMilli);
//                        arrayBaseItem.add(item);
//                    }
//                    responseListener.onResponse(arrayBaseItem);
                } catch (JSONException e) {
                    responseListener.onResponseError(message_Json_Parse_error, CODE_DATA_MANAGER_PARSE_ERROR);
                    printError(e, uri);

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                responseListener.onResponseError(message_server_error, CODE_DATA_MANAGER_PARSE_ERROR);
                printError(error, uri);
            }
        });
        VolleyHelper.getInstance(InkarneAppContext.getAppContext()).addToRequestQueue(jsObjRequest);
    }










    /********************************************** OLD ************************************************************/
    /********************************************** OLD ************************************************************/

    public void updateDefaultHairstyle(final String uri, final OnResponseHandlerInterface responseListener) {
        Log.d(LOGTAG, "uri :" + uri);
        //get from database
        final JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, uri, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(LOGTAG, "Response: " + response.toString());
                JSONObject jsonObj = null;
                responseListener.onResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                responseListener.onResponseError(message_server_error, 0);
                printError(error, uri);
            }
        });
        VolleyHelper.getInstance(InkarneAppContext.getAppContext()).addToRequestQueue(jsObjRequest);
    }

    public void updateDefaultSpecs(final String uri, final OnResponseHandlerInterface responseListener) {
        Log.d(LOGTAG, "uri :" + uri);
        //get from database
        final JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, uri, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(LOGTAG, "Response: " + response.toString());
                JSONObject jsonObj = null;
                responseListener.onResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                responseListener.onResponseError(message_server_error, 0);
                printError(error, uri);
            }
        });
        VolleyHelper.getInstance(InkarneAppContext.getAppContext()).addToRequestQueue(jsObjRequest);
    }

    protected void updateAccessorySelectToServer(final String uri, final OnResponseHandlerInterface responseListener) {
        Log.d(LOGTAG, "uri :" + uri);
        final JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, uri, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(LOGTAG, "Response: " + response.toString());
                        try {
                            int result = response.getInt("UpdateCartResult");
                            if (result != 300) {

                            }

                        } catch (JSONException e) {
                            if (e != null && e.getMessage() != null) {
                                Log.e(LOGTAG, e.getMessage());
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        responseListener.onResponseError("Error occurred", 0);
                        printError(error, uri);
                    }
                });
        VolleyHelper.getInstance(InkarneAppContext.getAppContext()).addToRequestQueue(jsObjRequest);
    }

    public void requestHairstyleList(final String uri, boolean shouldRefresh, final OnResponseHandlerInterface responseListener) {
        Log.d(LOGTAG, "uri :" + uri);
        if (!shouldRefresh && !isTimeForRefresh()) {
            ArrayList<BaseAccessoryItem> arrayList = getAccessoryFromDB(ConstantsUtil.EAccessoryType.eAccTypeHair.toString());
            if (arrayList != null && arrayList.size() != 0) {
                Log.d(LOGTAG, "hair from DB :");
                ArrayList<HairContent> hairArray = new ArrayList<HairContent>();
                String faceId = User.getInstance().getDefaultFaceId();
                for (int i = 0; i < arrayList.size(); i += 2) {
                    BaseAccessoryItem item = arrayList.get(i);
                    item.itemType = 0;
                    if (faceId != null && faceId.length() != 0)
                        item.setFaceID(faceId);
                    HairContent hairContent = new HairContent();
                    hairContent.hairItem1 = item;
                    if (i + 1 < arrayList.size()) {
                        BaseAccessoryItem item2 = arrayList.get(i + 1);
                        item2.itemType = 1;
                        if (faceId != null && faceId.length() != 0)
                            item2.setFaceID(faceId);
                        hairContent.hairItem2 = item2;
                    }
                    hairArray.add(hairContent);
                }
                responseListener.onResponse(hairArray);
            } else {
                getHairStyleListFromServer(uri, responseListener);
            }
        } else {
            getHairStyleListFromServer(uri, responseListener);
        }
    }

    public void getHairStyleListFromServer(final String uri, final OnResponseHandlerInterface responseListener) {
        Log.d(LOGTAG, "uri :" + uri);
        final JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, uri, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(LOGTAG, "Response: " + response.toString());
                ArrayList<HairContent> hairArray = new ArrayList<HairContent>();
                JSONArray array = null;
                try {
                    array = response.getJSONArray("ReconcileHairstyleMasterInfoResult");

                    //i=9, i=8
                    String faceId = User.getInstance().getDefaultFaceId();
                    for (int i = 0; i < array.length(); i += 2) {
                        JSONObject item = array.getJSONObject(i);
                        BaseAccessoryItem hItem1 = new BaseAccessoryItem();

                        hItem1.setObjId(item.getString(BaseItem.keyNameID));
                        int count = hairArray.size() + i;
                        hItem1.setThumbnailAwsKey(item.getString(BaseItem.keyNamePng));
                        hItem1.itemType = 0;
                        hItem1.setAccessoryType(ConstantsUtil.EAccessoryType.eAccTypeHair.toString());

                        HairContent hairContent = new HairContent();
                        hairContent.hairItem1 = hItem1;
                        if (faceId != null && faceId.length() != 0) {
                            hItem1.setFaceID(faceId);
                            dataSource.create(hairContent.hairItem1);
                        }

                        if (i + 1 < array.length()) {
                            JSONObject item2 = array.getJSONObject(i + 1);
                            BaseAccessoryItem hItem2 = new BaseAccessoryItem();
                            hItem2.setObjId(item2.getString(BaseItem.keyNameID));
                            count = hairArray.size() + i;
                            hItem2.itemType = 1;
                            hItem2.setThumbnailAwsKey(item2.getString(BaseItem.keyNamePng));
                            hItem2.setAccessoryType(ConstantsUtil.EAccessoryType.eAccTypeHair.toString());
                            hairContent.hairItem2 = hItem2;
                            if (faceId != null && faceId.length() != 0) {
                                hItem2.setFaceID(faceId);
                                dataSource.create(hairContent.hairItem2);
                            }
                        }
                        hairArray.add(hairContent);
                    }
                    responseListener.onResponse(hairArray);
                } catch (JSONException e) {
                    responseListener.onResponseError(message_Json_Parse_error, CODE_DATA_MANAGER_PARSE_ERROR);
                    printError(e, uri);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                responseListener.onResponseError(message_server_error, 0);
                printError(error, uri);
            }
        });
        VolleyHelper.getInstance(InkarneAppContext.getAppContext()).addToRequestQueue(jsObjRequest);
    }

    private BaseAccessoryItem populateSpecsItem(JSONObject jsonObj, int itemType) {
        BaseAccessoryItem hItem = new BaseAccessoryItem();
        try {
            hItem.setObjId(jsonObj.getString(BaseItem.keyNameID));
            hItem.itemType = itemType;
            hItem.setThumbnailAwsKey(jsonObj.getString(BaseItem.keyNamePng));
            hItem.setAccessoryType(ConstantsUtil.EAccessoryType.eAccTypeSpecs.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return hItem;
    }

    //faceobj and png
    public void requestSpecsList(final String uri, boolean shouldRefresh, final OnResponseHandlerInterface responseListener) {
        Log.d(LOGTAG, "uri :" + uri);
        if (!shouldRefresh && !isTimeForRefresh()) {
            ArrayList<BaseAccessoryItem> arrayList = getAccessoryFromDB(ConstantsUtil.EAccessoryType.eAccTypeSpecs.toString());
            if (arrayList != null && arrayList.size() != 0) {
                ArrayList<SpecsContent> contentArray = new ArrayList<SpecsContent>();
                String faceId = User.getInstance().getDefaultFaceId();
                for (int i = 0; i < arrayList.size(); i += 3) {
                    SpecsContent specsContent = new SpecsContent();
                    BaseAccessoryItem item1 = arrayList.get(i);
                    item1.itemType = 0;
                    specsContent.item1 = item1;
                    //specsContent.item1.setFaceID(faceId);
                    //dataSource.create((BaseAccessoryItem)specsContent.item1);
                    if (i + 1 < arrayList.size()) {
                        BaseAccessoryItem item2 = arrayList.get(i + 1);
                        item2.itemType = 1;
                        specsContent.item2 = item2;
                    }
                    if (i + 2 < arrayList.size()) {
                        BaseAccessoryItem item3 = arrayList.get(i + 2);
                        item3.itemType = 2;
                        specsContent.item3 = item3;
                    }
                    contentArray.add(specsContent);
                }
                responseListener.onResponse(contentArray);
            } else {
                requestSpecsListFromServer(uri, responseListener);
            }
        } else {
            requestSpecsListFromServer(uri, responseListener);
        }
    }

    public void requestSpecsListFromServer(final String uri, final OnResponseHandlerInterface responseListener) {
        Log.d(LOGTAG, "uri :" + uri);
        //get from database
        final JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, uri, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(LOGTAG, "Response: " + response.toString());
                ArrayList<SpecsContent> contentArray = new ArrayList<SpecsContent>();
                JSONArray array = null;
                try {
                    array = response.getJSONArray("ReconcileSpecsMasterInfoV2Result");
                    //i=9, i=8
                    String faceId = User.getInstance().getDefaultFaceId();
                    for (int i = 0; i < array.length(); i += 3) {
                        SpecsContent specsContent = new SpecsContent();
                        specsContent.item1 = populateSpecsItem(array.getJSONObject(i), 0);
                        specsContent.item1.setFaceID(faceId);
                        dataSource.create(specsContent.item1);

                        if (i + 1 < array.length()) {
                            specsContent.item2 = populateSpecsItem(array.getJSONObject(i + 1), 1);
                            specsContent.item2.setFaceID(faceId);
                            dataSource.create(specsContent.item2);
                        }
                        if (i + 2 < array.length()) {
                            specsContent.item3 = populateSpecsItem(array.getJSONObject(i + 2), 2);
                            specsContent.item3.setFaceID(faceId);
                            dataSource.create(specsContent.item3);
                        }
                        contentArray.add(specsContent);
                    }
                    responseListener.onResponse(contentArray);
                } catch (JSONException e) {
                    responseListener.onResponseError(message_Json_Parse_error, CODE_DATA_MANAGER_PARSE_ERROR);
                    printError(e, uri);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                responseListener.onResponseError(message_server_error, 0);
                printError(error, uri);
            }
        });
        VolleyHelper.getInstance(InkarneAppContext.getAppContext()).addToRequestQueue(jsObjRequest);
    }

    public void requestReconcileBags() {
        //http://inkarnepub-prod.elasticbeanstalk.com/Service1.svc/ReconcilesunglassesData/4/m
        final String uri = ConstantsUtil.URL_BASEPATH + ConstantsUtil.URL_METHOD_BAGS + User.getInstance().getmUserId() + "/" + User.getInstance().getmGender();
        Log.d(LOGTAG, "uri :" + uri);

        final JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, uri, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(LOGTAG, "Response: " + response.toString());

                try {
                    JSONArray jsonArray = response.getJSONArray("ReconcileBagsMasterInfoResult");
                    populateAccessoryItem(jsonArray, ConstantsUtil.EAccessoryType.eAccTypeBags.toString());

                } catch (JSONException e) {

                    printError(e, uri);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                printError(error, uri);
            }
        });
        VolleyHelper.getInstance(InkarneAppContext.getAppContext()).addToRequestQueue(jsObjRequest);
    }

    public void requestReconcileClutches() {
        final String uri = ConstantsUtil.URL_BASEPATH + ConstantsUtil.URL_METHOD_CLUTCHES + User.getInstance().getmUserId() + "/" + User.getInstance().getmGender();
        Log.d(LOGTAG, "uri :" + uri);

        final JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, uri, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(LOGTAG, "Response: " + response.toString());

                JSONObject jsonObj = null;
                try {
                    JSONArray jsonArray = response.getJSONArray("ReconcileClutchesMasterInfoResult");
                    populateAccessoryItem(jsonArray, ConstantsUtil.EAccessoryType.eAccTypeClutches.toString());

                } catch (JSONException e) {

                    printError(e, uri);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                printError(error, uri);
            }
        });
        VolleyHelper.getInstance(InkarneAppContext.getAppContext()).addToRequestQueue(jsObjRequest);
    }

    public void requestReconcileSunglasses() {
        //http://inkarnepub-prod.elasticbeanstalk.com/Service1.svc/ReconcilesunglassesData/4/m
        final String uri = ConstantsUtil.URL_BASEPATH + ConstantsUtil.URL_METHOD_SUNGLASSES + User.getInstance().getmUserId() + "/" + User.getInstance().getmGender();
        Log.d(LOGTAG, "uri :" + uri);

        final JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, uri, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(LOGTAG, "Response: " + response.toString());
                try {
                    JSONArray jsonArray = response.getJSONArray("ReconcileSunglassesMasterInfoResult");
                    populateAccessoryItem(jsonArray, ConstantsUtil.EAccessoryType.eAccTypeSunglasses.toString());

                } catch (JSONException e) {

                    printError(e, uri);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                printError(error, uri);
            }
        });
        VolleyHelper.getInstance(InkarneAppContext.getAppContext()).addToRequestQueue(jsObjRequest);
    }

    public void requestReconcileEarrings() {
        final String uri = ConstantsUtil.URL_BASEPATH + ConstantsUtil.URL_METHOD_EARRINGS + User.getInstance().getmUserId() + "/" + User.getInstance().getmGender();
        Log.d(LOGTAG, "uri :" + uri);
        final JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, uri, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(LOGTAG, "Response: " + response.toString());
                try {
                    JSONArray jsonArray = response.getJSONArray("ReconcileEarringsMasterInfoResult");
                    populateAccessoryItem(jsonArray, ConstantsUtil.EAccessoryType.eAccTypeEarrings.toString());

                } catch (JSONException e) {

                    printError(e, uri);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                printError(error, uri);
            }
        });
        VolleyHelper.getInstance(InkarneAppContext.getAppContext()).addToRequestQueue(jsObjRequest);
    }

    public void requestReconcileShoes() {
        final String uri = ConstantsUtil.URL_BASEPATH + ConstantsUtil.URL_METHOD_SHOES + User.getInstance().getmUserId() + "/" + User.getInstance().getmGender();
        Log.d(LOGTAG, "uri :" + uri);

        final JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, uri, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(LOGTAG, "Response: " + response.toString());
                try {
                    JSONArray jsonArray = response.getJSONArray("ReconcileShoesMasterInfoResult");
                    populateAccessoryItem(jsonArray, ConstantsUtil.EAccessoryType.eAccTypeShoes.toString());

                } catch (JSONException e) {

                    printError(e, uri);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                printError(error, uri);
            }
        });
        VolleyHelper.getInstance(InkarneAppContext.getAppContext()).addToRequestQueue(jsObjRequest);
    }


    public void requestFiducialPoints(final String uri, final OnResponseHandlerInterface responseListener) {
        Log.d(LOGTAG, "uri :" + uri);

        final JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, uri, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(LOGTAG, "Response: " + response.toString());

                JSONObject jsonObj = null;
                try {
                    JSONArray fDetails = response.getJSONArray("GetReferenceFidicualsResult");
                    responseListener.onResponse(fDetails);
                } catch (JSONException e) {
                    responseListener.onResponseError(message_Json_Parse_error, CODE_DATA_MANAGER_PARSE_ERROR);
                    printError(e, uri);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                responseListener.onResponseError(message_server_error, 0);
                printError(error, uri);
            }
        });
        VolleyHelper.getInstance(InkarneAppContext.getAppContext()).addToRequestQueue(jsObjRequest);
    }

}
