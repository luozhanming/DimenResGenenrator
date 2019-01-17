package com.example.administrator.dimenresgenenrator;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.ZipUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    EditText etBaseSize;
    RecyclerView rvReferenceSizeList;
    Button btnConfirm;
    private ReferenceAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        etBaseSize = findViewById(R.id.et_base_size);
        rvReferenceSizeList = findViewById(R.id.rv_referece_size);
        btnConfirm = findViewById(R.id.btn_generate);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generateRes();
            }
        });
        mAdapter = new ReferenceAdapter(new ArrayList<Integer>());
        rvReferenceSizeList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvReferenceSizeList.setAdapter(mAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void generateRes() {
        if (!TextUtils.isDigitsOnly(etBaseSize.getText())) {
            Toast.makeText(this, "请输入数字", Toast.LENGTH_SHORT).show();
            return;
        }
        int baseSize = Integer.parseInt(etBaseSize.getText().toString());
        final DimenResGenerator generator = new DimenResGenerator(baseSize, "dp_%d_dp");
        List<Observable<File>> list = new ArrayList<>();
        list.add(generator.generatorDimenRes(baseSize, true));
        list.add(generator.generatorDimenRes(baseSize, false));
        List<Integer> datas = mAdapter.getDatas();
        for (Integer data : datas) {
            list.add(generator.generatorDimenRes(data, false));
        }
        Observable.merge(list)
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        FileUtils.deleteAllInDir(new File(DimenResGenerator.PATH_DIMEN_RES));
                    }
                })
                .doOnComplete(new Action() {
                    @Override
                    public void run() throws Exception {
                        ZipUtils.zipFile(new File(DimenResGenerator.PATH_DIMEN_RES + "dimens/"), new File(DimenResGenerator.PATH_DIMEN_RES + "dimens.zip"));
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.add) {
            new AddReferenceSizeDialog(this, new AddReferenceSizeDialog.ResultCallback() {
                @Override
                public void onResult(int referenceSize) {
                    mAdapter.addReference(referenceSize);
                }
            }).show();
        }else if(id ==R.id.refresh){
            mAdapter.getDatas().clear();
            mAdapter.notifyDataSetChanged();
            etBaseSize.setText("");
        }
        return true;
    }
}
