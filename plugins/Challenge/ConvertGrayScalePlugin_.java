import ij.*;
import ij.plugin.filter.PlugInFilter;
import ij.process.*;
import java.awt.*;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import ij.*;
import ij.io.*;
import ij.gui.*;
import java.io.File;


public class ConvertGrayScalePlugin_ implements PlugInFilter {
ImagePlus imp;
	public int setup(String arg, ImagePlus imp) {
		return DOES_ALL+DOES_STACKS+SUPPORTS_MASKING;
	}

	public void run(ImageProcessor ip) {
		imp = IJ.getImage();
		
		ImagePlus imp2 = new ImagePlus();
		imp2.setImage(imp);
		imp2.show();
		
		ImageConverter ic = new ImageConverter(imp2);
		ic.convertToGray8();
		
		saveData();
		
	}
	
	void saveData() {
	
		String currentPath = getDirectory(imp);
		if(currentPath != null){
			
			//String fileName = "/newfile.txt";
			String text = "neuer Versuch";
			byte[] data = text.getBytes();
			try {
				File f = new File(currentPath, "newfile.txt");
					if (!f.exists()) {
					f.createNewFile();
					}
				FileOutputStream out = new FileOutputStream(f);
				out.write(data, 0, data.length);
				out.close();
			} catch (IOException e) {
				IJ.error("HarrisCornerDetector", ""+e);
			}
		}
	
	}
	
	String getDirectory(ImagePlus imp) {
		FileInfo fi = imp.getOriginalFileInfo();
		if (fi==null) return null;
		String dir = fi.openNextDir;
		if (dir==null) dir = fi.directory;
		return dir;
	}

}

