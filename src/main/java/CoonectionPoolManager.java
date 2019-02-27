import java.sql.Connection;
import java.util.Collection;

/**
 * @author: fanbopeng
 * @Date: 2019/2/27 14:42
 * @Description:
 */
public class CoonectionPoolManager {


    private  static CollectionPool collectionPool=new CollectionPool(new DbBean());

    //获取连接池(重复利用机制)
    public  static Connection getConnection(){


        return collectionPool.getConnection();
    }


    //释放连接(可回收机制)
     static boolean closeConnection(Connection connection){


         return  collectionPool.closeConnection(connection);
    }

}
