package com.example.demo.layout;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.aidlsvr.IMyAidlInterface;
import com.example.demo.R;
import com.example.demo.rebuild.Rebuild;
import com.example.demo.svr.LocalService;

public class LinearActivity extends AppCompatActivity {
    private Intent mSvrIntent;
    private Intent mSvrAIntent;
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d("svr", "connected");
            ((TextView) findViewById(R.id.svrStr)).setText(((LocalService.MyBinder) service).getString());
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d("svr", "be disconnected");
        }
    };

    private ServiceConnection mServiceAConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            IMyAidlInterface iMyAidlInterface = IMyAidlInterface.Stub.asInterface(service);
            try {
                Log.d("svr aidl", "connected");
                ((TextView) findViewById(R.id.svrStr)).setText(iMyAidlInterface.getString());
            } catch (Exception e) {
                Log.d("error: ", e.getMessage());
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d("svr", "be disconnected");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSvrIntent = new Intent(LinearActivity.this, LocalService.class);
        mSvrAIntent = new Intent();
        mSvrAIntent.setAction("com.example.aidlsvr.MyService");
        mSvrAIntent.setPackage("com.example.aidlsvr");

        setContentView(R.layout.activity_linear_layout);

        findViewById(R.id.firstLine).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LinearActivity.this, FullscreenActivity.class));
            }
        });

        findViewById(R.id.firstLine).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                startActivity(new Intent(LinearActivity.this, Rebuild.class));
                return true;
            }
        });

        findViewById(R.id.secondLine).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LinearActivity.this, FrameLayout.class));
            }
        });
        findViewById(R.id.thirdLine).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LinearActivity.this, TableLayout.class));
            }
        });
        findViewById(R.id.fourthLine).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LinearActivity.this, ConstraintLayout.class));
            }
        });
    }

    public void bindSvr(View view) {
        bindService(mSvrIntent, mServiceConnection, BIND_AUTO_CREATE);
    }

    public void unbindSvr(View view) {
        try {
            unbindService(mServiceConnection);
            ((TextView) findViewById(R.id.svrStr)).setText("local server gone");
            Log.d("svr", "disconnected");
        } catch (Exception e) {
            ((TextView) findViewById(R.id.svrStr)).setText(e.getMessage());
            Log.d("error: ", e.getMessage());
        }
    }

    public void bindASvr(View view) {
        bindService(mSvrAIntent, mServiceAConnection, BIND_AUTO_CREATE);
    }

    public void unbindASvr(View view) {
        try {
            unbindService(mServiceAConnection);
            ((TextView) findViewById(R.id.svrStr)).setText("aidl server gone");
            Log.d("aidl svr", "disconnected");
        } catch (Exception e) {
            ((TextView) findViewById(R.id.svrStr)).setText(e.getMessage());
            Log.d("error: ", e.getMessage());
        }
    }
}
