package com.example.daniel.proyectobiblioteca;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.pdf.PrintedPdfDocument;
import android.support.annotation.RequiresApi;

import com.example.daniel.proyectobiblioteca.POJOS.Lectura;

import java.io.FileOutputStream;
import java.io.IOException;

@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public class Imprimir extends PrintDocumentAdapter {

    Lectura lectura;
    Context context;
    private int pageHeight;
    private int pageWidth;
    public PdfDocument myPdfDocument;
    public int totalpages = 1;


    public Imprimir(Context context, Lectura lectura) {
        this.context = context;
        this.lectura = lectura;
    }

    @Override
    public void onLayout(PrintAttributes oldAttributes, PrintAttributes newAttributes, CancellationSignal cancellationSignal, LayoutResultCallback callback, Bundle extras) {

        myPdfDocument = new PrintedPdfDocument(context, newAttributes);
        pageHeight = newAttributes.getMediaSize().getHeightMils() / 1000 * 72;
        pageWidth = newAttributes.getMediaSize().getWidthMils() / 1000 * 72;


        if (cancellationSignal.isCanceled()) {

            callback.onLayoutCancelled(); //Si el usuario cancela la impresión.

            return;

        }

        //Importante (si el layout no controla esto)

        if (totalpages > 0) {

            /* Creamos un documento info, siempre será así. Este llevará los metadatos del archivo. */

            PrintDocumentInfo.Builder builder = new PrintDocumentInfo
                    .Builder("print_output.pdf") //Nombre del documento con el que hemos decidio guardarlo
                    .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT) //Se trata de un documento
                    .setPageCount(totalpages); //Numpáginas

            PrintDocumentInfo info = builder.build();

            callback.onLayoutFinished(info, true); //Metadatos + true (ha habido cambios)

        } else {

            callback.onLayoutFailed("Page count is zero.");

        }
    }

    @Override
    public void onWrite(PageRange[] pageRanges, ParcelFileDescriptor destination, CancellationSignal cancellationSignal, WriteResultCallback callback) {
        for (int i = 0; i < totalpages; i++) {

            if (pageInRange(pageRanges, i)) { //Controlar el rango de páginas

                PdfDocument.PageInfo newPage = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, i).create(); //Esto no es obligatorio, se puede hacer de otro modo
                //Ver web de android

                PdfDocument.Page page = myPdfDocument.startPage(newPage);

                if (cancellationSignal.isCanceled()) {

                    callback.onWriteCancelled();
                    myPdfDocument.close();
                    myPdfDocument = null;

                    return;
                }

                drawPage(page, i);
                myPdfDocument.finishPage(page);
            }
        }

        try {

            myPdfDocument.writeTo(new FileOutputStream(destination.getFileDescriptor()));

        } catch (IOException e) {

            callback.onWriteFailed(e.toString());
            return;

        } finally {

            myPdfDocument.close();
            myPdfDocument = null;

        }

        callback.onWriteFinished(pageRanges);
    }

    private boolean pageInRange(PageRange[] pageRanges, int page) {

        for (int i = 0; i < pageRanges.length; i++) {

            if ((page >= pageRanges[i].getStart()) && (page <= pageRanges[i].getEnd()))

                return true;
        }

        return false;
    }

    private void drawPage(PdfDocument.Page page, int nPaginas) {

        Canvas canvas = page.getCanvas();

        nPaginas++;

        int titleBaseLine = 72;
        int leftMargin = 54;

        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(30);
        canvas.drawText(lectura.getTitulo(), leftMargin, titleBaseLine, paint);
        paint.setTextSize(24);
        titleBaseLine += 25;
        canvas.drawText(context.getResources().getString(R.string.title) + " " + lectura.getTitulo(), leftMargin, titleBaseLine, paint);
        titleBaseLine += 25;
        canvas.drawText(context.getResources().getString(R.string.autor) + " " + lectura.getAutor(), leftMargin, titleBaseLine, paint);
        titleBaseLine += 25;
        canvas.drawText(context.getResources().getString(R.string.resume) + " " + lectura.getResumen(), leftMargin, titleBaseLine, paint);

        PdfDocument.PageInfo pageInfo = page.getInfo();
    }
}