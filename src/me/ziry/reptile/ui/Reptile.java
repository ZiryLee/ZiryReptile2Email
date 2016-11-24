package me.ziry.reptile.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

import me.ziry.reptile.util.ResolvingEmail;
import me.ziry.reptile.util.ResolvingURL;

import org.jvnet.substance.skin.SubstanceAutumnLookAndFeel;

/**
 * Ziry作品：Email爬虫-V2.6 
 * 此版本实现了： 输出文本
 * 注：
 * 1. 广度获取为： 输入网址下的所有连接（包含下一页）
 * 2. 深度获取为：所有下一页
 */

public class Reptile extends JFrame implements ActionListener {
	
	private static final long serialVersionUID = 1L;

	// Email爬虫版本号
	public static final String VERSION = "V2.6";

	// 文件过滤器，只允许打开文本txt文件
	FileNameExtensionFilter filter = 
		new FileNameExtensionFilter("文本文件(*.txt)","txt");

	// 文件选择器用于输出文档，保存Email
	JFileChooser chooser = new JFileChooser(".");

	// 设置用户界面居中
	public static final int WIDTH = 600; // 界面宽度
	public static final int HEIGHT = 700; // 界面高度
	private Toolkit tk = Toolkit.getDefaultToolkit();
	private Dimension d = tk.getScreenSize();
	private int y = d.height / 2 - HEIGHT / 2; // 居中坐标X
	private int x = d.width / 2 - WIDTH / 2; // 居中坐标y

	// 地址栏面板
	JLabel url_JL = new JLabel("网址：");
	JTextField url_JTF = new JTextField(45);
	JPanel url_JP = new JPanel();

	// 按钮面板
	JButton breadth_JB = new JButton("广度获取");
	JButton depth_JB = new JButton("深度获取");
	JButton stop_JB = new JButton("停止获取");
	JPanel jb_JP = new JPanel();

	// 放url_JP面板和jb_JP面板
	JPanel north_JP = new JPanel();

	// 显示获取邮箱的JTable，带滚动条面板
	String[] columnNames = { "序列", "Email" }; // 列名
	Object[][] rowData = {}; // 字段开始为空
	DefaultTableModel defaultTableModel = 
		new DefaultTableModel(rowData,columnNames); // 表格模型
	JTable reult_JT = new JTable(defaultTableModel); // JTable用于显示获取邮箱
	JScrollPane showEmail_JSP = new JScrollPane(reult_JT); // 带滚动条面板

	// 功能面板
	JButton outText_JB = new JButton("输出文档");
	JButton sentEmail_JB = new JButton("发送邮箱");
	JButton reset_JB = new JButton("重置");
	JPanel south_JP = new JPanel();

	boolean isStop = true; // 是否已停止 初始true是已停止
	List<String> urlList = new ArrayList<String>(); // 保存URL用于循环获取
	List<String> emailList = new ArrayList<String>(); // 保存得到的Email
	boolean stopDetection = true; // 用于控制检测有没关闭线程类

	//得到图标
	Image imageIco = Toolkit.getDefaultToolkit().getImage("images/lzr.jpg");		
	
	InetAddress addr = null; // 得到IP地址，用于保存在数据库IP列

