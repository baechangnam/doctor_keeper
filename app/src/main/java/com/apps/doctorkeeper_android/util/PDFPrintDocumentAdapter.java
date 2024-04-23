package com.apps.doctorkeeper_android.util;

import android.content.Context;
import android.graphics.pdf.PdfRenderer;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.pdf.PrintedPdfDocument;

import androidx.annotation.RequiresApi;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public class PDFPrintDocumentAdapter extends PrintDocumentAdapter {
    private PrintedPdfDocument pdfDocument = null;
    private Context context = null;
    private File pdfFile = null;
    private int totalPages = 0;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public PDFPrintDocumentAdapter(Context context, File pdfFile) throws IOException {
        this.pdfFile = pdfFile;
        this.context = context;
        this.totalPages = this.getPrintItemCount(pdfFile);
    }

    @Override
    public void onLayout(PrintAttributes oldAttributes, PrintAttributes newAttributes, CancellationSignal cancellationSignal, LayoutResultCallback callback, Bundle metadata) {
        pdfDocument = new PrintedPdfDocument(this.context, newAttributes);
        if (cancellationSignal.isCanceled() ) {
            callback.onLayoutCancelled();
            return;
        }
        PrintDocumentInfo info = new PrintDocumentInfo
                .Builder(pdfFile.getName())
                .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                .setPageCount(this.totalPages)
                .build();
        callback.onLayoutFinished(info, oldAttributes != newAttributes);
    }

    @Override
    public void onWrite(PageRange[] pageRanges, ParcelFileDescriptor destination, CancellationSignal cancellationSignal, WriteResultCallback callback) {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            inputStream = new FileInputStream(pdfFile);
            outputStream = new FileOutputStream(destination.getFileDescriptor());
            byte[] buf = new byte[16384];
            int size;
            while ((size = inputStream.read(buf)) >= 0
                    && !cancellationSignal.isCanceled()) {
                outputStream.write(buf, 0, size);
            }
            outputStream.flush();
            if (cancellationSignal.isCanceled()) {
                callback.onWriteCancelled();
            } else {
                callback.onWriteFinished(new PageRange[] { PageRange.ALL_PAGES });
            }
        } catch (Exception e) {
            callback.onWriteFailed(e.getMessage());
        } finally {
            try {
                if(inputStream != null)  inputStream.close();
            }  catch (IOException e) { }
            try {
                if(outputStream != null)  outputStream.close();
            }  catch (IOException e) { }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private int getPrintItemCount(File pdfFile) throws IOException {
        int totalpages = 0;
        PdfRenderer pdfRenderer = null;
        ParcelFileDescriptor parcelFileDescriptor = null;
        try {
            parcelFileDescriptor = ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                pdfRenderer = new PdfRenderer(parcelFileDescriptor);
                totalpages = pdfRenderer.getPageCount();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if(pdfRenderer != null) {
                pdfRenderer.close();
            }
            if(parcelFileDescriptor != null) {
                parcelFileDescriptor.close();
            }
        }
        return totalpages;
    }
}
