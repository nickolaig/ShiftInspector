package com.ortaib.shiftinspector.Activities;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.ortaib.shiftinspector.R;

import java.io.File;

public class ViewPdfActivity extends AppCompatActivity {
    private static final String TAG = "ViewPdfActivity";
    private PDFView pdfView;
    private File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pdf);
        pdfView = (PDFView)findViewById(R.id.pdf_viewer);
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            file = new File(bundle.getString("path"));
        }
        pdfView.fromFile(file)
                .enableSwipe(true)
                .swipeHorizontal(false)
                .enableDoubletap(true)
                .enableAntialiasing(true)
                .load();
        Toast.makeText(this,"Saved in Downloads/PDF",Toast.LENGTH_LONG).show();

    }
}
