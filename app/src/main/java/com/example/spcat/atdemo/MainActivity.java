package com.example.spcat.atdemo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.DynamicDrawableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends Activity implements View.OnClickListener {
    public static final int REQUEST_CODE_SELECTUSER = 1;
    private EditText mEditText;
    private Button mButton;
    /**
     * 存储@的id、name的map
     */
    private Map<String, String> mATMap = new HashMap<String, String>();
    private String nameStr;
    /**
     * 上一次返回的用户名，用于把要@的用户名拼接到输入框中
     */
    private String lastNameStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        mEditText = (EditText) findViewById(R.id.edit_input);
        mButton = (Button) findViewById(R.id.btn_ok);
        mButton.setOnClickListener(this);
        mEditText.setFilters(new InputFilter[]{new MyInputFilter()});
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (data == null || resultCode != RESULT_OK)
                return;
            String tmpCidStr = data.getStringExtra(UserSelectActivity.TAG_USERID);
            String tmpNameStr = data.getStringExtra(UserSelectActivity.TAG_USERNAME);

            String[] tmpCids = tmpCidStr.split(" ");
            String[] tmpNames = tmpNameStr.split(" ");

            if (tmpCids != null && tmpCids.length > 0) {
                for (int i = 0; i < tmpCids.length; i++) {
                    if (tmpNames.length > i) {
                        mATMap.put(tmpCids[i], tmpNames[i]);
                    }
                }
            }
            if (nameStr == null) {
                nameStr = tmpNameStr;
            } else {
                nameStr = nameStr + tmpNameStr;
            }
            lastNameStr = tmpNameStr;
            // 获取光标当前位置
            int curIndex = mEditText.getSelectionStart();
            // 把要@的人插入光标所在位置
            mEditText.getText().insert(curIndex, lastNameStr);
            // 通过输入@符号进入好友列表并返回@的人，要删除之前输入的@
            if (curIndex >= 1) {
                mEditText.getText().replace(curIndex - 1, curIndex, "");
            }
            setAtImageSpan(nameStr);
        }


    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_ok) {
            String mAtedMemId = "";
            String content = mEditText.getText().toString();
            if (mATMap.size() > 0) {
                //mATMap.size() > 0 可能会有@的成员，因为可能存在删除的，需要核对一遍
                StringBuffer remindEIdBuf = new StringBuffer();
                for (Map.Entry<String, String> entry : mATMap.entrySet()) {
                    if (content.contains(entry.getValue())) {
                        remindEIdBuf.append(entry.getKey());
                        remindEIdBuf.append(",");
                    }
                }
                if (remindEIdBuf.length() > 0) {
                    mAtedMemId = remindEIdBuf.substring(0, remindEIdBuf.length() - 1).toString();
                }
                mATMap.clear();
                mEditText.setText("");
                Toast.makeText(MainActivity.this, "the uids your at is  " + mAtedMemId, Toast.LENGTH_SHORT).show();
            }
        }
    }

    class MyInputFilter implements InputFilter {

        @Override
        public CharSequence filter(CharSequence source, int start, int end,
                                   Spanned dest, int dstart, int dend) {
            if (source.toString().equalsIgnoreCase("@")
                    || source.toString().equalsIgnoreCase("＠")) {
                startActivityForResult(new Intent(MainActivity.this, UserSelectActivity.class), REQUEST_CODE_SELECTUSER);
            }
            return source;
        }
    }


    private void setAtImageSpan(String nameStr) {

        String content = String.valueOf(mEditText.getText());
        if (content.endsWith("@") || content.endsWith("＠")) {
            content = content.substring(0, content.length() - 1);
        }
        String tmp = content;
        SpannableString ss = new SpannableString(tmp);
        if (nameStr != null) {
            String[] names = nameStr.split(" ");
            if (names != null && names.length > 0) {
                for (String name : names) {
                    if (name != null && name.trim().length() > 0) {
                        final Bitmap bmp = getNameBitmap(name);

                        // 这里会出现删除过的用户，需要做判断，过滤掉
                        if (tmp.indexOf(name) >= 0
                                && (tmp.indexOf(name) + name.length()) <= tmp
                                .length()) {

                            // 把取到的要@的人名，用DynamicDrawableSpan代替
                            ss.setSpan(
                                    new DynamicDrawableSpan(
                                            DynamicDrawableSpan.ALIGN_BASELINE) {

                                        @Override
                                        public Drawable getDrawable() {
                                            BitmapDrawable drawable = new BitmapDrawable(
                                                    getResources(), bmp);
                                            drawable.setBounds(0, 0,
                                                    bmp.getWidth(),
                                                    bmp.getHeight());
                                            return drawable;
                                        }
                                    }, tmp.indexOf(name),
                                    tmp.indexOf(name) + name.length(),
                                    SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }
                    }
                }
            }
        }
        mEditText.setTextKeepState(ss);
    }

    /**
     * 把返回的人名，转换成bitmap
     */
    private Bitmap getNameBitmap(String name) {
        /* 把@相关的字符串转换成bitmap 然后使用DynamicDrawableSpan加入输入框中 */
        name = "" + name;
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setAntiAlias(true);
        paint.setTextSize(40);
        Rect rect = new Rect();
        paint.getTextBounds(name, 0, name.length(), rect);
        // 获取字符串在屏幕上的长度
        int width = (int) (paint.measureText(name));
        final Bitmap bmp = Bitmap.createBitmap(width, rect.height(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);
        canvas.drawText(name, rect.left, rect.height() - rect.bottom, paint);
        return bmp;
    }


}
