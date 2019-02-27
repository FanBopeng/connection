import java.sql.Connection;
import java.util.List;
import java.util.Vector;

/**
 * @author: fanbopeng
 * @Date: 2019/2/27 13:16
 * @Description:
 *
 * 核心参数
 * 一. 1.空闲线程
 *     2.活动线程
 *二. 1.初始化线程池(初始化空闲线程)
 *三. 调用getconnection方法 获取连接
 *      1.先去空闲集合获取当前连接存放在 activeConnection中
 *      2.
 *
 *四. 调用releaseConnecton方法 释放连接  资源回收
 *      1.获取活动集合 放到freeConnection中去
 *
 */
public class Test1 {


    private List<Connection> freeConnection= new Vector<Connection>();
    private List<Connection> activeConnection= new Vector<Connection>();

    public static void main(String[] args) {

        ThreadConnection threadConnection=new ThreadConnection();
        for (int i=0;i<3;i++){

            Thread thread =new Thread(threadConnection,"线程"+i);
            thread.start();

        }


    }
}
class  ThreadConnection implements  Runnable{


    public void run() {
 for (int i=0;i<10;i++){

     Connection connection = CoonectionPoolManager.getConnection();
     System.out.println(Thread.currentThread().getName()+ connection+"");
     CoonectionPoolManager.closeConnection(connection);
 }
    }
}



