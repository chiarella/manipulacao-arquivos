package Pdf;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PRStream;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfNumber;
import com.itextpdf.text.pdf.PdfObject;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.parser.PdfImageObject;

public class CompactandoPdf {

	public static float FACTOR = 0.4f;

	public void compactar() throws IOException, DocumentException {

		String src2 = "C:/PDF/origem/a2.PDF";
		String dest2 = "C:/PDF/destino/";

		String src = "C:/PDF/origem/";

//		System.out.println(src2);
//
//		System.out.println(dest2);

		File arquivos = new File("C:/PDF/origem/");

		File teste2 = arquivos;

		for (File arquivo : arquivos.listFiles()) {

			String teste = arquivo.toString();

			long length = arquivo.length();
			long umMega = 1000000;

//			File f = arquivo;
//			long tamanho = f.length();
//			System.out.println(tamanho);

			if (!(length == umMega)) {
				System.out.println("true");

				PdfName key = new PdfName("ITXT_SpecialId");
				PdfName value = new PdfName("123456789");
				// Read the file
				PdfReader reader = new PdfReader(teste);
				int n = reader.getXrefSize();
				PdfObject object;
				PRStream stream;
				for (int i = 0; i < n; i++) {
					object = reader.getPdfObject(i);
					if (object == null || !object.isStream())
						continue;
					stream = (PRStream) object;

					PdfObject pdfsubtype = stream.get(PdfName.SUBTYPE);
					if (pdfsubtype != null && pdfsubtype.toString().equals(PdfName.IMAGE.toString())) {
						PdfImageObject image = new PdfImageObject(stream);
						BufferedImage bi = image.getBufferedImage();
						if (bi == null)
							continue;
						int width = (int) (bi.getWidth() * FACTOR);
						int height = (int) (bi.getHeight() * FACTOR);
						BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
						AffineTransform at = AffineTransform.getScaleInstance(FACTOR, FACTOR);
						Graphics2D g = img.createGraphics();
						g.drawRenderedImage(bi, at);
						ByteArrayOutputStream imgBytes = new ByteArrayOutputStream();
						ImageIO.write(img, "JPG", imgBytes);
						stream.clear();
						stream.setData(imgBytes.toByteArray(), false, PRStream.BEST_COMPRESSION);
						stream.put(PdfName.TYPE, PdfName.XOBJECT);
						stream.put(PdfName.SUBTYPE, PdfName.IMAGE);
						stream.put(key, value);
						stream.put(PdfName.FILTER, PdfName.DCTDECODE);
						stream.put(PdfName.WIDTH, new PdfNumber(width));
						stream.put(PdfName.HEIGHT, new PdfNumber(height));
						stream.put(PdfName.BITSPERCOMPONENT, new PdfNumber(8));
						stream.put(PdfName.COLORSPACE, PdfName.DEVICERGB);

					}

				}

				// PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(dest2 +
				// arquivo.getName().trim().toString().substring(0, 2) + "_" + j + ".pdf"));
				PdfStamper stamper = new PdfStamper(reader,
						new FileOutputStream(dest2 + arquivo.getName().trim().toString()));
				stamper.close();
				reader.close();

				// deletano arquivo na pasta principal
				arquivo.delete();

			}
		}
	}

	public static void main(String[] args) throws IOException, DocumentException {
		// createPdf(RESULT);
		new CompactandoPdf().compactar();
	}

}
