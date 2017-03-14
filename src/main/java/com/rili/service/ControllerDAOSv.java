package com.rili.service;

import com.google.common.collect.Maps;
import com.rili.Constant;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

/**
 * Created by CYM on 2017/3/13.
 */

@Service
public class ControllerDAOSv {

    private static final Logger LOGGER = LoggerFactory.getLogger(ControllerDAOSv.class);

    // TODO: 2017/3/14
    public void test(String fullFilePath) {
        processMapData(fileData2MapData(fullFilePath));
    }

    private Map<String, Map<String, String>> fileData2MapData(String fullFilePath) {
        Map<String, String> fieldDeclarationMap = Maps.newHashMap();
        Map<String, String> unclassifiedUsageMap = Maps.newHashMap();
        try (BufferedReader br = new BufferedReader(new FileReader(fullFilePath))) {
            String line;
            String key = null;
            StringBuilder value = new StringBuilder();
            int[] valueType = null;
            Map<String, String> tempMap = Maps.newHashMap();
            String tempStr;
            while ((line = br.readLine()) != null) {
                int type = StringUtils.indexOfAnyBut(line, Constant.SPACE);
                if (type == 4) {
                    // 寻找 FIELD_DECLARATION UNCLASSIFIED_USAGE 其他不处理
                    if (StringUtils.contains(line, Constant.FIELD_DECLARATION)) {
                        tempMap = fieldDeclarationMap;
                        valueType = new int[]{20}; // FIELD_DECLARATION 声明成员变量信息
                    } else if (StringUtils.contains(line, Constant.UNCLASSIFIED_USAGE)) {
                        tempMap = unclassifiedUsageMap;
                        valueType = new int[]{20, 24}; // UNCLASSIFIED_USAGE 时 引用类名 方法名
                    } else {
                        valueType = null;
                    }
                } else if (valueType != null && type == 16) {
                    // 取类名
                    key = StringUtils.substring(line, 16, line.indexOf('(')).trim();
                    value = new StringBuilder();
                } else if (valueType != null && ArrayUtils.contains(valueType, type)) {
                    tempStr = StringUtils.substring(line, type).trim();
                    if (valueType.length > 1) {
                        if (type == 24) {
                            value.append("**").append(StringUtils.remove(tempStr, ";"));
                        } else {
                            value.append(";").append(tempStr);
                        }
                    } else {
                        value.append(tempStr);
                    }
                }

                if (key != null && valueType != null) {
                    tempMap.put(key, value.toString());
                }

            }
        } catch (FileNotFoundException fileNotFoundException) {
            LOGGER.error("FileNotFoundException:{}", fileNotFoundException);
        } catch (IOException ioException) {
            LOGGER.error("IOException:{}", ioException);
        }
        Map<String, Map<String, String>> result = Maps.newHashMap();
        result.put(Constant.FIELD_DECLARATION_MAP, fieldDeclarationMap);
        result.put(Constant.UNCLASSIFIED_USAGE_MAP, unclassifiedUsageMap);
        return result;
    }

    private void processMapData(Map<String, Map<String, String>> mapData) {
        String className;
        String methodName;
        String refClassName;
        String refMethodName;
        String[] temp1;
        String[] temp2;
        String tempVariable;
        String tempStr;
        for (Map.Entry<String, String> fieldDeclarationEntry : mapData.get(Constant.FIELD_DECLARATION_MAP).entrySet()) {
            className = fieldDeclarationEntry.getKey();
            for (String s : StringUtils.splitByWholeSeparator(fieldDeclarationEntry.getValue(), ";")) {
                if (StringUtils.isEmpty(s)) {
                    continue;
                }
                temp1 = StringUtils.splitByWholeSeparator(s, Constant.SPACE);
                refClassName = new String(temp1[1]);
                tempVariable = new String(temp1[2]);
                temp1 = StringUtils.splitByWholeSeparator(mapData.get(Constant.UNCLASSIFIED_USAGE_MAP).get(className), ";");
                if (temp1 == null) {
                    continue;
                }
                for (String str : temp1) {
                    temp2 = StringUtils.splitByWholeSeparator(str.replaceAll(Constant.SPACE, ""), ")(");
                    methodName = temp2[0] + ")";
                    for (String rs : StringUtils.splitByWholeSeparator(temp2[1], "**")) {
                        if (StringUtils.contains(rs, tempVariable + ".")) {
                            tempStr = StringUtils.substring(rs, StringUtils.indexOf(rs, tempVariable + "."));
                            refMethodName = StringUtils.splitByWholeSeparator(StringUtils.substring(tempStr, StringUtils.indexOf(tempStr, tempVariable + "."), StringUtils.indexOf(tempStr, "(")), ".")[1];
                            if (!StringUtils.isAnyEmpty(className, methodName, refClassName, refMethodName)) {
                                LOGGER.info("***** className:{}, methodName:{}, refClassName:{}, refMethodName:{} *****", className, methodName, refClassName, refMethodName);

                            }
                        }
                    }

                }
            }

        }
    }

}
