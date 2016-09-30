package com.zheng.util;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by zhenglian on 2016/9/30.
 */
public class PDFPrintUtil {
    public static void editPdfTemplate(String templateFile, String outFile)
            throws IOException, DocumentException {
        PdfReader reader = new PdfReader(templateFile); // 模版文件目录
        PdfStamper ps = new PdfStamper(reader, new FileOutputStream(outFile)); // 生成的输出流

        BaseFont bf = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);

        AcroFields s = ps.getAcroFields();
        //设置文本域表单的字体
        // 对于模板要显中文的，在此处设置字体比在pdf模板中设置表单字体的好处：
        //1.模板文件的大小不变；2.字体格式满足中文要求
        s.setFieldProperty("username", "textfont", bf, null);
        s.setFieldProperty("school", "textfont", bf, null);
        //编辑文本域表单的内容
        s.setField("username", "姚 秀 才");
        s.setField("school", "深圳罗湖中学");

        Image gif = Image.getInstance("d://images/me.png");

        File file = new File("d://images/me.png");
        BufferedImage image = ImageIO.read(file);
        int width = image.getWidth();
        int height = image.getHeight();
//        gif.setDpi(200, 200);
//        gif.setBorderWidth(200);
        gif.scaleAbsolute(width / 3, height / 3);
        gif.setAbsolutePosition(100, 100);
        PdfContentByte over = ps.getOverContent(1);
        over.addImage(gif);

        ps.setFormFlattening(true); // 这句不能少
        ps.close();
        reader.close();
    }
}
