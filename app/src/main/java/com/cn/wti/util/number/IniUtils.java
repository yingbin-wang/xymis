package com.cn.wti.util.number;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Properties;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("rawtypes")
public class IniUtils {

	
	protected HashMap sections = new HashMap();
	private  transient String currentSecion;
	private  transient Properties current;
	private  static  String filename;

	public IniUtils(String filename) throws IOException {
		this.filename = filename;
		current = new Properties();
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filename), "utf8")); 
		read(reader);
		reader.close();

	}

	public IniUtils(InputStream inputStream) throws IOException {
		current = new Properties();
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "utf8"));
		read(reader);
		reader.close();

	}

	protected void read(BufferedReader reader) throws IOException {
		String line;
		while ((line = reader.readLine()) != null) {
			parseLine(line);
		}
	}

	@SuppressWarnings("unchecked")
	protected void parseLine(String line) {
		line = line.trim();
		if (line.matches("//[.*//]")) {
			currentSecion = line.replaceFirst("//[(.*)//]", "$1");
			current = new Properties();
			sections.put(currentSecion, current);
		} else if (line.matches(".*=.*")) {
			if (current != null) {
				int i = line.indexOf('=');
				String name = line.substring(0, i);
				String value = line.substring(i + 1);
				current.setProperty(name, value);
			}
		}
	}

	public String getValue(String name) {
		String value = current.getProperty(name);
		return value;
	}


	/**
	 * 修改ini配置文档中变量的值
	 * @param section 要修改的变量所在段名称
	 * @param variable 要修改的变量名称
	 * @param value 变量的新值
	 * @throws IOException 抛出文档操作可能出现的io异常
	 */
	public static boolean setProfileString(
			String section,
			String variable,
			String value)
			throws IOException {
		String fileContent, allLine,strLine, newLine, remarkStr;
		String getValue;
		BufferedReader bufferedReader = new BufferedReader(new FileReader(filename));
		boolean isInSection = false;
		fileContent = "";
		try {

			while ((allLine = bufferedReader.readLine()) != null) {
				allLine = allLine.trim();
				System.out.println("allLine == "+allLine);
				strLine = allLine;
				Pattern p;
				Matcher m;
				p = Pattern.compile("]");
				m = p.matcher((strLine));
				//System.out.println("+++++++ ");
				if (m.matches()) {
					System.out.println("+++++++ ");
					p = Pattern.compile("]");
					m = p.matcher(strLine);
					if (m.matches()) {
						System.out.println("true ");
						isInSection = true;
					} else {
						isInSection = false;
						System.out.println("+++++++ ");
					}
				}

				if (isInSection == true) {

					strLine = strLine.trim();
					String[] strArray = strLine.split("=");
					getValue = strArray[0].trim();

					if (getValue.equalsIgnoreCase(variable)) {
						// newLine = getValue + " = " + value + " " + remarkStr;
						newLine = getValue + " = " + value + " ";
						fileContent += newLine + "\r\n";
						while ((allLine = bufferedReader.readLine()) != null) {
							fileContent += allLine + "\r\n";
						}
						bufferedReader.close();
						BufferedWriter bufferedWriter =
								new BufferedWriter(new FileWriter(filename, false));
						bufferedWriter.write(fileContent);
						bufferedWriter.flush();
						bufferedWriter.close();

						return true;
					}
				}
				fileContent += allLine + "\r\n";
			}
		}catch(IOException ex){
			throw ex;
		} finally {
			bufferedReader.close();
		}
		return false;
	}


	/**
	 * 创建任意长度随机数
	 * @param strLength
	 * @return
     */
	public static String getFixLenthString(int strLength) {

		Random rm = new Random();

		// 获得随机数
		double pross = (1 + rm.nextDouble()) * Math.pow(10, strLength);

		// 将获得的获得随机数转化为字符串
		String fixLenthString = String.valueOf(pross);

		// 返回固定的长度的随机数
		return fixLenthString.substring(1, strLength + 1);
	}

}
