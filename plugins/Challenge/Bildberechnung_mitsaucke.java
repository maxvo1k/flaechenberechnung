import ij.*;
import ij.plugin.filter.PlugInFilter;
import ij.plugin.*;
import ij.process.*;

import java.awt.*;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import ij.*;
import ij.io.*;
import ij.gui.*;
import java.io.File;

import ij.plugin.*;
import ij.*;
import ij.io.*;
import java.io.*;
import javax.swing.*;
import javax.swing.filechooser.*;

import java.util.Arrays;
import java.util.Collections;
import ij.io.Opener;

import java.util.ArrayList;
import java.util.List;

import ij.measure.Calibration;
import ij.macro.Interpreter;

import java.text.DecimalFormat;

/*
Algemeine Infos:

Bilder auf jeden Fall unter 4.000px * 3.000px = 12.000.000px
Könnte auch sein dass es noch deutlich kleiner sein muss!

Funktioniert: 3.834.560

*/

public class Bildberechnung_mitsaucke extends ImagePlus implements PlugIn {
	
ImagePlus imp;
ImagePlus imp_a;
protected byte[] pixels;
String path;

File[] files;

private int type;

String StartFileName;
String StartFileNameSmall;
String StartFileDirectory;
String[] coords_array;
String[] good_coords_array;
String[] clean_coords_array;

String[] exportImages;
String[] exportCalc;

static float alpha = HarrisCornerDetector.DEFAULT_ALPHA;
static int threshold = HarrisCornerDetector.DEFAULT_THRESHOLD;
static int nmax = 0; //points to show
static File dir;

	public void run(String arg) {
		
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		String selection = showChoice();
		System.out.println("Auswahl: " + selection);
		
		if(selection.equals("VORAN")){
			startVORAN();
		}
		if(selection.equals("AKWHA")){
			//startAKHWA();
		}
		if(selection.equals("OSCAR")){
			//startOSCAR();
		}
		if(selection.equals("Einzelnes Viereck")){
			startEinzelnViereck();
		}
		if(selection.equals("")){
			//if (!showInfo("Fehler", "Fehler beim Erstellen des Export-Verzeichnisses")) return;
		}
		
		
	}
	
	public void startVORAN(){
		files = openFiles();
			
			Opener opener = new Opener();
			if(files != null){
				
				boolean isCreated = false;
				
				File dir = new File(path+"/Export");
				if(!dir.exists()){
					isCreated = dir.mkdir();
				}
				
				if(dir.exists() || isCreated){
					boolean finished = false;
					for (int i=0; i<files.length; i++) {
						ImagePlus img = opener.openImage(path, files[i].getName());
						if (img!=null)
							imp = img;
							imp.show();
							loadimages(i);
							if(i == files.length-1){
								finished = true;
							}
							if(finished){
								showInfo("Info", "Exportierung und Berechnung erfolgreich abgeschlossen");
							}
					}
				} else {
					//Not created, message!
					if (!showInfo("Fehler", "Fehler beim Erstellen des Export-Verzeichnisses")) return;
				}
			} else {
				//No files selected, message!
				if (!showInfo("Fehler", "Keine Dateien ausgew\u00e4hlt.")) return;
			}
	}
	
	public void startEinzelnViereck(){
		files = openFiles();
			
			Opener opener = new Opener();
			if(files != null){
				
				boolean isCreated = false;
				
				File dir = new File(path+"/Export");
				if(!dir.exists()){
					isCreated = dir.mkdir();
				}
				
				if(dir.exists() || isCreated){
					boolean finished = false;
					for (int i=0; i<files.length; i++) {
						ImagePlus img = opener.openImage(path, files[i].getName());
						if (img!=null)
							imp = img;
							imp.show();
							loadimagesEinzeln(i);
							if(i == files.length-1){
								finished = true;
							}
							if(finished){
								showInfo("Info", "Exportierung und Berechnung erfolgreich abgeschlossen");
							}
					}
				} else {
					//Not created, message!
					if (!showInfo("Fehler", "Fehler beim Erstellen des Export-Verzeichnisses")) return;
				}
			} else {
				//No files selected, message!
				if (!showInfo("Fehler", "Keine Dateien ausgew\u00e4hlt.")) return;
			}
	}
	
