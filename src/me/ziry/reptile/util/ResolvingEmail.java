package me.ziry.reptile.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ResolvingEmail {

	// 得到URL字符串中的所有Email集合
	public static synchronized void getEmailSet(String urlStr, List<String> list) {

		// 编译Email正则表达式
		Pattern p = Pattern
				.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");

		// 用于读取一行字符串
		BufferedReader br = null;

		HttpURLConnection http = null;

//		System.out.println("【Emali】" + urlStr);
		try {
			URL url = new URL(urlStr);
			http = (HttpURLConnection) url.openConnection();
			http.setRequestProperty("accept", "*/*");
			http.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.8");
			http.setRequestProperty("Content-Type", "text/xml;charset=utf-8");
			http.setRequestProperty("connection", "keep-alive");
			http.setRequestProperty("user-agent",
					"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
			http.setRequestMethod("GET"); // 设定请求的方法为"POST"，默认是GET
			http.setConnectTimeout(10000); // 设置连接主机超时（单位：毫秒)
			http.setReadTimeout(10000); // 设置从主机读取数据超时（单位：毫秒)
			http.setUseCaches(false); // Post 请求不能使用缓存
			http.connect();

			// 打开此URL的io流并套上处理流
			br = new BufferedReader(new InputStreamReader(
					http.getInputStream(), "UTF-8"));
			String line = null;
			while ((line = br.readLine()) != null) {
				Matcher m = p.matcher(line); // 匹配正则表达式
				while (m.find()) {
					// 判断有无重复
					if (!list.contains(m.group())) {
//						System.out.println(m.group());
						list.add(m.group()); // 返回得到的Email并放入集合
					}
				}
			}
		} catch (IOException e) {
			System.out.println("【无效URL】" + urlStr);
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

}
