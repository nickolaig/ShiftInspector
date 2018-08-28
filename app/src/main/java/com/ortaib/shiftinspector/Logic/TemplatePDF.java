package com.ortaib.shiftinspector.Logic;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.ortaib.shiftinspector.Activities.ViewPdfActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Ortaib on 17/08/2018.
 */

public class TemplatePDF  {
    private Context context;
    private File pdfFile;
    private PdfWriter pdfWriter;
    private Document document;
    private String path;
    private Paragraph paragraph;
    private Font fTitle = new Font(Font.FontFamily.TIMES_ROMAN,20, Font.BOLD);
    private Font fSubtitle = new Font(Font.FontFamily.TIMES_ROMAN,18, Font.BOLD);
    private Font fText = new Font(Font.FontFamily.TIMES_ROMAN,12, Font.BOLD);
    private Font fHighText = new Font(Font.FontFamily.TIMES_ROMAN,15, Font.BOLD, BaseColor.RED);

    public TemplatePDF(Context c,String path){
        this.context = c;
        this.path = path;
    }
    public void openDocument(){
        createFile();
        try{
            document = new Document(PageSize.A4);
            pdfWriter = PdfWriter.getInstance(document,new FileOutputStream(pdfFile));
            document.open();
        }catch(Exception e){
            Log.e("openDocument: ",e.toString() );
        }
    }
    public void createFile(){

        File folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),"PDF");
        if(!folder.exists()) {
            folder.mkdir();
        }
            pdfFile = new File(folder,path+".pdf");
    }
    public void closeDocument(){
        document.close();
    }
    public void addMetaData(String title,String subject,String author){
         document.addTitle(title);
         document.addSubject(subject);
         document.addAuthor(author);
    }
    public void addTitle(String title){
        try{
            paragraph = new Paragraph();
            addChild(new Paragraph(title, fTitle));
            paragraph.setSpacingAfter(30);
            document.add(paragraph);

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void addSummary(String[] header,String[] summary){
        paragraph = new Paragraph();
        paragraph.setFont(fText);
        PdfPTable pdfPTable = new PdfPTable(header.length);
        pdfPTable.setWidthPercentage(100);
        PdfPCell pdfPCell;
        int index=0;
        while(index<header.length){
            pdfPCell = new PdfPCell(new Phrase(header[index++],fSubtitle));
            pdfPCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            pdfPCell.setBackgroundColor(BaseColor.GREEN);
            pdfPTable.addCell(pdfPCell );
        }
        index=0;
        while(index < summary.length){
            pdfPCell = new PdfPCell(new Phrase(summary[index++]));
            pdfPCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            pdfPCell.setFixedHeight(40);
            pdfPTable.addCell(pdfPCell);
        }
        try {
            paragraph.add(pdfPTable);
            paragraph.setSpacingAfter(30);
            document.add(paragraph);
        }catch (Exception e){
            Log.e("createTable: ",e.toString() );
        }
    }
    public void createTable(String[] header,ArrayList<Shift> shifts){
        paragraph = new Paragraph();
        paragraph.setFont(fText);
        PdfPTable pdfPTable = new PdfPTable(header.length);
        pdfPTable.setWidthPercentage(100);
        PdfPCell pdfPCell;
        int index=0;
        while(index<header.length){
            pdfPCell = new PdfPCell(new Phrase(header[index++],fSubtitle));
            pdfPCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            pdfPCell.setBackgroundColor(new BaseColor(3,169,244));
            pdfPTable.addCell(pdfPCell );
        }
        for (int i = 0; i < shifts.size(); i++) {
            pdfPCell = new PdfPCell(new Phrase(shifts.get(i).getStartTime().toString()));
            pdfPCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            pdfPCell.setFixedHeight(30);
            pdfPTable.addCell(pdfPCell);
            pdfPCell = new PdfPCell(new Phrase(shifts.get(i).getEndTime().toString()));
            pdfPCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            pdfPCell.setFixedHeight(30);
            pdfPTable.addCell(pdfPCell);
            pdfPCell = new PdfPCell(new Phrase(Double.toString(shifts.get(i).getMoneyEarned())));
            pdfPCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            pdfPCell.setFixedHeight(30);
            pdfPTable.addCell(pdfPCell);
        }

        /*for(int indexRow=0;indexRow < clients.size();indexRow++){
            String[] row = clients.get(indexRow);
            for(indexC=0;indexC<row.length;indexC++){
                pdfPCell = new PdfPCell(new Phrase(row[indexC]));
                pdfPCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                pdfPCell.setFixedHeight(40);
                pdfPTable.addCell(pdfPCell);
            }
        }*/
        try {
            paragraph.add(pdfPTable);
            document.add(paragraph);
        }catch (Exception e){
            Log.e("createTable: ",e.toString() );
        }
    }
    public void addTitles(String title,String subTitle,String date){
        try {
            paragraph = new Paragraph();
            addChild(new Paragraph(title, fTitle));
            addChild(new Paragraph(subTitle, fSubtitle));
            addChild(new Paragraph(date, fHighText));
            paragraph.setSpacingAfter(30);
            document.add(paragraph);
        }catch(Exception e){
            Log.e("addTitle: ",e.toString() );
        }

    }
    private void addChild(Paragraph childParagraph){
        childParagraph.setAlignment(Element.ALIGN_CENTER);
        paragraph.add(childParagraph);
    }
    public void addParagraph(String text){
        try {
            paragraph = new Paragraph(text, fText);
            paragraph.setSpacingAfter(5);
            paragraph.setSpacingBefore(5);
            document.add(paragraph);
        }catch(Exception e){
            Log.e( "addParagraph: ",e.toString() );
        }
    }

    public void viewPDF(){
        Intent intent = new Intent(context, ViewPdfActivity.class);
        intent.putExtra("path",pdfFile.getAbsolutePath());
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

}
