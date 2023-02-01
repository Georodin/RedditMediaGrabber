package model;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.imageio.ImageIO;

import org.jcodec.api.FrameGrab;
import org.jcodec.common.model.Picture;
import org.jcodec.scale.AWTUtil;

public class ImageConverter {
	
	public static BufferedImage createThumbnailFromMP4(String filename) {
	
		int frameNumber = 0;

		try {
			Picture picture = FrameGrab.getFrameFromFile(
					new File(filename), frameNumber);
			return AWTUtil.toBufferedImage(picture);
			
		} catch (Exception e) {
			StringWriter sw = new StringWriter(); PrintWriter pw = new PrintWriter(sw); e.printStackTrace(pw); LogUtility.newLineToErrorLog(sw);
		}
		return null;
	}

	public static boolean createPreview(String inputImgPath) {
		
		BufferedImage preview = null;
		
		if(inputImgPath.endsWith(".gif")) {
			preview = getStillFromGIF(inputImgPath);
		}else if(inputImgPath.endsWith(".mp4")) {
			preview = createThumbnailFromMP4(inputImgPath);
		}else{
			try {
				FileInputStream inputStream = new FileInputStream(inputImgPath);
				preview = removeAlphaChannel(ImageIO.read(inputStream));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				StringWriter sw = new StringWriter(); PrintWriter pw = new PrintWriter(sw); e.printStackTrace(pw); LogUtility.newLineToErrorLog(sw);
			}
		}
		
		preview = proportionalResizing(preview, inputImgPath);
		
		String outputImgPath = inputImgPath.substring(0, inputImgPath.lastIndexOf('/'));
		String outputImgFile = inputImgPath.substring(inputImgPath.lastIndexOf('/'), inputImgPath.length());
		outputImgFile = outputImgFile.substring(0, outputImgFile.lastIndexOf('.'))+".jpg";
		
		File outputfile = new File(outputImgPath+"/previews/"+outputImgFile);
		
		try {
			ImageIO.write(preview, "jpg", outputfile);
			return true;
		} catch (IOException e) {
//			//System.out.println("image: "+preview);
//			//System.out.println("loc: "+outputfile);
			StringWriter sw = new StringWriter(); PrintWriter pw = new PrintWriter(sw); e.printStackTrace(pw); LogUtility.newLineToErrorLog(sw);
			return false;
		}
	}
	
	static BufferedImage proportionalResizing(BufferedImage img, String imagePath) {
		
		BufferedImage output = img;
		long fileSize = 0;
		
		Path filePath = Paths.get(imagePath);
		FileChannel fileChannel;
		try {
			fileChannel = FileChannel.open(filePath);
			fileSize = fileChannel.size() / 1024;
			fileChannel.close();
		}catch (Exception e) {
			StringWriter sw = new StringWriter(); PrintWriter pw = new PrintWriter(sw); e.printStackTrace(pw); LogUtility.newLineToErrorLog(sw);
		}
		
		try {
			if(img.getWidth()>1200||img.getHeight()>1200||fileSize>300) {
		        if(img.getWidth()>=img.getHeight()) {
					output = resizeImage(img, 1000, Math.round((1000f/img.getWidth())* (float) img.getHeight()));
		        }else {
		        	output = resizeImage(img, Math.round((1000f/img.getHeight())* (float) img.getWidth()), 1000);
		        }
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			StringWriter sw = new StringWriter(); PrintWriter pw = new PrintWriter(sw); e.printStackTrace(pw); LogUtility.newLineToErrorLog(sw);
		}
        
		return output;
	}
	
    public static BufferedImage getStillFromGIF(String inputImgPath)
	{
    	BufferedImage outputImage = null;
		try {

			FileInputStream inputStream = new FileInputStream(inputImgPath);
			outputImage = removeAlphaChannel(ImageIO.read(inputStream));
			
			inputStream.close();
		} catch (Exception e) {
			StringWriter sw = new StringWriter(); PrintWriter pw = new PrintWriter(sw); e.printStackTrace(pw); LogUtility.newLineToErrorLog(sw);
		}
		return outputImage;
	}
    
    private static BufferedImage createImage(int width, int height, boolean hasAlpha) {
        return new BufferedImage(width, height, hasAlpha ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB);
    }
    
    private static BufferedImage removeAlphaChannel(BufferedImage img) {
        if (!img.getColorModel().hasAlpha()) {
            return img;
        }

        BufferedImage target = createImage(img.getWidth(), img.getHeight(), false);
        Graphics2D g = target.createGraphics();
        g.fillRect(0, 0, img.getWidth(), img.getHeight());
        g.drawImage(img, 0, 0, null);
        g.dispose();

        return target;
    }
    
    static BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) throws IOException {
        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = resizedImage.createGraphics();
        graphics2D.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
        graphics2D.dispose();
        return resizedImage;
    }
}