	File[] openFiles() {
		
		File[] files_open;
		
		JFileChooser fc = null;
		try {
			fc = new JFileChooser();
		}
		catch (Throwable e) {
			IJ.error("This plugin requires Java 2 or Swing."); return null;
		}
		fc.setMultiSelectionEnabled(true);
		
		if (dir==null) {
			String sdir = OpenDialog.getDefaultDirectory();
			if (sdir!=null)
				dir = new File(sdir);
		}
		if (dir!=null)
			fc.setCurrentDirectory(dir);
		
		int returnVal = fc.showOpenDialog(IJ.getInstance());
		
		if (returnVal!=JFileChooser.APPROVE_OPTION)
			return null;
		
		files_open = fc.getSelectedFiles();
		
		if (files_open.length==0) { // getSelectedFiles does not work on some JVMs
			files_open = new File[1];
			files_open[0] = fc.getSelectedFile();
		}
		path = fc.getCurrentDirectory().getPath()+Prefs.getFileSeparator();
		
		return files_open;
	}
	
	public void loadimages(int CountImages){
		imp = IJ.getImage();
		ImageProcessor originalip = imp.getProcessor();
		
		StartFileDirectory = getDirectory(imp);
		StartFileName = getFileName(imp);
		
		String splitString[] = StartFileName.split("_");
		String StartFileMask = splitString[0];
		
		StartFileNameSmall = StartFileName.substring(6);
		
		
		String CropPath = StartFileDirectory + StartFileNameSmall;
				
		ImagePlus imp2 = new ImagePlus();
		imp2.setImage(imp);
		imp.close();
		imp2.show();
		
		ImageConverter ic = new ImageConverter(imp2);
		ic.convertToGray8();
		
		ImageProcessor newip = imp2.getProcessor();
		
		//if (!showDialog()) return;
		
		HarrisCornerDetector hcd = new HarrisCornerDetector(newip,alpha,threshold);
		hcd.findCorners();
		//ImageProcessor result = hcd.showCornerPoints(newip);
		ImageProcessor result = hcd.showCornerPoints(newip);
		
		String coords = hcd.getCoordinatesString();
		//String coords = "";
		coords_array = hcd.getCoordinatesArray();
		
		ImagePlus win = new ImagePlus("Corners from " + imp.getTitle(),result);
		win.show();
		imp2.close();
		win.close(); //Hier die erkannten Eckpunkte anschauen
		
		//saveData(coords, "coordsnew.txt");
		exportCropImages(CropPath, CountImages, StartFileMask);
	}
	
