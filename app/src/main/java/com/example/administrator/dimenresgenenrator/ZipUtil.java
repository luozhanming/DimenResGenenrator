package com.example.administrator.dimenresgenenrator;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.CRC32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipUtil {
    public static void compress(String srcFilePath, String destFilePath) {

        File src = new File(srcFilePath);

        if (!src.exists()) {

            throw new RuntimeException(srcFilePath + "不存在");

        }

        File zipFile = new File(destFilePath);

        try {

            FileOutputStream fos = new FileOutputStream(zipFile);

            CheckedOutputStream cos = new CheckedOutputStream(fos, new CRC32());

            ZipOutputStream zos = new ZipOutputStream(cos);

            String baseDir = "";

            compressbyType(src, zos, baseDir);

            zos.close();

        } catch (Exception e) {

            // TODO Auto-generated catch block

            e.printStackTrace();

        }



    }



    private static void compressbyType(File src, ZipOutputStream zos,

                                       String baseDir) {

        if (!src.exists())

            return;

        System.out.println("压缩" + baseDir + src.getName());

        if (src.isFile()) {

            compressFile(src, zos, baseDir);

        } else if (src.isDirectory()) {

            compressDir(src, zos, baseDir);

        }

    }



    /**

     * 压缩文件

     *

     */

    private static void compressFile(File file, ZipOutputStream zos,

                                     String baseDir) {

        if (!file.exists())

            return;

        try {

            BufferedInputStream bis = new BufferedInputStream(

                    new FileInputStream(file));

            ZipEntry entry = new ZipEntry(baseDir + file.getName());

            zos.putNextEntry(entry);

            int count;

            byte[] buf = new byte[1024];

            while ((count = bis.read(buf)) != -1) {

                zos.write(buf, 0, count);

            }

            bis.close();

        } catch (Exception e) {

            // TODO: handle exception

        }



    }



    /**

     * 压缩文件夹

     *

     */

    private static void compressDir(File dir, ZipOutputStream zos,

                                    String baseDir) {

        if (!dir.exists())

            return;

        File[] files = dir.listFiles();

        if(files.length == 0){

            try {

                zos.putNextEntry(new ZipEntry(baseDir + dir.getName()

                        + File.separator));



            } catch (IOException e) {

                e.printStackTrace();

            }



        }

        for (File file : files) {

            compressbyType(file, zos, baseDir + dir.getName() + File.separator);

        }



    }
}