	public Reptile() {

		this.setTitle("Ziry作品：Email爬虫-" + VERSION); 						// 界面标题
		this.setIconImage(imageIco);										//设置图标
		this.setBounds(x, y, WIDTH, HEIGHT); 								// 设置界面位置及大小，这里使用居中位置x,y
		this.setLayout(new BorderLayout()); 								// 设置界面布局为边界布局，这里只使用NORTH、CENTER、SOUTH

		// 设置文件选择器
		chooser.setDialogTitle("输出文档"); // 设置文件选择器标题
		chooser.setFileFilter(filter); // 设置文件过滤器

		// new出两个布局器并设置控件间隔
		GridLayout gridLayout = new GridLayout(2, 1); // 网格布局
		FlowLayout flowLayout = new FlowLayout(); // 流水布局
		gridLayout.setVgap(0); // 控件间隔
		flowLayout.setVgap(2); // 控件间隔

		// 设置布局管理器
		url_JP.setLayout(flowLayout);
		jb_JP.setLayout(flowLayout);
		north_JP.setLayout(gridLayout);

		// 设置界面内容
		url_JP.add(url_JL);
		url_JP.add(url_JTF);
		north_JP.add(url_JP);

		jb_JP.add(breadth_JB);
		jb_JP.add(depth_JB);
		jb_JP.add(stop_JB);
		north_JP.add(jb_JP);

		this.add(north_JP, BorderLayout.NORTH);

		this.add(showEmail_JSP, BorderLayout.CENTER);

		south_JP.add(outText_JB);
		south_JP.add(sentEmail_JB);
		south_JP.add(reset_JB);
		this.add(south_JP, BorderLayout.SOUTH);

		// 设置按钮监听
		breadth_JB.addActionListener(this);
		depth_JB.addActionListener(this);
		stop_JB.addActionListener(this);
		reset_JB.addActionListener(this);
		outText_JB.addActionListener(this);
		sentEmail_JB.addActionListener(this);

		this.setDefaultCloseOperation(EXIT_ON_CLOSE); // 设置屏幕关闭事件
		this.setVisible(true); // 显示界面

	}

