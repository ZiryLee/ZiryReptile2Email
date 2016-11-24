package me.ziry.reptile.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//可爬去网页中的所有连接
public class ResolvingURL {

	//判断url是否可用
	public static boolean isUsable(String urlStr) {
		try {
			URL url = new URL(urlStr);
			url.openStream().close();
		} catch (Exception e) {
			return false;
		}
		return true;
	}
		
	//得到此URL字符串中所有是链接的字符串集合
	public static synchronized void getURL2List(String urlStr, List<String> list)throws MalformedURLException,IOException  {
		//编译Email正则表达式
		Pattern p = Pattern.compile("<a.*href*=*['\"]*(\\S+)[\"']");
		//用于读取一行字符串
		BufferedReader br = null;
		HttpURLConnection http = null;
		
		try {
			URL url = new URL(urlStr);
			http = (HttpURLConnection) url.openConnection();
			http.setRequestProperty("accept", "*/*");
			http.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.8");
			http.setRequestProperty("Content-Type", "text/xml;charset=utf-8");
			http.setRequestProperty("connection", "keep-alive");
			http.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
			http.setRequestMethod("GET"); // 设定请求的方法为"POST"，默认是GET
			http.setConnectTimeout(10000); // 设置连接主机超时（单位：毫秒)
			http.setReadTimeout(10000); // 设置从主机读取数据超时（单位：毫秒)
			http.setDoOutput(true); // post请求参数要放在http正文内，顾设置成true，默认是false
			http.setDoInput(true); // 设置是否从httpUrlConnection读入，默认情况下是true
			http.setUseCaches(false); // Post 请求不能使用缓存
			http.connect();
			
			//打开此URL的io流并套上处理流
			br = new BufferedReader(new InputStreamReader(http.getInputStream(),"UTF-8"));
			String line = null;	
			
			//得到协议和域名
			String realmName = url.getProtocol()+"://"+url.getHost();
			
			while( (line=br.readLine()) != null ) {
				
				Matcher m = p.matcher(line.replaceAll(" ", ""));		//匹配正则表达式				
				while( m.find() ) {
					
					String substring = m.group(1);  //得到子串
					
					//判断子串是否以指定协议开头
					if(substring.startsWith("http://")) {
						
						//再判断是否是是指定URL下的链接
						if( substring.startsWith(urlStr) ) {
							//判断有无重复
							if(!list.contains(substring)){
								//无重复放在list集合中
								list.add(substring);
							}
						}
						
					} else if(substring.startsWith("/")) {			//判断知否以/开头，去除杂项
						String newurl = realmName+substring; 
						//再判断是否是是指定URL下的链接
						if( newurl.startsWith(urlStr) ) {
							//判断有无重复
							if(!list.contains(newurl)){
								//无重复放在list集合中
								list.add(newurl);
							}
						}
					} else {
						String newurl = realmName+"/"+substring; 
						
						//再判断是否是是指定URL下的链接
						if( newurl.startsWith(urlStr) ) {
							//判断有无重复
							if(!list.contains(newurl)){
								//无重复放在list集合中
								list.add(newurl);
							}
						}
					}
				}
				
			}
		} catch (Exception e) {
			System.out.println("【无效URL】"+urlStr);
		} finally {
			if(br!=null){
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
		
	//得到此URL字符串中所有下一页链接的字符串集合
	public static synchronized void getNextURL2List(String urlStr, List<String> list)throws MalformedURLException,IOException  {
		//编译Email正则表达式
		Pattern p = Pattern.compile("<a.*href*=*['\"]*(\\S+)[\"']>下一页</a>");
		//用于读取一行字符串
		BufferedReader br = null;
		
		HttpURLConnection http = null;
		
		try {
			URL url = new URL(urlStr);
			http = (HttpURLConnection) url.openConnection();
			http.setRequestProperty("accept", "*/*");
			http.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.8");
			http.setRequestProperty("Content-Type", "text/xml;charset=utf-8");
			http.setRequestProperty("connection", "keep-alive");
			http.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
			http.setRequestMethod("GET"); // 设定请求的方法为"POST"，默认是GET
			http.setConnectTimeout(10000); // 设置连接主机超时（单位：毫秒)
			http.setReadTimeout(10000); // 设置从主机读取数据超时（单位：毫秒)
			http.setDoOutput(true); // post请求参数要放在http正文内，顾设置成true，默认是false
			http.setDoInput(true); // 设置是否从httpUrlConnection读入，默认情况下是true
			http.setUseCaches(false); // Post 请求不能使用缓存
			http.connect();
			
			//打开此URL的io流并套上处理流
			br = new BufferedReader(new InputStreamReader(http.getInputStream(),"UTF-8"));
			String line = null;	
			
			//得到协议和域名
			String realmName = url.getProtocol()+"://"+url.getHost();
			
			while( (line=br.readLine()) != null ) {
				
				Matcher m = p.matcher(line.replaceAll(" ", ""));		//匹配正则表达式	
				
				while( m.find() ) {
					
					String substring = m.group(1);  //得到子串
					
					//判断子串是否以指定协议开头
					if(substring.startsWith("http://")) {
						
						//再判断是否是是指定URL下的链接
						if( substring.startsWith(urlStr) ) {
							//判断有无重复
							if(!list.contains(substring)){
								//无重复放在list集合中
								list.add(substring);
							}
						}
						
					} else if(substring.startsWith("/")) {			//判断知否以/开头，去除杂项
						String newurl = realmName+substring; 
						//再判断是否是是指定URL下的链接
						if( newurl.startsWith(urlStr) ) {
							//判断有无重复
							if(!list.contains(newurl)){
								//无重复放在list集合中
								list.add(newurl);
							}
						}
					} else {
						String newurl = realmName+"/"+substring; 
						
						//再判断是否是是指定URL下的链接
						if( newurl.startsWith(urlStr) ) {
							//判断有无重复
							if(!list.contains(newurl)){
								//无重复放在list集合中
								list.add(newurl);
							}
						}
					}
				}
				
			}
		} catch (Exception e) {
			System.out.println("【无效URL】"+urlStr);
		}  finally {
			if(br!=null){
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	
	
}
