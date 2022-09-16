package com.example.testtask.services;


import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
public class XmlService {

    public String xmlToJson(String xmlBody, String params) {

        //remove spaces and invisible spaces. u2063 - invisible space
        xmlBody = xmlBody.replaceAll(">[\\u2063\\s]*<", "><");

        JSONObject json = null;
        try {
            json = XML.toJSONObject(xmlBody); // converts xml to json
        } catch (JSONException e) {
            log.error("Can't parse xml document", e);
        }

        List<Integer> defaultValueList = parseParams(params);
        json = changeDefaultValue(json, defaultValueList, new AtomicInteger(0));

        return json.toString(4);
    }


    private JSONObject changeDefaultValue(JSONObject jsonObject, List<Integer> defaultValues, AtomicInteger index){
        JSONArray keys = jsonObject.names();
        for (int i = 0; i < keys.length(); i++) {
            String name = keys.getString(i);
            if ("DefaultValue".equalsIgnoreCase(name)){
                int currentIndex = index.getAndIncrement();
                if (defaultValues.size() > currentIndex){
                    Integer value = defaultValues.get(currentIndex);
                    jsonObject.put(name, value);
                } else {
                    jsonObject.put(name, JSONObject.NULL);
                }
                continue;
            }
            Object o = jsonObject.get(name);
            if (o instanceof JSONObject){
                changeDefaultValue((JSONObject) o, defaultValues, index);
            }

            if (o instanceof JSONArray){
                changeDefaultValue((JSONArray) o, defaultValues, index);
            }
        }
        return jsonObject;
    }

    private JSONArray changeDefaultValue(JSONArray jsonArray, List<Integer> defaultValues, AtomicInteger index){
        jsonArray.forEach(value->{
            if (value instanceof JSONObject){
                changeDefaultValue((JSONObject) value, defaultValues, index);
            }
            if (value instanceof JSONArray){
                changeDefaultValue((JSONArray) value, defaultValues, index);
            }
        });

        return jsonArray;
    }

    private List<Integer> parseParams(String param){
        String[] paramsValues = param.split(",");

        List<Integer> result = new ArrayList<>(paramsValues.length);
        for (int i = 0; i < paramsValues.length; i++) {
            result.add(Integer.parseInt(paramsValues[i].trim()));
        }
        return result;
    }
}
