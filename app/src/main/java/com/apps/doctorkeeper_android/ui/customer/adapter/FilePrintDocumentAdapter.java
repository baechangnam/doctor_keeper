package com.apps.doctorkeeper_android.ui.customer.adapter;

import android.content.Context;
import android.graphics.pdf.PdfDocument;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.PrintManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class FilePrintDocumentAdapter extends PrintDocumentAdapter {

    private Context context;
    private String filePath;
    private String documentName;

    public FilePrintDocumentAdapter(Context context, String filePath, String documentName) {
        this.context = context;
        this.filePath = filePath;
        this.documentName = documentName;
    }

    @Override
    public void onLayout(PrintAttributes oldAttributes, PrintAttributes newAttributes,
                         CancellationSignal cancellationSignal, LayoutResultCallback callback,
                         Bundle extras) {
        if (cancellationSignal.isCanceled()) {
            callback.onLayoutCancelled();
            return;
        }

        PrintDocumentInfo info = new PrintDocumentInfo.Builder(documentName)
                .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                .build();

        callback.onLayoutFinished(info, true);
    }

    @Override
    public void onWrite(PageRange[] pages, ParcelFileDescriptor destination,
                        CancellationSignal cancellationSignal, WriteResultCallback callback) {
        new WriteFileAsyncTask(destination, callback).execute();
    }

    private class WriteFileAsyncTask extends AsyncTask<Void, Void, Void> {
        private ParcelFileDescriptor destination;
        private WriteResultCallback callback;

        public WriteFileAsyncTask(ParcelFileDescriptor destination, WriteResultCallback callback) {
            this.destination = destination;
            this.callback = callback;
        }

        @Override
        protected Void doInBackground(Void... params) {
            // Generate the print content here

            PdfDocument pdfDocument = new PdfDocument();
            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create();
            PdfDocument.Page page = pdfDocument.startPage(pageInfo);

            try {
                InputStream inputStream = new FileInputStream(filePath);
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                float y = 50; // Vertical position to start printing

                while ((line = reader.readLine()) != null) {
                    page.getCanvas().drawText(line, 50, y, null);
                    y += 20; // Increment for the next line
                }

                pdfDocument.finishPage(page);
                pdfDocument.writeTo(new FileOutputStream(destination.getFileDescriptor()));
                pdfDocument.close();
                inputStream.close();
                callback.onWriteFinished(new PageRange[]{PageRange.ALL_PAGES});
            } catch (IOException e) {
                Log.e("PrintAdapter", "Error writing file", e);
                callback.onWriteFailed(e.getMessage());
            }

            return null;
        }
    }
}