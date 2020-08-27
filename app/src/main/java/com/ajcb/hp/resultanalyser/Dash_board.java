package com.ajcb.hp.resultanalyser;


import androidx.appcompat.app.AppCompatActivity;


import com.ezydev.bigscreenshot.BigScreenshot;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintJob;
import android.print.PrintManager;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import static com.ajcb.hp.resultanalyser.MainActivity.ArrOfArr;
public class Dash_board extends AppCompatActivity implements BigScreenshot.ProcessScreenshot {
    List subjects = new ArrayList<>();

    TextView Totalstudents,pass1,distinction,firstclass,fail1;
    Button generatepdf,nextpage;
    WebView webView1,webView2;


    int Distinction = 0,First_Class = 0,Pass_ = 0,Fail_ = 0,totalstudents = 0;
    int pass = 0,fail = 0;
    ArrayList<BarEntry> Subjectattper = new ArrayList<>();


    private ScrollView scrollView;

    private LinearLayout parentlinearlayout;
    public  String summary =
            "  <tr>\n" +
                    "    <th>Subject<br>Code</th>\n" +
                    "    <th>Above<br>Attainment</th>\n" +
                    "    <th>Below<br>Attainment</th>\n" +
                    "    <th>Attainment<br>Level</th>\n" +
                    "  </tr>\n";


    BigScreenshot longScreenshot;

