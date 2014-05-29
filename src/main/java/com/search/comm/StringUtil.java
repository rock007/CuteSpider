package com.search.comm;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;

public class StringUtil {

	public static  DateFormat formatStyle1 = new SimpleDateFormat("yyyy-MM-dd");        
	public static  DateFormat formatStyle2= new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒"); 
	
	public static String htmlRemoveTag(String inputString) {  
	    if (inputString == null)  
	        return null;  
	    String htmlStr = inputString; // 含html标签的字符串  
	    String textStr = "";  
	    java.util.regex.Pattern p_script;  
	    java.util.regex.Matcher m_script;  
	    java.util.regex.Pattern p_style;  
	    java.util.regex.Matcher m_style;  
	    java.util.regex.Pattern p_html;  
	    java.util.regex.Matcher m_html;  
	    try {  
	        //定义script的正则表达式{或<script[^>]*?>[\\s\\S]*?<\\/script>  
	        String regEx_script = "<[\\s]*?script[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?script[\\s]*?>";   
	        //定义style的正则表达式{或<style[^>]*?>[\\s\\S]*?<\\/style>  
	        String regEx_style = "<[\\s]*?style[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?style[\\s]*?>";   
	        String regEx_html = "<[^>]+>"; // 定义HTML标签的正则表达式  
	        p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);  
	        m_script = p_script.matcher(htmlStr);  
	        htmlStr = m_script.replaceAll(""); // 过滤script标签  
	        p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);  
	        m_style = p_style.matcher(htmlStr);  
	        htmlStr = m_style.replaceAll(""); // 过滤style标签  
	        p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);  
	        m_html = p_html.matcher(htmlStr);  
	        htmlStr = m_html.replaceAll(""); // 过滤html标签  
	        textStr = htmlStr;  
	    } catch (Exception e) {  
	        e.printStackTrace();  
	    }  
	    return textStr;// 返回文本字符串  
	}
	
	public static String date2String(Date d,DateFormat dateformat){
		
		return dateformat.format(d);
	}
	
	public static String Date2String(Date d){
		
		return date2String(d,formatStyle1);
	}
	
	public static String Date2String(){
		
		return date2String(new Date(),formatStyle1);
	}
	
	public static String html2text(String html) {
	    return Jsoup.parse(html).text();
	}
}
