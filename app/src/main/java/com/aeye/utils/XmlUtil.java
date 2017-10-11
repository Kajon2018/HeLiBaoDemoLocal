package com.aeye.utils;

import java.util.Iterator;  

/** 
 * XMl工具类 
 
 */  
public class XmlUtil {  
      
    static String lt = "<";  
    static String ltEnd = "</";  
    static String rt = ">";  
    static String rhtEnd = "/>";  
    static String quotes = "\"";  
    static String equal = "=";  
    static String blank = " ";  
      
    public static String elementToXml(Element el){  
        StringBuffer result = new StringBuffer();  
        //元素开始  
        result.append(lt).append(el.getName());  
        //判断是否有属性  
        if(el.getProperty() != null && el.getProperty().size() > 0 ){  
            Iterator iter = el.getProperty().keySet().iterator();  
            while (iter.hasNext()) {  
                String key = String.valueOf(iter.next());  
                String value = el.getProperty().get(key);  
                result.append(blank).append(key).append(equal)  
                .append(quotes).append(value).append(quotes).append(blank);  
            }  
        }  
        result.append(rt);//结束标记  
        /* 
         * 判断是否是叶子节点 
         * 是叶子节点，添加节点内容 
         * 不是叶子节点，循环添加子节点 
         */  
        if(el.isIsleaf()){  
            result.append(el.getNodeText());  
        }else{  
            for(Element element :el.getChild()){  
                result.append(elementToXml(element));  
            }  
        }  
        //元素结束  
        result.append(ltEnd).append(el.getName()).append(rt);  
        return result.toString();  
    }  
}  