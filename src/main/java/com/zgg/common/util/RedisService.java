package com.zgg.common.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Description: Redis工具类
 * Author: zy
 * Date: 2020-03-25 20:32:28
 */
@Component
public class RedisService {

    //定义释放锁的lua脚本
    private final static DefaultRedisScript<Long> UNLOCK_LUA_SCRIPT = new DefaultRedisScript<>(
            "if redis.call('get',KEYS[1]) == ARGV[1] then return redis.call('del',KEYS[1]) else return -1 end"
            , Long.class
    );

    /**
     * 注入redisTemplate bean
     */
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    // ---------------------------------------- key ----------------------------------------

    /**
     * 删除key
     *
     * @param key 可以传一个值 或多个
     */
    public void del(String... key) {
        if (key != null && key.length > 0) {
            if (key.length == 1) {
                redisTemplate.delete(key[0]);
            } else {
                redisTemplate.delete(CollectionUtils.arrayToList(key));
            }
        }
    }

    /**
     * 根据正则获取所有key（可能会影响性能慎用）
     * 1. KEYS * 匹配数据库中所有 key 。
     * 2. KEYS h?llo 匹配 hello ， hallo 和 hxllo 等。
     * 3. KEYS h*llo 匹配 hllo 和 heeeeello 等。
     * 4. KEYS h[ae]llo 匹配 hello 和 hallo ，但不匹配 hillo 。
     * 5. 特殊符号用 \ 隔开
     *
     * @param key
     * @param pattern
     * @return
     */
    public Set<String> keysByPattern(String key, String pattern) {
        return redisTemplate.keys(key + pattern);
    }

    /**
     * 从当前数据库中随机返回一个key
     *
     * @return
     */
    public String randomKey() {
        return redisTemplate.randomKey();
    }

    /**
     * 返回key的剩余生存时间
     * 1. 当 key 不存在时，返回 -2 。
     * 2. 当 key 存在但没有设置剩余生存时间时，返回 -1 。
     * 3. 否则，以秒为单位，返回 key 的剩余生存时间。
     *
     * @param key
     * @return
     */
    public long ttl(String key) {
        return redisTemplate.getExpire(key);
    }

