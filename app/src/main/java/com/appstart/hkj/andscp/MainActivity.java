package com.appstart.hkj.andscp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.appstart.hkj.andscp.FtpTrans.FtpInfo;
import com.appstart.hkj.andscp.FtpTrans.FtpUpDown;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

public class MainActivity extends Activity implements View.OnClickListener {

    public static FtpUpDown ftp=new FtpUpDown();
    private FtpInfo ftpinfo;
    private int loginresult;

    private boolean mUpLoadRun;
    private Button bt;
    private EditText tvIp;
    private EditText tvPort;
    private EditText tvNmae;
    private EditText tvPassword;

    private Spinner spTransName;
    private List<String> name_list;
    private ArrayAdapter<String> arr_adapter;

    private String transProtocol;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ftpinfo = new FtpInfo();
        //ftpClient = new FTPClient();

        mUpLoadRun = true;
        bt=(Button)findViewById(R.id.add_server_button);
        bt.setOnClickListener(this);

        tvIp = (EditText)findViewById(R.id.server_name);
        tvPort = (EditText)findViewById(R.id.server_port);
        tvNmae = (EditText)findViewById(R.id.server_username);
        tvPassword = (EditText)findViewById(R.id.server_password);

        spTransName = (Spinner)findViewById(R.id.transName);
        name_list = new ArrayList<String>();
        name_list.add("FTP");
        name_list.add("SFTP");
        name_list.add("SCP");

        arr_adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,name_list);
        arr_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTransName.setAdapter(arr_adapter);
        spTransName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,int position, long id){
                Spinner spinner = (Spinner) parent;
                transProtocol = spinner.getItemAtPosition(position).toString();
                Toast.makeText(getApplicationContext(), spinner.getItemAtPosition(position).toString(), Toast.LENGTH_LONG).show();
                switch (transProtocol){
                    case "FTP":
                        tvPort.setText("21");break;
                    case "SFTP":
                        tvPort.setText("22");break;
                    case "SCP":
                        tvPort.setText("80");break;
                    default:
                        ;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                 Toast.makeText(getApplicationContext(), "没有改变的处理", Toast.LENGTH_LONG).show();
            }
        });
    }
    @Override
    public void onClick(View arg0) {
        switch(transProtocol){
            case "FTP":
                ftp.setStrIp(tvIp.getText().toString());
                ftp.setIntPort(Integer.valueOf(tvPort.getText().toString()));
                ftp.setUser(tvNmae.getText().toString());
                ftp.setPassword(tvPassword.getText().toString());

                Thread thread = new LoginThread();
                thread.start();
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                switch (loginresult){
                    case 0:
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("transProtocol",transProtocol);
                        Intent intent = new Intent();
                        intent.setClass(MainActivity.this,FileTrans.class);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        ShowToast("login is succeed!");break;
                    case 1:
                        ShowToast("IP can't be connected");break;
                    case 2:
                        ShowToast("username or password is error!");break;
                    case 4:
                        ShowToast("login is failed!");break;
                    default:
                        ShowToast("unexpected result!");
                }
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


    public void ShowToast(String msg){
        Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_SHORT).show();
    }

    public class LoginThread extends Thread{
        public void run(){
            loginresult = ftp.ftpLogin();
        }
    }
}


