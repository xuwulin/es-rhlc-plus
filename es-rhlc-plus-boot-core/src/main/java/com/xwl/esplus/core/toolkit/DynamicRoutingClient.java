package com.xwl.esplus.core.toolkit;

import org.elasticsearch.client.RestHighLevelClient;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description:
 * @Author: hl
 * @Date: 2022/10/28 16:51
 */
public class DynamicRoutingClient {

    private static final Map<String, RestHighLevelClient> clientMap = new ConcurrentHashMap<>();

    public static void addClient(String client, RestHighLevelClient restHighLevelClient) {
        clientMap.put(client, restHighLevelClient);
    }

    public static void removeClient(String client) {
        clientMap.remove(client);
    }

    public static RestHighLevelClient getClient(String client) {
        return clientMap.get(client);
    }

    public static boolean containsClient(String primary) {
        return clientMap.containsKey(primary);
    }
}