    public  String summary1 =
            "  <tr>\n" +
                    "    <th>Subject<br>Code</th>\n" +
                    "    <th>Above<br>Attainment</th>\n" +
                    "    <th>Below<br>Attainment</th>\n" +
                    "    <th>Attainment<br>Level</th>\n" +
                    "  </tr>\n";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);

        parentlinearlayout = findViewById(R.id.parentlinearlayout);
        scrollView = (ScrollView)findViewById(R.id.scroll);
        webView1 = (WebView)findViewById(R.id.webview1);
        webView2 = (WebView)findViewById(R.id.webview2);



        Totalstudents = (TextView)findViewById(R.id.TotalStudents);
        distinction = (TextView)findViewById(R.id.Distinction);

        pass1 = (TextView)findViewById(R.id.pass);
        generatepdf = (Button)findViewById(R.id.savepdf);
        //nextpage = (Button)findViewById(R.id.nextpage);


        fail1 = (TextView)findViewById(R.id.fail);
        firstclass = (TextView)findViewById(R.id.Firstclass);


        longScreenshot = new BigScreenshot(this, scrollView,parentlinearlayout);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                Dash_board.this);
        alertDialogBuilder.setTitle("Caution");
        alertDialogBuilder.setMessage(
                "Please Scroll to the top of the page before saving " +
                        "to obtain a clear image, cause the page " +
                        "scrolls automatically!!! during this process...");
        alertDialogBuilder.setIcon(android.R.drawable.ic_dialog_info);
        alertDialogBuilder.setCancelable(true);
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();


        PieChart pieChart = findViewById(R.id.pie);
        for(int i = 4 ; i < 8; i++){
            subjects.add(ArrOfArr.get(0).get(i));
        }

        ResultCalculation();
        SubjectAttainmentCalculation();
        InternalAttainmentCalculation();


        ArrayList<PieEntry> visitors = new ArrayList<>();
        visitors.add(new PieEntry(((float) Distinction/(float) 46)*100,"Distinction"));
        visitors.add(new PieEntry(((float) First_Class/(float) 46)*100,"First Class"));
        visitors.add(new PieEntry(((float) Pass_/(float) 46)*100,"Pass"));
        visitors.add(new PieEntry(((float)Fail_/(float) 46)*100,"Fail"));
        //visitors.add(new PieEntry(totalstudents,2020));
        PieDataSet pieDataSet = new PieDataSet(visitors,"");

        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        pieDataSet.setValueTextColor(Color.BLACK);
        pieDataSet.setValueTextSize(16f);

        PieData pieData = new PieData(pieDataSet);

        pieChart.setData(pieData);
        pieChart.getDescription().setTextAlign(Paint.Align.RIGHT);
        pieChart.setDrawEntryLabels(false);
        pieChart.getDescription().setEnabled(false);
        pieChart.setCenterText("Result %");
        pieChart.setCenterTextSize(18f);
        pieChart.animate();

        BarChart barChart = findViewById(R.id.bar1);
        BarDataSet barDataSet = new BarDataSet(Subjectattper,"");
        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(16f);

        BarData barData = new BarData(barDataSet);

        barChart.setFitBars(true);
        barChart.setData(barData);
        barChart.getDescription().setText("Bar Chart Template");
        barChart.animateY(2000);
        XAxis xAxis = barChart.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setCenterAxisLabels(true);
        xAxis.setLabelRotationAngle(-90);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override

            public String getFormattedValue(float value, AxisBase axis) {
                if (value>=0){
                    if (value <= subjects.size() - 1){
                        return (String) subjects.get((int) value);
                    }
                    return "";
                }
                return "";
            }
        });




        generatepdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                longScreenshot.startScreenshot();
                Toast.makeText(Dash_board.this,"Started to take screenshot",Toast.LENGTH_LONG);
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        longScreenshot.stopScreenshot();
                        /*Intent intent = new Intent(Dash_board.this,Dash_board2.class);
                        intent.putExtra("Flag",1);
                        startActivity(intent);*/
                    }
                }, 2700);

            }
        });
        /*nextpage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Dash_board.this,Dash_board2.class);
                intent.putExtra("Flag",0);
                startActivity(intent);

            }
        });*/
        String top="<center><table border = 1>";
        String bottom="</table></center>";
        webView1.loadData(top+summary+bottom, "text/html", null);
        webView2.loadData(top+summary1+bottom,"text/html",null);

    }
    public void ResultCalculation(){




        for (int i = 1; i < ArrOfArr.size(); i++) {
            //printlnToUser(ArrOfArr.get(i).get(pos+4));


            if(ArrOfArr.get(i).get(22).equals("Distinction")){Distinction++;}
            if(ArrOfArr.get(i).get(22).equals("First Class")){First_Class++;}
            if(ArrOfArr.get(i).get(22).equals("Pass")){Pass_++;}
            if(ArrOfArr.get(i).get(22).equals("Fail")){Fail_++;}
            totalstudents++;
        }
        Totalstudents.setText("Total no. of students: "+totalstudents);
        distinction.setText("No. of Distictions: "+Distinction);
        firstclass.setText("No. of First Classes: "+First_Class);
        pass1.setText("No. of Passes: "+Pass_);
        fail1.setText("No. of Failures: "+Fail_);
        //Totalstudents.setText(String.valueOf(ArrOfArr.get(4).size()));
    }
    public void SubjectAttainmentCalculation(){
        int Subjectattainment;
        float attperr;
        float z = 0f;
        int Attainment;


        Double total,internal;


        for (int j = 4; j < 8; j++) {
            pass = 0;
            for (int i = 1; i < ArrOfArr.size(); i++) {
                total = Double.parseDouble(ArrOfArr.get(i).get(j).replaceAll(" ", ""));
                internal = Double.parseDouble(ArrOfArr.get(i).get(j+8).replaceAll(" ", ""));
                if (internal+total>=45 && total>=35){
                    pass++;
                }
                else {
                    fail++;
                }
            }
            Subjectattper.add(new BarEntry(z,pass));
            z++;
        }



        for (int j = 4; j<8/*11*/; j++){
            Subjectattainment = 0;
            for(int i = 1; i < ArrOfArr.size(); i++){
                if(Double.parseDouble(ArrOfArr.get(i).get(j).replaceAll(" ",""))>=50){
                    Subjectattainment++;
                }
            }
            attperr = ((float)Subjectattainment/(float)totalstudents)*100;
            if ( attperr  > 70 ){ Attainment = 3;}
            else if ( attperr<= 70  && attperr  >60){ Attainment = 2;}
            else if ( attperr <= 60 && attperr  >50){ Attainment = 1;}
            else{ Attainment = 0; }  /* That is  Attainment Percentage is less than zero */



            String data="<tr>\n"+"<td style=text-align:center>"+ArrOfArr.get(0).get(j)+"</td>\n" +
                    "<td style=text-align:center>"+Subjectattainment+"</td>\n" +
                    "<td style=text-align:center>"+(totalstudents-Subjectattainment)+"</td>\n" +
                    "<td style=text-align:center>"+Attainment+"</td>\n" +
                    "</tr>";
            summary+=data;

//            Subjectattper.add(new BarEntry(z,attperr));
//            z++;
        }
        for (int j = 8; j<11; j++){
            Subjectattainment = 0;
            for(int i = 1; i < ArrOfArr.size(); i++){
                if(Double.parseDouble(ArrOfArr.get(i).get(j).replaceAll(" ",""))>=25){
                    Subjectattainment++;
                }
            }

            attperr = ((float)Subjectattainment/(float)totalstudents)*100;
            if ( attperr  > 70 ){ Attainment = 3;}
            else if ( attperr<= 70  && attperr  >60){ Attainment = 2;}
            else if ( attperr <= 60 && attperr  >50){ Attainment = 1;}
            else{ Attainment = 0; }
            String data="<tr>\n"+"<td style=text-align:center>"+ArrOfArr.get(0).get(j)+"</td>\n" +
                    " <td style=text-align:center>"+Subjectattainment+"</td>\n" +
                    "    <td style=text-align:center>"+String.valueOf(totalstudents-Subjectattainment)+"</td>\n" +
                    "<td style=text-align:center>"+Attainment+"</td>\n" +
                    "  </tr>";
            summary+=data;


//            Subjectattper.add(new BarEntry(z,attperr));
//            z++;
        }
    }


    public void InternalAttainmentCalculation(){
        int Internalattainment;
        float attperr;
        int Attainment;
        for (int j = 12; j<19; j++){
            Internalattainment = 0;
            for(int i = 1; i < ArrOfArr.size(); i++){
                if(Integer.parseInt(ArrOfArr.get(i).get(j).replaceAll(" ",""))>=12.5){
                    Internalattainment++;
                }
            }
            attperr = ((float)Internalattainment/(float)totalstudents)*100;
            if ( attperr  > 70 ){ Attainment = 3;}
            else if ( attperr<= 70  && attperr  >60){ Attainment = 2;}
            else if ( attperr <= 60 && attperr  >50){ Attainment = 1;}
            else{ Attainment = 0; }
            String data="<tr>\n"+"<td style=text-align:center>"+ArrOfArr.get(0).get(j)+"</td>\n" +
                    "<td style=text-align:center>"+Internalattainment+"</td>\n" +
                    "<td style=text-align:center>"+(totalstudents-Internalattainment)+"</td>\n" +
                    "<td style=text-align:center>"+Attainment+"</td>\n" +
                    "</tr>";
            summary1+=data;
        }
    }


    /*private void printlnToUser(String str) {
        final String string = str;
        if (output.length()>8000) {
            CharSequence fullOutput = output.getText();
            fullOutput = fullOutput.subSequence(5000,fullOutput.length());
            output.setText(fullOutput);
            //output.setSelection(fullOutput.length());
        }
        output.append(string+"\n");
    }*/

    @Override
    public void getScreenshot(Bitmap bitmap) {
        storeImage(bitmap);
        Toast.makeText(Dash_board.this,"Result stored as image in Internal/Result Analyser/",Toast.LENGTH_LONG).show();

    }
    private void storeImage(Bitmap image) {
        File pictureFile = getOutputMediaFile();
        if (pictureFile == null) {
            //Log.d(TAG,"Error creating media file, check storage permissions: ");// e.getMessage());
            return;
        }
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            image.compress(Bitmap.CompressFormat.PNG, 90, fos);
            fos.close();
        } catch (FileNotFoundException e) {
            //Log.d(TAG, "File not found: " + e.getMessage());
        } catch (IOException e) {
            //Log.d(TAG, "Error accessing file: " + e.getMessage());
        }
    }


    private  File getOutputMediaFile(){

        File mediaStorageDir = new File(Environment.getExternalStorageDirectory() + "/Result Analyser");

        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmmss").format(new Date());
        File mediaFile;
        String mImageName= timeStamp +"_RA_1_"+ ".jpg";
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
        return mediaFile;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    ArrOfArr.clear();
                    Dash_board.this.finish();

            }

        }
        return super.onKeyDown(keyCode, event);
    }

}