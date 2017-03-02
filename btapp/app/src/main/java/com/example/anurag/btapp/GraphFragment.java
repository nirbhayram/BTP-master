package com.example.anurag.btapp;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.GraphViewStyle;
import com.jjoe64.graphview.LineGraphView;

import java.util.LinkedList;



@SuppressWarnings("ALL")
public class GraphFragment extends Fragment {


    private Button start;
    private int maxY=250;
    private int minY=0;

    private GraphView graphView,graphView1;
    private GraphViewSeries graphViewSeries,graphViewSeries1;
    private GraphViewData graphViewData[],graphViewData1[];
    private GraphViewStyle graphViewStyle,graphViewStyle1;

    public interface OnSendRequestListener {
        public void onSendRequest(String s);
    }
    OnSendRequestListener SendRequestCallback;

    public interface OnValueRequestListener{
        public LinkedList<String> OnValueRequest();
    }
    OnValueRequestListener ValueRequestCallBack;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            SendRequestCallback = (OnSendRequestListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnSendRequestListener");
        }

        try {
            ValueRequestCallBack = (OnValueRequestListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnValueRequestListener");
        }

        return;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onCreateView(inflater,container,savedInstanceState);
        ViewGroup rv=(ViewGroup) inflater.inflate(R.layout.fragment_graph,container,false);

        start=(Button)rv.findViewById(R.id.start_stop);
        start.setText("start/stop");

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    SendRequestCallback.onSendRequest("1");    //Work on this where in sending a prompt to stop sending data from device
                }catch(NullPointerException e){

                }
            }
        });
        /*
        graphView = new LineGraphView(
                this.getActivity().getApplicationContext(), ""
        );

        graphViewData = new GraphViewData[Mainscreen.numValues];
        setGraphViewData(ValueRequestCallBack.OnValueRequest());

        graphViewSeries = new GraphViewSeries(graphViewData);
        graphView.addSeries(graphViewSeries);

        graphView.setManualYAxisBounds(maxY,minY);
        graphView.setScalable(true);
        graphView.setScrollable(true);
        graphView.setViewPort(1,80);

        graphViewStyle = new GraphViewStyle();
        graphViewStyle.setHorizontalLabelsColor(0);
        graphViewStyle.setNumHorizontalLabels(2);
        graphViewStyle.setNumVerticalLabels(1+(maxY-minY)/50);
        graphViewStyle.setVerticalLabelsColor(Color.parseColor("#CD5C5C"));

        graphView.setGraphViewStyle(graphViewStyle);

        LinearLayout g = (LinearLayout)rv.findViewById(R.id.graph1);
        g.addView(graphView);

        graphView.scrollToEnd();


        //2nd graph la

        graphView1 = new LineGraphView(
                this.getActivity().getApplicationContext(), ""
        );

        graphViewData1 = new GraphViewData[Mainscreen.numValues];
        setGraphViewData1(ValueRequestCallBack.OnValueRequest());

        graphViewSeries1 = new GraphViewSeries(graphViewData1);
        graphView.addSeries(graphViewSeries1);

        graphView1.setManualYAxisBounds(maxY,minY);
        graphView1.setScalable(true);
        graphView1.setScrollable(true);
        graphView1.setViewPort(1, 80);

        graphViewStyle1 = new GraphViewStyle();
        graphViewStyle1.setHorizontalLabelsColor(0);
        graphViewStyle1.setNumHorizontalLabels(2);
        graphViewStyle1.setNumVerticalLabels(1+(maxY-minY)/50);          //change here to set the scale for the graph
        graphViewStyle1.setVerticalLabelsColor(Color.parseColor("#CD5C5C"));
        graphView1.setGraphViewStyle(graphViewStyle1);

        //LinearLayout g2 = (LinearLayout)rv.findViewById(R.id.graph2);
        g.addView(graphView1);

        graphView1.scrollToEnd();

        Paint paint =new Paint();
        paint.setColor(Color.parseColor("#CD5C5C"));
        Bitmap bg = Bitmap.createBitmap(1000, 1000, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bg);
        canvas.drawCircle(bg.getWidth()/2,bg.getHeight()/2,300,paint);
        LinearLayout g2 = (LinearLayout)rv.findViewById(R.id.graph2);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            g2.setBackground(new BitmapDrawable(getResources(),bg));
        }
        */
        return rv;
    }


    @Override
    public void onViewStateRestored (Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        /*
         Double d = graphViewData[graphViewData.length-1].getY();
         Double d2 = graphViewData1[graphViewData1.length-1].getY();
        */
        return;
    }


    public static void initList(LinkedList<String> l, int size) {
        int i;
        for (i=0;i<size;i++) {
            l.add(null);
        }
    }



    public static void parsevalues(String s,LinkedList<String> l) {
        s = s.trim();
        int j = 0;

        s = s.replace("\n", "").replaceAll("\r", "");

        LinkedList<String> meoww = new LinkedList<String>();
        String sout[] = s.split(";");
        for (int i = 0; i < sout.length; i++) {
            String sin[] = sout[i].split(",", 2);

            //Log.i("dfdv",sin.length+" ");
            if (sin.length == 2 && sin[0]!=null && isNumeric(sin[0])==true && isNumeric(sin[1])==true && sin[1]!=null) {
                meoww.add(sout[i]);
                //b[j]=sout[i];
                //Log.i("dfdv",b[j]);
                //j++;

            }

        }
        String[] b = new String[meoww.size()];
        for (int k = 0; k < b.length; k++) {
            b[k] = meoww.get(k);
            l.add(b[k]);
            l.remove();
        }

        //for(int i=0;i<b.length;i++){
        //    l.add(b[i]);
        //    l.remove();
        //}

        return;
    }


    void setGraphViewData(LinkedList<String> l){
        String x=null;
        int z=0;
        for(int i=0;i<l.size();i++) {
            x = l.get(i);

            if (x != null) {
                String x1[] = x.split(",");
                String regex="\\d+";     //I did this so that to check whether its numerical or garbage
                //x1[0]=x1[0].replaceAll(";","");
                if (x1[0] != null && isInteger(x1[0])==true) {
                    Log.e("The value of x1 is",x1[0]);
                    z = Integer.parseInt(x1[0]);
                    z=z/3;
                    if((int)(z/1000)>0){
                        z=0;
                    }
                } else {
                    z = 0;
                }
            }
            else{
                z=0;
            }
            Log.e("The value of Z is","value"+z);
                graphViewData[i] = new GraphViewData(i, z);
            }
        }


    void setGraphViewData1(LinkedList<String> l){
        String x=null;
        int z=0;
        for(int i=0;i<l.size();i++) {
            x = l.get(i);
            if (x != null) {
                String x2[] = x.split(",");
                String regex="\\d+";
                //x2[1]=x2[1].replaceAll(";","");
                if (x2[1] != null && isInteger(x2[1])==true) {
                    z = Integer.parseInt(x2[1]);
                    z=z/4;
                    if((int)(z/1000)>0){
                        z=0;
                    }
                } else {
                    z = 0;
                }
            }
            else{
                z=0;
            }
                graphViewData1[i] = new GraphViewData(i, z);
            }
        }


    public void updateGraph(LinkedList<String> l) {
        setGraphViewData(l);
        graphViewSeries.resetData(graphViewData);
        graphViewSeries.removeGraphView(graphView);
        graphViewSeries.addGraphView(graphView);

        graphView.scrollToEnd();

    }
    public void updateGraph1(LinkedList<String> l) {
        setGraphViewData1(l);
        graphViewSeries1.resetData(graphViewData1);
        graphViewSeries1.removeGraphView(graphView1);
        graphViewSeries1.addGraphView(graphView1);

        graphView1.scrollToEnd();

    }
    public static boolean isNumeric(String str)
    {
        return str.matches("-?\\d+(\\.\\d+)?");
    }
    public static boolean isInteger(String s) {
        int x=0;
        try {
            Integer.parseInt(s);
        } catch(NumberFormatException e) {
            x=1;
            return false;
        } catch(NullPointerException e) {
            x=1;
            return false;
        }
        x=0;
        //Log.e("The integer check is","True"+x);
        return true;
    }

    public static void convertstring(LinkedList<String> l){
        double[] a = new double[l.size()];
        double diff=0;
        int[] sign = new int[l.size()];
        for(int i=0;i<l.size();i++){
            a[i]=Double.parseDouble(l.get(i));
        }
        for(int j=0;j<l.size()-1;j++){
            diff=a[j+1]-a[j];
            if(diff==0){
                sign[j]=0;
            }
            else if (diff>0){
                sign[j]=1;
            }
            else{
                sign[j]=-1;
            }

        }

    }
}
