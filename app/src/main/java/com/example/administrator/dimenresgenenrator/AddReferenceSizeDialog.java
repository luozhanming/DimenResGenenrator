package com.example.administrator.dimenresgenenrator;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddReferenceSizeDialog extends Dialog {

    private EditText etReference;
    private Button btnConfirm;

    private ResultCallback mCallback;


    public interface ResultCallback{
        void onResult(int referenceSize);
    }

    public AddReferenceSizeDialog(@NonNull Context context,ResultCallback callback) {
        super(context);
        this.mCallback = callback;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_add_reference);
        setCanceledOnTouchOutside(true);
        etReference = findViewById(R.id.et_reference);
        btnConfirm = findViewById(R.id.btn_confirm);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if(confirm()){
                   dismiss();
               }
            }
        });
    }

    private boolean confirm() {
        if(TextUtils.isEmpty(etReference.getText())){
            Toast.makeText(getContext(), "请输入参考尺寸", Toast.LENGTH_SHORT).show();
            return false;
        }
        int referenceSize = Integer.parseInt(etReference.getText().toString());
        if(mCallback!=null)mCallback.onResult(referenceSize);
        return true;

    }
}
