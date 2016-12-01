// IOnNewBookArrivedListener.aidl
package com.android.sjq.aidl_service;

// Declare any non-default types here with import statements
import com.android.sjq.aidl_service.Book;
interface IOnNewBookArrivedListener {
   void onNewBookArrivedListener(in Book book);
}
