package com.apps.doctorkeeper_android.util;

// 공통으로 사용하는 메소드

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.print.PrintHelper;

import com.apps.doctorkeeper_android.ui.customer.adapter.FilePrintDocumentAdapter;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Locale;
import java.util.SimpleTimeZone;


public class CommonUtils {
    boolean isInternetWiMax = false;
    boolean isInternetWiFi = false;
    boolean isInternetMobile = false;
    private static CommonUtils current = null;
    AlertDialog.Builder alert = null;

    String arrays[];
    TextView textView;

    //회전정보 받아보기
    public int getOrientationOfImage(String filepath) {
        ExifInterface exif = null;

        try {
            exif = new ExifInterface(filepath);
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }

        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);

        if (orientation != -1) {
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    return 90;

                case ExifInterface.ORIENTATION_ROTATE_180:
                    return 180;

                case ExifInterface.ORIENTATION_ROTATE_270:
                    return 270;
            }
        }

        return 0;
    }

    //비트맵회전
    public static Bitmap getRotatedBitmap(Bitmap bitmap, int degrees) throws Exception {
        if (bitmap == null) return null;
        if (degrees == 0) return bitmap;

        Matrix m = new Matrix();
        m.setRotate(degrees, (float) bitmap.getWidth() / 2, (float) bitmap.getHeight() / 2);

        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
    }


    //파일경로 받아서 파일 리사이즈,회전
    private Uri resize(Context context, String path, int resize) {
        Bitmap resizeBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();
        try {
            BitmapFactory.decodeFile(path, options);

            int width = options.outWidth;
            int height = options.outHeight;
            int samplesize = 1;

            while (true) {//2번
                if (width / 2 < resize || height / 2 < resize)
                    break;
                width /= 2;
                height /= 2;
                samplesize *= 2;
            }

            options.inSampleSize = samplesize;
            File f = new File(path);
            Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(f), null, options); //3번
            resizeBitmap = bitmap;

            int degree = getOrientationOfImage(path);
            try {
                resizeBitmap = getRotatedBitmap(resizeBitmap, degree);
            } catch (Exception e) {
                return null;
            }

            String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
            String tempImg = "gold_" + timeStamp + ".jpg";
            File storageDir = new File(context.getFilesDir() + "/goldcost/"); //clip 경로에 이미지를 저장하기 위함
            if (!storageDir.exists()) {
                storageDir.mkdirs();
            }
            File file = new File(storageDir + "/" + tempImg);

            // OutputStream 선언 -> bitmap데이터를 OutputStream에 받아 File에 넣어주는 용도
            OutputStream out = null;
            try {
                // 파일 초기화
                file.createNewFile();

                // OutputStream에 출력될 Stream에 파일을 넣어준다
                out = new FileOutputStream(file);

                // bitmap 압축
                resizeBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            Uri uriResize = Uri.fromFile(file);

            return uriResize;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return null;

    }


    //uri 로 실제경로 찾기
    public static String getFullPathFromUri(Context ctx, Uri fileUri) {
        String fullPath = null;
        final String column = "_data";
        Cursor cursor = ctx.getContentResolver().query(fileUri, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            String document_id = cursor.getString(0);
            if (document_id == null) {
                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    if (column.equalsIgnoreCase(cursor.getColumnName(i))) {
                        fullPath = cursor.getString(i);
                        break;
                    }
                }
            } else {
                document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
                cursor.close();

                final String[] projection = {column};
                try {
                    cursor = ctx.getContentResolver().query(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            projection, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
                    if (cursor != null) {
                        cursor.moveToFirst();
                        fullPath = cursor.getString(cursor.getColumnIndexOrThrow(column));
                    }
                } finally {
                    if (cursor != null) cursor.close();
                }
            }
        }
        return fullPath;
    }


    public static String getDeviceID(Context context){
        String result = null;
        try {
            String androidId = "" + Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            androidId = Base64.encodeToString(androidId.getBytes("UTF-8"), Base64.DEFAULT);

            result = androidId.trim();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public static String moneyFormatToWon(String inputMoney) {
        DecimalFormat decimalFormat = new DecimalFormat("#,##0");

        int price = 0;
        try {
            price = Integer.parseInt(inputMoney.replace(",", ""));
        } catch (Exception e) {
            price = 0;
        }

        return decimalFormat.format(price) + "";
    }

    public static String moneyFormatToPoint(String inputMoney) {
        DecimalFormat decimalFormat = new DecimalFormat("#,##0");

        int price = 0;
        try {
            price = Integer.parseInt(inputMoney.replace(",", ""));
        } catch (Exception e) {
            price = 0;
        }

        return decimalFormat.format(price) + "코인";
    }

    public static long calDateBetweenAandB(String date1 , String date2)
    {


        try{ // String Type을 Date Type으로 캐스팅하면서 생기는 예외로 인해 여기서 예외처리 해주지 않으면 컴파일러에서 에러가 발생해서 컴파일을 할 수 없다.
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            // date1, date2 두 날짜를 parse()를 통해 Date형으로 변환.
            Date FirstDate = format.parse(date1);
            Date SecondDate = format.parse(date2);

            // Date로 변환된 두 날짜를 계산한 뒤 그 리턴값으로 long type 변수를 초기화 하고 있다.
            // 연산결과 -950400000. long type 으로 return 된다.
            long calDate = FirstDate.getTime() - SecondDate.getTime();

            // Date.getTime() 은 해당날짜를 기준으로1970년 00:00:00 부터 몇 초가 흘렀는지를 반환해준다.
            // 이제 24*60*60*1000(각 시간값에 따른 차이점) 을 나눠주면 일수가 나온다.
            long calDateDays = calDate / ( 24*60*60*1000);

            //calDateDays = Math.abs(calDateDays);


            return calDateDays;
        }
        catch(ParseException e)
        {
            // 예외 처리
        }

        return 0;
    }


    public static String getLevel(String memwr,String memcom) {
        int write = Integer.valueOf(memwr);
        int comm = Integer.valueOf(memcom);

        if(write==0){
            return "LV." +1;
        }else if(write<=1 ){
            return "LV." +2;
        }else if(write<=5){
            return "LV." +3;
        }else if(write<=10 ){
            return "LV." +4;
        }else if(write<=20){
            return "LV." +5;
        }else if(write<=50){
            return "LV." +6;
        }else if(write<=200){
            return "LV." +7;
        }else if(write<=500){
            return "LV." +8;
        }else if(write<=1000){
            return "LV." +9;
        }else if(write<=2000){
            return "LV." +10;
        }

        else{
            return "LV.1";
        }


    }

    public static long DateToMillHH(String date) {
        String pattern = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        Date trans_date = null;
        try {
            trans_date = formatter.parse(date);
        } catch (ParseException e) {
        }
        return trans_date.getTime();
    }

    public static String DateToMillHHAA() {
        Calendar cal = Calendar.getInstance(new SimpleTimeZone(0x1ee6280, "KST"));
        Date date = cal.getTime();

        String pattern = "MM.dd a HH:mm:ss";
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);

        return formatter.format(date.getTime());
    }

    private String getMonthAgoDate() {
        Calendar cal = Calendar.getInstance(new SimpleTimeZone(0x1ee6280, "KST"));
        cal.add(Calendar.MONTH ,1); // 한달전 날짜 가져오기
        Date monthago = cal.getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return formatter.format(monthago);
    }




    public static CommonUtils getInstance() {
        if (current == null) {
            current = new CommonUtils();
        }
        return current;
    }



    public static boolean IsStringInt(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // 아이피 정보
    public String getLocalIpAddress() {
        try {
            for (Enumeration en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = (NetworkInterface) en.nextElement();
                for (Enumeration enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = (InetAddress) enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        if (inetAddress instanceof Inet4Address) {
                            return inetAddress.getHostAddress().toString();
                        }

                    }
                }
            }
        } catch (SocketException ex) {
        }
        return "";
    }




    //네트워크 체크

    public boolean isNetwork(Context context) {
        boolean netCheck = false;
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm.getActiveNetworkInfo() != null) {
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

            switch (activeNetwork.getType()) {
                case ConnectivityManager.TYPE_WIMAX: // 4g
                    isInternetWiMax = true;
                    netCheck = true;
                    break;
                case ConnectivityManager.TYPE_WIFI: // wifi

                    isInternetWiFi = true;
                    netCheck = true;
                    break;
                case ConnectivityManager.TYPE_MOBILE: // 3g

                    isInternetMobile = true;
                    netCheck = true;
                    break;
            }
        } else {
            return false;
        }
        return netCheck;
    }


    public static String getResourseStr(int resource, Context context ) {
        return  context.getResources().getString(resource);

    }

    public void showAlertList(String array[], String title, Context context,
                              TextView txt) {
        arrays = array;
        textView = txt;

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setItems(arrays, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int pos) {
                dialog.dismiss();
                textView.setText(arrays[pos]);

            }
        });
        AlertDialog alert = builder.create();
        alert.show();

    }

    public static void showSnackBar(View view,String msg) {
        Snackbar.make(view , msg , Snackbar.LENGTH_SHORT).show();
    }


    public static void showAlert(String msg, Context context) {
        AlertDialog.Builder adialog = new AlertDialog.Builder(context);
        adialog.setMessage(msg).setTitle("")
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = adialog.create();
        alert.show();
    }

    public static String getVersionName(Context context) {
        try {
            PackageInfo pi = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return pi.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }


    public static void showToast(String msg, Context context) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public static boolean isPackageInstalled(String packageName, PackageManager packageManager) {
        try {
            packageManager.getPackageInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }


    public int getDisplyHeight(Context context) {
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(metrics);
        return metrics.heightPixels;

    }

    public int getDisplyWidth(Context context) {
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(metrics);
        return metrics.widthPixels;

    }


    public boolean checkRooting(){
        boolean check=true;
        try {
            Runtime.getRuntime().exec("su");

        } catch ( Exception e) {
            check=false;
        }

        return check;

    }


    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.AT_MOST);

        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }



    private static class TIME_MAXIMUM{
        public static final int SEC = 60;
        public static final int MIN = 60;
        public static final int HOUR = 24;
        public static final int DAY = 30;
        public static final int MONTH = 12;
    }

    public static String formatTimeString(long regTime) {
        long curTime = System.currentTimeMillis();
        long diffTime = (curTime - regTime) / 1000;
        String msg = null;
        if (diffTime < TIME_MAXIMUM.SEC) {
            msg = "방금 전";
        } else if ((diffTime /= TIME_MAXIMUM.SEC) < TIME_MAXIMUM.MIN) {
            msg = diffTime + "분 전";
        } else if ((diffTime /= TIME_MAXIMUM.MIN) < TIME_MAXIMUM.HOUR) {
            msg = (diffTime) + "시간 전";
        } else if ((diffTime /= TIME_MAXIMUM.HOUR) < TIME_MAXIMUM.DAY) {
            msg = (diffTime) + "일 전";
        } else if ((diffTime /= TIME_MAXIMUM.DAY) < TIME_MAXIMUM.MONTH) {
            msg = (diffTime) + "달 전";
        } else {
            msg = (diffTime) + "년 전";
        }
        return msg;
    }

    // /////////////////////////////////////////////////////////////////

    // //////////////////// density ////////////////////////////////////

    public float getDensity(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(dm);
        float density = dm.density;

        return density;

    }

    public String getTimes(Context context) { //현재시간
        SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat( "yyyy.MM.dd HH:mm:ss", Locale.KOREA );
        Date currentTime = new Date();
        String mTime = mSimpleDateFormat.format ( currentTime );


        return mTime;

    }


    public static String getDateYYmmdd() {
        Calendar cal = Calendar.getInstance(new SimpleTimeZone(0x1ee6280, "KST"));
        Date monthago = cal.getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return formatter.format(monthago);
    }


    public static String getDateHHmmss() {
        Calendar cal = Calendar.getInstance(new SimpleTimeZone(0x1ee6280, "KST"));
        Date monthago = cal.getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-ddHHmmss", Locale.getDefault());
        return formatter.format(monthago);
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public  static void printImage(File pdfFile, Activity activity) {
//        try {
//            PrintDocumentAdapter printAdapter = new FilePrintDocumentAdapter(activity, pdfFile.getPath(), "MyDocument");
//            PrintManager printManager = (PrintManager) activity.getSystemService(Context.PRINT_SERVICE);
//            String jobName =  "document_"+pdfFile.getName();
//            printManager.print(jobName, printAdapter, null); //
//        } catch (Exception  e) {
//            e.printStackTrace();
//        }

        PrintHelper photoPrinter = new PrintHelper(activity);
        photoPrinter.setScaleMode(PrintHelper.SCALE_MODE_FIT);
//        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
//                R.drawable.droids);

        Bitmap bitmap = BitmapFactory.decodeFile(pdfFile.getPath());
        photoPrinter.printBitmap( "document_"+pdfFile.getName(), bitmap);
    }



}

