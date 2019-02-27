import java.sql.Connection;

/**
 * @author: fanbopeng
 * @Date: 2019/2/27 13:35
 * @Description:数据库连接池
 */
public interface IConnectionPool {


        //获取连接池(重复利用机制)
       public    Connection getConnection();


         //释放连接(可回收机制)
      boolean closeConnection(Connection connection);

}
