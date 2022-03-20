package cn.LTCraft.core.utils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {


    /**
     * 文本转 BufferedReader
     * @param source 文本
     * @return BufferedReader
     */
    public static BufferedReader StringToBufferedReader(String source){
        InputStream inputStream = new ByteArrayInputStream(source.getBytes());
        return new BufferedReader(new InputStreamReader(inputStream));
    }
    public static String readToString(String fileName) {
        String encoding = "UTF-8";
        File file = new File(fileName);
        Long filelength = file.length();
        byte[] filecontent = new byte[filelength.intValue()];
        try {
            FileInputStream in = new FileInputStream(file);
            in.read(filecontent);
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            return new String(filecontent, encoding);
        } catch (UnsupportedEncodingException e) {
            System.err.println("The OS does not support " + encoding);
            e.printStackTrace();
            return null;
        }
    }
    public static List<File> getFiles(File dir) {
        return getFiles(dir, new ArrayList<>(), false);
    }
    public static List<File> getFiles(File dir, List<File> list) {
        return getFiles(dir, list, false);
    }
    public static List<File> getFiles(File dir, List<File> list, boolean recursion){
        if (dir == null || !dir.isDirectory() || dir.listFiles() == null)return list;
        for (File file : dir.listFiles()) {
            if (file.isDirectory()) {
                if (recursion) getFiles(file, list, true);
            }else {
                list.add(file);
            }
        }
        return list;
    }
}
