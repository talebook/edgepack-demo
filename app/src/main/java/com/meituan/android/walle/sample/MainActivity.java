package com.meituan.android.walle.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.meituan.android.walle.ChannelInfo;
import com.meituan.android.walle.WalleChannelReader;


public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.read_channel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                readChannel();
            }
        });

    }

    private void readChannel() {
        final TextView tv = (TextView) findViewById(R.id.tv_channel);
        final long startTime = System.currentTimeMillis();
        final String info = WalleChannelReader.getString(this.getApplicationContext());
        if (info != null) {
            tv.setText(info);
        }
        Toast.makeText(this, "ChannelReader takes " + (System.currentTimeMillis() - startTime) + " milliseconds", Toast.LENGTH_SHORT).show();
    }
}
