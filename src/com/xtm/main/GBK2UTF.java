package com.xtm.main;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.SortedMap;

/**
 * Function: 把PATH这个文件夹下所有的java文件由原来的GBK转换为UTF-8
 * Author: TianMing.Xiong
 * Date: Created in 18-4-27 下午6:00
 */
public class GBK2UTF {
    //源文件夹路径  注意：转换前务必备份，因为会替换源文件！！！
    public static final String PATH = "./../D601_3288";
    public static final String SRC_CHARSET = "GBK";//源文件的编码字符集
    public static final String DEST_CHARSET = "UTF-8";//目标文件的编码字符集

    public static void main(String[] args) throws Exception{

        File file = new File(PATH);
        if(!file.exists()){
            System.out.println("文件不存在！");
            return;
        }
        //列出所有java支持转换的字符集
        SortedMap<String, Charset> charsetMap = Charset.availableCharsets();
        for (String alias :
                charsetMap.keySet()) {
            System.out.println("java支持的字符集："+charsetMap.get(alias));
        }
        //遍历目录的所有java文件(不包括R.java文件)
        Files.walkFileTree(Paths.get(PATH),new SimpleFileVisitor<Path>(){
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes basicFileAttributes) throws IOException {
                if(file.toString().endsWith(".java") && !file.toString().endsWith("R.java")){
                    System.out.println("java文件:"+file);
                    try {
                        handleFileAndSaveNewFile(file,SRC_CHARSET,DEST_CHARSET);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes basicFileAttributes) throws IOException {
                return FileVisitResult.CONTINUE;
            }
        });

    }

    /**
     * 转换文件编码格式（覆盖源文件）
     * @param path 源文件路径
     * @param srcCharset 源文件编码时的字符集
     * @param destCharset 转换后文件的字符集
     * @throws Exception io异常
     */
    private static void handleFileAndSaveNewFile(Path path, String srcCharset, String destCharset) throws Exception{
        //读取源文件
        File file = new File(path.toString());
        FileInputStream fileInputStream = new FileInputStream(file);
        FileChannel inChannel = fileInputStream.getChannel();
        MappedByteBuffer mappedByteBuffer = inChannel.map(FileChannel.MapMode.READ_ONLY, 0, file.length());

        //解码
        Charset decCharset = Charset.forName(srcCharset);
        CharsetDecoder decoder = decCharset.newDecoder();
        CharBuffer charBuffer = decoder.decode(mappedByteBuffer);

        //编码
        Charset encCharset = Charset.forName(destCharset);
        CharsetEncoder encoder = encCharset.newEncoder();
        ByteBuffer encodeByte = encoder.encode(charBuffer);

        //替换保存
        FileChannel outChannel = new FileOutputStream(path.toString()).getChannel();
        outChannel.write(encodeByte);
    }
}


