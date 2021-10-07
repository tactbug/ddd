package com.tactbug.ddd.common.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;
import java.util.regex.Pattern;

/**
 * @Author tactbug
 * @Email tactbug@Gmail.com
 * @Time 2021/10/3 21:32
 */
public class SerializeUtil {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final Pattern NUM_PATTERN = Pattern.compile("^[-\\+]?[\\d]*$");

    public static String mapToString(Map<String, Object> map) throws JsonProcessingException {
        return OBJECT_MAPPER.writeValueAsString(map);
    }

    public static<T> T jsonToObject(String json, TypeReference<T> typeReference) throws JsonProcessingException {
        return OBJECT_MAPPER.readValue(json, typeReference);
    }

    public static boolean isNumber(String str){
        return NUM_PATTERN.matcher(str).matches();
    }
}
