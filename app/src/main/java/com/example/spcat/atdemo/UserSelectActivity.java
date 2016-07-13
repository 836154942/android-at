package com.example.spcat.atdemo;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.spcat.atdemo.domain.User;

import java.util.ArrayList;
import java.util.List;

public class UserSelectActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {


    private ListView mListView;
    private Adapter mAdapter;
    private List<User> mList;//the  data source
    public static String TAG_USERID = "userid";
    public static String TAG_USERNAME = "username";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_select);

        mList = new ArrayList<>();
        mList.add(new User("Tom1", "1"));
        mList.add(new User("Tom2", "2"));
        mList.add(new User("Tom3", "3"));
        mList.add(new User("Tom4", "4"));
        mList.add(new User("Tom5", "5"));
        mList.add(new User("Tom6", "6"));

        initView();
    }

    private void initView() {
        mListView = (ListView) findViewById(R.id.list_view);
        mAdapter = new Adapter(this);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Intent intent = new Intent();
        intent.putExtra(TAG_USERID, mList.get(position).getId() + " ");
        intent.putExtra(TAG_USERNAME, "@" + mList.get(position).getNickName() + " ");
        setResult(RESULT_OK, intent);
        finish();
    }


    class Adapter extends BaseAdapter {
        private Context mContext;

        public Adapter(Context c) {
            mContext = c;
        }

        @Override
        public int getCount() {
            return mList == null ? 0 : mList.size();
        }

        @Override
        public Object getItem(int position) {
            return mList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_user_listview, null);
                holder = new ViewHolder();
                holder.mIdTV = (TextView) convertView.findViewById(R.id.tv_id);
                holder.mNameTV = (TextView) convertView.findViewById(R.id.tv_name);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.mIdTV.setText(mList.get(position).getId());
            holder.mNameTV.setText(mList.get(position).getNickName());
            return convertView;
        }
    }

    class ViewHolder {
        private TextView mNameTV, mIdTV;
    }
}
