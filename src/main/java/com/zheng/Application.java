package com.zheng;

import com.zheng.util.PDFPrintUtil;

/**
 * Created by zhenglian on 2016/9/30.
 */
public class Application {
    public static void main(String[] args) throws Exception {
        String outFile = "D://images/out.pdf";
        String template = "D://images/template.pdf";

        PDFPrintUtil.editPdfTemplate(template, outFile);

    }

}
