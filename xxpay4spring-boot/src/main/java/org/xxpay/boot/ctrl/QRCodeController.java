package org.xxpay.boot.ctrl;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.xxpay.common.util.MyLog;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @description: QcodeTest
 * @author: aoxiang
 * @create: 2020/02/07 14:21
 **/
public class QRCodeController {

    private final MyLog _log = MyLog.getLog(QRCodeController.class);

    @RequestMapping(value = "/api/qrcode/ger_qrcode")
    @ApiOperation(value = "请求二维码")
    public String accessCodeVerity(@ApiParam(value = "身份标识", required = true) String clientId,
                                   @ApiParam(value = "机器编号", required = true) String assetId,
                                   @ApiParam(value = "商品名称", required = true) String goodsName,
                                   @ApiParam(value = "商品价格", required = true) String price,
                                   @ApiParam(value = "商品编码", required = true) String productNum,
                                   @ApiParam(value = "支付成功回调地址", required = true) String notify_url,
                                   @ApiParam(value = "订单编号", required = true) String orderNo,
                                   @ApiParam(value = "随机数", required = true) String nonceStr,
                                   @ApiParam(value = "签名", required = true) String sign) {

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        String qrContent = "";
        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
        try{
            BitMatrix bitMatrix = qrCodeWriter.encode(qrContent, BarcodeFormat.QR_CODE, 200, 200);

            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
        } catch (WriterException e) {
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "{\"result\":\"SUCCESS\", " +
                "\"qr_code\":pngOutputStream.toString()}";

    }
    public static String getQRCode(String text, int width, int height) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);

        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
//        byte[] pngData = pngOutputStream.toByteArray();
        return pngOutputStream.toString();
    }
    /**
     *二维码实现
     * @param msg
     * @param path
     */
    public static void getBarCode(String msg,String path){
        try {
            File file=new File(path);
            OutputStream ous=new FileOutputStream(file);
            if(StringUtils.isEmpty(msg) || ous==null)
                return;
            String format = "png";
            MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
            Map<EncodeHintType,String> map =new HashMap<EncodeHintType, String>();
            //设置编码 EncodeHintType类中可以设置MAX_SIZE， ERROR_CORRECTION，CHARACTER_SET，DATA_MATRIX_SHAPE，AZTEC_LAYERS等参数
            map.put(EncodeHintType.CHARACTER_SET,"UTF-8");
            map.put(EncodeHintType.MARGIN,"2");
            //生成二维码
            BitMatrix bitMatrix = new MultiFormatWriter().encode(msg, BarcodeFormat.QR_CODE,300,300,map);
            MatrixToImageWriter.writeToStream(bitMatrix,format,ous);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 识别二维码
     */
    public static void QRReader(File file) throws IOException, NotFoundException {
        MultiFormatReader formatReader = new MultiFormatReader();
        //读取指定的二维码文件
        BufferedImage bufferedImage = ImageIO.read(file);
        BinaryBitmap binaryBitmap= new BinaryBitmap(new HybridBinarizer(new BufferedImageLuminanceSource(bufferedImage)));
        //定义二维码参数
        Map  hints= new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        com.google.zxing.Result result = formatReader.decode(binaryBitmap, hints);
        //输出相关的二维码信息
        System.out.println("解析结果："+result.toString());
        System.out.println("二维码格式类型："+result.getBarcodeFormat());
        System.out.println("二维码文本内容："+result.getText());
        bufferedImage.flush();
    }

    /**
     * 生成二维码
     */
    public static void QREncode() throws WriterException, IOException {
        String content = "    姓名:卿香 爱好:老公和儿子\n" +
                "     年龄:永远22\n     理想:过上猪一样的生活\n    喜欢的网站: https://www.baidu.com ";//二维码内容
        int width = 200; // 图像宽度
        int height = 200; // 图像高度
        String format = "gif";// 图像类型
        Map<EncodeHintType, Object> hints = new HashMap<>();
        //内容编码格式
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        // 指定纠错等级
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        //设置二维码边的空度，非负数
        hints.put(EncodeHintType.MARGIN, 1);
        BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hints);
        MatrixToImageWriter.writeToPath(bitMatrix, format, new File("D:\\qcode.png").toPath());// 输出原图片
        MatrixToImageConfig matrixToImageConfig = new MatrixToImageConfig(0xFF000001, 0xFFFFFFFF);
        /*
            问题：生成二维码正常,生成带logo的二维码logo变成黑白
            原因：MatrixToImageConfig默认黑白，需要设置BLACK、WHITE
            解决：https://ququjioulai.iteye.com/blog/2254382
         */
        BufferedImage bufferedImage = LogoMatrix(MatrixToImageWriter.toBufferedImage(bitMatrix,matrixToImageConfig), new File("D:\\logo.png"));
//        BufferedImage bufferedImage = LogoMatrix(toBufferedImage(bitMatrix), new File("D:\\logo.png"));
        ImageIO.write(bufferedImage, "png", new File("D:\\qcode.png"));//输出带logo图片
        System.out.println("输出成功.");
    }

    /**
     * 二维码添加logo
     * @param matrixImage 源二维码图片
     * @param logoFile logo图片
     * @return 返回带有logo的二维码图片
     * 参考：https://blog.csdn.net/weixin_39494923/article/details/79058799
     */
    public static BufferedImage LogoMatrix(BufferedImage matrixImage, File logoFile) throws IOException{
        /**
         * 读取二维码图片，并构建绘图对象
         */
        Graphics2D g2 = matrixImage.createGraphics();

        int matrixWidth = matrixImage.getWidth();
        int matrixHeigh = matrixImage.getHeight();

        /**
         * 读取Logo图片
         */
        BufferedImage logo = ImageIO.read(logoFile);

        //开始绘制图片
        g2.drawImage(logo,matrixWidth/5*2,matrixHeigh/5*2, matrixWidth/5, matrixHeigh/5, null);//绘制
        BasicStroke stroke = new BasicStroke(5,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND);
        g2.setStroke(stroke);// 设置笔画对象
        //指定弧度的圆角矩形
        RoundRectangle2D.Float round = new RoundRectangle2D.Float(matrixWidth/5*2, matrixHeigh/5*2, matrixWidth/5, matrixHeigh/5,20,20);
        g2.setColor(Color.white);
        g2.draw(round);// 绘制圆弧矩形

        //设置logo 有一道灰色边框
        BasicStroke stroke2 = new BasicStroke(1,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND);
        g2.setStroke(stroke2);// 设置笔画对象
        RoundRectangle2D.Float round2 = new RoundRectangle2D.Float(matrixWidth/5*2+2, matrixHeigh/5*2+2, matrixWidth/5-4, matrixHeigh/5-4,20,20);
        g2.setColor(new Color(128,128,128));
        g2.draw(round2);// 绘制圆弧矩形

        g2.dispose();
        matrixImage.flush() ;
        return matrixImage ;
    }

    public static void main(String[] args) {
//        String msg = "姓名:卿香 goods_id:10000201 machine_id:ty_000020 price:2.5 open_id:wx2019100225";
//        String path = "D:\\qcode.png";
//        getBarCode(msg,path);

        try{
            QREncode();
            QRReader(new File("D:\\qcode.png"));
        } catch (WriterException e) {
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        } catch (NotFoundException e) {
            e.printStackTrace();
        }

    }
}
