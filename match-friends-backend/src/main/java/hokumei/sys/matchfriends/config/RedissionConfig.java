package hokumei.sys.matchfriends.config;

import lombok.Data;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.redisson.config.TransportMode;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Redis配置
 */
@Configuration
@ConfigurationProperties(prefix = "spring.redis")
@Data
public class RedissionConfig {

    private String port;

    private String host;

    private Integer database;
    @Bean
    public RedissonClient redisClient(){
        Config config = new Config();
        config.setTransportMode(TransportMode.NIO);
        SingleServerConfig singleServerConfig = config.useSingleServer();
        String address = String.format("redis://%s:%d", host, Integer.valueOf(port));
        //可以用"rediss://"来启用SSL连接
        singleServerConfig.setAddress(address).setDatabase(database);
        RedissonClient redisson = Redisson.create(config);
        return redisson;
    }
}
