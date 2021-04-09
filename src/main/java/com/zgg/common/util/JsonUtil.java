package com.zgg.common.util;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;


/**
 * Json工具类
 *
 * @author
 * @date 2015年1月13日
 */
public class JsonUtil {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(JsonUtil.class);

    private static ObjectMapper objectmapper = new ObjectMapper();

    /**
     * 把任何对象转换成Json字符串
     *
     * @param object
     * @return Json字符串
     */
    public static String objectToJson(Object object) {
        Writer strWriter = new StringWriter();
        try {
            objectmapper.writeValue(strWriter, object);
        } catch (Exception e) {
            logger.error("JSON解析异常:", e);
        }
        String json = strWriter.toString();
        return json;
    }

    /**
     * 把任何对象转换成Json字符串 再转为byte
     *
     * @param object
     * @return Json字符串 byte[]
     */
    public static byte[] objectToJsonByte(Object object) {
        Writer strWriter = new StringWriter();
        try {
            objectmapper.writeValue(strWriter, object);
        } catch (Exception e) {
            logger.error("JSON解析异常:", e);
        }
        String json = strWriter.toString();
        return json.getBytes();
    }

    /**
     * Json串转Object
     *
     * @param json  需要转的JSON
     * @param clazz 需要转换的Object
     * @return 实体对象
     */
    public static <T> T jsonToObject(String json, Class<T> clazz) {
        T obj = null;
        try {
            obj = objectmapper.readValue(json, clazz);
        } catch (Exception e) {
            logger.error("JSON解析异常:", e);
        }
        return obj;
    }

    /**
     * Json串byte数组转Object
     *
     * @param byteJson  需要转的JSON byte
     * @param clazz 需要转换的Object
     * @return 实体对象
     */
    public static <T> T byteJsonToObject(byte[] byteJson, Class<T> clazz) {
        T obj = null;
        try {
            obj = objectmapper.readValue(new String(byteJson), clazz);
        } catch (Exception e) {
            logger.error("JSON解析异常:", e);
        }
        return obj;
    }

    /**
     * 根据传入的KEY从JSON串中取出对应的VALUE
     *
     * @param json
     * @param key
     * @return
     */
    public static String jsonToStringByKey(String json, String key) {
        try {
            JsonNode rootNode = objectmapper.readTree(json);
            String value = rootNode.path(key).asText();
            return value;
        } catch (Exception e) {
            logger.error("JSON解析异常:", e);
            return null;
        }
    }

    /**
     * Json串转换成Map
     *
     * @param json
     * @return
     */
    public static Map toMap(String json) {
        try {
            return objectmapper.readValue(json, Map.class);
        } catch (Exception e) {
            logger.error("JSON解析异常:", e);
            return null;
        }
    }


}