	public void loadimagesEinzeln(int CountImages){
		imp = IJ.getImage();
		ImageProcessor originalip = imp.getProcessor();
		
		StartFileDirectory = getDirectory(imp);
		StartFileName = getFileName(imp);
		
		String splitString[] = StartFileName.split("_");
		String StartFileMask = splitString[0];
		
		StartFileNameSmall = StartFileName.substring(6);
		
		
		String CropPath = StartFileDirectory + StartFileNameSmall;
				
		ImagePlus imp2 = new ImagePlus();
		imp2.setImage(imp);
		imp.close();
		imp2.show();
		
		ImageConverter ic = new ImageConverter(imp2);
		ic.convertToGray8();
		
		ImageProcessor newip = imp2.getProcessor();
		
		//if (!showDialog()) return;
		
		HarrisCornerDetector hcd = new HarrisCornerDetector(newip,alpha,threshold);
		hcd.findCorners();
		//ImageProcessor result = hcd.showCornerPoints(newip);
		ImageProcessor result = hcd.showCornerPoints(newip);
		
		String coords = hcd.getCoordinatesString();
		//String coords = "";
		coords_array = hcd.getCoordinatesArray();
		
		ImagePlus win = new ImagePlus("Corners from " + imp.getTitle(),result);
		win.show();
		imp2.close();
		win.close(); //Hier die erkannten Eckpunkte anschauen
		
		//saveData(coords, "coordsnew2.txt");
		exportCropImagesEinzelViereck(CropPath, CountImages, StartFileMask);
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
	
	private boolean showInfo(String title, String message) {
		// display dialog , return false if cancelled or on error.
		GenericDialog dlg = new GenericDialog(title, IJ.getInstance());
		dlg.hideCancelButton();
		dlg.addMessage(message);
		
		dlg.showDialog();
		if(dlg.invalidNumber()) {
			IJ.showMessage("Error", "Invalid input number");
			return false;
		}
		
		return true;
	}
	
	String showChoice() {
		// display dialog , return false if cancelled or on error.
		int index = 0;
		String[] projektArray = { "VORAN", "AKHWA", "OSCAR", "Einzelnes Viereck"};
		String choice = "";
		
		GenericDialog dlg = new GenericDialog("Auswahl", IJ.getInstance());
		dlg.addMessage("Bitte Projekt ausw\u00e4hlen");
		dlg.addChoice( "Projekt", projektArray, projektArray[index] );
		
		dlg.showDialog();
		
		choice = dlg.getNextChoice();
		if(dlg.wasCanceled())
			return "";
		if(choice.equals("")) {
			IJ.showMessage("Error", "Invalid input number");
			return "";
		}
		
		return choice;
	}
	
	void saveData(String text, String filename) {
		//text = "";
		
		//Arrays.sort(coords_array);
		
		//for(int i = 0; i<good_coords_array.length; i++){
		//	text = text + good_coords_array[i] + "\n";
		//}
	
		String currentPath = getDirectory(imp);
		if(currentPath != null){
			String savePath = currentPath + "/Export";
			byte[] data = text.getBytes();
			try {
				File f = new File(savePath, filename);
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
	
	void exportCropImages(String ImageToCrop, int CountImages, String Maske) {
		
		int blockcounter = 1;
		int counter = 0;
		int good_counter = 0;
		boolean startsaving = false;
		boolean two_control = false;
		int two_counter = 1;
		
		String entrys = "Saved Entries: \n";
		
		Arrays.sort(coords_array);
		
		String[] better_coords_array = new String[coords_array.length];
		
		for(int i=0; i<coords_array.length; i++){
			if(i > 0){
				//Vergleichen: Wenn array(i) und array(i-1) ungefähr gleich sind (innerhalb 5px) dann counter++
				String entry = coords_array[i];
				String oldentry = coords_array[i-1];
				
				String entrysplitarray[]= entry.split("_");
				String oldentrysplitarray[]= oldentry.split("_");
				
				String entry_x = entrysplitarray[0];
				String oldentry_x = oldentrysplitarray[0];
				
				
				int x = Integer.parseInt(entry_x);
				int old_x = Integer.parseInt(oldentry_x);
				
				int diff = x - old_x;
				
				/*if(diff >= 0 && diff < 5){
					blockcounter++;
				}*/
				
				//entrys = entrys + String.valueOf(blockcounter) + ": " + entry + "\n";
				
				if(startsaving){
					better_coords_array[good_counter] = entry;
					//System.out.println(entry);
					entrys = entrys + entry + "\n";
					good_counter++;
					counter++;
					if(counter == 16){
						counter = 0;
						startsaving = false;
						two_counter++; //
					}
				} else {
					if(diff >= 0 && diff < 5){
						blockcounter++;
					} else {
						if(blockcounter == 2){
							if(!two_control){
								two_control = true;
							}
							blockcounter = 1;
						}
						if(two_counter == 4){
							two_control = false;
							two_counter = 1;
						}
						if(!two_control && blockcounter == 5){
						blockcounter = 1;
						}
					}
					if(two_control && blockcounter == 5){
						startsaving = true;
						blockcounter = 1;
						
					}
					
				}
				
			}
		}
		
		good_coords_array = RemoveNullArray(better_coords_array);
		
		clean_coords_array = CleanArray(good_coords_array);
		
		//saveData(String.valueOf(good_coords_array.length), "coordsArray.txt");
		
		int imagecounter = 0;
		
		String[] correct_numbers = {"04", "03", "02", "01", "08", "07", "06", "05", "12", "11", "10", "09", "16", "15", "14", "13", "20", "19", "18", "17", "24", "23", "22", "21", "28", "27", "26", "25", "32", "31", "30", "29", "36", "35", "34", "33", "40", "39", "38", "37", "44", "43", "42", "41", "48", "47", "46", "45"};
		
		exportImages = new String[4*12];
		exportCalc = new String[4*12];
		String export = "Export der Fl\u00e4chenberechnung: \n";
		String filename = "";
		
		Opener opener = new Opener();
		ImagePlus impToCrop = opener.openImage(ImageToCrop);
		
		for(int i=0; i<23; i+=2){
			for(int k=0; k<8; k+=2){
		//for(int i=0; i<5; i+=2){
		//	for(int k=6; k==0; k-=2){
				
				int calc = i*8+k;
				int bigcalc = calc + 1;
				String x1y1 = clean_coords_array[calc]; 
				String x2y1 = clean_coords_array[calc + 8];
				String x1y2 = clean_coords_array[bigcalc]; 
				String x2y2 = clean_coords_array[bigcalc + 8];
				
				String x1y1split[]= x1y1.split("_");
				String x2y1split[]= x2y1.split("_");
				String x1y2split[]= x1y2.split("_");
				String x2y2split[]= x2y2.split("_");
				
				String x1 = x1y1split[0];
				String x2 = x2y1split[0];
				String y1 = x1y1split[1];
				String y2 = x2y2split[1];
				
				int roi_x1 = Integer.parseInt(x1);
				int roi_x2 = Integer.parseInt(x2);
				int roi_y1 = Integer.parseInt(y1);
				int roi_y2 = Integer.parseInt(y2);
				
				int a = roi_x2 - roi_x1;
				int b = roi_y2 - roi_y1;
				
				Roi roi_imp = new Roi(roi_x1, roi_y1, a, b);
				
				impToCrop.setRoi(roi_imp);
				ImagePlus imp3 = impToCrop.crop();
				
				//String name = StartFileDirectory + "/Export/" + String.valueOf(CountImages) + "_" + Maske + "_" + String.valueOf(imagecounter);
				String name = StartFileDirectory + "/Export/" + Maske + "_" + correct_numbers[imagecounter];
				
				exportImages[imagecounter] = name + ".jpg";
				
				IJ.saveAs(imp3, "jpg", name);
				
				imagecounter++;
				
			}	
		}
		
		//for(int i = 0; i < imagecounter; i++){
			//open exported image and perform LAB conversion
			
		//}
		/*
		
		Opener opener = new Opener();
		ImagePlus impToCrop = opener.openImage(ImageToCrop);
		impToCrop.setRoi(roi_imp);
		ImagePlus imp3 = impToCrop.crop();
		*/
		for(int i=0; i<exportImages.length; i++){
			Opener opener_toconvert = new Opener();
			ImagePlus impToConvert = opener_toconvert.openImage(exportImages[i]);
			
			//System.out.println(exportImages[i]);
			
			type = impToConvert.getType();
			
			ImagePlus converted_imp = convertToLab(impToConvert);
			
			converted_imp.show();
			
			System.out.println(String.valueOf(converted_imp.getStackSize()));
			
			imp_a = convertStackToImages(converted_imp);
			
						
			
			if(imp_a != null){
				
				imp_a.show();
				//IJ.run(imp_a, "Make Binary", "");
				ImageProcessor ip_binary = imp_a.getProcessor();
				ip_binary.setAutoThreshold(AutoThresholder.Method.Shanbhag, false, ImageProcessor.BLACK_AND_WHITE_LUT);
				ip_binary.autoThreshold(); 
				
				ImagePlus imagenew = new ImagePlus();
				imagenew.setProcessor(ip_binary);
				imp_a.close();
				imagenew.show();
				
				ImageStatistics stats = ImageStatistics.getStatistics(ip_binary, ImageStatistics.AREA_FRACTION, null);

				double aFraction = stats.areaFraction;
				
				double area = (aFraction * 27)/100;
				
				DecimalFormat df = new DecimalFormat("#.##");
				String aFraction_df = df.format(aFraction); 
				String area_df = df.format(area);
				
				String name = StartFileDirectory + "/Export/" + Maske + "_" + correct_numbers[i] + "_binary";
				
				IJ.saveAs(imagenew, "jpg", name);
				
				imagenew.close();
			
				System.out.println("Flaechenberechnung (" + correct_numbers[i] + "): " + String.valueOf(aFraction));
				//export = export + String.valueOf(CountImages) + "_" + Maske + "_" + correct_numbers[i] + ": " + area_df + "m\u00B2 (" + aFraction_df + "%)\n";
				exportCalc[i]= String.valueOf(CountImages) + "_" + Maske + "_" + correct_numbers[i] + ": " + area_df + "m\u00B2 (" + aFraction_df + "%)";
				filename = Maske + ".txt";
			}
		}
		
		Arrays.sort(exportCalc);
		
		for(int i=0; i<exportCalc.length; i++){
			export = export + exportCalc[i] + "\n";
		}
		
		saveData(export, filename);
	}
	
	void exportCropImagesEinzelViereck(String ImageToCrop, int CountImages, String Maske) {
		
		int blockcounter = 1;
		int counter = 0;
		int good_counter = 0;
		boolean startsaving = false;
		boolean two_control = false;
		int two_counter = 1;
		int imagecounter = 0;
		
		String entrys = "Saved Entries: \n";
		
		Arrays.sort(coords_array);
		
		good_coords_array = RemoveNullArray(coords_array);
		
		//saveData("text", "coordsArray.txt");
		
		exportImages = new String[1];
		exportCalc = new String[1];
		String export = "Export der Fl\u00e4chenberechnung: \n";
		String filename = "";
		
		Opener opener = new Opener();
		ImagePlus impToCrop = opener.openImage(ImageToCrop);
				
		String x1y1 = good_coords_array[0]; 
		String x2y1 = good_coords_array[2];
		String x1y2 = good_coords_array[1]; 
		String x2y2 = good_coords_array[3];
		
		String x1y1split[]= x1y1.split("_");
		String x2y1split[]= x2y1.split("_");
		String x1y2split[]= x1y2.split("_");
		String x2y2split[]= x2y2.split("_");
		
		String x1 = x1y1split[0];
		String x2 = x2y1split[0];
		String y1 = x1y1split[1];
		String y2 = x2y2split[1];
		
		int roi_x1 = Integer.parseInt(x1);
		int roi_x2 = Integer.parseInt(x2);
		int roi_y1 = Integer.parseInt(y1);
		int roi_y2 = Integer.parseInt(y2);
		
		int a = roi_x2 - roi_x1;
		int b = roi_y2 - roi_y1;
		
		Roi roi_imp = new Roi(roi_x1, roi_y1, a, b);
		
		impToCrop.setRoi(roi_imp);
		ImagePlus imp3 = impToCrop.crop();
		
		//String name = StartFileDirectory + "/Export/" + String.valueOf(CountImages) + "_" + Maske + "_" + String.valueOf(imagecounter);
		String name = StartFileDirectory + "/Export/" + Maske + "_" + String.valueOf(imagecounter);
		
		exportImages[imagecounter] = name + ".jpg";
		
		IJ.saveAs(imp3, "jpg", name);
	
		/*		
		Opener opener = new Opener();
		ImagePlus impToCrop = opener.openImage(ImageToCrop);
		impToCrop.setRoi(roi_imp);
		ImagePlus imp3 = impToCrop.crop();
		*/
		for(int i=0; i<exportImages.length; i++){
			Opener opener_toconvert = new Opener();
			ImagePlus impToConvert = opener_toconvert.openImage(exportImages[i]);
			
			//System.out.println(exportImages[i]);
			
			type = impToConvert.getType();
			
			ImagePlus converted_imp = convertToLab(impToConvert);
			
			converted_imp.show();
			
			System.out.println(String.valueOf(converted_imp.getStackSize()));
			
			imp_a = convertStackToImages(converted_imp);
									
			
			if(imp_a != null){
				
				imp_a.show();
				//IJ.run(imp_a, "Make Binary", "");
				ImageProcessor ip_binary = imp_a.getProcessor();
				ip_binary.setAutoThreshold(AutoThresholder.Method.Shanbhag, false, ImageProcessor.BLACK_AND_WHITE_LUT);
				ip_binary.autoThreshold(); 
				
				ImagePlus imagenew = new ImagePlus();
				imagenew.setProcessor(ip_binary);
				imp_a.close();
				imagenew.show();
				
				ImageStatistics stats = ImageStatistics.getStatistics(ip_binary, ImageStatistics.AREA_FRACTION, null);

				double aFraction = stats.areaFraction;
				
				double area = (aFraction * 36)/100;
				
				DecimalFormat df = new DecimalFormat("#.##");
				String aFraction_df = df.format(aFraction); 
				String area_df = df.format(area);
				
				String name_export = StartFileDirectory + "/Export/" + Maske + "_" + String.valueOf(i) + "_binary";
				
				IJ.saveAs(imagenew, "jpg", name_export);
				
				imagenew.close();
			
				System.out.println("Flaechenberechnung (" + String.valueOf(i) + "): " + String.valueOf(aFraction));
				//export = export + String.valueOf(CountImages) + "_" + Maske + "_" + correct_numbers[i] + ": " + area_df + "m\u00B2 (" + aFraction_df + "%)\n";
				exportCalc[i]= String.valueOf(CountImages) + "_" + Maske + "_" + String.valueOf(i) + ": " + area_df + "m\u00B2 (" + aFraction_df + "%)";
				filename = Maske + ".txt";
			}
		}
		
		Arrays.sort(exportCalc);
		
		for(int i=0; i<exportCalc.length; i++){
			export = export + exportCalc[i] + "\n";
		}
		
		saveData(export, filename);
	}
	
	ImagePlus convertStackToImages(ImagePlus imp) { //StackEditor
		
		ImagePlus returnimage = null;
		
		if (!imp.lock())
			return null;
		ImageStack stack = imp.getStack();
		int size = stack.size();
		
		Calibration cal = imp.getCalibration();
		CompositeImage cimg = imp.isComposite()?(CompositeImage)imp:null;
		if (imp.getNChannels()!=imp.getStackSize()) cimg = null;
		Overlay overlay = imp.getOverlay();
		int lastImageID = 0;
		for (int i=1; i<=size; i++) {
			String label = stack.getShortSliceLabel(i);
			if (label!=null && (label.contains("/") || label.contains("\\") || label.contains(":")))
				label = null;
			String title = label!=null&&!label.equals("")?label:getTitle(imp, i);
			ImageProcessor ip = stack.getProcessor(i);
			if (cimg!=null) {
				LUT lut = cimg.getChannelLut(i);
				if (lut!=null) {
					ip.setColorModel(lut);
					ip.setMinAndMax(lut.min, lut.max);
				}
			}
			ImagePlus imp2 = new ImagePlus(title, ip);
			imp2.setCalibration(cal);
			String info = stack.getSliceLabel(i);
			if (info!=null && !info.equals(label))
				imp2.setProperty("Info", info);
			imp2.setIJMenuBar(i==size);
			if (overlay!=null) {
				Overlay overlay2 = new Overlay();
				for (int j=0; j<overlay.size(); j++) {
					Roi roi = overlay.get(j);
					if (roi.getPosition()==i) {
						roi.setPosition(0);
						overlay2.add((Roi)roi.clone());
					}
				}
				if (overlay2.size()>0)
					imp2.setOverlay(overlay2);
			}
			if (i==size)
				lastImageID = imp2.getID();
			//System.out.println(label);
			if(label.equals("a*")){
				//imp2.show();
				returnimage = imp2;
			}
			
		}
		imp.changes = false;
		ImageWindow win = imp.getWindow();
		if (win!=null)
			win.close();
		else if (Interpreter.isBatchMode())
			Interpreter.removeBatchModeImage(imp);
		imp.unlock();
		return returnimage;
	}
	
	String getTitle(ImagePlus imp, int n) {
		String digits = "00000000"+n;
		return getShortTitle(imp)+"-"+digits.substring(digits.length()-4,digits.length());
	}
	
	private String getShortTitle(ImagePlus imp) {
		String title = imp.getTitle();
		int index = title.indexOf(' ');
		if (index>-1)
			title = title.substring(0, index);
		index = title.lastIndexOf('.');
		if (index>0)
			title = title.substring(0, index);
		return title;
    }
	
	ImagePlus convertToLab(ImagePlus impToConvert) {
        if (type!=ImagePlus.COLOR_RGB)
            throw new IllegalArgumentException("Image must be RGB");
        ColorSpaceConverter converter = new ColorSpaceConverter();
        ImagePlus imp2 = converter.RGBToLab(impToConvert);
        Point loc = null;
        ImageWindow win = imp.getWindow();
        if (win!=null)
            loc = win.getLocation();
        ImageWindow.setNextLocation(loc);
        //imp2.show();
        imp.hide();
        imp2.copyAttributes(imp);
        imp.changes = false;
        imp.close();
		return imp2;
    }
	
	String[] RemoveNullArray(String[] removenullStringArray){
		
		List<String> list = new ArrayList<String>();

		for(String s : removenullStringArray) {
		   if(s != null && s.length() > 0) {
			  list.add(s);
		   }
		}
		removenullStringArray = list.toArray(new String[list.size()]);
		
		return removenullStringArray;
		
	}
	
	String[] CleanArray(String[] cleanStringArray){
		
		String[] result = new String[cleanStringArray.length];
		int counter = 0;
		double calc = 0;
		DecimalFormat df = new DecimalFormat("#");
		
		for(int i=0; i<cleanStringArray.length; i++){
			String entry = cleanStringArray[i];
			
			String entrysplitarray[]= entry.split("_");
			
			String entry_x = entrysplitarray[0];
			
			int x = Integer.parseInt(entry_x);
			
			counter++;
			
			calc = calc + x;
			
			String new_x = "";
			
			if(counter == 8){
				calc = calc/8;
				String value = df.format(calc);
				new_x = value;
				
				if(calc <= 9){
					new_x = "000" + value;
				}
				if(calc > 9 && calc <= 99){
					new_x = "00" + value;
				}
				if(calc > 99 && calc <= 999){
					new_x = "0" + value;
				}
				
				System.out.println(new_x);
				//result[i] = "bla";
				for(int k = 7; k>=0; k--){
					
					String splitentry = cleanStringArray[i-k];
					String splitentrysplitarray[]= splitentry.split("_");
					String entry_y = splitentrysplitarray[1];
					
					result[i-k] = new_x + "_" + entry_y;
					//result[i-k] = "bla";
				}
				counter = 0;
				calc = 0;
			}
			
		}
		Arrays.sort(result);
		return result;
		
	}
	
	String getDirectory(ImagePlus imp) {
		FileInfo fi = imp.getOriginalFileInfo();
		if (fi==null) return null;
		String dir = fi.openNextDir;
		if (dir==null) dir = fi.directory;
		return dir;
	}
	
	String getFileName(ImagePlus imp) {
		String name = imp.getOriginalFileInfo().fileName;
		return name;
	}

}

