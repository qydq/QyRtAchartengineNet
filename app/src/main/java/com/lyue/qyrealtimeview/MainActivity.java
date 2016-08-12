package com.lyue.qyrealtimeview;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private RealTimeView realTimeView = null;
    private TextView username = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        realTimeView = (RealTimeView) findViewById(R.id.hrRealWave);

        username = (TextView) findViewById(R.id.username);
        username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, RtChartsActivity.class);
                startActivity(intent);
            }
        });
    }

}
