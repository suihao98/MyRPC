package com.test.register;

import com.test.loadbalance.RandomLoadBalance;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.util.List;

@Component
public class ZkServiceRegister implements ServiceRegister {

    // curator 提供的zookeeper客户端
    private CuratorFramework client;

    public ZkServiceRegister(@Value("${rpc.registry_address}") String connectString){
        // 指数时间重试
        // baseSleepTimeMs参数代表两次连接的等待时间(单位毫秒ms),maxRetries参数表示最大的尝试连接次数
        RetryPolicy policy = new ExponentialBackoffRetry(3000, 10);
        // zookeeper的地址固定，不管是服务提供者还是，消费者都要与之建立连接
        // sessionTimeoutMs 与 zoo.cfg中的tickTime 有关系，
        // zk还会根据minSessionTimeout与maxSessionTimeout两个参数重新调整最后的超时值。默认分别为tickTime 的2倍和20倍
        // 使用心跳监听状态
        client = CuratorFrameworkFactory.builder()
                .connectString(connectString) //注册中心ZooKeeper服务的地址和端口号
                .sessionTimeoutMs(60000)
                .connectionTimeoutMs(15000)
                .retryPolicy(policy) // retryPolicy参数是指在连接ZK服务过程中重新连接测策略.
                .namespace("MyRPCDemo")
                .build();
        client.start();
        System.out.println("zookeeper 连接成功");
    }


    @Override
    public void register(String serviceName, InetSocketAddress serverAddress){
        try {
            // serviceName创建成永久节点，服务提供者下线时，不删服务名，只删地址
            // 首次注册服务
            if(client.checkExists().forPath("/" + serviceName) == null){
                client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath("/" + serviceName);
            }
            // 路径地址，一个/代表一个节点
            String path = "/" + serviceName +"/"+ getServiceAddress(serverAddress);
            // 临时节点，服务器下线就删除节点
            client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(path);
        } catch (Exception e) {
            System.out.println("此服务已存在");
        }
    }
    // 根据服务名返回地址
    @Override
    public InetSocketAddress serviceDiscovery(String serviceName) {
        try {
            List<String> strings = client.getChildren().forPath("/" + serviceName);
            // 负载均衡
            RandomLoadBalance balance = new RandomLoadBalance();
            String address = balance.balance(strings);
            return parseAddress(address);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // 地址 -> XXX.XXX.XXX.XXX:port 字符串
    private String getServiceAddress(InetSocketAddress serverAddress) {
        return serverAddress.getHostName() +
                ":" +
                serverAddress.getPort();
    }
    // 字符串解析为地址
    private InetSocketAddress parseAddress(String address) {
        String[] result = address.split(":");
        return new InetSocketAddress(result[0], Integer.parseInt(result[1]));
    }


}