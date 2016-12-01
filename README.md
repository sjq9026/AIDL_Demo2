# AIDL_Demo2
CopyOnWriteArrayList<Book> mBooks = new CopyOnWriteArrayList<>();
使用CopyOnWriteArrayList可以线程安全地遍历，因为如果另外一个线程在遍历的时候修改List的话，实际上会拷贝出一个新的List上修改，而不影响当前正在被遍历的List。


RemoteCallbackList（远程接口回调）即在AIDL文件中声明接口。在服务端的service中调用，在客户端实现接口。
Service通过客户端注册的回调来调用客户端的方法。RemoteCallbackList就是用来保存注册进来的回调实例，它自动处理了Link-To-Death的问题，当客户端意外退出时，自动删掉对应的实例。
