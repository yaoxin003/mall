#mall
mall-member-service的dubbo端口：20880
mall-member-service：用户服务的后台端口8070
mall-member-web：用户服务的前台端口8080
mall-manage-service的dubbo端口：20881
mall-manage-service：系统管理服务的后台端口8071
mall-manage-web：系统管理服务的前台端口8081
mall-item-web：商品详情的前台端口8082
mall-search-service的dubbo端口：20883
mall-search-service：搜索服务的后台端口8073
mall-search-web：搜索服务的前台端口8083
mall-cart-service的dubbo端口：20884
mall-cart-service：购物车服务的后台端口8074
mall-cart-web：购物车服务的前台端口8084
mall-passport-web：认证中心的前台端口8085
mall-order-service：订单服务的后台端口8076
mall-order-web：订单服务的前台端口8086
mall-payment：支付系统前后台合一端口：8087
mall-seckill：秒杀系统前后台合一端口：8088
gware-manage：库存系统前后台合一端口：9001# mall

启动项目相关软件
#zookeeper
#dubbo-admin
#FastDFS
#redis
cd /etc/redis
redis-server ./redis6379.conf
redis-server ./redis6389.conf
#ngnix
/usr/local/nginx/sbin/nginx
#redisson
#elastic
su es(192.168.1.121)
cd /usr/local/elasticsearch
nohup bin/elasticsearch &
su es(192.168.1.122)
cd /usr/local/elasticsearch
nohup bin/elasticsearch &
#kibana
(192.168.1.121)
cd /usr/local/kibana
nohup bin/kibana &
(192.168.1.122)
cd /usr/local/kibana
nohup bin/kibana &
#cerebro
C:\yxtmp\cerebro-0.8.3\bin\cerebro.bat