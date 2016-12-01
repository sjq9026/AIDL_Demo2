// IBookManagerInterface.aidl
package com.android.sjq.aidl_service;

// Declare any non-default types here with import statements
import com.android.sjq.aidl_service.Book;
import com.android.sjq.aidl_service.IOnNewBookArrivedListener;
interface IBookManagerInterface {
        List<Book> getBookList();
        void addBook(in Book book);
        void registerListener(in IOnNewBookArrivedListener listener);
        void unregisterListener(in IOnNewBookArrivedListener listener);
}