    /**
     * 判断key是否存在
     *
     * @param key 键
     * @return true 存在 false不存在
     */
    public boolean hasKey(String key) {
        try {
            return redisTemplate.hasKey(key);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 将 key 改名为 newkey
     *
     * @param key
     * @param newKey
     */
    public void rename(String key, String newKey) {
        redisTemplate.rename(key, newKey);
    }

    /**
     * 当newKey不存在时 将 key 改名为 newkey
     *
     * @param key
     * @param newKey
     */
    public void renamenx(String key, String newKey) {
        redisTemplate.renameIfAbsent(key, newKey);
    }

    /**
     * 指定缓存失效时间
     *
     * @param key  键
     * @param time 时间(秒)
     * @return
     */
    public boolean expire(String key, long time) {
        try {
            if (time > 0) {
                redisTemplate.expire(key, time, TimeUnit.SECONDS);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 清空失效时间 使之成为永久化
     *
     * @param key 键
     * @return
     */
    public boolean persist(String key) {
        try {
            redisTemplate.persist(key);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 前缀模糊匹配删除
     *
     * @param prefix
     */
    public void deleteByPrefix(String prefix) {
        Set<String> keys = redisTemplate.keys(prefix + "*");
        if (!CollectionUtils.isEmpty(keys)) {
            redisTemplate.delete(keys);
        }
    }

    /**
     * 后缀模糊匹配删除
     *
     * @param suffix
     */
    public void deleteBySuffix(String suffix) {
        Set<String> keys = redisTemplate.keys("*" + suffix);
        if (!CollectionUtils.isEmpty(keys)) {
            redisTemplate.delete(keys);
        }
    }


    // ---------------------------------------- string ----------------------------------------

    /**
     * 普通缓存放入
     *
     * @param key   键
     * @param value 值
     */
    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 批量插入
     * MSET 是一个原子性(atomic)操作
     *
     * @param map
     */
    public void mset(Map<String, Object> map) {
        redisTemplate.opsForValue().multiSet(map);
    }

    /**
     * 当且仅当所有给定 key 都不存在 批量插入
     * msetnx 是一个原子性(atomic)操作
     *
     * @param map
     */
    public void msetnx(Map<String, Object> map) {
        redisTemplate.opsForValue().multiSetIfAbsent(map);
    }

    /**
     * 当key存在追加  反之插入
     *
     * @param key
     * @param value
     */
    public void append(String key, String value) {
        redisTemplate.opsForValue().append(key, value);
    }

    /**
     * 普通缓存放入并设置时间
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒) time要大于0 如果time小于等于0 将设置无限期
     * @return true成功 false 失败
     */
    public boolean set(String key, Object value, long time) {
        try {
            if (time > 0) {
                redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
            } else {
                set(key, value);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 用 value 参数覆写(overwrite)给定 key 所储存的字符串值，从偏移量 offset 开始。
     * 当key不存在时用"\x00"填充
     *
     * @param key
     * @param value
     * @param offset
     */
    public void setRange(String key, String value, long offset) {
        redisTemplate.opsForValue().set(key, value, offset);
    }

    /**
     * 普通缓存获取
     *
     * @param key 键
     * @return 值
     */
    public Object get(String key) {
        return key == null ? null : redisTemplate.opsForValue().get(key);
    }

    /**
     * 批量获取
     *
     * @param keys
     * @return
     */
    public List<Object> mget(List<String> keys) {
        return redisTemplate.opsForValue().multiGet(keys);
    }

    /**
     * 返回原来的值并设置新值
     *
     * @param key
     * @param value
     * @return
     */
    public Object getSet(String key, Object value) {
        return redisTemplate.opsForValue().getAndSet(key, value);
    }

    /**
     * 根据索引返回字符串
     *
     * @param key
     * @param start
     * @param end
     * @return
     */
    public String getRange(String key, long start, long end) {
        return redisTemplate.opsForValue().get(key, start, end);
    }

    /**
     * 返回字符串长度
     *
     * @param key
     * @return
     */
    public long strlen(String key) {
        return redisTemplate.opsForValue().size(key);
    }

    /**
     * 递增
     *
     * @param key
     * @return
     */
    public long incr(String key) {
        return redisTemplate.opsForValue().increment(key);
    }

    /**
     * 递减
     *
     * @param key
     * @return
     */
    public long decr(String key) {
        return redisTemplate.opsForValue().decrement(key);
    }

    /**
     * 递增**个
     *
     * @param key
     * @param delta
     * @return
     */
    public long incrBy(String key, long delta) {
        return redisTemplate.opsForValue().increment(key, delta);
    }

    /**
     * 递减**个
     *
     * @param key
     * @param delta
     * @return
     */
    public long decrBy(String key, long delta) {
        return redisTemplate.opsForValue().decrement(key, delta);
    }


    // ---------------------------------------- hash ----------------------------------------


    /**
     * 向一张hash表中放入数据,如果不存在将创建
     *
     * @param key   键
     * @param item  项
     * @param value 值
     */
    public void hset(String key, String item, Object value) {
        redisTemplate.opsForHash().put(key, item, value);
    }

    /**
     * 当且仅当域 field 不存在创建 反之无效
     *
     * @param key   键
     * @param item  项
     * @param value 值
     */
    public Boolean hsetnx(String key, String item, Object value) {
        return redisTemplate.opsForHash().putIfAbsent(key, item, value);
    }

    /**
     * HashSet
     *
     * @param key 键
     * @param map 对应多个键值
     */
    public void hmset(String key, Map<String, Object> map) {
        redisTemplate.opsForHash().putAll(key, map);
    }

    /**
     * HashSet 并设置时间
     *
     * @param key  键
     * @param map  对应多个键值
     * @param time 时间(秒)
     */
    public void hmset(String key, Map<String, Object> map, long time) {
        redisTemplate.opsForHash().putAll(key, map);
        if (time > 0) {
            expire(key, time);
        }
    }

    /**
     * HashGet
     *
     * @param key  键 不能为null
     * @param item 项 不能为null
     * @return 值
     */
    public Object hget(String key, String item) {
        return redisTemplate.opsForHash().get(key, item);
    }

    /**
     * 获取hashKey对应的所有键值
     *
     * @param key 键
     * @return 对应的多个键值
     */
    public Map<Object, Object> hgetAll(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * 删除hash表中的值
     *
     * @param key  键 不能为null
     * @param item 项 可以使多个 不能为null
     */
    public void hdel(String key, Object... item) {
        redisTemplate.opsForHash().delete(key, item);
    }

    /**
     * 获取hash数量
     *
     * @param key
     * @return
     */
    public long hlen(String key) {
        return redisTemplate.opsForHash().size(key);
    }

    /**
     * 返回哈希表 key 中的所有域。
     *
     * @param key
     * @return
     */
    public Set<Object> hkeys(String key) {
        return redisTemplate.opsForHash().keys(key);
    }

    /**
     * 返回哈希表 key 中所有域的值。
     *
     * @param key
     * @return
     */
    public List<Object> hvals(String key) {
        return redisTemplate.opsForHash().values(key);
    }

    /**
     * 判断hash表中是否有该项的值
     *
     * @param key  键 不能为null
     * @param item 项 不能为null
     * @return true 存在 false不存在
     */
    public boolean hexists(String key, String item) {
        return redisTemplate.opsForHash().hasKey(key, item);
    }

    /**
     * hash递增 如果不存在,就会创建一个 并把新增后的值返回
     *
     * @param key  键
     * @param item 项
     * @return
     */
    public double hincr(String key, String item) {
        return redisTemplate.opsForHash().increment(key, item, 1);
    }

    /**
     * hash递增 如果不存在,就会创建一个 并把新增后的值返回
     *
     * @param key  键
     * @param item 项
     * @param by   要增加几(大于0)
     * @return
     */
    public double hincrBy(String key, String item, double by) {
        return redisTemplate.opsForHash().increment(key, item, by);
    }

    /**
     * hash递减
     *
     * @param key  键
     * @param item 项
     * @return
     */
    public double hdecr(String key, String item) {
        return redisTemplate.opsForHash().increment(key, item, -1);
    }

    /**
     * hash递减
     *
     * @param key  键
     * @param item 项
     * @param by   要减少记(小于0)
     * @return
     */
    public double hdecrBy(String key, String item, double by) {
        return redisTemplate.opsForHash().increment(key, item, -by);
    }


    // ---------------------------------------- list ----------------------------------------

    /**
     * 从最左边插入元素到列表
     *
     * @param key   键
     * @param value 值
     * @return
     */
    public void lpush(String key, Object value) {
        redisTemplate.opsForList().leftPush(key, value);
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @return
     */
    public void lpush(String key, List<Object> value) {
        redisTemplate.opsForList().leftPushAll(key, value);
    }

    /**
     * 从最右边插入元素到列表
     *
     * @param key   键
     * @param value 值
     * @return
     */
    public void rpush(String key, Object value) {
        redisTemplate.opsForList().rightPush(key, value);
    }

    /**
     * 从最左边弹出元素到列表
     *
     * @param key 键
     * @return
     */
    public Object lpop(String key) {
        return redisTemplate.opsForList().leftPop(key);
    }

    /**
     * 它是 LPOP 命令的阻塞版本，当给定列表内没有任何元素可供弹出的时候，连接将被BLPOP 命令阻塞，直到等待超时或发现可弹出元素为止。
     * 案例：事件提醒
     *
     * @param key     键
     * @param timeout 过期时间
     * @return
     */
    public Object blpop(String key, long timeout) {
        return redisTemplate.opsForList().leftPop(key, timeout, TimeUnit.SECONDS);
    }

    /**
     * 从最右边弹出元素到列表
     *
     * @param key 键
     * @return
     */
    public Object rpop(String key, Object value) {
        return redisTemplate.opsForList().rightPop(key);
    }

    /**
     * 获取list缓存的长度
     *
     * @param key 键
     * @return
     */
    public long llen(String key) {
        return redisTemplate.opsForList().size(key);
    }

    /**
     * 获取list缓存的内容
     *
     * @param key   键
     * @param start 开始
     * @param end   结束 0 到 -1代表所有值
     * @return
     */
    public List<Object> lrange(String key, long start, long end) {
        return redisTemplate.opsForList().range(key, start, end);
    }

    /**
     * 根据参数 count 的值，移除列表中与参数 value 相等的元素。
     * count 的值可以是以下几种：
     * count > 0 : 从表头开始向表尾搜索，移除与 value 相等的元素，数量为 count 。
     * count < 0 : 从表尾开始向表头搜索，移除与 value 相等的元素，数量为 count 的绝对值。
     * count = 0 : 移除表中所有与 value 相等的值。
     *
     * @param key
     * @param count
     * @param value
     * @return
     */
    public Long lrem(String key, long count, Object value) {
        return redisTemplate.opsForList().remove(key, count, value);
    }

    /**
     * 将列表 key 下标为 index 的元素的值设置为 value 。
     *
     * @param key   键
     * @param index 索引
     * @param value 值
     * @return
     */
    public void lset(String key, long index, Object value) {
        redisTemplate.opsForList().set(key, index, value);
    }

    /**
     * 对一个列表进行修剪(trim)，就是说，让列表只保留指定区间内的元素，不在指定区间之内的元素都将被删除。
     *
     * @param key
     * @param start
     * @param end
     */
    public void ltrim(String key, long start, long end) {
        redisTemplate.opsForList().trim(key, start, end);
    }

    /**
     * 通过索引 获取list中的值
     *
     * @param key   键
     * @param index 索引 index>=0时， 0 表头，1 第二个元素，依次类推；index<0时，-1，表尾，-2倒数第二个元素，依次类推
     * @return
     */
    public Object lindex(String key, long index) {
        return redisTemplate.opsForList().index(key, index);
    }

    /**
     * 命令 RPOPLPUSH 在一个原子时间内，执行以下两个动作：
     * 1. 将列表 source 中的最后一个元素(尾元素)弹出，并返回给客户端。
     * 2. 将 source 弹出的元素插入到列表 destination ，作为 destination 列表的的头元素。
     * 案例：安全的队列、循环列表
     *
     * @param source
     * @param destination
     * @return
     */
    public Object rpoplpush(String source, String destination) {
        return redisTemplate.opsForList().rightPopAndLeftPush(source, destination);
    }

    /**
     * 命令 RPOPLPUSH 在一个原子时间内，执行以下两个动作：（阻塞）
     * 1. 将列表 source 中的最后一个元素(尾元素)弹出，并返回给客户端。
     * 2. 将 source 弹出的元素插入到列表 destination ，作为 destination 列表的的头元素。
     * 案例：安全的队列、循环列表
     *
     * @param source
     * @param destination
     * @return
     */
    public Object brpoplpush(String source, String destination, long timeout) {
        return redisTemplate.opsForList().rightPopAndLeftPush(source, destination, timeout, TimeUnit.SECONDS);
    }


    // ---------------------------------------- set ----------------------------------------


    /**
     * 将数据放入set缓存
     *
     * @param key    键
     * @param values 值 可以是多个
     * @return 成功个数
     */
    public long sadd(String key, Object... values) {
        return redisTemplate.opsForSet().add(key, values);
    }

    /**
     * 移除值为value的
     *
     * @param key    键
     * @param values 值 可以是多个
     * @return 移除的个数
     */
    public long srem(String key, Object... values) {
        return redisTemplate.opsForSet().remove(key, values);
    }

    /**
     * 根据key获取Set中的所有值
     *
     * @param key 键
     * @return
     */
    public Set<Object> smembers(String key) {
        return redisTemplate.opsForSet().members(key);
    }

    /**
     * 根据value从一个set中查询,是否存在
     *
     * @param key   键
     * @param value 值
     * @return true 存在 false不存在
     */
    public boolean sisMember(String key, Object value) {
        return redisTemplate.opsForSet().isMember(key, value);
    }

    /**
     * 获取set缓存的长度
     *
     * @param key 键
     * @return
     */
    public long scard(String key) {
        return redisTemplate.opsForSet().size(key);
    }

    /**
     * 将 member 元素从 source 集合移动到 destination 集合。
     * SMOVE 是原子性操作。
     *
     * @param source
     * @param value
     * @param destination
     * @return
     */
    public Boolean smove(String source, Object value, String destination) {
        return redisTemplate.opsForSet().move(source, value, destination);
    }

    /**
     * 移除并随机返回
     *
     * @param key
     * @return
     */
    public Object spop(String key) {
        return redisTemplate.opsForSet().pop(key);
    }

    /**
     * 随机返回 不对原集合操作
     *
     * @param key
     * @return
     */
    public Object srandomMember(String key) {
        return redisTemplate.opsForSet().randomMember(key);
    }

    /**
     * 随机返回 不对原集合操作
     *
     * @param key
     * @return
     */
    public Set<Object> srandomMemberByCount(String key, long count) {
        return redisTemplate.opsForSet().distinctRandomMembers(key, count);
    }

    /**
     * 交集
     *
     * @param key
     * @return
     */
    public Set<Object> sinter(String key, String otherKey) {
        return redisTemplate.opsForSet().intersect(key, otherKey);
    }

    /**
     * 并集
     *
     * @param key
     * @return
     */
    public Set<Object> sunion(String key, String otherKey) {
        return redisTemplate.opsForSet().union(key, otherKey);
    }

    /**
     * 差集
     *
     * @param key
     * @return
     */
    public Set<Object> sdiff(String key, String otherKey) {
        return redisTemplate.opsForSet().difference(key, otherKey);
    }


    // ---------------------------------------- zset ----------------------------------------

    /**
     * 添加元素
     *
     * @param key
     * @param value
     * @return
     */
    public boolean zadd(String key, Object value, double score) {
        return redisTemplate.opsForZSet().add(key, value, score);
    }

    /**
     * 移除某个某些元素
     *
     * @param key
     * @param values
     * @return
     */
    public long zrem(String key, Object... values) {
        return redisTemplate.opsForZSet().remove(key, values);
    }

    /**
     * 获取总数
     *
     * @param key
     * @return
     */
    public long zcard(String key) {
        return redisTemplate.opsForZSet().size(key);
    }

    /**
     * 返回有序集 key 中， score 值在 min 和 max 之间(默认包括 score 值等于 min 或 max )的成员的数量
     *
     * @param key
     * @param min
     * @param max
     * @return
     */
    public long zcount(String key, long min, long max) {
        return redisTemplate.opsForZSet().count(key, min, max);
    }

    /**
     * 返回有序集 key 中，成员 member 的 score 值
     *
     * @param key
     * @param member
     * @return
     */
    public Double zscore(String key, Object member) {
        return redisTemplate.opsForZSet().score(key, member);
    }

    /**
     * 为有序集 key 的成员 member 的 score 值加上增量 increment
     *
     * @param key
     * @param member
     * @param increment
     * @return
     */
    public Double zincrBy(String key, Object member, double increment) {
        return redisTemplate.opsForZSet().incrementScore(key, member, increment);
    }

    /**
     * 从小到大获取元素
     *
     * @param key
     * @param start
     * @param end
     * @return
     */
    public Set<Object> zrange(String key, long start, long end) {
        return redisTemplate.opsForZSet().range(key, start, end);
    }

    /**
     * 从大到小获取元素
     *
     * @param key
     * @param start
     * @param end
     * @return
     */
    public Set<Object> zrevrange(String key, long start, long end) {
        return redisTemplate.opsForZSet().reverseRange(key, start, end);
    }

    /**
     * 返回有序集 key 中，所有 score 值介于 min 和 max 之间(包括等于 min 或 max )的成员 从小到大
     *
     * @param key
     * @param min
     * @param max
     * @return
     */
    public Set<Object> zrangeByScore(String key, double min, double max) {
        return redisTemplate.opsForZSet().rangeByScore(key, min, max);
    }

    /**
     * 返回有序集 key 中，所有 score 值介于 min 和 max 之间(包括等于 min 或 max )的成员 从大到小
     *
     * @param key
     * @param min
     * @param max
     * @return
     */
    public Set<ZSetOperations.TypedTuple<Object>> zrevrangeByScore(String key, double min, double max) {
        return redisTemplate.opsForZSet().reverseRangeByScoreWithScores(key, min, max);
    }

    /**
     * 判断有序集合是否存在该元素
     *
     * @param key
     * @param value
     * @return false 不存在  true 存在
     */
    public boolean zisMember(String key, Object value) {
        Long index = redisTemplate.opsForZSet().rank(key, value);
        return index != null;
    }

    /**
     * 有序集 key 中成员 member 的排名   从小到大
     *
     * @param key
     * @param member
     * @return
     */
    public long zrank(String key, Object member) {
        return redisTemplate.opsForZSet().rank(key, member);
    }

    /**
     * 有序集 key 中成员 member 的排名   从大到小
     *
     * @param key
     * @param member
     * @return
     */
    public long zrevrank(String key, Object member) {
        return redisTemplate.opsForZSet().reverseRank(key, member);
    }

    /**
     * 交集 并将该交集(结果集)储存到 destKey
     *
     * @param key
     * @param other
     * @param destKey
     * @return
     */
    public long zinterStore(String key, String other, String destKey) {
        return redisTemplate.opsForZSet().intersectAndStore(key, other, destKey);
    }

    /**
     * 并集 并将该并集(结果集)储存到 destKey
     *
     * @param key
     * @param other
     * @param destKey
     * @return
     */
    public long zunionStore(String key, String other, String destKey) {
        return redisTemplate.opsForZSet().unionAndStore(key, other, destKey);
    }

    // ---------------------------------------- transaction ----------------------------------------

    /**
     * 开启事务
     */
    public void multi() {
        redisTemplate.multi();
    }

    /**
     * 执行事务
     */
    public void exec() {
        redisTemplate.exec();
    }

    /**
     * 监视一个key
     */
    public void watch(String key) {
        redisTemplate.watch(key);
    }

    /**
     * 监视多个key
     */
    public void watch(List<String> keys) {
        redisTemplate.watch(keys);
    }

    /**
     * 取消监视
     */
    public void unwatch() {
        redisTemplate.unwatch();
    }

    /**
     * 取消事务
     */
    public void discard() {
        redisTemplate.discard();
    }


    // ---------------------------------------- lock ----------------------------------------

    /**
     * 加锁（原子性）
     * setnx是『SET if Not eXists』(如果不存在，则 SET)的简写
     *
     * @param key        锁的 key 值
     * @param requestId  请求id，防止解了不该由自己解的锁 (随机生成)
     * @param expireTime 锁的超时时间(秒)
     * @param retryTimes 获取锁的重试次数
     * @return true 成功  false 失败
     */
    public boolean lock(String key, String requestId, long expireTime, int retryTimes) throws InterruptedException {
        int count = 0;
        while (true) {
            if (redisTemplate.opsForValue().setIfAbsent(key, requestId, expireTime, TimeUnit.SECONDS)) {
                System.out.println("--zy--RedisUtil--lock：第" + count + "次加锁成功，   key:" + key + "，   requestId:" + requestId);
                return true;
            } else {
                count++;
                System.out.println("--zy--RedisUtil--lock：第" + count + "次加锁失败，   key:" + key + "，   requestId:" + requestId);
                if (retryTimes == count) {
                    return false;
                } else {
                    Thread.sleep(1000);
                    continue;
                }
            }
        }
    }

    /**
     * 释放锁（value确保不会误删除）
     *
     * @param key
     * @param requestId
     */
    public void unlock(String key, String requestId) {
        redisTemplate.execute(UNLOCK_LUA_SCRIPT, Arrays.asList(key), requestId);
    }
}
