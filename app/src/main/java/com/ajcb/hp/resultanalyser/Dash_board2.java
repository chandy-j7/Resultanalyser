package com.ajcb.hp.resultanalyser;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.KeyEvent;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.ezydev.bigscreenshot.BigScreenshot;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.ajcb.hp.resultanalyser.MainActivity.ArrOfArr;
public class Dash_board2 extends AppCompatActivity implements BigScreenshot.ProcessScreenshot{
    public int flag;
    public int totalstudents = 0;
    public  String summary1 =
            "  <tr>\n" +
                    "    <th>Subject<br>Code</th>\n" +
                    "    <th>Above<br>Attainment</th>\n" +
                    "    <th>Below<br>Attainment</th>\n" +
                    "  </tr>\n";
    ArrayList<BarEntry> Internalattper = new ArrayList<>();
    WebView webView2;
    private ScrollView scrollView2;

    private LinearLayout parentlinearlayout2;
    List internals = new ArrayList<>();

    BigScreenshot longScreenshot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board2);
        webView2 = (WebView)findViewById(R.id.webview2);
        scrollView2 = (ScrollView)findViewById(R.id.scroll2);
        parentlinearlayout2 = (LinearLayout)findViewById(R.id.parentlinearlayout2);
        flag = getIntent().getExtras().getInt("Flag");

        for (int j = 12; j<19; j++){
            internals.add(ArrOfArr.get(0).get(j));
        }
        for (int i = 1; i < ArrOfArr.size(); i++) {
            totalstudents++;
        }
        InternalAttainmentCalculation();


        BarChart barChart2 = findViewById(R.id.bar2);
        BarDataSet barDataSet2 = new BarDataSet(Internalattper,"");
        barDataSet2.setColors(ColorTemplate.MATERIAL_COLORS);
        barDataSet2.setValueTextColor(Color.BLACK);
        barDataSet2.setValueTextSize(16f);

        BarData barData2 = new BarData(barDataSet2);

        barChart2.setFitBars(true);
        barChart2.setData(barData2);
        barChart2.getDescription().setText("Bar Chart Template");
        XAxis xAxis1 = barChart2.getXAxis();
        xAxis1.setGranularity(1f);
        xAxis1.setCenterAxisLabels(true);
        xAxis1.setLabelRotationAngle(-90);
        xAxis1.setValueFormatter(new IAxisValueFormatter() {
            @Override

            public String getFormattedValue(float value, AxisBase axis) {
                if (value>=0){
                    if (value <= internals.size() - 1){
                        return (String) internals.get((int) value);
                    }
                    return "";
                }
                return "";
            }
        });



        String top="<center><table border = 1>";
        String bottom="</table></center>";
        webView2.loadData(top+summary1+bottom,"text/html",null);


        if(flag == 1){
            longScreenshot = new BigScreenshot(this, scrollView2,parentlinearlayout2);
            longScreenshot.startScreenshot();
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    longScreenshot.stopScreenshot();
                    Dash_board2.this.finish();
                }
            }, 100);
        }
    }


    public void InternalAttainmentCalculation(){
        int Internalattainment;
        float attperr;
        float z = 0f;
        for (int j = 12; j<19; j++){
            Internalattainment = 0;
            for(int i = 1; i < ArrOfArr.size(); i++){
                if(Integer.parseInt(ArrOfArr.get(i).get(j).replaceAll(" ",""))>=12.5){
                    Internalattainment++;
                }
            }
            String data="<tr>\n"+"<td style=text-align:center>"+ArrOfArr.get(0).get(j)+"</td>\n" +
                    "<td style=text-align:center>"+Internalattainment+"</td>\n" +
                    "<td style=text-align:center>"+(totalstudents-Internalattainment)+"</td>\n" +
                    "</tr>";
            summary1+=data;
            attperr = ((float)Internalattainment/(float)totalstudents)*100;
            Internalattper.add(new BarEntry(z,attperr));
            z++;
        }
    }


    public void getScreenshot(Bitmap bitmap) {
        storeImage(bitmap);
        Toast.makeText(Dash_board2.this,"Result stored as image in Internal/Result Analyser/",Toast.LENGTH_LONG).show();

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


    private File getOutputMediaFile(){

        File mediaStorageDir = new File(Environment.getExternalStorageDirectory() + "/Result Analyser");

        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmmss").format(new Date());
        File mediaFile;
        String mImageName= timeStamp +"_RA_2_"+ ".jpg";
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
        return mediaFile;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    Dash_board2.this.finish();

            }

        }
        return super.onKeyDown(keyCode, event);
    }
}