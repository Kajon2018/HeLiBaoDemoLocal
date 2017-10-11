package com.aeye.utils;

import java.util.ArrayList;  
import java.util.HashMap;  
import java.util.List;  
import java.util.Map;  
  
/** 
 * XMLԪ���� 
 
 */  
public class Element {  
    private String name;                 //Ԫ����  
    private String nodeText = "";        //�ı�ֵ  
    private Map<String,String> property = new HashMap<String,String>(); //����  
    private boolean isleaf = true;       //�Ƿ��ӽڵ�  
    private List<Element> child = new ArrayList<Element>();         //�ӽڵ�  
      
    public Element(String name) {  
        this.name = name;  
    }  
      
    public String getName() {  
        return name;  
    }  
  
    public void setName(String name) {  
        this.name = name;  
    }  
    public String getNodeText() {  
        return nodeText;  
    }  
    public void setNodeText(String nodeText) {  
        this.nodeText = nodeText;  
    }  
    public Map<String, String> getProperty() {  
        return property;  
    }  
    public void setProperty(Map<String, String> property) {  
        this.property = property;  
    }  
    public boolean isIsleaf() {  
        return isleaf;  
    }  
    //�������Ӧ������  
    public void setIsleaf(boolean isleaf) {  
        this.isleaf = isleaf;  
    }  
    public List<Element> getChild() {  
        return child;  
    }  
    public void setChild(List<Element> child) {  
        this.child = child;  
        if(this.isleaf && this.child.size() > 0){  
            this.isleaf = false;  
        }  
    }  
  
    /** 
     * ������� 
     * @param key 
     * @param value 
     */  
    public void addProperty(String key,String value){  
        this.property.put(key, value);  
    }  
      
    /** 
     * ����ӽڵ� 
     * @param el 
     */  
    public void addChild(Element el){  
        this.child.add(el);  
        if(this.isleaf && this.child.size() > 0){  
            this.isleaf = false;  
        }  
    }  
}  
