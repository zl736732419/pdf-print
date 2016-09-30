package com.zheng;

import com.zheng.util.PDFPrintUtil;

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
        PDFPrintUtil.createPdfByTemplate(template, outFile);
    }

    private void mergePdfs() {
        String dirPath = "C:/Users/dell/Desktop/pdfs";
        String outPath = "C:/Users/dell/Desktop/merge.pdf";
        PDFPrintUtil.mergePdfs(dirPath, outPath);
    }

}
