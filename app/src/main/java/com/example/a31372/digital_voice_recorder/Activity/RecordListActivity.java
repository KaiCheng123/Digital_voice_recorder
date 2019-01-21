package com.example.a31372.digital_voice_recorder.Activity;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.a31372.digital_voice_recorder.Db.DBHelper;
import com.example.a31372.digital_voice_recorder.R;
import com.example.a31372.digital_voice_recorder.util.MyAdapter;
import com.example.a31372.digital_voice_recorder.util.RecordingItem;

import java.io.File;
import java.util.ArrayList;

import gdut.bsx.share2.Share2;

public class RecordListActivity extends AppCompatActivity {

    private DBHelper dbHelper;

    private ListView simpleList;
    private ArrayList<RecordingItem> recordList=new ArrayList<>();
    private MyAdapter myAdapter;
    private Context mContext;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_list);
        // android 7.0系统问题
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();
        mContext = this;
        simpleList = (ListView) findViewById(R.id.simpleListView);
        dbHelper = new DBHelper(this);
        dbHelper.getWritableDatabase();
        dbHelper.addRecording();
        Initialize();       //初始化列表

        simpleList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {      //列表单点事件监听
                Intent intent = new Intent(RecordListActivity.this,PlayRecord.class);
                String path = dbHelper.getItemAt(position).getFilePath();
                intent.putExtra("recordPath",path);
                startActivity(intent);
            }
        });

        simpleList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {     //列表长按事件监听
                ArrayList<String> entrys = new ArrayList<String>();
                entrys.add("分享");
                entrys.add("修改文件名");
                entrys.add("删除文件");

                final CharSequence[] items = entrys.toArray(new CharSequence[entrys.size()]);
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("提示");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        if (item == 0) {
                            shareFileDialog(position);
                        } if (item == 1) {
                            renameFileDialog(position);
                        } else if (item == 2) {
                            deleteFileDialog(position);
                        }
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.create().show();
                return true;
            }
        });
    }

    /**
     * 分享功能
     * @param position
     */
    public void shareFileDialog(int position) {
        String path = dbHelper.getItemAt(position).getFilePath();
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(path)));
        shareIntent.setType("audio/wav");
        startActivity(Intent.createChooser(shareIntent, "发送"));
    }

    /**
     * 修改文件名提示框
     * @param position
     */
    public void renameFileDialog(final int position) {
        AlertDialog.Builder renameFileBuilder = new AlertDialog.Builder(mContext);

        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.dialog_rename_file, null);

        final EditText input = (EditText) view.findViewById(R.id.new_name);
        String name = dbHelper.getItemAt(position).getName();
        name = name.substring(0,name.lastIndexOf('.'));
        input.setText(name);
        renameFileBuilder.setTitle("确定修改？");
        renameFileBuilder.setCancelable(true);
        renameFileBuilder.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        try {
                            String value = input.getText().toString().trim() + ".wav";
                            rename(position, value);
                        } catch (Exception e) {
                            Log.e("renameFileBuilder", "exception", e);
                        }
                        dialog.cancel();
                    }
                });
        renameFileBuilder.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        renameFileBuilder.setView(view);
        AlertDialog alert = renameFileBuilder.create();
        alert.show();
    }

    /**
     * 修改文件名
     * @param position
     * @param name
     */
    public void rename(int position, String name) {

        String mFilePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFilePath += "/record_test/" + name;
        File f = new File(mFilePath);

        if (f.exists() && !f.isDirectory()) {
            Toast.makeText(mContext,
                    "文件名已存在！",
                    Toast.LENGTH_SHORT).show();

        } else {
            File oldFilePath = new File(dbHelper.getItemAt(position).getFilePath());
            oldFilePath.renameTo(f);
        }
    }

    /**
     * 删除文件提示框
     * @param position
     */
    private void deleteFileDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage("确定删除？");
        builder.setTitle("提示");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                remove(position);
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.create().show();
    }

    /**
     * 删除文件
     * @param position
     */
    private void remove(int position) {
        File file = new File(dbHelper.getItemAt(position).getFilePath());
        file.delete();
        Toast.makeText(
                mContext,
                "文件已删除！",
                Toast.LENGTH_SHORT
        ).show();
        recordList.remove(position);
        myAdapter.notifyDataSetChanged();
    }

    /**
     * 初始化列表
     */
    private void Initialize() {
        int recordCount = dbHelper.getRecordCount();
        Log.d("recordCount",recordCount+"");
        for (int i = 0; i < recordCount; i++){
            RecordingItem item = dbHelper.getItemAt(i);
            recordList.add(item);
        }
        myAdapter=new MyAdapter(this,R.layout.list_view_items,recordList);
        simpleList.setAdapter(myAdapter);
    }

}
