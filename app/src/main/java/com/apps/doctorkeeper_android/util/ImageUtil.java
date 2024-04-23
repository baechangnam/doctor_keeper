package com.apps.doctorkeeper_android.util;

import static com.apps.doctorkeeper_android.util.CommonUtils.getDateHHmmss;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.graphics.pdf.PdfRenderer;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.Objects;

public class ImageUtil {

    public static int checkRotate(String imagePath) {
        int rotationInDegrees = 0;
        try {
            ExifInterface exif = new ExifInterface(imagePath);
            int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            rotationInDegrees = exifToDegrees(rotation);


        } catch (Exception e) {
            return 0;
        }

        return rotationInDegrees;

    }

    public static Bitmap RotateBitmap(Bitmap source, int angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    private static int exifToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }

    public static File createImageFile(Activity mActivity, String custNo) {
        String filename = getSaveFilename(custNo);
        File storageDir = new File(mActivity.getFilesDir() + "/image");

        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }

        File image = new File(storageDir.getPath(), filename);
        try {
            image.createNewFile();
        } catch (Exception ex) {
        }

        // Save a file: path for use with ACTION_VIEW intents
        return image;
    }

    private static String getSaveFilename(String custNo) {
        return getDateHHmmss() + "-" + custNo + ".jpg";
    }


    @SuppressLint("NewApi")
    public static String getPath(Context context, Uri uri) throws URISyntaxException {
        final boolean needToCheckUri = Build.VERSION.SDK_INT >= 19;
        String selection = null;
        String[] selectionArgs = null;
        // Uri is different in versions after KITKAT (Android 4.4), we need to
        // deal with different Uris.
        if (needToCheckUri && DocumentsContract.isDocumentUri(context.getApplicationContext(), uri)) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    return context.getExternalFilesDir((Environment.DIRECTORY_PICTURES)) + "/" + split[1];
                } else {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

            } else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                uri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
            } else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("image".equals(type)) {
                    uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                selection = "_id=?";
                selectionArgs = new String[]{split[1]};
            }
        }
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {MediaStore.Images.Media.DATA};
            Cursor cursor = null;
            try {
                cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }


    public static void takeScreenshot(View v) {
        Date now = new Date();
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);
        try {
            String fileName = "doctorkeeper_android_scshot_" + now + ".jpg";
            View v1 = v;
            v1.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            v1.setDrawingCacheEnabled(false);

            int quality = 100;

            OutputStream fos;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ContentResolver resolver = v1.getContext().getContentResolver();
                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
                contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg");
                contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);
                Uri imageUri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues);
                fos = resolver.openOutputStream(Objects.requireNonNull(imageUri));
            } else {
                String imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
                File image = new File(imagesDir, fileName);
                fos = new FileOutputStream(image);
            }

            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, fos);
            fos.flush();
            fos.close();

            CommonUtils.showToast("이미지가 캡쳐 되었습니다.", v.getContext());

        } catch (IOException e) {
            e.printStackTrace();
        }



    }

    public static String mergeImage(View v) {
        try {
            String fileName = "doctorkeeper_android_mergeImage_" + CommonUtils.getDateHHmmss() + ".jpg";
            View v1 = v;
            v1.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            v1.setDrawingCacheEnabled(false);

            int quality = 100;

            OutputStream fos;
            //String imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
            File filePath = new File(v.getContext().getFilesDir()+"/merge/");
            if(!filePath.exists()){
                filePath.mkdir();
            }
            File image = new File(filePath, fileName);
            fos = new FileOutputStream(image);

            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, fos);
            fos.flush();
            fos.close();

            return image.getPath();

        } catch (IOException e) {
            e.printStackTrace();
        }


        return null;


    }

    public static String editImage(View v) {
        try {
            String fileName = "doctorkeeper_android_editImage_" + CommonUtils.getDateHHmmss() + ".jpg";
            View v1 = v;
            v1.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            v1.setDrawingCacheEnabled(false);

            int quality = 100;

            OutputStream fos;
            //String imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
            File filePath = new File(v.getContext().getFilesDir()+"/edit/");
            if(!filePath.exists()){
                filePath.mkdir();
            }
            File image = new File(filePath, fileName);
            fos = new FileOutputStream(image);

            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, fos);
            fos.flush();
            fos.close();

            return image.getPath();

        } catch (IOException e) {
            e.printStackTrace();
        }


        return null;


    }

    public static Bitmap pdfToBitmap(Context context, File pdfFile, int pageNumber) {
        try {
            ParcelFileDescriptor fileDescriptor = ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_WRITE);
            PdfRenderer pdfRenderer = new PdfRenderer(fileDescriptor);
            PdfRenderer.Page page = pdfRenderer.openPage(pageNumber);

            int rendererPageWidth = page.getWidth();

            int rendererPageHeight = page.getHeight();

            int width = context.getResources().getDisplayMetrics().widthPixels;
            int height = (width * rendererPageHeight) /
                    (rendererPageWidth);

            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            canvas.drawColor(context.getColor(android.R.color.white));

            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_PRINT);

            page.close();
            pdfRenderer.close();
            fileDescriptor.close();

            return bitmap;
        } catch (IOException e) {
            Log.e("PdfToBitmapConverter", "Error converting PDF to Bitmap", e);
            return null;
        }
    }

    public static  Bitmap getBitmapFromView(View view, int totalHeight, int totalWidth) {

        Bitmap returnedBitmap = Bitmap.createBitmap(totalWidth, totalHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null)
            bgDrawable.draw(canvas);
        else
            canvas.drawColor(Color.WHITE);
        view.draw(canvas);
        return returnedBitmap;
    }


    public static void resizeBitmap(Uri imageUri, String imagePath, Activity activity, String mPhotoFile) {
        try {
            int quality = 0;

            InputStream input = activity.getContentResolver().openInputStream(imageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(new BufferedInputStream(input));

            int rot = checkRotate(imagePath);
            try {
                bitmap = RotateBitmap(bitmap, rot);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (bitmap.getWidth() > 2048 && bitmap.getHeight() > 2048) {
                quality = 20;
            } else if (bitmap.getWidth() > 1024 && bitmap.getHeight() > 1024) {
                quality = 50;
            } else {
                quality = 80;
            }

            try (OutputStream output = new FileOutputStream(mPhotoFile)) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, output);
                output.flush();
            } catch (Exception e) {

            }

        //    fileUpload();


        } catch (Exception e) {

        }

    }


}
