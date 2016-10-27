package com.zheng.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.AcroFields.Item;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

/**
 * Created by zhenglian on 2016/9/30.
 */
public class PDFPrintUtil {

    private static Logger logger = LoggerFactory.getLogger(PDFPrintUtil.class);

    private static final String PDF_SUFFIX = "pdf";
    private static final float scale = 3.5f; //缩放的大小

    static {
        System.setProperty("sun.java2d.cmm", "sun.java2d.cmm.kcms.KcmsServiceProvider");
    }

    /**
     * 根据模板动态批量生成pdf内容
     * @param templateFile
     * @param outFilePath
     * @param params
     * @throws IOException
     * @throws DocumentException
     */
    public static void createPdfByTemplate(String templateFile, String outFilePath,
                                           Map<String, Object> params)
            throws IOException, DocumentException {
        Preconditions.checkNotNull(templateFile, "模板pdf文件不能为空!");
        Preconditions.checkNotNull(outFilePath, "输出目标pdf文件路径不能为空!");

        File outFile = new File(outFilePath);
        createFile(outFile);

        PdfReader reader = new PdfReader(templateFile); // 模版文件目录
        PdfStamper ps = new PdfStamper(reader, new FileOutputStream(outFile)); // 生成的输出流
        AcroFields s = ps.getAcroFields();
        BaseFont bf = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
        Item item = null;
        for(Map.Entry<String, Object> entry : params.entrySet()) {
            //设置文本域表单的字体
            // 对于模板要显中文的，在此处设置字体比在pdf模板中设置表单字体的好处：
            //1.模板文件的大小不变；2.字体格式满足中文要求
        	item = s.getFieldItem(entry.getKey());
        	if(item == null) { //检查当前模板中是否存在指定的字段
        		continue;
        	}
        	
            s.setFieldProperty(entry.getKey(), "textfont", bf, null);
            //编辑文本域表单的内容
            s.setField(entry.getKey(), entry.getValue().toString());
        }

        //这里动态生成条码/二维码
        Image gif = Image.getInstance("d://images/me.png");

        File file = new File("d://images/me.png");
        BufferedImage image = ImageIO.read(file);
        int width = image.getWidth();
        int height = image.getHeight();
//        gif.setDpi(200, 200);
//        gif.setBorderWidth(200);
        gif.scaleAbsolute(width / scale, height / scale);
        PdfContentByte over = ps.getOverContent(1);

        float pageWidth = over.getPdfDocument().getPageSize().getWidth();
        float pageHeight = over.getPdfDocument().getPageSize().getHeight();
        System.out.println("width:" + pageWidth + ",height:" + pageHeight);

        //这里的位置坐标需要通过计算的到
        gif.setAbsolutePosition(363f+3, 754f - height/3.5f-3);

        over.addImage(gif);
        ps.setFormFlattening(true); // 这句不能少

        ps.close();
        reader.close();
        logger.info("生成pdf文件"+outFile.getName()+"------------成功!");
    }

    /**
     * 将同一个路径下的所有pdf文件合并成一个pdf
     * @param dirPath
     */
    public static void mergePdfs(String dirPath, String outPath) {
        Preconditions.checkNotNull(dirPath, "pdf文件目录路径不能为空!");
        Preconditions.checkNotNull(outPath, "合并的pdf路径不能为空!");
        File outFile = new File(outPath);

        createFile(outFile);

        File dir = new File(dirPath);
        if(!dir.exists() || !dir.isDirectory()) {
            Throwables.propagate(new RuntimeException("合并pdf文件失败，给定路径" + dirPath +"不是目录!"));
        }

        List<File> pdfs = getPdfFiles(dir);

        PDFMergerUtility mergePdf = new PDFMergerUtility();
        mergePdf.setDestinationFileName(outPath);
        long start = System.currentTimeMillis();
        try {
            for(File pdf : pdfs) {
                mergePdf.addSource(pdf);
            }
            mergePdf.mergeDocuments(MemoryUsageSetting.setupMainMemoryOnly());
        } catch (Exception e) {
            Throwables.propagate(new RuntimeException("合并pdf异常：" + e.getMessage()));
        }

        long end = System.currentTimeMillis();

        logger.info("合并pdf---------------成功!共耗时:" + (end - start));
    }

    /**
     * 在磁盘上生成文件
     * @param outFile
     */
    private static void createFile(File outFile) {
        File parentFile = outFile.getParentFile();
        if(!parentFile.exists()) {
            parentFile.mkdirs();
        }

        if(!outFile.exists()) {
            try {
                outFile.createNewFile();
            } catch (IOException e) {
                Throwables.propagate(new RuntimeException("创建文件:" + outFile.getName() + "失败!"));
                logger.debug(e.getMessage());

            }
        }
    }

    /**
     * 获取指定目录下的一级目录下的pdf文件列表
     * @param dir
     * @return
     */
    private static List<File> getPdfFiles(File dir) {
        List<File> pdfs = Lists.newArrayList();
        File[] items = dir.listFiles();
        String name = null;
        String suffix = null;

        for(File item : items) {
            if(!item.isDirectory()) {
                name = item.getName();
                suffix = name.substring(name.lastIndexOf(".")+1);
                if(suffix != null && suffix.toLowerCase().equals(PDF_SUFFIX)) {
                    pdfs.add(item);
                }
            }
        }

        return pdfs;
    }

}
