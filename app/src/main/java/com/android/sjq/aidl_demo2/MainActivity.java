package com.android.sjq.aidl_demo2;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.android.sjq.aidl_service.Book;
import com.android.sjq.aidl_service.IBookManagerInterface;
import com.android.sjq.aidl_service.IOnNewBookArrivedListener;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button getBook_btn;
    private Button addBook_btn;
    private IBookManagerInterface mBookManagerInterface;
    private static final int MESSAGE_NEW_BOOK_ARRIVED = 1;


    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_NEW_BOOK_ARRIVED:
                    Log.i("Book", "new book arrived !!!");
                    break;
                default:
                    super.handleMessage(msg);
            }

        }
    };


    //service端调用，client端实现
    IOnNewBookArrivedListener onNewBookArrivedListener = new IOnNewBookArrivedListener.Stub() {
        @Override
        public void onNewBookArrivedListener(Book book) throws RemoteException {
            mHandler.obtainMessage(MESSAGE_NEW_BOOK_ARRIVED, book).sendToTarget();
        }

    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.android.sjq.aidl_service", "com.android.sjq.aidl_service.BookManagerService"));
        bindService(intent, mServiceConnection, BIND_AUTO_CREATE);


    }

    ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mBookManagerInterface = IBookManagerInterface.Stub.asInterface(iBinder);

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };


    private void initView() {
        getBook_btn = (Button) findViewById(R.id.get_book_list_btn);
        addBook_btn = (Button) findViewById(R.id.add_book_btn);
        getBook_btn.setOnClickListener(this);
        addBook_btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.get_book_list_btn:
                try {
                    Log.i("Book", mBookManagerInterface.getBookList().toString());
                } catch (RemoteException e) {
                    e.printStackTrace();
                }

                break;
            case R.id.add_book_btn:
                if (mBookManagerInterface != null) {
                    try {
                        mBookManagerInterface.addBook(new Book(mBookManagerInterface.getBookList().size() + 1, "Android群英传"));
                        mBookManagerInterface.registerListener(onNewBookArrivedListener);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }


    @Override
    protected void onDestroy() {
        if (mBookManagerInterface != null && mBookManagerInterface.asBinder().isBinderAlive()) {
            try {
                mBookManagerInterface.unregisterListener(onNewBookArrivedListener);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        unbindService(mServiceConnection);
        super.onDestroy();
    }
}
