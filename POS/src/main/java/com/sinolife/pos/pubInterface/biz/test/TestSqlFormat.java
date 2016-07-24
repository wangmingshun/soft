package com.sinolife.pos.pubInterface.biz.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class TestSqlFormat {
	
	private static StringBuffer sb = new StringBuffer();
	
	private static final String FILE_PATH = "src/replace/";//文件上层目录
	private static final String SOURCE_FILE = "1.txt";
	private static final int MAX_LINE_NO = 1000;//多少行为一个文件
	
	public static void main(String[] args) {
		TestSqlFormat.readFileByLines(FILE_PATH + SOURCE_FILE);
	}

    public static void readFileByLines(String fileName) {
        File file = new File(fileName);
        BufferedReader reader = null;
        FileWriter writer = null;
        int lineNo = 0;
        int fileLineNo = 0;
        int fileLineNoSum = 0;
        int fileNo = 2;
        boolean isGen = false; 
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            while ((tempString = reader.readLine()) != null) {
            	if("".equals(tempString)) continue;
                if(++lineNo%MAX_LINE_NO != 0){
                	sb.append("'").append(tempString).append("',\n");
                	isGen = false;
                	fileLineNo++;
                }else{
                	sb.append("'").append(tempString).append("'");
                	isGen = true;
                	fileLineNo++;
                }
                if(isGen){
                	System.out.println("生成：" + fileNo +".txt，行数："+fileLineNo);
                	fileLineNoSum+=fileLineNo;
                	writer = new FileWriter(FILE_PATH + fileNo++ +".txt");
                	writer.write("policy_no in \n(\n" + sb.toString() + "\n)");
                	writer.close();
                	sb.setLength(0);
                	fileLineNo = 0;
                }
            }
            if(sb.length() != 0){
            	System.out.println("生成：" + fileNo +".txt，行数："+fileLineNo);
            	fileLineNoSum+=fileLineNo;
            	writer = new FileWriter(FILE_PATH + fileNo++ +".txt");
            	sb.append("''\n");
            	writer.write("policy_no in \n(\n" + sb.toString() + "\n)");
            	writer.close();
            	sb.setLength(0);
            }
            System.out.println("生成文件总行数："+fileLineNoSum);
            System.out.println("原始文件总行数："+lineNo);
            
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
            if (writer != null) {
                try {
                	writer.close();
                } catch (IOException e1) {
                }
            }
        }
    }
}
