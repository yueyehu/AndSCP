package com.appstart.hkj.andscp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.appstart.hkj.andscp.FtpTrans.FtpUpDown;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by HKJ on 2016/8/3.
 */
public class FileTrans extends Activity implements View.OnClickListener{
    private Button btUpload;
    private Button btDownload;
    private Button btRelogin;
    private Button btExit;
    private EditText etLocalfile;
    private EditText etRemotedir;
    private EditText etLocaldir;
    private EditText etRemotefile;

    private String transProtocol;
    private FtpUpDown ftp;

    private int exitState;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        btUpload = (Button)findViewById(R.id.uploader);
        btUpload.setOnClickListener(this);
        btDownload = (Button)findViewById(R.id.download);
        btDownload.setOnClickListener(this);
        btRelogin = (Button)findViewById(R.id.relogin);
        btRelogin.setOnClickListener(this);
        btExit = (Button)findViewById(R.id.exit);
        btExit.setOnClickListener(this);

        etLocalfile = (EditText)findViewById(R.id.loaclfile);
        etRemotedir = (EditText)findViewById(R.id.remotedir);

        etLocaldir = (EditText)findViewById(R.id.localdir);
        etRemotefile = (EditText)findViewById(R.id.remotefile);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        transProtocol = (String)bundle.getSerializable("transProtocol");
        switch(transProtocol){
            case "FTP":
                ftp = MainActivity.ftp;//静态变量传递
                break;
            case "SFTP":
                Toast.makeText(getApplicationContext(), "目前不支持协议SFTP", Toast.LENGTH_LONG).show();
                break;
            case "SCP":
                Toast.makeText(getApplicationContext(), "目前不支持协议SCP", Toast.LENGTH_LONG).show();
                break;
            default:
                Toast.makeText(getApplicationContext(), "未知协议", Toast.LENGTH_LONG).show();
        }
    }
    @Override
    public void onClick(View arg0){
        switch(arg0.getId()){
            case R.id.uploader:
                new Thread(upload).start();break;
            case R.id.download:
                new Thread(download).start();break;
            case R.id.relogin:
                new Thread(relogin).start();break;
            case R.id.exit:
                new Thread(ftpexit).start();break;
            default:
                ;
        }

    }
    Runnable upload = new Runnable(){
        @Override
        public void run() {
            File file=new File(etLocalfile.getText().toString());
            ftp.uploadFile(file, etRemotedir.getText().toString());
        }
    };

    Runnable download = new Runnable(){
        @Override
        public void run() {
            String str = etRemotefile.getText().toString();
            int flag=0;
            for(int i=str.length()-1;i>=0;i--){
                if(str.charAt(i)=='/'){
                    flag=i;
                    break;
                }
            }
            ftp.downloadFile(str.substring(flag+1),etLocaldir.getText().toString(),str.substring(0,flag+1));
        }
    };

    Runnable relogin = new Runnable(){
        @Override
        public void run() {
            //Intent intent = new Intent();
            //intent.setClass(FileTrans.this, MainActivity.class);
            //startActivity(intent);
            exitState = ftp.ftpLogOut();
            System.exit(0);
        }
    };

    Runnable ftpexit = new Runnable(){
        @Override
        public void run() {
            exitState = ftp.ftpLogOut();
            System.exit(0);
        }
    };
}
