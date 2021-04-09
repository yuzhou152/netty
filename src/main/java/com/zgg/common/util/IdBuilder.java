package com.zgg.common.util;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/**
 * ID生成器
 *
 * @author zyl
 *
 */
@Component
public class IdBuilder {


	/**
	 * 生成32位UUID，没有“-”
	 *
	 * @return String 32位字符
	 */
	public static String getID() {
		UUID id = UUID.randomUUID();
		return id.toString().replaceAll("-", "");
	}
}