	public static void main(String[] args) {
		// 使标题栏的风格也跟着一起改变
		JFrame.setDefaultLookAndFeelDecorated(true);
		JDialog.setDefaultLookAndFeelDecorated(true);
		// 设置皮肤
		try {
			UIManager.setLookAndFeel(new SubstanceAutumnLookAndFeel());
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		new Reptile();
	}

	// 设置按钮监听
	public void actionPerformed(ActionEvent e) {

		// 广度获取
		if (e.getSource() == breadth_JB) {

			// 判断是否是可用链接
			if (ResolvingURL.isUsable(url_JTF.getText())) {

				this.reset(); // 重置现场（清空文本域和集合）
				this.setEnabledAll(false); // 设置相关按钮不可用

				urlList.add(url_JTF.getText()); // 把输入的URL地址放在List集合里

				isStop = false; // 设置开关，便于停止

				new BreadthStart().start(); // 启动单独线程去广度获取URL
				new EmailStart().start(); // 启动单独线程去获取URLList集合中的Email

			} else {
				JOptionPane.showMessageDialog(this,
						"连接错误！请检测URL地址是否正确，网络是否有有误！", "提示",
						JOptionPane.INFORMATION_MESSAGE);
			}

		}

		// 深度获取
		if (e.getSource() == depth_JB) {
			if (ResolvingURL.isUsable(url_JTF.getText())) {
				this.reset(); // 重置现场（清空文本域和集合）
				this.setEnabledAll(false); // 设置相关按钮不可用

				urlList.add(url_JTF.getText()); // 把输入的URL地址放在List集合里

				isStop = false; // 设置开关，便于停止

				new DepthStart().start(); // 启动单独线程去深度获取URL
				new EmailStart().start(); // 启动单独线程去获取URLList集合中的Email

			} else {
				JOptionPane.showMessageDialog(this,
						"连接错误！请检测URL地址是否正确，网络是否有有误！", "提示",
						JOptionPane.INFORMATION_MESSAGE);
			}
		}

		// 停止获取
		if (e.getSource() == stop_JB) {
			isStop = true;
			JOptionPane.showMessageDialog(this, "已通知爬虫线程停止，它们正在收尾中，请稍等...",
					"提示", JOptionPane.INFORMATION_MESSAGE);
		}

		// 重置
		if (e.getSource() == reset_JB) {
			// 清空文本域和集合
			this.reset();
		}

		// 输出文档
		if (e.getSource() == outText_JB) {
			//用于指向选择的文件
			File file = null;
			// 打开文件选择器
			int returnVal = chooser.showSaveDialog(this);
			try {
				//如果按保存
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					file = chooser.getSelectedFile();//得到选择文件
					//判断是否以.txt结尾
					if (file.getName().endsWith(".txt")) {
						//如果是以.txt结尾，且文件不存在
						if (!file.exists()) {
							//则创建文件
							file.createNewFile();
						}
						
					} else {
						//否则在文件后加.txt后缀
						file = new File(file + ".txt");
						if (!file.exists()) {
							//如果文件不存在则创建文件
							file.createNewFile();
						}
					}
					
					//用于向文件输出数据
					BufferedWriter bw = 
						new BufferedWriter(new FileWriter(file,true));

					// 先输出版本号和时间
					bw.newLine(); // 换行
					bw.write("=======欢迎使用Ziry作品：Email爬虫-" + VERSION+ "======="); 	// 版本号
					bw.newLine();
					bw.write("=========" + new Date() + "========"); 					// 输出输出时间
					bw.newLine();

					for (int i = 0; i < reult_JT.getRowCount(); i++) {
						
						//得到JTable i行0列值
						String id = (String) reult_JT.getValueAt(i, 0);
						
						//得到JTable i行1列值
						String value = (String) reult_JT.getValueAt(i, 1); 	
						
						bw.write(id + "\t" + value);  	// 输出
						bw.newLine(); 					// 换行
					}
					
					bw.flush();							//清空缓存
					bw.close();							//关闭流
					
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

		// 发送Email 弹出提示：此功能未开发
		if (e.getSource() == sentEmail_JB) {
			JOptionPane.showMessageDialog(this, "此功能待开发中，详情请咨询：lee@ziry.me",
					"提示", JOptionPane.INFORMATION_MESSAGE);
		}

	}

	//设置所有按钮是否可用
	private void setEnabledAll(boolean b) {
		//可用时显示此按钮文本
		if(b) {
			breadth_JB.setText("广度获取");
			depth_JB.setText("深度获取");
		}
		breadth_JB.setEnabled(b);
		depth_JB.setEnabled(b);
		reset_JB.setEnabled(b);
		outText_JB.setEnabled(b);
		sentEmail_JB.setEnabled(b);
		url_JTF.setEnabled(b);
	}

	// 单独线程去爬取URL（广度获取）
	private class BreadthStart extends Thread {
		
		public void run() {
			
			for (int i = 0; i < urlList.size() && !isStop; i++) {
				
				//判断URL是否是可用的连接
				if ( ResolvingURL.isUsable(urlList.get(i)) ) {
					//按钮显示进度
					breadth_JB.setText("共爬取" + urlList.size() + "页");
					
					try {
						// 得到本页所有链接
						ResolvingURL.getURL2List(urlList.get(i), urlList); 
					} catch (Exception e) {
						e.printStackTrace();
					}
					
				}
				
			}
			
			setEnabledAll(true);
			isStop = true;
			
		}
	}

	// 单独线程去爬取URL（深度获取）
	private class DepthStart extends Thread {
		
		public void run() {
			
			for (int i = 0; i < urlList.size() && !isStop; i++) {
				//判断URL是否是可用的连接
				if (ResolvingURL.isUsable(urlList.get(i))) {
					//按钮显示进度
					breadth_JB.setText("共爬取" + urlList.size() + "页");
					
					try {
						// 得到本页所有下一页链接
						ResolvingURL.getNextURL2List(urlList.get(i), urlList); 
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			//设置停止和按钮可用
			setEnabledAll(true);
			isStop = true;
			
		}

	}

	// 单独线程爬Email
	private class EmailStart extends Thread {

		int i = 0;			//第几个网页
		int j = 0;			//第几个邮箱

		public void run() {
			
			while (!isStop) {
				
				for (; i < urlList.size() && !isStop; i++) {
					//判断URL是否是可用的连接
					if (ResolvingURL.isUsable(urlList.get(i))) {
						//按钮显示进度
						depth_JB.setText("正在爬取第" + i + "页Email");
						 //得到本页所有Email
						ResolvingEmail.getEmailSet(urlList.get(i),emailList);
					}
					
					while (j < emailList.size()) {
						//设置列表行信息
						Object[] o = { j + 1 + "", emailList.get(j) };
						 // 添加到JTable
						defaultTableModel.addRow(o);
						j++;
					}
				}
				
			}
			
		}

	}

	// 清空集合文本
	private void reset() {
		// 清空URL集合
		urlList.clear();
		// 清空Email集合
		emailList.clear();
		// 清空列表
		int rowCount = defaultTableModel.getRowCount();
		for (int i = 0; i < rowCount; i++) {
			defaultTableModel.removeRow(0);
		}
	}

}
