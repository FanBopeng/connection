import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author: fanbopeng
 * @Date: 2019/2/27 13:38
 * @Description:
 */
public class CollectionPool implements IConnectionPool {

    private List<Connection> freeConnection= new Vector<Connection>();
    private List<Connection> activeConnection= new Vector<Connection>();
    private AtomicInteger  countConnection=new AtomicInteger();

    private DbBean bean;


    public CollectionPool(DbBean bean) {

        //获取配置文件
        this.bean=bean;
    }

    private void init(){

        if (bean==null){

             throw new RuntimeException();
        }

        //1.获取初始化连接
        for (int i=0;i<bean.getInitConnections();i++){
            //2.创建connection连接
            Connection connection = newConnection();
            if (connection!=null){
                //3.存放在freeconnection集合
                freeConnection.add(connection);

            }


        }

    }
            //2.创建connection连接
    private Connection newConnection(){
        Connection connection=null ;
        try {
            Class.forName(bean.getDriverName());
           connection = DriverManager.getConnection(bean.getUrl(), bean.getUrl(), bean.getPassword());
            countConnection.getAndIncrement();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }




    public  synchronized Connection getConnection() {
            //怎么知道当前的创建的连接>最大连接数
        Connection connection=null;
        if (countConnection.get()>bean.getMaxConnections()){
            //大于最大活动连接数 进行等待
            try {
                wait(bean.getConnTimeOut());
                connection =getConnection();

            } catch (InterruptedException e) {
                e.printStackTrace();
            }


        }else {


            //判断空闲线程是否有数据
            if (freeConnection.size()>0){
                //空闲线程存在连接
                 connection = freeConnection.remove(0);
            }else {

                connection=newConnection();

            }

            //判断连接是否可用
            boolean available = isAvailable(connection);
            if (available){
                //存放在活动线程
                activeConnection.add(connection);
            }else {
                    //递归进行重试
                countConnection.decrementAndGet();
                 connection = getConnection();

            }

        }


        return  connection;
    }

    public boolean isAvailable(Connection connection){
        try {
            if (connection==null||connection.isClosed()){

                return false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return  true;




    }

    public  synchronized boolean closeConnection(Connection connection) {

        try {
            if (isAvailable(connection)){

                if (freeConnection.size()<bean.getMaxConnections()){

                    //空闲线程没满
                    freeConnection.add(connection);

                }else {
                    //已经满了
                    connection.close();

                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        activeConnection.remove(connection);
        countConnection.decrementAndGet();
        notifyAll();

        return true;
    }



}
