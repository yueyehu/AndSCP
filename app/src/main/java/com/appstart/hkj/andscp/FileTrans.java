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

import java.io.File;

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
    private FtpUpDown ftp;
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
        FtpInfo ftpinfo = (FtpInfo)bundle.getSerializable("ftpinfo");
        ftp = new FtpUpDown(ftpinfo.getStrIp(),ftpinfo.getIntPort(),ftpinfo.getUser(),ftpinfo.getPassword());
        new Thread(runlog).start();
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

    Runnable runlog = new Runnable(){
        @Override
        public void run() {
            if(!ftp.ftpLogin()){
                Intent intent = new Intent();
                intent.setClass(FileTrans.this, MainActivity.class);
                startActivity(intent);
            }
        }
    };

    Runnable relogin = new Runnable(){
        @Override
        public void run() {
            //Intent intent = new Intent();
            //intent.setClass(FileTrans.this, MainActivity.class);
            //startActivity(intent);
            System.exit(0);
        }
    };

    Runnable ftpexit = new Runnable(){
        @Override
        public void run() {
            // Intent intent = new Intent();
           // intent.setClass(FileTrans.this, FileExplorerTabActivity.class);
           // startActivity(intent);
            System.exit(0);
        }
    };
}
