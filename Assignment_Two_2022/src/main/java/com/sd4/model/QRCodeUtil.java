/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sd4.model;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 *
 * @author darwi
 */
public class QRCodeUtil {
   private static final String CHARSET = "utf-8";
   private static final String FORMAT_NAME = "JPG";
   // QR code size
   private static final int QRCODE_SIZE = 300;


/**
 * Generate QR code image
 * @param content QR code content
 * @param imgPath Picture address
 * @param needCompress Whether to compress 
 * @return
 * @throws Exception
 */
public static BufferedImage generateQRCodeImage(String barcodeText) throws Exception {
    QRCodeWriter barcodeWriter = new QRCodeWriter();
    BitMatrix bitMatrix = 
      barcodeWriter.encode(barcodeText, BarcodeFormat.QR_CODE, 200, 200);

    return MatrixToImageWriter.toBufferedImage(bitMatrix);
}



}