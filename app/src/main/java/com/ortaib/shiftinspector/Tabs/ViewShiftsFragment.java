package com.ortaib.shiftinspector.Tabs;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.ortaib.shiftinspector.Activities.MainActivity;
import com.ortaib.shiftinspector.Logic.Shift;
import com.ortaib.shiftinspector.Logic.ShiftAdapter;
import com.ortaib.shiftinspector.Logic.TemplatePDF;
import com.ortaib.shiftinspector.R;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;

import static android.content.Context.LOCATION_SERVICE;


public class ViewShiftsFragment extends Fragment {
    private static final String TAG = "ViewShiftsFragment";
    private final String[] months = {"January","Fabruary","March","April","May","June","July","August"
    ,"September","October","November","December"};
    private ImageButton nextBtn,backBtn,pdfBtn;
    private TextView monthTextView,fillPayout,fillHours,fillShifts;
    private Context context;
    ArrayList<Shift> shiftList;
    double hours = 0.0;
    double payout = 0.0;
    private int selectedMonth,selectedYear;
    private String emailKey;
    private String[] summary = {"Shifts","Hours","Money Earned"};
    private String[] header = {"Started","Finished","Money Earned"};
    private TemplatePDF templatePDF;



    private ListView listView;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference mDatabase;
    private ViewShiftFragmentListener listener;
    private LocationManager locationManager;

    public interface ViewShiftFragmentListener{
        void onLocationSend(Double longitude,Double latitude,Double fLon,Double fLat);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_view_shifts,container,false);
        context = getActivity();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        emailKey = currentUser.getEmail().replace(".","_dot_");
        nextBtn = (ImageButton) view.findViewById(R.id.next_btn);
        backBtn = (ImageButton) view.findViewById(R.id.back_btn);
        monthTextView = (TextView) view.findViewById(R.id.month);
        fillPayout = (TextView) view.findViewById(R.id.fill_payout);
        fillHours = (TextView) view.findViewById(R.id.fill_hours);
        fillShifts = (TextView) view.findViewById(R.id.fill_shifts);
        listView = (ListView) view.findViewById(R.id.list_view);
        pdfBtn = (ImageButton) view.findViewById(R.id.export_to_pdf);
        Calendar cal = Calendar.getInstance();
        selectedMonth = cal.get(Calendar.MONTH)+1;
        selectedYear = cal.get(Calendar.YEAR);
        monthTextView.setText(months[selectedMonth-1]+" "+selectedYear);
        setListener();

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedMonth--;
                if (selectedMonth == 0) {
                    selectedMonth = 12;
                    selectedYear--;
                }
                monthTextView.setText(months[selectedMonth - 1] + " " + selectedYear);

                setListener();
            }
                });
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedMonth++;
                if (selectedMonth > 12) {
                    selectedMonth = 1;
                    selectedYear++;
                }
                monthTextView.setText(months[selectedMonth - 1] + " " + selectedYear);
                setListener();
            }
        });
        pdfBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                templatePDF = new TemplatePDF(context,months[selectedMonth-1]+"_"+selectedYear);
                templatePDF.openDocument();
                templatePDF.addMetaData("Shifts","shift inspection : "+months[selectedMonth-1],"ShiftInspector");
                templatePDF.addTitles(" Shift Inspection",currentUser.getEmail(),months[selectedMonth-1]+" "+selectedYear);
                templatePDF.addTitle("Summary");
                templatePDF.addSummary(summary,new String[]{Integer.toString(shiftList.size()),Double.toString(hours),Double.toString(payout)});
                templatePDF.createTable(header,shiftList);
                templatePDF.closeDocument();
                templatePDF.viewPDF();
                //templatePDF.appViewPDF(getActivity());

            }
        });

        return view;
    }
    public void setListener(){
        listView.setAdapter(null);
        listView.setOnItemClickListener(null);
        fillHours.setText("");
        fillPayout.setText("");
        fillShifts.setText("");
        mDatabase = FirebaseDatabase.getInstance().getReference().child("shifts").child(emailKey)
                .child(Integer.toString(selectedYear)).child(Integer.toString(selectedMonth));
        final int queryMonth = selectedMonth;
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(queryMonth == selectedMonth) {
                    hours = 0.0;
                    payout = 0.0;
                    shiftList = new ArrayList<>();
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        Shift shift = ds.getValue(Shift.class);
                        if (shift.getEndTime() != null) {
                            hours += shift.getTimeWorked();
                            payout += shift.getMoneyEarned();
                        }
                        shiftList.add(shift);
                    }
                    if (!shiftList.isEmpty()) {
                        fillHours.setText(Double.toString((Math.round(hours*100))/100.0));
                        fillPayout.setText(Double.toString((Math.round(payout*100))/100.0) + " NIS");
                        fillShifts.setText(Long.toString(dataSnapshot.getChildrenCount()));

                        ShiftAdapter adapter = new ShiftAdapter(context, shiftList);
                        listView.setAdapter(adapter);
                        setListviewListener();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void setListviewListener(){
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(shiftList.get(position).getStartLat()!= null) {

                    listener.onLocationSend(shiftList.get(position).getStartLon(),shiftList.get(position).getStartLat()
                    ,shiftList.get(position).getFinishLon(),shiftList.get(position).getFinishLat());
                }
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),
                        android.R.style.Theme_Material_Dialog_Alert);
                builder.setTitle("Do you want to delete this shift?");
                builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mDatabase = FirebaseDatabase.getInstance().getReference().child("shifts").child(emailKey)
                                .child(Integer.toString(selectedYear)).child(Integer.toString(selectedMonth))
                                .child(shiftList.get(position).getId());
                        mDatabase.removeValue();
                        Toast.makeText(context,"Shift removed",Toast.LENGTH_LONG).show();
                        setListener();
                    }
                });
                builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.setIcon(android.R.drawable.ic_dialog_alert);
                builder.show();
                return false;
            }
        });
    }
    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof ViewShiftFragmentListener){
            listener = (ViewShiftFragmentListener) context;
        }else{
            throw new RuntimeException(context.toString()
                    + " must implement fragment listener");
        }
    }




}
