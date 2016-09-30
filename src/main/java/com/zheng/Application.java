package com.zheng;

import com.google.common.collect.Maps;
import com.zheng.util.PDFPrintUtil;

import java.util.Map;

/**
 * Created by zhenglian on 2016/9/30.
 */
public class Application {
    public static void main(String[] args) throws Exception {
//        new Application().createPdf();
        new Application().mergePdfs();

    }

    private void createPdf() throws Exception {
        String outFile = "D://images/59_out.pdf";
        String template = "D://images/59_template.pdf";
        Map<String, Object> params = Maps.newHashMap();
        params.put("username", "小赵");
        params.put("school", "南山实验中学");

        PDFPrintUtil.createPdfByTemplate(template, outFile, params);
    }

    private void mergePdfs() {
        //1000个学生为一组
//        String dirPath = "C:/Users/dell/Desktop/pdfs-A4";
//        String outPath = "C:/Users/dell/Desktop/merge-A4.pdf";

        //1000个学生为一组
        String dirPath = "C:/Users/dell/Desktop/pdfs-A3";
        String outPath = "C:/Users/dell/Desktop/merge-A3.pdf";


        PDFPrintUtil.mergePdfs(dirPath, outPath);
    }

}
