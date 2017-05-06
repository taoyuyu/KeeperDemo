/**
 * Created by yutao on 17/5/6.
 */
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ZookeeperTest {
    public static void main(String[] args) throws IOException, InterruptedException {
        ZooKeeper zooKeeper = new ZooKeeper("123.207.4.30:2181", 1000, new Watcher() {
            public void process(WatchedEvent event) {
                System.out.println("EventType:" + event.getType().name());
            }
        });
        //获取"/" node下的所有子node
        List<String> znodes = null;
        try {
            znodes = zooKeeper.getChildren("/", true);
        } catch (KeeperException e) {
            e.printStackTrace();
        }

        for (String path : znodes) {
            System.out.println(path);
        }
        //创建开放权限的持久化node "/test"

        try {
            String rs = zooKeeper.create("/test", "data".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            System.out.println(rs);
        } catch (KeeperException e) {
            System.err.println("Node already exists Error");
        }

        //同步获取"/test" node的数据
        Stat stat = new Stat();
        byte[] data = new byte[0];
        try {
            data = zooKeeper.getData("/test", true, stat);
        } catch (KeeperException e) {
            System.err.println("Node not exists Error");
        }

        System.out.println("value=" + new String(data));
        System.out.println(stat.toString());
        //异步获取"/test" node的数据
        zooKeeper.getData("/test", true, new AsyncCallback.DataCallback() {
            public void processResult(int rc, String path, Object ctx, byte[] data, Stat stat) {
                System.out.println(rc);
                System.out.println(path);
                System.out.println(ctx);
                System.out.printf(new String(data));
                System.out.println(stat.toString());
            }
        }, "Object ctx ..(提供的外部对象)");
        TimeUnit.SECONDS.sleep(10);
        zooKeeper.close();
    }
}
