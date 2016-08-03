package com.appstart.hkj.andscp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.appstart.hkj.andscp.FtpTrans.FtpInfo;
import com.appstart.hkj.andscp.FtpTrans.FtpUpDown;

public class MainActivity extends Activity implements View.OnClickListener {

    private boolean mUpLoadRun;
    private Button bt;
    private EditText tvIp;
    private EditText tvNmae;
    private EditText tvPassword;
    private FtpUpDown ftp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mUpLoadRun = true;
        bt=(Button)findViewById(R.id.add_server_button);
        bt.setOnClickListener(this);

        tvIp = (EditText)findViewById(R.id.server_name);
        tvNmae = (EditText)findViewById(R.id.server_username);
        tvPassword = (EditText)findViewById(R.id.server_password);
    }
    @Override
    public void onClick(View arg0){
        FtpInfo ftpinfo = new FtpInfo();
        ftpinfo.setIntPort(21);
        ftpinfo.setStrIp(tvIp.getText().toString());
        ftpinfo.setUser(tvNmae.getText().toString());
        ftpinfo.setPassword(tvPassword.getText().toString());
        Bundle bundle = new Bundle();
        bundle.putSerializable("ftpinfo",ftpinfo);
        Intent intent = new Intent();
        intent.setClass(MainActivity.this,FileTrans.class);
        intent.putExtras(bundle);
        startActivity(intent);

    }
}
