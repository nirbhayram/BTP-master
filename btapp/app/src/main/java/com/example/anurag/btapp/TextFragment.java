package com.example.anurag.btapp;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.LinkedList;

public class TextFragment extends Fragment {
    private ListView l1;
    private EditText text1;
    private Button send;

    private StringBuffer bufferout;

    public interface OnSendRequestListener {
        public void onSendRequest(String s);
    }

    OnSendRequestListener SendRequestCallback;

    public interface OnTextLogRequestListener {
        public ArrayAdapter<String> OnTextLogRequest();
    }

    OnTextLogRequestListener TextLogRequestCallBack;


    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            SendRequestCallback = (OnSendRequestListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnSendRequestListener");
        }

        try {
            TextLogRequestCallBack = (OnTextLogRequestListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnTextLogRequestListener");
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

        super.onCreateView(inflater, container, savedInstanceState);
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.activity_text_fragment, container, false);
        l1 = (ListView) rootView.findViewById(R.id.input);
        l1.setAdapter(TextLogRequestCallBack.OnTextLogRequest());

        text1 = (EditText) rootView.findViewById(R.id.edit_text_out);

        send = (Button) rootView.findViewById(R.id.button_send);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = text1.getText().toString();
                try {
                    SendRequestCallback.onSendRequest(message);
                }catch(NullPointerException e){}
            }


        });
        bufferout = new StringBuffer("");
        return rootView;
    }

    public void resetbuffer() {
        bufferout.setLength(0);
        text1.setText(bufferout);
    }

    private TextView.OnEditorActionListener writeListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
            if (i == EditorInfo.IME_NULL && keyEvent.getAction() == KeyEvent.ACTION_UP) {
                String message = textView.getText().toString();
                SendRequestCallback.onSendRequest(message);
            }
            return true;
        }
    };

    public void hideKeyboard(InputMethodManager mgr) {
        mgr.hideSoftInputFromWindow(text1.getWindowToken(), 0);
    }

    public static String[] parsevalues(String s) {
        s = s.trim();
        int j = 0;

        s = s.replace("\n", "").replaceAll("\r", "");

        LinkedList<String> meoww = new LinkedList<String>();
        String sout[] = s.split(";");
        for (int i = 0; i < sout.length; i++) {
            String sin[] = sout[i].split(",", 2);
            //Log.i("dfdv",sin.length+" ");
            if (sin.length == 2 && sin[0]!=null && isNumeric(sin[0])==true && isNumeric(sin[1])==true && sin[1]!=null) {
                //Log.v("the initial string"+i,sin[0]);

                meoww.add(sout[i]);
                //b[j]=sout[i];
                //Log.i("dfdv",b[j]);
                //j++;

            }

        }
        String[] b = new String[meoww.size()];
        for (int k = 0; k < b.length; k++) {


            b[k] = meoww.get(k);
            Log.v("the array created is"+k,b[k]);

            //String x1[]=b[k].split(",");
            //Log.v("the value in graph"+k,x1[0]);
        }


        return b;
    }

    public static boolean isNumeric(String str)
    {
        return str.matches("-?\\d+(\\.\\d+)?");
    }
}
