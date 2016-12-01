package com.android.sjq.aidl_service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Administrator on 2016/12/1.
 */

public class BookManagerService extends Service {
    CopyOnWriteArrayList<Book> mBooks = new CopyOnWriteArrayList<>();
    //采用以下集合会导致取消注册的时候找不到对象，因为反序列化之后虽然对象的属性相同，但是两者并不是同一个对象
    //CopyOnWriteArrayList<IOnNewBookArrivedListener> mListeners = new CopyOnWriteArrayList<>();
    RemoteCallbackList<IOnNewBookArrivedListener> mListeners = new RemoteCallbackList<>();
    private AtomicBoolean mAtomicBoolean = new AtomicBoolean(false);

    @Override
    public void onCreate() {
        super.onCreate();
        mBooks.add(new Book(1, "安卓开发艺术之旅"));
        mBooks.add(new Book(2, "第一行代码 第一版"));
        new Thread(new WorkerThread()).start();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    IBookManagerInterface.Stub binder = new IBookManagerInterface.Stub() {
        @Override
        public List<Book> getBookList() throws RemoteException {
            return mBooks;
        }

        @Override
        public void addBook(Book book) throws RemoteException {
            mBooks.add(book);
            onNewBookArrived(book);
        }

        @Override
        public void registerListener(IOnNewBookArrivedListener listener) throws RemoteException {
            mListeners.register(listener);
        }

        @Override
        public void unregisterListener(IOnNewBookArrivedListener listener) throws RemoteException {
            mListeners.unregister(listener);
        }
    };


    class WorkerThread implements Runnable {
        @Override
        public void run() {
            try {
                while (!mAtomicBoolean.get()) {
                    Thread.sleep(5000);
                    int bookId = mBooks.size() + 1;
                    Book book = new Book(bookId, "第一行代码第" + bookId + "版");
                    onNewBookArrived(book);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void onNewBookArrived(Book book) {
        //添加新书，并且通知所有注册过的客户端有新书了
        mBooks.add(book);
        final int N = mListeners.beginBroadcast();
        for (int i = 0; i < N; i++) {
            IOnNewBookArrivedListener L = mListeners.getBroadcastItem(i);
            if (L != null) {
                try {
                    L.onNewBookArrivedListener(book);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            mListeners.finishBroadcast();
        }
    }

}
