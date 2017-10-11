package com.aeye.utils;

import java.util.Iterator;  

/** 
 * XMl������ 
 
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
        //Ԫ�ؿ�ʼ  
        result.append(lt).append(el.getName());  
        //�ж��Ƿ�������  
        if(el.getProperty() != null && el.getProperty().size() > 0 ){  
            Iterator iter = el.getProperty().keySet().iterator();  
            while (iter.hasNext()) {  
                String key = String.valueOf(iter.next());  
                String value = el.getProperty().get(key);  
                result.append(blank).append(key).append(equal)  
                .append(quotes).append(value).append(quotes).append(blank);  
            }  
        }  
        result.append(rt);//�������  
        /* 
         * �ж��Ƿ���Ҷ�ӽڵ� 
         * ��Ҷ�ӽڵ㣬��ӽڵ����� 
         * ����Ҷ�ӽڵ㣬ѭ������ӽڵ� 
         */  
        if(el.isIsleaf()){  
            result.append(el.getNodeText());  
        }else{  
            for(Element element :el.getChild()){  
                result.append(elementToXml(element));  
            }  
        }  
        //Ԫ�ؽ���  
        result.append(ltEnd).append(el.getName()).append(rt);  
        return result.toString();  
    }  
}  