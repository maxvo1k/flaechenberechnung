import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import ij.*;
import ij.io.*;
import ij.gui.*;
import java.io.File;

public class HarrisCornerPlugin_ implements PlugInFilter {
ImagePlus imp;
static float alpha = HarrisCornerDetector.DEFAULT_ALPHA;
static int threshold = HarrisCornerDetector.DEFAULT_THRESHOLD;
static int nmax = 0; //points to show

public int setup(String arg, ImagePlus imp) {
	IJ.register(HarrisCornerPlugin_.class);
	this.imp = imp;
	if (arg.equals("about")) {
		showAbout();
		return DONE;
	}
	return DOES_8G + NO_CHANGES;
	//return DOES_ALL + NO_CHANGES;
}

public void run(ImageProcessor ip) {
	if (!showDialog()) return;
	
	//ImagePlus imp = IJ.getImage();
	//ImageConverter ic = new ImageConverter(imp);
	//ic.convertToGray8();
	//imp.updateAndDraw();
	
	//dialog was cancelled or error occured
	HarrisCornerDetector hcd = new HarrisCornerDetector(ip,alpha,threshold);
	hcd.findCorners();
	ImageProcessor result = hcd.showCornerPoints(ip);
	ImagePlus win = new ImagePlus("Corners from " + imp.getTitle(),result);
	win.show();
}

void showAbout() {
	String cn = getClass().getName();
	IJ.showMessage("About "+cn+" ...", "Harris Corner Detector");
}

private boolean showDialog() {
	// display dialog , return false if cancelled or on error.
	GenericDialog dlg = new GenericDialog("Harris Corner Detector", IJ.getInstance());
	float def_alpha = HarrisCornerDetector.DEFAULT_ALPHA;
	dlg.addNumericField("Alpha (default: "+def_alpha+")", alpha, 3);
	int def_threshold = HarrisCornerDetector.DEFAULT_THRESHOLD;
	dlg.addNumericField("Threshold (default: "+def_threshold+")", threshold, 0);
	dlg.addNumericField("Max. points (0 = show all)", nmax, 0);
	dlg.showDialog();
	if(dlg.wasCanceled())
		return false;
	if(dlg.invalidNumber()) {
		IJ.showMessage("Error", "Invalid input number");
		return false;
	}
	alpha = (float) dlg.getNextNumber();
	threshold = (int) dlg.getNextNumber();
	nmax = (int) dlg.getNextNumber();
	return true;
}

}