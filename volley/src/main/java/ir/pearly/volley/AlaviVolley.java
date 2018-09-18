package ir.pearly.volley;


import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import ir.pearly.volley.Volley.AlaviInputStreamVolleyRequest;
import ir.pearly.volley.Volley.AlaviVolleyAppController;
import ir.pearly.volley.Volley.VolleyMultipartRequest;

//  android:name="alavi.Volley.AlaviVolleyAppController" in AndroidManifest.xml


public class AlaviVolley {
    public boolean success = false;
    public Object respons = null;
    public Context context;
    public boolean wait = false;
    public String subname = null;
    public String job = null;
    public String query = null;
    public String siteurl = null;
    public String siteCheckUrl = null;
    public String filename1 = "";
    public String filename2 = "";
    public String dir = "";
    public String fileuploadname = "";
    public Map PostData = null;

    public static final Handler handler = new Handler() {

        @Override
        public void handleMessage(Message mesg) {
            throw new RuntimeException();
        }
    };

    public static void waitstart() {
        try {
            Looper.loop();
        } catch (RuntimeException e) {
        }
    }

    public static void waitstop() {
        handler.sendMessage(handler.obtainMessage());
    }

    public AlaviVolley(Context context) {
        this.context = context;
    }


    public void GetStringRequest(String siteurl, final String subname, String job, final String query, final boolean wait) {
        success = false;
        respons = null;
        this.siteurl = siteurl;
        this.subname = subname;
        this.job = job;
        this.query = query;
        this.wait = wait;

        StringRequest sr = new StringRequest(Request.Method.POST, siteurl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                respons = response;
                success = true;

                if (wait) {
                    waitstop();
                } else {
                    AlaviUtill.callsub(context, subname, 0, response, AlaviVolley.this);
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                respons = error;
                success = false;
                if (wait) {
                    waitstop();
                } else {
                    AlaviUtill.callsub(context, subname, 0, error, AlaviVolley.this);
                }

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                if (PostData == null) {
                    params.put("query", query);
                } else {
                    return PostData;
                }
                return params;

            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };
        AlaviVolleyAppController.getInstance().addToRequestQueue(sr);
        if (wait) {
            waitstart();
        }
        ;
    }

    public void DownloadFile(String siteurl, final String subname, String job, final String dir, final String filename1, final boolean wait) {
        success = false;
        respons = null;
        this.siteurl = siteurl;
        this.subname = subname;
        this.job = job;
        this.dir = dir;
        this.filename1 = filename1;
        this.wait = wait;

        AlaviInputStreamVolleyRequest request = new AlaviInputStreamVolleyRequest(Request.Method.GET, siteurl,
                new Response.Listener<byte[]>() {
                    @Override
                    public void onResponse(byte[] response) {
                        // TODO handle the response
                        try {
                            if (response != null) {
                                InputStream in = new ByteArrayInputStream(response);
                                try {

                                    OutputStream out = new FileOutputStream(dir + "/" + filename1);
                                    try {
                                        // Transfer bytes from in to out
                                        byte[] buf = new byte[1024];
                                        int len;
                                        while ((len = in.read(buf)) > 0) {
                                            out.write(buf, 0, len);
                                        }
                                        success = true;
                                    } finally {
                                        out.close();
                                    }

                                } finally {
                                    in.close();
                                }

                            }
                        } catch (Exception e) {
                            // TODO Auto-generated catch block

                            e.printStackTrace();
                        }

                        success = true;
                        if (wait) {
                            waitstop();
                        } else {
                            AlaviUtill.callsub(context, subname, 0, null, AlaviVolley.this);
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO handle the error
                success = false;
                respons = error;
                if (wait) {
                    waitstop();
                } else {
                    AlaviUtill.callsub(context, subname, 0, error, AlaviVolley.this);
                }


            }
        }, null);


        AlaviVolleyAppController.getInstance().addToRequestQueue(request);
        if (wait) {
            waitstart();
        }
        ;
    }

    public boolean CheckInternet(String siteCheckUrl) {
        success = false;
        respons = null;
        this.siteCheckUrl = siteCheckUrl;
        String mUrl = siteCheckUrl;
        AlaviInputStreamVolleyRequest request = new AlaviInputStreamVolleyRequest(Request.Method.GET, mUrl,
                new Response.Listener<byte[]>() {
                    @Override
                    public void onResponse(byte[] response) {
                        // TODO handle the response
                        try {
                            if (response != null) {
                                success = true;
                                waitstop();
                            }
                        } catch (Exception e) {
                            // TODO Auto-generated catch block

                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO handle the error
                success = false;
                respons = error;
                waitstop();


            }
        }, null);

        AlaviVolleyAppController.getInstance().addToRequestQueue(request);
        waitstart();
        return success;
    }


    public static String Encrypt(String str, String code) throws Exception {
        int strlen = str.length() - 1;
        int lenSplit = 1000;
        int codelen = code.length();
        int k = strlen / lenSplit;
        k++;
        String[] strsplit = new String[k];
        String Encrypt1 = "";
        int j = 0, i = 0;
        for (i = 0; i <= strlen; i++) {
            char s = (str.charAt(i));
            char len = (code.charAt((i + 7) % codelen));
            Encrypt1 = Encrypt1 + ((char) ((int) s + (int) (Double.parseDouble(String.valueOf(len)))));
            if (i % lenSplit == 0) {
                j = i / lenSplit;

                strsplit[j] = Encrypt1;
                Encrypt1 = "";
            }

        }

        String s2 = "";
        for (i = 0; i <= j; i++) {
            s2 = s2 + strsplit[i];
            Encrypt1 = s2 + Encrypt1;
        }


        return Encrypt1;
    }

    public static String Decrypt(String str, String code) throws Exception {
        int strlen = str.length() - 1;
        int lenSplit = 1000;
        int codelen = code.length();
        int k = strlen / lenSplit;
        k++;
        String[] strsplit = new String[k];
        String Decrypt1 = "";
        int j = 0, i = 0;
        for (i = 0; i <= strlen; i++) {
            char s = (str.charAt(i));
            char len = (code.charAt((i + 7) % codelen));
            Decrypt1 = Decrypt1 + ((char) ((int) s - (int) (Double.parseDouble(String.valueOf(len)))));
            if (i % lenSplit == 0) {
                j = i / lenSplit;

                strsplit[j] = Decrypt1;
                Decrypt1 = "";
            }

        }

        String s2 = "";
        for (i = 0; i <= j; i++) {
            s2 = s2 + strsplit[i];
            Decrypt1 = s2 + Decrypt1;
        }


        return Decrypt1;
    }


    public void UploadFile(String siteurl, final String subname, String job, final String dir, final String filename1, final String fileuploadname, final boolean wait) {
        success = false;
        respons = null;
        this.siteurl = siteurl;
        this.subname = subname;
        this.job = job;
        this.dir = dir;
        this.filename1 = filename1;
        this.fileuploadname = fileuploadname;
        this.wait = wait;


        //our custom volley request
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, siteurl,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {

                        try {

                            success = true;
                            //InputStream in = new ByteArrayInputStream(response.data);
                            JSONObject obj = new JSONObject(new String(response.data));
                            respons = obj;
                            if (wait) {
                                waitstop();
                            } else {
                                AlaviUtill.callsub(context, subname, 0, obj, AlaviVolley.this);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        respons = error;
                        success = false;
                        if (wait) {
                            waitstop();
                        } else {
                            AlaviUtill.callsub(context, subname, 0, error, AlaviVolley.this);
                        }

                    }
                }) {

            /*
             * If you want to add more parameters with the image
             * you can do it here
             * here we have only one parameter with the image
             * which is tags
             * */
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("getdata", "UploadFile");
                params.put("FileName", fileuploadname);
                return params;
            }

            /*
             * Here we are passing image by renaming it with a unique name
             * */
            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long imagename = System.currentTimeMillis();

                params.put("FileData", new DataPart("fileup", getStringFile()));
                return params;
            }
        };

        //adding the request to volley

        AlaviVolleyAppController.getInstance().addToRequestQueue(volleyMultipartRequest);
        if (wait) {
            waitstart();
        }
        ;
    }

    public byte[] getStringFile() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            FileInputStream fis = new FileInputStream(dir + "/" + filename1);

            byte[] b = new byte[1024];


            for (int readNum; (readNum = fis.read(b)) != -1; ) {
                bos.write(b, 0, readNum);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        byte[] bytes = bos.toByteArray();

        return bytes;
    }

    public void UploadFile_getdownload(String siteurl, final String subname, String job, final String dir, final String filename1, final String fileuploadname, final String filename2, final boolean wait) {
        success = false;
        respons = null;
        this.siteurl = siteurl;
        this.subname = subname;
        this.job = job;
        this.dir = dir;
        this.filename1 = filename1;
        this.filename2 = filename2;
        this.fileuploadname = fileuploadname;
        this.wait = wait;


        //our custom volley request
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, siteurl,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {

                        try {
                            if (response != null) {
                                InputStream in = new ByteArrayInputStream(response.data);
                                try {

                                    OutputStream out = new FileOutputStream(dir + "/" + filename2);
                                    try {
                                        // Transfer bytes from in to out
                                        byte[] buf = new byte[1024];
                                        int len;
                                        while ((len = in.read(buf)) > 0) {
                                            out.write(buf, 0, len);
                                        }
                                        success = true;
                                    } finally {
                                        out.close();
                                    }

                                } finally {
                                    in.close();
                                }

                            }
                        } catch (Exception e) {
                            // TODO Auto-generated catch block

                            e.printStackTrace();
                        }
                        if (wait) {
                            waitstop();
                        } else {
                            AlaviUtill.callsub(context, subname, 0, null, AlaviVolley.this);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        respons = error;
                        success = false;
                        if (wait) {
                            waitstop();
                        } else {
                            AlaviUtill.callsub(context, subname, 0, error, AlaviVolley.this);
                        }
                    }
                }) {

            /*
             * If you want to add more parameters with the image
             * you can do it here
             * here we have only one parameter with the image
             * which is tags
             * */
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("getdata", "UploadFile");
                params.put("FileName", fileuploadname);
                return params;
            }

            /*
             * Here we are passing image by renaming it with a unique name
             * */
            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long imagename = System.currentTimeMillis();

                params.put("FileData", new DataPart("fileup", getStringFile()));
                return params;
            }
        };

        //adding the request to volley

        AlaviVolleyAppController.getInstance().addToRequestQueue(volleyMultipartRequest);
        if (wait) {
            waitstart();
        }
        ;
    }


}
