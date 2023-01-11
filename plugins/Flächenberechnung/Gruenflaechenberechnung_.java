import ij.*;
import ij.plugin.filter.PlugInFilter;

import ij.plugin.filter.Analyzer;
import ij.measure.ResultsTable;
import ij.plugin.filter.ParticleAnalyzer;
//import ij.plugin.frame.ColorThresholder;

import ij.plugin.frame.*;
import ij.plugin.*;
import ij.process.*;

import java.awt.*;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ByteOrder;

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
import java.lang.Math;

import java.util.Date;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;

import java.awt.*;
import javax.swing.*;

import ij.gui.NonBlockingGenericDialog;

/*
Algemeine Infos:

Bilder auf jeden Fall unter 4.000px * 3.000px = 12.000.000px
KÃ¶nnte auch sein dass es noch deutlich kleiner sein muss!

Funktioniert: 3.834.560px

*/

public class Gruenflaechenberechnung_ extends ImagePlus implements PlugIn {
	
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

String color_string_value;
Integer[] color_int_values;
Boolean[] color_bool_values;


static float alpha = HarrisCornerDetector.DEFAULT_ALPHA;
static int threshold = HarrisCornerDetector.DEFAULT_THRESHOLD;
static int nmax = 0; //points to show
static File dir;

//ColorThresholderExtended
String method = "DEFAULT";
int minHue = 20;
int maxHue = 100;
int minSat = 0;
int maxSat = 255;
int minBri = 0;
int maxBri = 170;
int mode = 0;
int colorSpace = 0;
int method_int = 0;
boolean bandPassH = true;
boolean bandPassS = true;
boolean bandPassB = true;
boolean darkBackground = false;

byte[] PixelMask;

boolean finished = false;
boolean problem = true;

double scale_inCM = 0;
double scale_PxCM = 0;
double scale_CMPx = 0;

//Baitplates-----------
int[] dark_pixel_array = {434,435,436,437,438,472,473,474,475,476,508,509,510,511,512,546,547,548,549,550,584,585,586,587,588,622,623,624,625,626,627,628,629,630,631,660,661,662,663,664,665,666,667,668,669,698,699,700,701,702,703,704,705,706,707,708,709,710,736,737,738,739,740,741,742,743,744,745,746,747,748,774,775,776,777,778,779,780,781,782,783,784,785,786,787,788,812,813,814,815,816,817,818,819,820,821,822,823,824,825,826,850,851,852,853,854,855,856,857,858,859,860,861,862,863,864,889,890,891,892,893,894,895,896,897,898,899,900,928,929,930,931,932,933,934,935,936,937,938,966,967,968,969,970,971,972,973,974,975,976};

int dpa_min = 0;
int dpa_max = 120;
int dpa_mean = 65;

double[][] global_start_XY = new double[1][2];
double[][] global_end_XY = new double[1][2];

//Erste Berechnungen
/*int[] empty_pixel_array_high = {175,176,177,178,179,213,214,215,216,217,250,251,252,253,254,255,256,288,289,290,291,292,293,294,326,327,328,329,330,331,332,333,334,364,365,366,367,368,369,370,371,372,402,403,404,405,406,407,408,409,410,440,441,442,443,444,445,446,447,448,479,480,481,482,483,484,485,486,487};

int epa_h_min = 165;
int epa_h_max = 253;
int epa_h_mean = 228;*/

int[] empty_pixel_array_high = {175,176,177,178,179,213,214,215,216,217,250,251,252,253};

int epa_h_min = 194;
int epa_h_max = 233;
int epa_h_mean = 220;

//Erste Berechnungen
/*int[] empty_pixel_array_low = {394,395,432,433,469,470,471,507,508,509,545,546,547,583,584,585,586,621,622,623,624,659,660,661,662,663,664,665,666,697,698,699,700,701,702,703,704,735,736,737,738,739,740,741,742,743,744,745,746,773,774,775,776,777,778,779,780,781,782,783,784,785,786,787,788,812,813,814,815,816,817,818,819,820,821,822,823,824,825,826,850,851,852,853,854,855,856,857,858,859,860,861,862,888,889,890,891,892,893,894,895,896,897,898,899,900,928,929,930,931,932,933,934};

int epa_l_min = 36;
int epa_l_max = 135;
int epa_l_mean = 77;*/

int[] empty_pixel_array_low = {699,700,701,702,703,704,705,706,737,738,739,740,741,742,743,744,775,776,777,778,779,780,781,782,813,814,815,816,817,818,819,820,851,852,853,854,855,856,857,858,889,890,891,892,893,894,895,896};

int epa_l_min = 50;
int epa_l_max = 107;
int epa_l_mean = 71;

boolean darkpixel = false;
boolean emptypixel_high = false;
boolean emptypixel_low = false;

int[] circle_pixel_array = {248,249,250,251,285,286,287,288,289,290,320,321,322,323,324,325,326,327,328,329,330,358,359,360,361,362,363,364,365,366,367,368,394,395,396,397,398,399,400,401,402,403,404,405,406,407,408,432,433,434,435,436,437,438,439,440,441,442,443,444,445,446,470,471,472,473,474,475,476,477,478,479,480,481,482,483,484,505,506,507,508,509,510,511,512,513,514,515,516,517,518,519,520,521,522,523,543,544,545,546,547,548,549,550,551,552,553,554,555,556,557,558,559,560,561,581,582,583,584,585,586,587,588,589,590,591,592,593,594,595,596,597,598,599,600,619,620,621,622,623,624,625,626,627,628,629,630,631,632,633,634,635,636,637,638,657,658,659,660,661,662,663,664,665,666,667,668,669,670,671,672,673,674,675,676,695,696,697,698,699,700,701,702,703,704,705,706,707,708,709,710,711,712,713,714,735,736,737,738,739,740,741,742,743,744,745,746,747,748,749,750,751,752,773,774,775,776,777,778,779,780,781,782,783,784,785,786,787,788,789,790,812,813,814,815,816,817,818,819,820,821,822,823,824,825,826,827,850,851,852,853,854,855,856,857,858,859,860,861,862,863,864,865,889,890,891,892,893,894,895,896,897,898,899,900,901,927,928,929,930,931,932,933,934,935,936,937,938,939,966,967,968,969,970,971,972,973,974,975,976,1007,1008,1009,1010};

int cpa_zero_min = 50;
int cpa_zero_max = 233;
int cpa_zero_mean = 129;
int cpa_zero_sd = 50;

int cpa_one_min = 36;
int cpa_one_max = 137;
int cpa_one_mean = 90;
int cpa_one_sd = 22;

int cpa_two_min = 86;
int cpa_two_max = 195;
int cpa_two_mean = 151;
int cpa_two_sd = 28;


//Einzelnes Viereck Batch-----------
Roi batch_rois;
String batchName = "dlg.canceled";
String summary_batch = "plot|name|dateandtime|qcm|percentage";
String summary_batch_name;
ImageStack imstack;
String[] time_array;

//Oscar I-----------

int Oscar_I = 0;
/*Maske_alt
int[] Oscar_I_x = {32,72,116,156,221,261,305,345,410,450,494,534,599,639,683,723,809,849,893,933,998,1038,1082,1122,1187,1227,1271,1311,1376,1416,1460,1500};
int[] Oscar_I_y = {850,705,631,486,412,267,193,48};
*/
//Maske Neu
int[] Oscar_I_x = {115,230,354,468,652,766,890,1004,1188,1302,1426,1540,1724,1838,1963,2077,2320,2434,2559,2673,2857,2971,3095,3209,3393,3507,3631,3745,3929,4043,4168,4282};
int[] Oscar_I_y = {2582,2170,1961,1549,1339,927,718,306};

//AKWHA I-----------

int AKWHA_I = 1;

String[] AKWHA_I_lengths = {"5.91;6.21;5.28", "5.72;5.16;5.53", "11.26;11.9;6.61", "6.34;6.79;6.82", "6.66;6.82;7.22", "10.65;10.82;7.65"};
//a;b;c
int AKWHA_I_I_II = 0;
int AKWHA_I_III_IV = 1;

int point_counter = 0;
int points_up_counter = 0;
int points_down_counter = 0;

double[] distances_left = {6,12,13.5,19.5,25.5,27,33,39,40.5,46.5,52.5};
double[] distances_right = {6,12,13.5,19.5,25.5,27,33,39,40.5,46.5,52.5,55.5};
double[] distances_y = {15,30,45,60};

Point[] points_up = new Point[24];
Point[] points_down= new Point[24];

//AKWHA II---------------

int AKWHA_II = 2;

String[] AKWHA_II_lengths = {"7.71;7.36;3", "7.3;7.19;3.57", "7.52;7.24;4.02", "5.65;6.29;5.14", "9.98;8.19;6.82", "9.67;7.63;7.65"};

double[] distances_left_II = {1.5,7.6,13.7,15.2,21.3,27.4,28.9,35,41.1,42.6,48.7,54.8};
double[] distances_right_II = {6.1,12.2,13.7,19.8,25.9,27.4,33.5,39.6,41.1,47.2,53.3};

int[] xpoints = new int[12];
int[] ypoints = new int[12];

int[] xpoints_flaechen = new int[120];
int[] ypoints_flaechen = new int[120];

//Baitplates-------------
Point[] final_points = new Point[310];
int histogram_reference = 0;
StringBuilder summary_builder = new StringBuilder();
int empty_counter = 0;

	public void run(String arg) {
		
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		String selection = showChoice();
		System.out.println("Auswahl: " + selection);
		
		if(selection.equals("Baitplates")){
			startBaitplates();
		}
		if(selection.equals("Einzelnes Viereck Batch")){
			startevBatch();
		}
		if(selection.equals("Oscar I")){
			startOscar();
		}
		if(selection.equals("VORAN")){
			startVORAN();
		}
		if(selection.equals("AKHWA I")){
			startReferencePoints(AKWHA_I);
		}
		if(selection.equals("AKHWA II")){
			startReferencePoints(AKWHA_II);
		}
		if(selection.equals("OSCAR")){
			//startOSCAR();
		}
		if(selection.equals("Einzelnes Viereck")){
			startEinzelnViereck();
		}
		if(selection.equals("Mehrere Vierecke")){
			startMehrereVierecke();
		}
		if(selection.equals("")){
			//if (!showInfo("Fehler", "Fehler beim Erstellen des Export-Verzeichnisses")) return;
		}
		
	}
	
	public void startBaitplates(){
		files = openFiles();
		for(int c = 31; c > 0; c--) {
			if(c == 31) {
				summary_builder.append("Name," + c + ",");
			} else {
				if(c > 1){
					summary_builder.append(c + ",");
				} else {
					summary_builder.append(c + "\n");
				}
			}
		}
			Opener opener = new Opener();
			if(files != null){
				
				boolean isCreated = false;
				
				File dir = new File(path+"/Export");
				if(!dir.exists()){
					isCreated = dir.mkdir();
				}
				
				if(dir.exists() || isCreated){
					finished = false;
					for (int i=0; i<files.length; i++) {
						ImagePlus img = opener.openImage(path, files[i].getName());
						if (img!=null)
							imp = img;
							imp.show();							
							loadimagesBaitplatesManual(i);
							if(i == files.length-1){
								finished = true;
							}
							if(finished && !problem){
								String summary_string = summary_builder.toString();
								saveData(summary_string, "Allgemein_Zusammenfassung.txt");
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
	
	public void startevBatch(){
		files = openFiles();
			
			Opener opener = new Opener();
			if(files != null){
				
				boolean isCreated = false;
				
				File dir = new File(path+"/Export");
				if(!dir.exists()){
					isCreated = dir.mkdir();
				}
				
				if(dir.exists() || isCreated){
					finished = false;
					
					int files_amount = files.length;
					int maximum = files_amount + files_amount/50;
					
					//Progress bar
					final JFrame f;
					final JProgressBar b;
				    
				    // create a frame
			        f = new JFrame("Progress...");
			        JPanel p = new JPanel();
			        b = new JProgressBar();
			        b.setMinimum(0);
			        b.setMaximum(maximum);
			        b.setValue(0);
			        b.setStringPainted(true);
			        p.add(b);
			        f.add(p);
			        f.setSize(300, 100);
			        
			        int step = 1;
			        
			        time_array = new String[files.length];
			        
					for (int i = 0; i < files.length; i++) {
						ImagePlus img = opener.openImage(path, files[i].getName());
						
						if (img!=null) {
							imp = img;
							//imp.show();
							//showInfo("Info", datetime);
							
							StartFileDirectory = getDirectory(imp);
							StartFileName = getFileName(imp);
							
							String datetime = getFileDateandTime(imp);
							time_array[i] = datetime;
							
							String StartFilePathName = StartFileDirectory + StartFileName;
							
							ImagePlus imp2 = new ImagePlus();
							imp2.setImage(imp);
							//imp.close();
							//imp2.show();
							
							if(i == 0) {
								
								imp2.show();
								
								//For real reference
								getScale(imp2);
								
								IJ.setTool("rectangle");
								
								InputDialog inputrectangle = new InputDialog("Input", "Please draw Rectangle\nRectangle and Points are movable", "Rectangle", imp2);
								inputrectangle.show();
								
								Point[] points;
								Rectangle rectangle;
								
								batch_rois = imp2.getRoi();
								
								rectangle = batch_rois.getBounds();
								System.out.println("x,y: " + rectangle.x + "," + rectangle.y);
								
								while(batchName.equals("dlg.canceled")) {
									batchName = enterName();
								}
								
								imp2.setRoi(batch_rois);
								ImagePlus temp_crop = imp2.crop();
								ImageProcessor iptemp = temp_crop.getProcessor();
								int width = iptemp.getWidth();
								int height = iptemp.getHeight();
								
								imstack = new ImageStack(width, height);
									
							} 
							
							imp2.close();

							f.setVisible(true);
							b.setValue(i*step);
							
							Opener opener_new = new Opener();
							ImagePlus impToCrop = opener_new.openImage(StartFilePathName);
							
							impToCrop.setRoi(batch_rois);
							
							ImagePlus imp3 = impToCrop.crop();
							
							String name = StartFileDirectory + "/Export/" + StartFileName + "_" + batchName + "_" + String.valueOf(i) + "_" + "original";
							
							String export_name = name + ".tif";
							
							ImageProcessor ip3 = imp3.getProcessor();
							
							imstack.addSlice(StartFileName, ip3);
							
							//IJ.saveAs(imp3, "tiff", name); //Hier am Ende nur den Stack speichern?
							
							if(i == files.length-1){
								finished = true;
								//Das muss noch wo anders hin
								problem = false;
							}
							/*if(finished && !problem){
								//saveData(summary_batch, summary_batch_name);
								showInfo("Info", "Exportierung und Berechnung erfolgreich abgeschlossen");
							}*/
						}
					}
					f.setVisible(false);
					String stackexportname = StartFileDirectory + "/Export/" + batchName + "_original_stack";
					
					ImagePlus StackImage = new ImagePlus("Batchname", imstack);
					IJ.saveAs(StackImage, "tiff", stackexportname);
					
					//After all Rectangles aquired and stacked, start thresholding
					
					loadimagesevBatchStack(imstack, StackImage);
					
				} else {
					//Not created, message!
					if (!showInfo("Fehler", "Fehler beim Erstellen des Export-Verzeichnisses")) return;
				}
			} else {
				//No files selected, message!
				if (!showInfo("Fehler", "Keine Dateien ausgew\u00e4hlt.")) return;
			}
	}
	
	public void startOscar(){
		files = openFiles();
			
			Opener opener = new Opener();
			if(files != null){
				
				boolean isCreated = false;
				
				File dir = new File(path+"/Export");
				if(!dir.exists()){
					isCreated = dir.mkdir();
				}
				
				if(dir.exists() || isCreated){
					finished = false;
					for (int i=0; i<files.length; i++) {
						ImagePlus img = opener.openImage(path, files[i].getName());
						if (img!=null)
							imp = img;
							imp.show();
							loadimagesOscar(i);
							if(i == files.length-1){
								finished = true;
							}
							if(finished && !problem){
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
					finished = false;
					for (int i=0; i<files.length; i++) {
						ImagePlus img = opener.openImage(path, files[i].getName());
						if (img!=null)
							imp = img;
							imp.show();
							loadimages(i);
							if(i == files.length-1){
								finished = true;
							}
							if(finished && !problem){
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
					finished = false;
					for (int i=0; i<files.length; i++) {
						ImagePlus img = opener.openImage(path, files[i].getName());
						if (img!=null)
							imp = img;
							imp.show();
							loadimagesEinzeln(i);
							if(i == files.length-1){
								finished = true;
							}
							if(finished && !problem){
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
	
	public void startMehrereVierecke(){
		files = openFiles();
			
			Opener opener = new Opener();
			if(files != null){
				
				boolean isCreated = false;
				
				File dir = new File(path+"/Export");
				if(!dir.exists()){
					isCreated = dir.mkdir();
				}
				
				if(dir.exists() || isCreated){
					finished = false;
					for (int i=0; i<files.length; i++) {
						ImagePlus img = opener.openImage(path, files[i].getName());
						if (img!=null)
							imp = img;
							imp.show();
							loadimagesMehrereVierecke(i);
							if(i == files.length-1){
								finished = true;
							}
							if(finished && !problem){
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
	
	public void startReferencePoints(int project){
		files = openFiles();
			
			Opener opener = new Opener();
			if(files != null){
				
				boolean isCreated = false;
				
				File dir = new File(path+"/Export");
				if(!dir.exists()){
					isCreated = dir.mkdir();
				}
				
				if(dir.exists() || isCreated){
					finished = false;
					for (int i=0; i<files.length; i++) {
						ImagePlus img = opener.openImage(path, files[i].getName());
						if (img!=null)
							imp = img;
							imp.show();
							loadimagesReferencePoints(i, project);
							if(i == files.length-1){
								finished = true;
							}
							if(finished && !problem){
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
	
	public void loadimagesOscar(int CountImages){
		imp = IJ.getImage();
		ImageProcessor originalip = imp.getProcessor();
		
		StartFileDirectory = getDirectory(imp);
		StartFileName = getFileName(imp);
		
		String splitString[] = StartFileName.split("_");
		String StartFileMask = splitString[0];
		
		StartFileNameSmall = StartFileName.substring(6);
		
		String CropPath = StartFileDirectory + StartFileNameSmall;
		
		//Starte Funktion Ausschneiden und Thresholding
		
		loadimagesFixPoints(CountImages, Oscar_I);
		
	}
	
	public void loadimagesBaitplatesManual(int CountImages) {
		//Bild laden
		//Eingabe manueller Punkte (drei oder vier?)
		//Berechnung aller Punkte
		//Auswertung
		
		imp = IJ.getImage();
		ImageProcessor originalip = imp.getProcessor();
		
		StartFileDirectory = getDirectory(imp);
		StartFileName = getFileName(imp);
		
		String StartFilePathName = StartFileDirectory + StartFileName;
				
		ImagePlus imp2 = new ImagePlus();
		imp2.setImage(imp);
		ImagePlus imp_roitest = new ImagePlus();
		
		imp_roitest.setImage(imp);
		imp_roitest.setTitle("imp_roitest");
		
		imp.close();
		imp2.show();
		
		int height = imp.getHeight();
		int width = imp.getWidth();
		
		IJ.setTool("point");
			
		Point[] points;
		Point point_upleft = new Point();
		Point point_downleft = new Point();
		Point point_upright = new Point();
		Point point_downright = new Point();
		Point point_reference_empty = new Point();
		
		Roi rois;
		
		int[] xpoints = new int[310];
		int[] ypoints = new int[310];
		
		double temp_radius = 16.0;
		int radius = (int)temp_radius;
		
		RoiManager roi_manager = new RoiManager();
		
		
		
		if(CountImages == 0) {
		
			InputDialog inputreferencepoint_upleft = new InputDialog("Input", "Bitte Feld oben links mit Punkt auswählen\nPunkt ist bewegbar\nLöschen mit ALT + Klick auf den Punkt", "Baitplates", imp2);
			inputreferencepoint_upleft.show();
			
			rois = imp2.getRoi();
			
			if( rois instanceof PointRoi ){
				points = rois.getContainedPoints();
				//System.out.println("Length: " + points.length);
				
				int x = points[0].x;
				int y = points[0].y;
				xpoints[0] = x;
				ypoints[0] = y;
				
				point_upleft.x = x;
				point_upleft.y = y;
				
				//String toappend = String.valueOf("Upleft" + String.valueOf(point_upleft.x) + "," + String.valueOf(point_upleft.y));
				//System.out.println(toappend);	
				
				imp2.deleteRoi();
			
			}
			
			IJ.run("Scale to Fit", "");
			
			InputDialog inputreferencepoint_upright = new InputDialog("Input", "Bitte Feld oben rechts mit Punkt auswählen\nPunkt ist bewegbar\nLöschen mit ALT + Klick auf den Punkt", "Baitplates", imp2);
			inputreferencepoint_upright.show();
			
			rois = imp2.getRoi();
			
			if( rois instanceof PointRoi ){
				points = rois.getContainedPoints();
				//System.out.println("Length: " + points.length);
				
				int x = points[0].x;
				int y = points[0].y;
				xpoints[1] = x;
				ypoints[1] = y;
				
				point_upright.x = x;
				point_upright.y = y;
				
				//String toappend = String.valueOf("Upright" + String.valueOf(point_upright.x) + "," + String.valueOf(point_upright.y));
				//System.out.println(toappend);	
				
				imp2.deleteRoi();
			
			}
			
			IJ.run("Scale to Fit", "");
			
			InputDialog inputreferencepoint_downleft = new InputDialog("Input", "Bitte Feld unten links mit Punkt auswählen\nPunkt ist bewegbar\nLöschen mit ALT + Klick auf den Punkt", "Baitplates", imp2);
			inputreferencepoint_downleft.show();
			
			rois = imp2.getRoi();
			
			if( rois instanceof PointRoi ){
				points = rois.getContainedPoints();
				//System.out.println("Length: " + points.length);
				
				int x = points[0].x;
				int y = points[0].y;
				xpoints[2] = x;
				ypoints[2] = y;
				
				point_downleft.x = x;
				point_downleft.y = y;
				
				//String toappend = String.valueOf("Reference" + String.valueOf(point_reference_empty.x) + "," + String.valueOf(point_reference_empty.y));
				//System.out.println(toappend);	
				
				imp2.deleteRoi();
			
			}
			
			IJ.run("Scale to Fit", "");
			
			InputDialog inputreferencepoint_referenceempty = new InputDialog("Input", "Zuletzt bitte ein leeres Feld mit Punkt auswählen\nPunkt ist bewegbar\nLöschen mit ALT + Klick auf den Punkt", "Baitplates", imp2);
			inputreferencepoint_referenceempty.show();
			
			rois = imp2.getRoi();
			
			if( rois instanceof PointRoi ){
				points = rois.getContainedPoints();
				//System.out.println("Length: " + points.length);
				
				int x = points[0].x;
				int y = points[0].y;
				
				point_reference_empty.x = x;
				point_reference_empty.y = y;
				
				//String toappend = String.valueOf("Reference" + String.valueOf(point_reference_empty.x) + "," + String.valueOf(point_reference_empty.y));
				//System.out.println(toappend);	
				
				imp2.deleteRoi();
			
			}
			
			IJ.run("Scale to Fit", "");
			
			int downright_helper_x = point_downleft.x - point_upleft.x;
			int downright_helper_y = point_upleft.y - point_downleft.y;
			
			point_downright.x = point_upright.x + downright_helper_x;
			point_downright.y = point_upright.y - downright_helper_y;
			
			xpoints[3] = point_downright.x;
			ypoints[3] = point_downright.y;
			
			double m = 0;
			double m_Winkel;
			
			double distance;
			double dist_step = 0;
			double dist_temp = 0;
			double x_temp = 0;
			double y_temp = 0;
			
			dist_step = 1.0/9.0;
			
			for(int i = 0; i < 8; i++) { //Punkte oben
				double step_here = dist_step * (i+1);
				double lerp_x = point_upleft.x + (step_here * (point_upright.x - point_upleft.x));
				double lerp_y = point_upleft.y + (step_here * (point_upright.y - point_upleft.y));
				
				xpoints[4+i] = (int)lerp_x;
				ypoints[4+i] = (int)lerp_y;
				//System.out.println("LERP: " + String.valueOf(lerp_x) + "," + String.valueOf(lerp_y));	
			}
			
			for(int i = 0; i < 8; i++) { //Punkte unten 
				double step_here = dist_step * (i+1);
				double lerp_x = point_downleft.x + (step_here * (point_downright.x - point_downleft.x));
				double lerp_y = point_downleft.y + (step_here * (point_downright.y - point_downleft.y));
				
				xpoints[12+i] = (int)lerp_x;
				ypoints[12+i] = (int)lerp_y;
				//System.out.println("LERP: " + String.valueOf(lerp_x) + "," + String.valueOf(lerp_y));	
			}
			
			dist_step = 1.0/30.0;
			
			for(int i = 0; i < 29; i++) { //Punkte linke Seite 
				double step_here = dist_step * (i+1);
				double lerp_x = point_upleft.x + (step_here * (point_downleft.x - point_upleft.x));
				double lerp_y = point_upleft.y + (step_here * (point_downleft.y - point_upleft.y));
				
				xpoints[20+i] = (int)lerp_x;
				ypoints[20+i] = (int)lerp_y;
				//System.out.println("LERP: " + String.valueOf(lerp_x) + "," + String.valueOf(lerp_y));	
			}
			
			for(int i = 0; i < 29; i++) { //Punkte rechte Seite 
				double step_here = dist_step * (i+1);
				double lerp_x = point_upright.x + (step_here * (point_downright.x - point_upright.x));
				double lerp_y = point_upright.y + (step_here * (point_downright.y - point_upright.y));
				
				xpoints[49+i] = (int)lerp_x;
				ypoints[49+i] = (int)lerp_y;
				//System.out.println("LERP: " + String.valueOf(lerp_x) + "," + String.valueOf(lerp_y));	
			}
			
			int counter = 78;
			
			for(int k = 0; k < 8; k++) { //Punkte alle Mitte von oben (points 4 - 11) nach unten (points 12 - 19) 
				for(int i = 0; i < 29; i++) {
					double step_here = dist_step * (i+1);
					double lerp_x = xpoints[k+4] + (step_here * (xpoints[k+12] - xpoints[k+4]));
					double lerp_y = ypoints[k+4] + (step_here * (ypoints[k+12] - ypoints[k+4]));
					
					xpoints[counter] = (int)lerp_x;
					ypoints[counter] = (int)lerp_y;
					counter++;
					//System.out.println(String.valueOf(counter));
					//System.out.println("LERP: " + String.valueOf(lerp_x) + "," + String.valueOf(lerp_y));
				}
			}
			
			imp2.setRoi(new PointRoi(xpoints, ypoints, ypoints.length));
			
			//System.out.println("Länge: " + String.valueOf(ypoints.length));
			
			imp_roitest.show();
			
			for(int i = 0; i < ypoints.length; i++) {
				final_points[i] = new Point();
				final_points[i].x = xpoints[i];
				final_points[i].y = ypoints[i];
				//System.out.println(String.valueOf(i) + "," + String.valueOf(final_points[i].x) + "," + String.valueOf(final_points[i].y));
				
				double x_neg = final_points[i].x - radius;
				double x_pos = final_points[i].x + radius;
				double y_neg = final_points[i].y - radius;
				double y_pos = final_points[i].y + radius;
				
				if(point_reference_empty.x < x_pos && point_reference_empty.x > x_neg && point_reference_empty.y < y_pos && point_reference_empty.y > y_neg) {
					Roi new_roi = new Roi(final_points[i].x-radius, final_points[i].y-radius, 2*radius, 2*radius);
					
					imp_roitest.setRoi(new_roi);		
					ImagePlus image_reference_empty = imp_roitest.crop();
					image_reference_empty.show();
					histogram_reference = addBlueGreenChannelsHisto(image_reference_empty);
					System.out.println("Histogram, Reference: " + String.valueOf(histogram_reference));
					image_reference_empty.close();
				}
			}
			
			Arrays.sort(final_points, new java.util.Comparator<Point>() {
			    public int compare(Point a, Point b) {
			    	int xComp = Integer.compare(a.y, b.y);
			        if(xComp == 0)
			            return Integer.compare(a.x, b.x);
			        else
			            return xComp;
			    }
			});
			
			/*for(int i = 0; i < ypoints.length; i++) {
				System.out.println(String.valueOf(i) + "," + String.valueOf(final_points[i].x) + "," + String.valueOf(final_points[i].y));
			}*/
			
			imp2.close();
		
		} else {
			imp2.close();
			imp_roitest.show();
		}
		
		Roi[] final_rois = new Roi[ypoints.length];
		ImagePlus[] final_images = new ImagePlus[ypoints.length];
		int[][] histo = new int[310][256];
		
		String[] letters = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J"};
		
		String pixel_erkannt = "";
		
		width = 0;
		height = 0;
		int numPixels = 0;

		//for(int k = 0; k < 10; k++) {
			//for(int l = 0; l < 31; l++) {
		
		String summary = "Integers";
		String summary_histo = "Histograms";
		String summary_empty = "Zusammenfassung";
		String summary_savehisto = "RGBHisto\n";
		
		summary_builder.append(StartFileName + ",");
		
		for(int k = 0; k < 31; k++) {
			for(int l = 0; l < 10; l++) {
				int place = k*10+l;
				
				Roi new_roi = new Roi(final_points[place].x-radius, final_points[place].y-radius, 2*radius, 2*radius);
				final_rois[place] = new_roi;
				
				imp_roitest.setRoi(new_roi);		
				
				final_images[place] = imp_roitest.crop();
				
				final_images[place].show();
				
				if(place == 0) {
					ImageProcessor ip = final_images[place].getProcessor();
					
					width = ip.getWidth();
					height = ip.getHeight();
					numPixels = width*height;
				}
				
				//Versuch Green Histogramm
				String savehisto = "";
				ImageStatistics stats_green;
				ImageStatistics stats_blue;
				
				long[] histogram;
				long[] histo_green;
				long[] histo_blue;
				
				int channel_green = 2; //Für Green-Channel
				int channel_blue = 3; //Für Blue-Channel
				
				ImageProcessor ip_green = final_images[place].getProcessor();

	            ColorProcessor cp_green = (ColorProcessor)final_images[place].getProcessor();
	            
	            ip_green = cp_green.getChannel(channel_green, null);
	            
	            ImagePlus imp_green = new ImagePlus("", ip_green);
	            
	            stats_green = imp_green.getStatistics(AREA+MEAN+MODE+MIN_MAX, 256, 0.0, 0.0);
	            
	            histo_green = stats_green.getHistogram();
	            
	            ImageProcessor ip_blue = final_images[place].getProcessor();

	            ColorProcessor cp_blue = (ColorProcessor)final_images[place].getProcessor();
	            
	            ip_blue = cp_blue.getChannel(channel_blue, null);
	            
	            ImagePlus imp_blue = new ImagePlus("", ip_blue);
	            
	            stats_blue = imp_blue.getStatistics(AREA+MEAN+MODE+MIN_MAX, 256, 0.0, 0.0);
	            
	            histo_blue = stats_blue.getHistogram();
	            histogram = histo_blue;
	            int histo_sum_greenblue = 0;
	            
	            for(int v = 0; v < histo_green.length; v++) {
	            	histogram[v] = histo_green[v] + histo_blue[v];
					if(v == 0) {
						savehisto +=  histogram[v];
					} else {
						savehisto +=  "," + histogram[v];
					}
					if(v > 29 && v < 81) {
						histo_sum_greenblue += histogram[v];
					}
				}
	            summary_savehisto += String.valueOf(histo_sum_greenblue) + "\n";
				//Versuche Ende
	            
				int[] rgb_values_int = extractPixelValuesNoMaskIntManual(final_images[place]);
				
				int[][] rgb_values_complete = extractPixelValuesRGB(final_images[place]);
				
				String savepixels = "";
				
				int red_pixel_sum = 0;
				int green_pixel_sum = 0;
				int blue_pixel_sum = 0;
				
				for(int v = 0; v < rgb_values_int.length; v++){
					if(v == 0) {
						savepixels +=  rgb_values_int[v];
					} else {
						savepixels +=  "," + rgb_values_int[v];
					}
					red_pixel_sum += rgb_values_int[v];
					//Grüne Pixel addieren im Bereich
					if(v > 28 && v < 86) {
						green_pixel_sum += rgb_values_complete[v][1];
					}	
					//Grüne Pixel addieren im Bereich
					if(v > 28 && v < 81) {
						blue_pixel_sum += rgb_values_complete[v][2];
					}
				}
				
				double red_percentage = red_pixel_sum * 100.0/(double)histogram_reference; //1024 Pixel insgesamt, ca. 270 rote Pixel wenn komplett leer
				System.out.println("Pixel-Summe: " + String.valueOf(red_pixel_sum) + " Prozent: " + String.valueOf(red_percentage));
				
				summary_empty += "\n" + letters[l] + "_" + String.valueOf(k+1);
				
				/*if(red_percentage >= 18.0) {
					//leere Felder
					summary_empty += ",0";
				} else {
					//nicht leere Felder
					//nochmal neu Berechnen wie viel Prozent und dann eintragen ob leer oder nicht
					summary_empty += ",1";
					roi_manager.addRoi(new_roi);
				}*/
				
				double histo_sum_percentage = histogram_reference/100*15;
				double histo_sum_percentage_neg = histogram_reference - histo_sum_percentage;
				double histo_sum_percentage_pos = histogram_reference + histo_sum_percentage;
				
				

				if(histo_sum_greenblue >= histo_sum_percentage_neg && histo_sum_greenblue <= histo_sum_percentage_pos) {
					//leere Felder
					summary_empty += ",0";
					empty_counter++;
				} else {
					//nicht leere Felder
					summary_empty += ",1";
					roi_manager.addRoi(new_roi);
				}
				
				if(l == 9) {
					if(k < 30) {
						summary_builder.append(empty_counter + ",");
					} else {
						summary_builder.append(empty_counter + "\n");
					}
					empty_counter = 0;
				}
				
				//summary_empty += "," + String.valueOf(green_pixel_sum) + "," + String.valueOf(blue_pixel_sum);
				
				//saveData(savepixels, StartFileName + "_" + String.valueOf(place) + "_" + letters[l] + "_" + String.valueOf(k+1)  + "_" + "pixelvaluesINT.txt");
				summary += "\n" + savepixels;
				
				/*String[] rgb_values = extractPixelValuesNoMask(final_images[place]);
				
				StringBuilder sb = new StringBuilder();
		
				for(int v = 0; v < rgb_values.length; v++){
					sb.append(rgb_values[v]);
				}
				String export_pixelvalues = sb.toString();
				
				pixel_erkannt += String.valueOf(place) + "," + export_pixelvalues + "\n";
				
				saveData(export_pixelvalues, StartFileName + "_" + String.valueOf(place) + "_" + letters[l] + "_" + String.valueOf(k+1)  + "_" + "pixelvaluesCPA.txt");*/
				
				//saveData(savetext, StartFileName + "_" + letters[k] + "_" + String.valueOf(l+1) + "_" + String.valueOf(place) + ".txt");
				//savetext = "";
			
				/*savepixels = "";
				ImageProcessor ip = final_images[place].getProcessor();
				int[] hist_temp = ip.getHistogram();
				
				for (int t = 0; t < hist_temp.length; t++) {
					histo[place][t] = hist_temp[t];
					savepixels += String.valueOf(hist_temp[t])+",";
				}
				
				summary_histo += "\n" + letters[l] + "_" + String.valueOf(k+1) + "," + savepixels;*/
				
				/*saveData(savepixels, StartFileName + "_" + String.valueOf(place) + "_" + letters[l] + "_" + String.valueOf(k+1)  + "_" + "histovaluesINT.txt");
				
				String name = StartFileDirectory + "/Export/" + StartFileName + "_" + String.valueOf(place) + "_" + letters[l] + "_" + String.valueOf(k+1) + "_" + "original";
				
				String export_name = name + ".tif";
				
				IJ.saveAs(final_images[place], "tiff", name);*/
				
				final_images[place].close();			
				
			}
		}
		
		//saveData(summary, StartFileName + "_" + "ALLpixelvaluesINT.txt");
		//saveData(summary_histo, StartFileName + "_" + "ALLHistograms.txt");
		saveData(summary_empty, StartFileName + "_" + "Zusammenfassung.txt");
		
		//saveData(summary_savehisto, StartFileName + "_" + "Zusammenfassung_SaveHisto.txt");
		
		//Close Result-Table
		//IJ.selectWindow("Results"); 
		//IJ.run("Close");
		
		roi_manager.runCommand(imp_roitest, "Show All");
		IJ.run("From ROI Manager", "");
		String overlay_export_name = StartFileDirectory + "/Export/" + StartFileName + "_overlay";
		IJ.saveAs(imp_roitest, "tiff", overlay_export_name);
		
		try {	            
            Thread.sleep(2500);
        }
        catch (Exception e) {
            System.out.println(e);
        }
		
		roi_manager.reset();
		roi_manager.close();
		imp_roitest.close();
		
		problem = false;
	}
	
	int addBlueGreenChannelsHisto(ImagePlus imp) {
		ImageStatistics stats_green;
		ImageStatistics stats_blue;
		
		long[] histogram;
		long[] histo_green;
		long[] histo_blue;
		
		int channel_green = 2; //Für Green-Channel
		int channel_blue = 3; //Für Blue-Channel
		
		ImageProcessor ip_green = imp.getProcessor();

        ColorProcessor cp_green = (ColorProcessor)imp.getProcessor();
        
        ip_green = cp_green.getChannel(channel_green, null);
        
        ImagePlus imp_green = new ImagePlus("", ip_green);
        
        stats_green = imp_green.getStatistics(AREA+MEAN+MODE+MIN_MAX, 256, 0.0, 0.0);
        
        histo_green = stats_green.getHistogram();
        
        ImageProcessor ip_blue = imp.getProcessor();

        ColorProcessor cp_blue = (ColorProcessor)imp.getProcessor();
        
        ip_blue = cp_blue.getChannel(channel_blue, null);
        
        ImagePlus imp_blue = new ImagePlus("", ip_blue);
        
        stats_blue = imp_blue.getStatistics(AREA+MEAN+MODE+MIN_MAX, 256, 0.0, 0.0);
        
        histo_blue = stats_blue.getHistogram();
        histogram = histo_blue;
        int histo_sum_greenblue = 0;
        
        for(int v = 0; v < histo_green.length; v++) {
        	histogram[v] = histo_green[v] + histo_blue[v];
			if(v > 29 && v < 81) {
				histo_sum_greenblue += histogram[v];
			}
		}
        
        return histo_sum_greenblue;
	}
	
	public void loadimagesBaitplates(int CountImages){
		
		//Bild laden
		//MakeBinary
		//Outline
		//SetMeasurements
		//AnalyzeParticles
		
		Roi[] roi_array;
		
		imp = IJ.getImage();
		ImageProcessor originalip = imp.getProcessor();
		
		//DPI des Bildes herausfinden
		int info_dpi = getFileDPI(imp);
		
		ImagePlus imp_a = new ImagePlus();
		ImagePlus imp_b = new ImagePlus();
		ImagePlus imp_roitest = new ImagePlus();
		
		imp_a.setImage(imp);
		imp_b.setImage(imp);
		imp_roitest.setImage(imp);
		
		imp_a.setTitle("imp_a");
		imp_b.setTitle("imp_b");
		imp_roitest.setTitle("imp_roitest");
		
		imp_a.show();
		
		ColorThresholderExtended thresher_a = new ColorThresholderExtended(); //Color Threshold Fenster open
		
		Integer[] input_int_a = new Integer[9];
		input_int_a[0] = 190;
		input_int_a[1] = 255;
		input_int_a[2] = 35;
		input_int_a[3] = 255;
		input_int_a[4] = 0;
		input_int_a[5] = 255;
		input_int_a[6] = 3; //B&W
		input_int_a[7] = 0; //HSB
		input_int_a[8] = 0; //default
		
		Boolean[] input_bool_a = new Boolean[4];
		input_bool_a[0] = true;
		input_bool_a[1] = true;
		input_bool_a[2] = true;
		input_bool_a[3] = false;
		
		//thresher.setStringValue(method);
		thresher_a.setIntValues(input_int_a);
		thresher_a.setBoolValues(input_bool_a);

		thresher_a.updateAfterSetting(imp_a);
		
		//PixelMask = thresher_a.getdrawfillMask();
		
		//thresher_a.drawBinary(PixelMask, imp_a);
		
		/*try {	            
            Thread.sleep(5000);
        }
        catch (Exception e) {
            System.out.println(e);
        }*/		
		
		thresher_a.close();
		
		imp_b.show();
		
		ColorThresholderExtended thresher_b = new ColorThresholderExtended(); //Color Threshold Fenster open
		
		Integer[] input_int_b = new Integer[9];
		input_int_b[0] = 190;
		input_int_b[1] = 255;
		input_int_b[2] = 35;
		input_int_b[3] = 255;
		input_int_b[4] = 0;
		input_int_b[5] = 255;
		input_int_b[6] = 3;
		input_int_b[7] = 0;
		input_int_b[8] = 0;
		
		Boolean[] input_bool_b = new Boolean[4];
		input_bool_b[0] = false;
		input_bool_b[1] = true;
		input_bool_b[2] = true;
		input_bool_b[3] = false;
		
		//thresher.setStringValue(method);
		thresher_b.setIntValues(input_int_b);
		thresher_b.setBoolValues(input_bool_b);
		
		thresher_b.updateAfterSetting(imp_b);
		
		//PixelMask = thresher_b.getdrawfillMask();
		
		//thresher_b.drawBinary(PixelMask, imp_b);
		
		/*try {	            
            Thread.sleep(5000);
        }
        catch (Exception e) {
            System.out.println(e);
        }*/
		
		thresher_b.close();
		
		StartFileDirectory = getDirectory(imp);
		StartFileName = getFileName(imp);
		
		String splitString[] = StartFileName.split("_");
		String StartFileMask = splitString[0];
		
		StartFileNameSmall = StartFileName.substring(6);
		
		String CropPath = StartFileDirectory + StartFileNameSmall;
		
		imp.close();
		
		int rawthreshold_a_min = 0;
		int rawthreshold_a_max = 65;
		int rawthreshold_b_min = 205;
		int rawthreshold_b_max = 255;
		
		//Prefs.blackBackground = false;
		//IJ.run(imp_a, "Color Threshold...", "sigma");
		
		IJ.run(imp_a, "8-bit", "");
		//ThresholdAdjuster.setMode("B&W");
		//IJ.setRawThreshold(imp_a, rawthreshold_a_min, rawthreshold_a_max, null);
		IJ.run(imp_a, "Convert to Mask", "");
		
		IJ.run(imp_b, "8-bit", "");
		//ThresholdAdjuster.setMode("B&W");
		//IJ.setRawThreshold(imp_b, rawthreshold_b_min, rawthreshold_b_max, null);
		//IJ.run(imp_b, "Convert to Mask", "");
		
		ImageCalculator ic = new ImageCalculator();
		ImagePlus imp_added = ic.run("XOR create", imp_b, imp_a);
		imp_added.setTitle("imp_added");
		imp_added.show();
		
		imp_a.changes = false;
		imp_b.changes = false;
		imp_a.close();
		imp_b.close();
		
		//IJ.run(imp_a, "8-bit", "");
		//IJ.setAutoThreshold(imp_a, "Yen");
		
		IJ.run("Set Measurements...", "area mean standard modal min centroid center perimeter bounding fit shape feret's integrated median skewness kurtosis area_fraction stack redirect=None decimal=3");
		//IJ.run(imp_a, "Analyze Particles...", "size=15-Infinity circularity=0.00-1.00 show=Outlines display clear include");
		
		RoiManager roi_manager = new RoiManager();
		ResultsTable rt = new ResultsTable();
		
		//Je nach DPI verschiedene Werte für den ParticleAnalyser und das erste entfernen von zu kleinen Flächen		
		double minSize = 5;
		double maxSize = Double.MAX_VALUE;
		double minCirc = 0;
		double maxCirc = 1;
		int percentage = 5;
		int radius = 10;
		
		//Funktioniert nur mit 300dpi
		
		if(info_dpi == 300 || info_dpi == 200 ) {
			if(info_dpi == 300) {
				minSize = 100;//15
				percentage = 10;//10
				radius = 30; //30 Do Not Change!!!!111eins (24.05.2022)
			}
			if(info_dpi == 200) {
				minSize = 5;
				percentage = 3;
				radius = 20;
			}
		} else {
			//Andere Dpi, keine Garantie!
		}
		
		//(int options, int measurements, ResultsTable rt, double minSize, double maxSize, double minCirc, double maxCirc)
		//ParticleAnalyzer particleAnalyzer = new ParticleAnalyzer(ParticleAnalyzer.INCLUDE_HOLES + ParticleAnalyzer.SHOW_OUTLINES , -1, rt, minSize, maxSize, minCirc, maxCirc);
		ParticleAnalyzer particleAnalyzer = new ParticleAnalyzer(ParticleAnalyzer.EXCLUDE_EDGE_PARTICLES + ParticleAnalyzer.SHOW_OUTLINES , -1, rt, minSize, maxSize, minCirc, maxCirc);
		
		System.out.println("DPI: " + String.valueOf(info_dpi));
		
		particleAnalyzer.setRoiManager(roi_manager);
		particleAnalyzer.setResultsTable(rt);
		
		ImagePlus analysedImage;
		
		particleAnalyzer.analyze(imp_added);
		//particleAnalyzer.analyze(imp_a);
		
		rt.show("Results");
		
		roi_array = roi_manager.getRoisAsArray();
	    
		double[] double_results = new double[roi_array.length];
		double[] double_center_X = new double[roi_array.length];
		double[] double_center_Y = new double[roi_array.length];
		
		/*for (int l = 0; l < rt.getLastColumn(); l++){
			
			double_results = rt.getColumn(l);
		}*/
		
		double_results = rt.getColumnAsDoubles(0); //Area
		double_center_X = rt.getColumnAsDoubles(8); //Area
		double_center_Y = rt.getColumnAsDoubles(9); //Area
		
		double results_mean = mean(double_results);
		
		double results_SD = calculateSD(double_results);
		
		double border_down = results_mean - results_SD; 
		double border_up = results_mean + results_SD;
		//Prozentsatz in Pixel des Durchschnitts
		double border_percentage = results_mean/100*percentage;
		
		int good_counter = 0;
		
		//Zählen wie viele Flächen direkt gelöscht werden können
		for (int l = 0; l < double_results.length; l++) {
			if(double_results[l] > border_percentage) {
				good_counter++;
			}
			
		}
		
		Roi[] roi_array_improv = new Roi[good_counter];
		double[] center_X_improv = new double[good_counter];
		double[] center_Y_improv = new double[good_counter];
		
		String savetext = "";
		
		good_counter = 0;
		
		//Flächen direkt rauswerfen
		for (int l = 0; l < double_results.length; l++) {
			if(double_results[l] > border_percentage) {
				roi_array_improv[good_counter] = roi_array[l];
				center_X_improv[good_counter] = double_center_X[l];
				center_Y_improv[good_counter] = double_center_Y[l];
				//System.out.println(String.valueOf(roi_array[l])+","+String.valueOf(double_center_X[l])+","+String.valueOf(double_center_Y[l]));
				savetext += String.valueOf(String.valueOf(double_center_X[l])+","+String.valueOf(double_center_Y[l])+"\n");
				good_counter++;
			}
		}

		/* Erkennen von Spalten mit einer Länge von 31
		 * Einen Eintrag herausnehmen
		 * Prüfen ob dieser bereits in good_columns gespeichert ist
		 * wenn nicht center_X_improv durchsuchen und abgleichen
		 * wenn 31 innerhalb 8 Pixel verfügbar dann neuer Eintrag in good_columns*/
		
		double[] good_columns = new double[good_counter];
		
		boolean is_in_columns = false;
		boolean saved_in_columns = false;
		
		double tolerance_X = 15;//8
		int good_check_counter;
		
		for(int l = 0; l < good_counter; l++) {
			
			is_in_columns = false;
			saved_in_columns = false;
			good_check_counter = 0;
			double check = center_X_improv[l];
			
			for(int c = 0; c < good_columns.length; c++) {
				if(good_columns[c] > 0.0) {
					if(check > good_columns[c] - tolerance_X && check < good_columns[c] + tolerance_X) {
						is_in_columns = true;
					}
				}
			}
			
			if(is_in_columns) {
				//Der aktuelle x-Wert ist bereits in good_columms
				//nichts tun, zum nächsten Wert
			} else {
				//Der aktuelle x-Wert ist noch nicht in good_columms
				for(int k = 0; k < good_counter; k++) {
					if(check > center_X_improv[k] - tolerance_X && check < center_X_improv[k] + tolerance_X) {
						if(k != l) {
							good_check_counter++;
						}
					}
				}
				if(good_check_counter >= 25) {
					int save_iterator = 0;
					while(!saved_in_columns) {
						if(good_columns[save_iterator]== 0.0) {
							good_columns[save_iterator] = check;
							saved_in_columns = true;
						} else {
							save_iterator++;
						}
					}
				}
			}		
		}
		
		saveData(savetext, "01_xycoords.txt");
		savetext = "";
		
		//System.out.println("Length of good_columns: " + String.valueOf(good_columns.length) + "\nEntry 1: " + String.valueOf(good_columns[1]));
		
		Arrays.sort(good_columns);
		
		good_counter = 0;
		
		for (int l = 0; l < good_columns.length; l++) {
			if(good_columns[l] > 0.0) {
				savetext += String.valueOf(good_columns[l])+"\n";
				good_counter++;
			}
		}
		
		double[] good_columns_clean = new double[good_counter];
		
		good_counter = 0;
		
		for (int l = 0; l < good_columns.length; l++) {
			if(good_columns[l] > 0.0) {
				good_columns_clean[good_counter] = good_columns[l];
				good_counter++;
			}
		}
		
		saveData(savetext, "02_good_columns.txt");
		savetext = "";
		
		Roi[] roi_array_improv_X_temp = new Roi[roi_array_improv.length];
		double[] center_X_improv_X_temp = new double[roi_array_improv.length];
		double[] center_Y_improv_X_temp = new double[roi_array_improv.length];
		
		good_counter = 0;
		
		//Flächen nach X verbessert rauswerfen
		for (int l = 0; l < roi_array_improv.length; l++) {
			if(is_improved_for_xy(center_X_improv[l], good_columns, tolerance_X)) {
				roi_array_improv_X_temp[good_counter] = roi_array_improv[l];
				center_X_improv_X_temp[good_counter] = center_X_improv[l];
				center_Y_improv_X_temp[good_counter] = center_Y_improv[l];
				//System.out.println(String.valueOf(roi_array[l])+","+String.valueOf(double_center_X[l])+","+String.valueOf(double_center_Y[l]));
				//savetext += String.valueOf(center_X_improv[l])+","+String.valueOf(center_Y_improv[l])+"\n";
				good_counter++;
			}
		}
		
		Roi[] roi_array_improv_X = new Roi[good_counter];
		double[] center_X_improv_X = new double[good_counter];
		double[] center_Y_improv_X = new double[good_counter];
		
		for(int k = 0; k < good_counter; k++) {
			roi_array_improv_X[k] = roi_array_improv_X_temp[k];
			center_X_improv_X[k] = center_X_improv_X_temp[k];
			center_Y_improv_X[k] = center_Y_improv_X_temp[k];
			savetext += String.valueOf(center_X_improv_X[k])+","+String.valueOf(center_Y_improv_X[k])+"\n";
		}
		
		saveData(savetext, "03_xy_improved_X.txt");
		savetext = "";
		
		/* Erkennen von Zeilen mit einer Länge von 10
		 * Einen Eintrag herausnehmen
		 * Prüfen ob dieser bereits in good_rows gespeichert ist
		 * wenn nicht center_Y_improv durchsuchen und abgleichen
		 * wenn 31 innerhalb 8 Pixel verfügbar dann neuer Eintrag in good_columns*/
		
		double[] good_rows = new double[good_counter];
		
		boolean is_in_rows = false;
		boolean saved_in_rows = false;
		
		double tolerance_Y = 10;//10
		
		for(int l = 0; l < good_counter; l++) {
			
			is_in_rows = false;
			saved_in_rows = false;
			good_check_counter = 0;
			double check = center_Y_improv_X[l];
			
			for(int c = 0; c < good_rows.length; c++) {
				if(good_rows[c] > 0.0) {
					if(check > good_rows[c] - tolerance_Y && check < good_rows[c] + tolerance_Y) {
						is_in_rows = true;
					}
				}
			}
			
			if(is_in_rows) {
				//Der aktuelle y-Wert ist bereits in good_rows
				//nichts tun, zum nächsten Wert
			} else {
				//Der aktuelle y-Wert ist noch nicht in good_rows
				for(int k = 0; k < good_counter; k++) {
					if(check > center_Y_improv_X[k] - tolerance_Y && check < center_Y_improv_X[k] + tolerance_Y) {
						if(k != l) {
							good_check_counter++;
						}
					}
				}
				if(good_check_counter >= 5) { //muss wieder auf 9 hoch
					int save_iterator = 0;
					while(!saved_in_rows) {
						if(good_rows[save_iterator] == 0.0) {
							good_rows[save_iterator] = check;
							saved_in_rows = true;
						} else {
							save_iterator++;
						}
					}
				} else {
					//was fehlt?
				}
			}		
		}
		
		//System.out.println("Length of good_rows: " + String.valueOf(good_rows.length) + "\nEntry 1: " + String.valueOf(good_rows[1]));
		
		Arrays.sort(good_rows);
		
		good_counter = 0;
		
		for (int l = 0; l < good_rows.length; l++) {
			if(good_rows[l] > 0.0) {
				savetext += String.valueOf(good_rows[l])+"\n";
				good_counter++;
			}
		}
		
		double[] good_rows_clean = new double[good_counter];
		
		good_counter = 0;
		
		for (int l = 0; l < good_rows.length; l++) {
			if(good_rows[l] > 0.0) {
				good_rows_clean[good_counter] = good_rows[l];
				good_counter++;
			}
		}
		
		saveData(savetext, "04_good_rows.txt");
		savetext = "";
		
		Roi[] roi_array_improv_Y_temp = new Roi[roi_array_improv_X.length];
		double[] center_X_improv_Y_temp = new double[roi_array_improv_X.length];
		double[] center_Y_improv_Y_temp = new double[roi_array_improv_X.length];
		
		good_counter = 0;
		
		//Flächen nach X verbessert rauswerfen
		for (int l = 0; l < roi_array_improv_X.length; l++) {
			if(is_improved_for_xy(center_Y_improv_X[l], good_rows, tolerance_Y)) {
				roi_array_improv_Y_temp[good_counter] = roi_array_improv_X[l];
				center_X_improv_Y_temp[good_counter] = center_X_improv_X[l];
				center_Y_improv_Y_temp[good_counter] = center_Y_improv_X[l];
				//System.out.println(String.valueOf(roi_array[l])+","+String.valueOf(double_center_X[l])+","+String.valueOf(double_center_Y[l]));
				//savetext += String.valueOf(center_X_improv[l])+","+String.valueOf(center_Y_improv[l])+"\n";
				good_counter++;
			}
		}
		
		Roi[] roi_array_improv_Y = new Roi[good_counter];
		double[] center_X_improv_Y = new double[good_counter];
		double[] center_Y_improv_Y = new double[good_counter];
		
		for(int k = 0; k < good_counter; k++) {
			roi_array_improv_Y[k] = roi_array_improv_Y_temp[k];
			center_X_improv_Y[k] = center_X_improv_Y_temp[k];
			center_Y_improv_Y[k] = center_Y_improv_Y_temp[k];
			savetext += String.valueOf(center_X_improv_Y[k])+","+String.valueOf(center_Y_improv_Y[k])+"\n";
		}
		
		saveData(savetext, "05_xy_improved_Y.txt");
		savetext = "";
		
		/*
		 * Abstand zwischen guten Reihen und Spalten, dann den kleineren Wert halbieren oder dritteln
		 * Prüfen ob in Kreis um Masse-Punkt jeglicher Flächen noch ein anderer Punkt einer anderen Fläche liegt
		 * Wenn ja dann die Fläche behalten deren Punkt näher am Durchschnitt liegt, die andere löschen*/
		
		double distance_columns = 0.0;
		double distance_rows = 0.0;
		double sum = 0.0;
		int good_value_counter = 0;
		
		for (int k = 0; k < good_columns_clean.length; k++) {
			if(k > 0) {
				sum += good_columns_clean[k] - good_columns_clean[k-1];
				//System.out.println("sum_column: " + String.valueOf(sum));
				good_value_counter++;
			}
		}
		
		double average_columns = (sum / (good_value_counter))/2;
		
		sum = 0.0;
		good_value_counter = 0;
		
		for (int k = 0; k < good_rows_clean.length; k++) {
			if(k > 0) {
				sum += good_rows_clean[k] - good_rows_clean[k-1];
				//System.out.println("sum_rows: " + String.valueOf(sum));
				good_value_counter++;
			}
		}
		
		double average_rows = (sum / (good_value_counter))/2;
		
		//System.out.println("Average Columns: " + String.valueOf(average_columns));
		//System.out.println("Average Rows: " + String.valueOf(average_rows));
		
		//Alle besten Koordinaten durchgehen und mit allen anderen Koordinaten vergleichen
		
		Roi[] roi_array_improv_prox_temp = new Roi[center_X_improv_Y.length];
		double[] center_X_improv_prox_temp = new double[center_X_improv_Y.length];
		double[] center_Y_improv_prox_temp = new double[center_X_improv_Y.length];
		
		good_counter = 0;
		boolean coord_exists;
		
		for (int k = 0; k < center_X_improv_Y.length; k++) {
			double current_x = center_X_improv_Y[k];
			double current_y = center_Y_improv_Y[k];
			Roi current_roi = roi_array_improv_Y[k];
			
			coord_exists = false;
			
			for (int l = 0; l < center_X_improv_prox_temp.length; l++) {
				double check_x = center_X_improv_prox_temp[l];
				double check_y = center_Y_improv_prox_temp[l];
				//Dürfen nicht null sein.
				if(check_x > 0.0 && check_y > 0.0) {					
					//Dürfen nicht genau die gleichen Koordinaten sein
					if(check_x != current_x && check_y != current_y) {
						//Wenn es nicht genau die gleichen sind dann checken ob im temp_array schon ein Eintrag ist, der sehr nahe oder der gleiche ist.
						//Wenn es einen gibt dann true
						double distance = Abstand_PunktPunkt(current_x, current_y, check_x, check_y);
						
						if(distance < average_columns && distance < average_rows) {
							coord_exists = true;
						}
						
					}
				}
			}
			//Wenn false dann gibt es noch keinen in dieser Nähe also Ausgangscoordinate speichern
			if(!coord_exists) {
				center_X_improv_prox_temp[good_counter] = current_x;
				center_Y_improv_prox_temp[good_counter] = current_y;
				roi_array_improv_prox_temp[good_counter] = current_roi;
				good_counter++;
				savetext += String.valueOf(current_x)+","+String.valueOf(current_y)+"\n";
			}
			
		}
		
		saveData(savetext, "06_xy_improved_prox.txt");
		savetext = "";
		
		Roi[] roi_array_improv_prox = new Roi[good_counter];
		double[] center_X_improv_prox = new double[good_counter];
		double[] center_Y_improv_prox = new double[good_counter];
		
		for(int k = 0; k < good_counter; k++) {
			roi_array_improv_prox[k] = roi_array_improv_prox_temp[k];
			center_X_improv_prox[k] = center_X_improv_prox_temp[k];
			center_Y_improv_prox[k] = center_Y_improv_prox_temp[k];
		}
		
		//Durchschnitt berechnen für jede column und row
		//Dabei auf fehlende Werte achten
		//Demenstprechend Werte anpassen
		
		double[][] center_XY = new double[center_X_improv_prox.length][2];
		double[][] center_YX = new double[center_X_improv_prox.length][2];
		
		for(int k = 0; k < good_counter; k++) {
			center_XY[k][0] = center_X_improv_prox[k];
			center_XY[k][1] = center_Y_improv_prox[k];
		}
		//Sort for x (Aufsteigend)
		Arrays.sort(center_XY, new java.util.Comparator<double[]>() {
		    public int compare(double[] a, double[] b) {
		        return Double.compare(a[0], b[0]);
		    }
		});
		
		for(int k = 0; k < good_counter; k++) {
			savetext += String.valueOf(center_XY[k][0])+","+String.valueOf(center_XY[k][1])+"\n";
		}
		
		saveData(savetext, "07_2darray_sorted_x.txt");
		savetext = "";
		
		//---------------------------------
		/* Alle Spalten durchsuchen und eine vollständige Spalte finden
		 * Wenn erster und letzter Punkt bekannt sind:
		 *  Zwischen erstem und letztem Punkt einer Spalte lineare Gleichung finden
		 *  Abstand zwischen x1 und x31 durch 31 teilen = neue Verteilung der x-Werte dann einsetzen für y-Werte
		 *
		 * Oder:
		 * 	In Schleife x-Werte vergleichen bis ein großer Unterschied besteht -> Ende dieser Spalte gefunden
		 *  Dabei Werte in neuem Array speichern. Wenn Spalte nicht vollständig (Anzahl geht aus Differenz zu 31 hervor)
		 *  Nach y-Werten sortieren, Lücken ausfindig machen. Wenn keine innerhalb der Werte zu finden ist, muss der fehlende Wert 
		 *  Neuer y-Wert durchschnitt aller y-Werte entfernt von den nächsten "guten" Punkten
		 *  x-Werte von den angrenzenden Punkten dienen als Durchschnitt-"Geber"
	 	 *  	
		 * */
		
		double[][] check_empty_XY = new double[31][2];
		double[][] check_empty_YX = new double[31][2];
		
		double[][] check_filled_XY = new double[31][2];
		
		int diff_counter = 1;
		int column_counter = 0;
		
		for(int k = 0; k < good_counter; k++) {
			
			double diff = 0.0;
			
			if(k == good_counter - 1) {
				diff = -1000;	
			} else {
				diff = center_XY[k+1][0] - center_XY[k][0];
			}
			
			check_empty_XY[diff_counter-1][0] = center_XY[k][0];
			check_empty_XY[diff_counter-1][1] = center_XY[k][1];
			
			check_empty_YX[diff_counter-1][0] = center_XY[k][1];
			check_empty_YX[diff_counter-1][1] = center_XY[k][0];
	
			if(diff > 0 && diff < 30) {
				//normaler Abstand, immernoch die gleiche Spalte
								
			} else {
				column_counter++;
				
				if(diff_counter < 31) {
					//Es fehlt mindestens ein Punkt, genau "missing" Punkte
					int missing = 31 - diff_counter;
					
					for(int j = 0; j < diff_counter; j++) {
						savetext += String.valueOf(check_empty_YX[j][0])+","+String.valueOf(check_empty_YX[j][1])+"\n";
					}
					
					saveData(savetext, "08_spalte_" + String.valueOf(column_counter) + "_unsorted_y.txt");
					savetext = "";
					
					System.out.println("In Spalte: " + String.valueOf(column_counter) + " fehlen: " + String.valueOf(missing) + " Punkte");
					
					Arrays.sort(check_empty_YX, new java.util.Comparator<double[]>() {
					    public int compare(double[] a, double[] b) {
					        return Double.compare(a[0], b[0]);
					    }
					});
					//Sortiertes Array speichern
					for(int j = 0; j < 31; j++) {
						savetext += String.valueOf(check_empty_YX[j][0])+","+String.valueOf(check_empty_YX[j][1])+"\n";
					}
					saveData(savetext, "08_spalte_" + String.valueOf(column_counter) + "_sorted_y.txt");
					savetext = "";

					for(int j = 0; j < 31-1; j++) {
						
						/*Verschiedene Möglichkeiten:
						 * 1-n nicht erkannte Löcher
						 * alle einzeln
						 * alle zusammen
						 * teilweise zusammen und teilweise einzeln
						 * teilweise zusammen und teilweise zusammen*/
						
						if(check_empty_YX[j][0] > 0) {						
							double y_diff = check_empty_YX[j+1][0] - check_empty_YX[j][0];
							
							if(y_diff < 80) {
								//Wert in Ordnung, Abstand erwartet
								check_filled_XY[j][0] = check_empty_YX[j][1];
								check_filled_XY[j][1] = check_empty_YX[j][0];
								
							} else {
								//Wert zu groß, hier fehlt ein Punkt
								check_filled_XY[j][0] = check_empty_YX[j][1];
								check_filled_XY[j][1] = check_empty_YX[j][0];
								//Fehlenden Punkt berechnen
								double x_neu = (check_empty_YX[j][1] + check_empty_YX[j+1][1]) / 2;
								double y_neu = (check_empty_YX[j][0] + check_empty_YX[j+1][0]) / 2;
								
								System.out.println("X_neu,Y_neu: " + String.valueOf(x_neu) + "," + String.valueOf(y_neu));
							
							}
						}
					}
					
					//Purging array
					for(int j = 0; j < 31; j++) {
						check_empty_YX[j][0] = 0.0;
						check_empty_YX[j][1] = 0.0;
					}
						
				} else {
					//Es fehlt nichts, weiter zur nächsten Spalte
					//Da es eine vollständige Spalte ist Erste und Letzte Koordinate in globale Variablen speichern
					// kann dann bei Bedarf abgerufen werden 
					/*
					 * durch check_empty_YX loopen und geringsten Wert größer 0 und höchsten Wert herausfinden
					 * geringester Wert ergibt mit x und y start_XY und höchster Wert ergibt end_XY
					 * Achtung x un y vertauschen von check_empty_YX*/
					
					global_start_XY[0][0] = 0.0;
					global_start_XY[0][1] = 0.0;
					
					global_end_XY[0][0] = 0.0;
					global_end_XY[0][1] = 0.0;
					
					global_end_XY = new double[1][2];
					
					//Purging array
					for(int j = 0; j < 31; j++) {
						check_empty_YX[j][0] = 0.0;
						check_empty_YX[j][1] = 0.0;
					}
				}
				diff_counter = 0;
			}	
			
			diff_counter++;
			
		}		
		
		//---------------------------------
		
			//Sort for x (Absteigend)
			/*
			Arrays.sort(center_XY, new java.util.Comparator<double[]>() {
			    public int compare(double[] a, double[] b) {
			        return Double.compare(b[0], a[0]);
			    }
			});
			
			for(int k = 0; k < good_counter; k++) {
				savetext += String.valueOf(center_XY[k][0])+","+String.valueOf(center_XY[k][1])+"\n";
			}
			
			saveData(savetext, "2darray_sorted_x.txt");*/
		
		/*
		double[] row_average = new double[10]; 		
		double row_sum = 0.0;
		
		for(int k = 0; k < 10; k++) {
			for(int l = 0; l < 31; l++) {
				int place = k*31+l;
				row_sum += center_XY[place][0];
			}
			row_average[k] = row_sum/31;
			savetext += row_average[k]+"\n"; 
			row_sum = 0.0;
		}
		
		saveData(savetext, "08_averages_x.txt");
		savetext = "";
		
		for(int k = 0; k < 10; k++) {
			for(int l = 0; l < 31; l++) {
				int place = k*31+l;
				center_XY[place][0] = row_average[k];
			}
		}
		
		for(int k = 0; k < good_counter; k++) {
			savetext += String.valueOf(center_XY[k][0])+","+String.valueOf(center_XY[k][1])+"\n";
		}
		
		saveData(savetext, "09_averages_new_x.txt");
		savetext = "";
		
		//x und y Koordinaten vertauschen
		
		for(int k = 0; k < good_counter; k++) {
			center_YX[k][0] = center_XY[k][1];
			center_YX[k][1] = center_XY[k][0];
		}
		
		//Sort for y (Aufsteigend)
		Arrays.sort(center_YX, new java.util.Comparator<double[]>() {
		    public int compare(double[] a, double[] b) {
		        return Double.compare(a[0], b[0]);
		    }
		});
		
		for(int k = 0; k < good_counter; k++) {
			savetext += String.valueOf(center_YX[k][0])+","+String.valueOf(center_YX[k][1])+"\n";
		}
		
		saveData(savetext, "10_average_x_sorted_y.txt");
		savetext = "";
		
		double[] column_average = new double[31]; 		
		double column_sum = 0.0;
		
		for(int k = 0; k < 31; k++) {
			for(int l = 0; l < 10; l++) {
				int place = k*10+l;
				column_sum += center_YX[place][0];
			}
			column_average[k] = column_sum/10;
			savetext += column_average[k]+"\n"; 
			column_sum = 0.0;
		}
		
		saveData(savetext, "11_averages_y_x.txt");
		savetext = "";
		
		for(int k = 0; k < 31; k++) {
			for(int l = 0; l < 10; l++) {
				int place = k*10+l;
				center_YX[place][0] = column_average[k];
			}
		}
		
		for(int k = 0; k < good_counter; k++) {
			savetext += String.valueOf(center_YX[k][0])+","+String.valueOf(center_YX[k][1])+"\n";
		}
		
		saveData(savetext, "12_averages_new_y.txt");
		savetext = "";
		
		//y und x Koordinaten vertauschen
		//und in int[] kopieren
		
		int[] xpoints = new int[good_counter];
		int[] ypoints = new int[good_counter];
		
		for(int k = 0; k < good_counter; k++) {
			center_XY[k][0] = center_YX[k][1];
			center_XY[k][1] = center_YX[k][0];
		}		
		
		//Sort for x (Aufsteigend)
		Arrays.sort(center_XY, new java.util.Comparator<double[]>() {
		    public int compare(double[] a, double[] b) {
		        return Double.compare(a[0], b[0]);
		    }
		});
		
		for(int k = 0; k < good_counter; k++) {
			savetext += String.valueOf(center_XY[k][0])+","+String.valueOf(center_XY[k][1])+"\n";
			xpoints[k] = (int)center_XY[k][0];
			ypoints[k] = (int)center_XY[k][1];
		}
		
		saveData(savetext, "13_new_x_y_sorted_x.txt");
		savetext = "";
		*/
		
		//----------------------------------------------------------------
		roi_manager.reset();
		
		/*for(int c = 0; c < roi_array_improv_prox.length; c++) {
			roi_manager.addRoi(roi_array_improv_prox[c]);
		}*/
		
		System.out.println("Mean: " + String.valueOf(results_mean));
		System.out.println("SD: " + String.valueOf(results_SD));
		
		analysedImage = particleAnalyzer.getOutputImage();
		
		analysedImage.close();
		
		//imp_a.close();
		//imp_b.close();
		//imp_added.close();
		
		imp_roitest.show();
		//imp_roitest.setRoi(roi_array[0], true);
		//imp_roitest.setRoi(new PointRoi(xpoints, ypoints, ypoints.length));
		
		double temp_radius = 0.0;
		
		if(average_columns == average_rows) {
			temp_radius = average_rows;
		} else {
			if(average_columns < average_rows) {
				temp_radius = average_columns;
			}
			if(average_rows < average_columns) {
				temp_radius = average_rows;
			}
		}
		
		temp_radius = temp_radius/1.5;
		radius = (int)temp_radius;
		
		Roi[] final_rois = new Roi[good_counter];
		ImagePlus[] final_images = new ImagePlus[good_counter];
		int[][] histo = new int[310][256];
		
		String[] letters = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J"};
		
		//Selber bestimmte Löcher
		int[] category_one = {41,50,51,52,81,97,101,112,128,135,147,149,151,154,161,165,189,193,194,195,198,209,210,211,287};
		int[] category_two = {9,10,11,13,14,28,29,30,44,59,60,73,89,90,91,103,104,105,106,107,120,121,122,123,136,137,138,145,152,153,166,167,169,170,175,178,179,183,184,197,203,205,214,215,227,234,235,236,237,246,263,265,266,267,269,277,295,296,297,298,299,300,301,302,308};
		
		String pixel_erkannt = "";
		
		int width = 0;
		int height = 0;
		int numPixels = 0;
		
		for(int k = 0; k < 10; k++) {
			for(int l = 0; l < 31; l++) {
				int place = k*31+l;
				
				Roi new_roi = new Roi(xpoints[place]-radius, ypoints[place]-radius, 2*radius, 2*radius);
				final_rois[place] = new_roi;
				roi_manager.addRoi(new_roi);
				imp_roitest.setRoi(new_roi);		
				
				final_images[place] = imp_roitest.crop();
				
				final_images[place].show();
				
				if(place == 0) {
					ImageProcessor ip = final_images[place].getProcessor();
					
					width = ip.getWidth();
					height = ip.getHeight();
					numPixels = width*height;
				}
				
				//Prefs.blackBackground = false;
				//IJ.run(final_images[place], "Make Binary", "");
				//IJ.run(final_images[place], "Histogram", "name = bla");
				
				//IJ.run(final_images[place], "Subtract Background...", "rolling=0.5 light separate create sliding");
				/*
				ImagePlus imp = IJ.getImage();
				
				String name_expo = StartFileDirectory + "/Export/" + StartFileName + "_" + letters[k] + "_" + String.valueOf(l+1) + "_" + String.valueOf(place) + "_" + "histo";
				
				String export_name_expo = name_expo + ".tif";
				
				IJ.saveAs(imp, "tiff", name_expo);
				
				imp.close();*/
				
					int[] rgb_values_int = extractPixelValuesNoMaskInt(final_images[place]);
					
					String savepixels = "";
					
					for(int v = 0; v < rgb_values_int.length; v++){
						if(v == 0) {
							savepixels +=  rgb_values_int[v];
						} else {
							savepixels +=  "," + rgb_values_int[v];
						}
						
					}
					
					saveData(savepixels, StartFileName + "_" + letters[k] + "_" + String.valueOf(l+1) + "_" + String.valueOf(place) + "_" + "pixelvaluesINT.txt");
					
					
					
					String[] rgb_values = extractPixelValuesNoMask(final_images[place]);
					
					StringBuilder sb = new StringBuilder();
			
					for(int v = 0; v < rgb_values.length; v++){
						sb.append(rgb_values[v]);
					}
					String export_pixelvalues = sb.toString();
					
					pixel_erkannt += String.valueOf(place) + "," + export_pixelvalues + "\n";
					
					saveData(export_pixelvalues, StartFileName + "_" + letters[k] + "_" + String.valueOf(l+1) + "_" + String.valueOf(place) + "_" + "pixelvaluesCPA.txt");
					
					//saveData(savetext, StartFileName + "_" + letters[k] + "_" + String.valueOf(l+1) + "_" + String.valueOf(place) + ".txt");
					//savetext = "";
				
				
				ImageProcessor ip = final_images[place].getProcessor();
				int[] hist_temp = ip.getHistogram();
				
				for (int m = 0; m < hist_temp.length; m++) {
					histo[place][m] = hist_temp[m];
					//savetext += String.valueOf(hist_temp[m])+"\n";
				}
				
				String name = StartFileDirectory + "/Export/" + StartFileName + "_" + letters[k] + "_" + String.valueOf(l+1) + "_" + String.valueOf(place) + "_" + "original";
				
				String export_name = name + ".tif";
				
				IJ.saveAs(final_images[place], "tiff", name);
				
				final_images[place].close();			
				
				
			}
		}
		
		int[] pixel_averages = new int[numPixels];
		
		for(int k = 0; k < 10; k++) {
			for(int l = 0; l < 31; l++) {
				int place = k*31+l;
					
				String fileName = StartFileDirectory + "/Export/" + StartFileName + "_" + letters[k] + "_" + String.valueOf(l+1) + "_" + String.valueOf(place) + "_" + "pixelvaluesINT.txt";
				
				String str = "";
				
                try {
                	File fe = new File(fileName);
                    FileInputStream fis = new FileInputStream(fe);
                    byte data[] = new byte[fis.available()];
                    fis.read(data);
                    fis.close();
                    str = new String(data);
                    //System.out.println("Gelesen: "+str);
                    fe.delete();
				} catch (Exception e) {
					e.printStackTrace();
				}
                
                if(isinintarray(place, category_one)) {
	                if(!str.equals("")) {
	                	String Averages_splitString[] = str.split(",");
	            		
	            		for (int m = 0; m < Averages_splitString.length; m++) {
	            			int temp = Integer.parseInt(Averages_splitString[m]);
	            			int temp_add = temp + pixel_averages[m]; 
	            			pixel_averages[m] = temp_add;
	            		}
	            		
	                } else {
	                	//Fehler beim lesen der Dateien
	                }
                }
		        
			}
		}
		int counter_averages = 0;
		String showaverages = "";
		
		for(int y = 0; y < height; y++){
			for(int x = 0; x < width; x++){
				//fill
				
				int mean = pixel_averages[counter_averages]/category_one.length;
				
				if(x == width-1){
					//rgb_values[counter] = "R:" + r + ",G:" + g + ",B:" + b + "\n";
					showaverages += mean + "\n";
				} else {
					//rgb_values[counter] = "R:" + r + ",G:" + g + ",B:" + b + "|";
					showaverages += mean + "|";
				}
				counter_averages++;
			}
		}
		
		saveData(showaverages, "pixelAVERAGES_SUM.txt");
		
		/*for(int k = 0; k < 10; k++) {
			for(int l = 0; l < 31; l++) {
				int place = k*31+l;
				savetext += String.valueOf(place);
				for (int m = 0; m < 256; m++) {
				savetext += ","+String.valueOf(histo[place][m]);
				}
				savetext += "\n";
				
			}
		}*/
		
		/*for(int k = 0; k < 256; k++) {
			for (int m = 0; m < 310; m++) {
				if(m==0) {
					savetext += String.valueOf(k) + "," +String.valueOf(histo[m][k]);
				} else {
					savetext += ","+String.valueOf(histo[m][k]);
				}
				
			}
			savetext += "\n";
		}
		
		saveData(savetext, "zusammenfassung.txt");
		savetext = "";*/
		
		saveData(pixel_erkannt, "PixelErkannt.txt");
		
		
		//for(int k = 0; k < good_counter; k++) {
			//Roi new_roi = new Roi(xpoints[k]-radius, ypoints[k]-radius, 2*radius, 2*radius/*, 2*radius*/);
			/*final_rois[k] = new_roi;
			roi_manager.addRoi(new_roi);
			imp_roitest.setRoi(new_roi);		
			
			final_images[k] = imp_roitest.crop();
			
			String extend = "";
			
			//String name = StartFileDirectory + "/Export/" + String.valueOf(CountImages) + "_" + Maske + "_" + String.valueOf(imagecounter);
			String name = StartFileDirectory + "/Export/" + StartFileName + "_" + String.valueOf(k) + "_" + "original";
			
			String export_name = name + ".tif";
			
			//IJ.saveAs(imp3, "jpg", name);
			
			//IJ.saveAs(final_images[k], "tiff", name);
		}*/
	
		//imp_roitest.close();
		
		
		
		//Close Result-Table
		IJ.selectWindow("Results"); 
		IJ.run("Close");

		roi_manager.runCommand(imp_roitest, "Show All");
		//Close RoiManager
		//roi_manager.close();
		
		
		
		/*
		Labels/Rois in die richtige Reihenfolge bringen
		"Falsche" Einträge entfernen (viel zu kleine Fläche)
		Sortierte gute Flächen als Roi im Originalbild laden
		Jede einzelne Fläche einzeln exportieren als Zwischenschritt
		Jede einzelne Fläche einzeln berechnen
			-> Durchschnitt der Pixelwerte?
			-> Anzahl heller/dunkler Pixel
		*/
		
	}
	
	boolean isinintarray(int index, int[] array) {
		
		boolean isinarray = false;
		
		for (int i = 0; i < array.length; i++) {
			if(index == array[i]) {
				isinarray = true;
			}
		}
		
		return isinarray;
	}
	
	void saveDataInt(byte[] save_array, String filename, String directory) {
		
		String currentPath = directory;
		if(currentPath != null){
			String savePath = currentPath;
			System.out.println("Pfad: " + savePath);
			byte[] data = save_array;
			try {
				File f = new File(savePath, filename);
					if (!f.exists()) {
					f.createNewFile();
					}
				FileOutputStream out = new FileOutputStream(f);
				out.write(data, 0, data.length);
				out.close();
			} catch (IOException e) {
				IJ.error("ColorThresholderSaving", ""+e);
			}
		}
	
	}
	
	byte[] readFileBinaryInt(String filename, String directory) {
		String currentPath = directory;
		String savePath = currentPath;
		
		File file = new File(savePath + "/" + filename);
		
		FileInputStream fileInputStream = null;
		byte[] bFile = new byte[(int) file.length()];
		try {
			//convert file into array of bytes
			fileInputStream = new FileInputStream(file);
			fileInputStream.read(bFile);
			fileInputStream.close();
			for (int i = 0; i < bFile.length; i++) {
				System.out.print((char) bFile[i]);
			}
		} catch (Exception e) {
         e.printStackTrace();
		}
		
		file.delete();
		
		return bFile;
	}
	
	boolean is_improved_for_xy(double check, double[] check_array, double tolerance) {
		
		boolean is_in_array = false;
		
		for(int k = 0; k < check_array.length; k++) {
			if(check > check_array[k] - tolerance && check < check_array[k] + tolerance) {
				is_in_array = true;
			}
		}
		
		return is_in_array;
	}
	
	private static double mean(double[] z) {
	    int l = z.length;
	    double sum = 0;
	    for(int i = 0; i < l; i++) {
	      sum += z[i];
	    }
	    double m = sum/l;
	    return m;
	  }
	
	public static double calculateSD(double numArray[]) {
        double sum = 0.0, standardDeviation = 0.0;
        int length = numArray.length;

        for(double num : numArray) {
            sum += num;
        }

        double mean = sum/length;

        for(double num: numArray) {
            standardDeviation += Math.pow(num - mean, 2);
        }

        return Math.sqrt(standardDeviation/length);
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
		//imp.close();
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
		
		for(int i = 0; i < coords_array.length; i++) {
			System.out.println(coords_array[i]);
		}
		
		ImagePlus win = new ImagePlus("Corners from " + imp.getTitle(),result);
		win.show();
		//imp2.close(); //Hier Originalbild anschauen
		//win.close(); //Hier die erkannten Eckpunkte anschauen
		
		//saveData(coords, "coordsnew.txt");
		//exportCropImages(CropPath, CountImages, StartFileMask);
	}
	
	public void loadimagesevBatchStack(ImageStack imstack, ImagePlus impstack){
		
		String filename = "";
		
		String export_pixelvalues = "";
		String filename_pixelvalues = "";
		
		impstack.show();
		
		Integer[] input_int = new Integer[9];
		input_int[0] = minHue;
		input_int[1] = maxHue;
		input_int[2] = minSat;
		input_int[3] = maxSat;
		input_int[4] = minBri;
		input_int[5] = maxBri;
		input_int[6] = mode;
		input_int[7] = colorSpace;
		input_int[8] = method_int;
		
		Boolean[] input_bool = new Boolean[4];
		input_bool[0] = bandPassH;
		input_bool[1] = bandPassS;
		input_bool[2] = bandPassB;
		input_bool[3] = darkBackground;
		
		ColorThresholderExtended thresher = new ColorThresholderExtended(); //Color Threshold Fenster Ã¶ffnen
		//thresher.setStringValue(method);
		thresher.setIntValues(input_int);
		thresher.setBoolValues(input_bool);
		
		thresher.updateAfterSetting(impstack);
		
		boolean stack_action = thresher.getStackAction();
		boolean stackdialog = false;
		
		while (!stackdialog) {
			stack_action = thresher.getStackAction();
			stackdialog = dialolgStackAction("Input", "Please first adjust Threshold\nThen use Stack and on completion click ok here", stack_action, thresher);
		}
		
		/*InputDialog inputcolorthresher = new InputDialog("Input", "Please first adjust Threshold\nThen use Stack and on completion click ok here", "Threshold", impstack);
		inputcolorthresher.show();*/
		
		//color_string_value = thresher.getStringValue();
		color_int_values = thresher.getIntValues();
		color_bool_values = thresher.getBoolValues();
		
		//method = color_string_value;
		method = "DEFAULT";
		minHue = color_int_values[0];
		maxHue = color_int_values[1];
		minSat = color_int_values[2];
		maxSat = color_int_values[3];
		minBri = color_int_values[4];
		maxBri = color_int_values[5];
		mode = color_int_values[6];
		colorSpace = color_int_values[7];
		method_int = color_int_values[8];
		bandPassH = color_bool_values[0];
		bandPassS = color_bool_values[1];
		bandPassB = color_bool_values[2];
		darkBackground = color_bool_values[3];
			
		System.out.println("Active Windows: " + WindowManager.getWindowCount());
		
		String added = color_int_values[0] + ", " + color_int_values[1] + ", " + color_bool_values[0] + ", " + color_int_values[2] + ", " + color_int_values[3] + ", " + color_bool_values[1] + ", " + color_int_values[4] + ", " + color_int_values[5] + ", " + color_bool_values[2] + ", " + color_int_values[8] + ", " + color_int_values[6] + ", " + color_int_values[7] + ", " + color_bool_values[3];
		
		System.out.println("Values: " + added);
		
		//Hier bei Bedarf stattdessen Alterselection einfügen
		
		int numSlices = impstack.getStackSize();
		
		for (int l = 1; l <= numSlices; l++) {
			
			impstack.setSlice(l);
			StartFileName = imstack.getSliceLabel(l);
			
			String datetime = time_array[l-1];
			
			byte[] maske = readFileBinary(impstack, String.valueOf(l) + "_Slice.bin");
			
			thresher.drawBinaryStack(maske, impstack, l);
			
			ImageProcessor ip = imstack.getProcessor(l);
			
			ImageStatistics stats = ImageStatistics.getStatistics(ip, ImageStatistics.AREA_FRACTION, null);
			
			int width = ip.getWidth();
			int height = ip.getHeight();
			
			double real_area = 0;
			
			if(scale_CMPx != 0) {
				real_area = (width * scale_CMPx) * (height * scale_CMPx);
			}
			
			double aFraction = stats.areaFraction;
			double greenFraction = 100-aFraction;
			//double area = (aFraction * 36)/100;
			
			DecimalFormat df = new DecimalFormat("#.#####");
			String aFraction_df = df.format(greenFraction); 
			
			double measure_counter = 0;
			
			for(int i = 0; i < maske.length; i++){
				if (maske[i]!=0){
					//fill
					measure_counter++;
				}
			}
			//Prozent Abdeckung der thresholded Pixel
			double PixelFraction = (measure_counter * 100) / maske.length;
			
			double RealFraction = (real_area * PixelFraction) / 100;
			
			System.out.println("maske.length: " + maske.length + "\nMeasure_Counter: " + measure_counter + "\nPixelFraction: " + PixelFraction + "\nArea: " + real_area);
			
			String pixel_aFraction_df = df.format(PixelFraction); 
			String real_Fraction_df = df.format(RealFraction);
			
			String real_Fraction_complete = real_Fraction_df + " cm²";
			
			if(RealFraction == 0) {
				real_Fraction_complete = "Konnte nicht ausgerechnet werden.";
			}
			
			filename = StartFileName + "_" + batchName + "_" + String.valueOf(l) + "_Fl\u00e4chenexport" + ".txt";		
			
			summary_batch = summary_batch + "\n" + batchName + "|" + StartFileName + "|" + datetime + "|" + real_Fraction_df + "|" + pixel_aFraction_df;
			
			String[] rgb_values = extractPixelValues(impstack, maske);
			
			StringBuilder sb = new StringBuilder();
	
			for(int i = 0; i < rgb_values.length; i++){
				sb.append(rgb_values[i]);
			}
			export_pixelvalues = sb.toString();
			filename_pixelvalues = StartFileName + "_" + batchName + "_" + String.valueOf(l) + "_Einzelpixel_Export" + ".txt";
			saveData(export_pixelvalues, filename_pixelvalues);
			
		}
		
		thresher.close();
		summary_batch_name = batchName + "_summary.txt";
		saveData(summary_batch, summary_batch_name);
		
		String stackexportname = StartFileDirectory + "/Export/" + batchName + "_binary_stack";
		IJ.saveAs(impstack, "tiff", stackexportname);	
		impstack.close();
		
	}
	
	byte[] readFileBinary(ImagePlus imp, String filename) {
		String currentPath = getDirectory(imp);
		String savePath = currentPath;
		
		File file = new File(savePath + "/" + filename);
		
		FileInputStream fileInputStream = null;
		byte[] bFile = new byte[(int) file.length()];
		try {
			//convert file into array of bytes
			fileInputStream = new FileInputStream(file);
			fileInputStream.read(bFile);
			fileInputStream.close();
			for (int i = 0; i < bFile.length; i++) {
				System.out.print((char) bFile[i]);
			}
		} catch (Exception e) {
         e.printStackTrace();
		}
		
		file.delete();
		
		return bFile;
	}
	
	public void loadimagesEinzeln(int CountImages){
		imp = IJ.getImage();
		ImageProcessor originalip = imp.getProcessor();
		
		StartFileDirectory = getDirectory(imp);
		StartFileName = getFileName(imp);
		
		String StartFilePathName = StartFileDirectory + StartFileName;
				
		ImagePlus imp2 = new ImagePlus();
		imp2.setImage(imp);
		imp.close();
		imp2.show();
		
		//For real reference
		//getScale(imp2);
		
		IJ.setTool("rectangle");
		
		InputDialog inputrectangle = new InputDialog("Input", "Please draw Rectangle\nRectangle and Points are movable", "Rectangle", imp2);
		inputrectangle.show();
		
		//IJ.run(imp2, "Color Threshold...", "sigma"); ------WICHTIG COLOR THRESHOLD
		Roi rois;
		Point[] points;
		Rectangle rectangle;
		
		rois = imp2.getRoi();
		
		rectangle = rois.getBounds();
		System.out.println("x,y: " + rectangle.x + "," + rectangle.y);
		
		/*for(int i = 0; i < points.length; i++){
			int x = points[i].x;
			int y = points[i].y;
			System.out.println(String.valueOf(i) + ": " + String.valueOf(x) + "," + String.valueOf(y));
		}*/
			
		
		imp2.close();
		//saveData(coords, "coordsnew2.txt");
		exportCropImagesEinzelViereck(StartFilePathName, StartFileName, rois, "1");
	}
	
	public void loadimagesMehrereVierecke(int CountImages){
		imp = IJ.getImage();
		ImageProcessor originalip = imp.getProcessor();
		
		StartFileDirectory = getDirectory(imp);
		StartFileName = getFileName(imp);
		
		String StartFilePathName = StartFileDirectory + StartFileName;
		
		int amount = 0;
		
		while(amount == 0) {
			amount = showMehrereVierecke();
		}
		
		if(amount > 0) {
			System.out.println("Amount: " + amount);
			
			getScale(imp);
			
			for(int i = 0; i < amount; i++) {
				ImagePlus imp2 = new ImagePlus();
				imp2.setImage(imp);
				//imp.close();
				imp2.show();
				
				IJ.setTool("rectangle");
				
				InputDialog inputrectangle = new InputDialog("Input", "Please draw Rectangle\nRectangle and Points are movable", "Rectangle", imp2);
				inputrectangle.show();
				
				Roi rois;
				Point[] points;
				Rectangle rectangle;
				
				rois = imp2.getRoi();
				
				rectangle = rois.getBounds();
				System.out.println("x,y: " + rectangle.x + "," + rectangle.y);
				
				imp2.close();
				
				//saveData(coords, "coordsnew2.txt");
				exportCropImagesEinzelViereck(StartFilePathName, StartFileName, rois, String.valueOf(i+1));
			}
			
			imp.close();
			
		} else {
			if(amount == -1) {
				showInfo("Info", "Eingabe abgebrochen");
				problem = true;
				imp.close();
			}
		}
	}
	
public void loadimagesFixPoints(int CountImages, int project){
		
		imp = IJ.getImage();
		ImageProcessor originalip = imp.getProcessor();
		
		StartFileDirectory = getDirectory(imp);
		StartFileName = getFileName(imp);
		
		String StartFilePathName = StartFileDirectory + StartFileName;
		String export = String.valueOf(CountImages);
		String summary = "plot|qcm|percentage";
		String name_export_summary = "";
				
		ImagePlus imp2 = new ImagePlus();
		imp2.setImage(imp);
		imp.close();
		imp2.show();
		
		int height = imp.getHeight();
		int width = imp.getWidth();
				
		if(project == Oscar_I) {
			
			Point[] points;
			Roi rois;
			
			ImagePlus imp_export = imp2;
			ImagePlus[] imp_crop = new ImagePlus[64];
			
			int crop_counter = 0;
			
			//Schleife mit der Länge 16
			//Unterschleife mit der Länge 4
			
			for(int i = 0; i < 16; i++) {
				for(int k = 0; k < 4; k++) {
					//System.out.println("Punkt:" + i + "," + xpoints_flaechen[i] + "," + ypoints_flaechen[i]);
					
					imp_crop[i] = new ImagePlus();
					imp_crop[i].setImage(imp2);					
							
					imp_crop[i].show();
					
					int roi_x1 = Oscar_I_x[i*2];
					int roi_x2 = Oscar_I_x[i*2+1];
					int roi_y1 = Oscar_I_y[k*2+1];;
					int roi_y2 = Oscar_I_y[k*2];
					
					int a = roi_x2 - roi_x1;
					int b = roi_y2 - roi_y1;
					
					Roi roi_imp = new Roi(roi_x1, roi_y1, a, b);
					
					imp_crop[i].setRoi(roi_imp);
					
					IJ.run(imp_crop[i], "Crop", "");
					IJ.run("Maximize", "");
					
					export = String.valueOf(crop_counter+1);
					
					String name = StartFileDirectory + "/Export/" + StartFileName + "_" + export;
					
					String export_name = name + ".tif";
					
					IJ.saveAs(imp_crop[i], "tiff", name);
					
					int crop_height = imp_crop[i].getHeight();
					int crop_width = imp_crop[i].getWidth();
					
					ColorThresholderExtended thresher = new ColorThresholderExtended(); //Color Threshold Fenster öffnen
					
					Integer[] input_int = new Integer[9];
					input_int[0] = minHue;
					input_int[1] = maxHue;
					input_int[2] = minSat;
					input_int[3] = maxSat;
					input_int[4] = minBri;
					input_int[5] = maxBri;
					input_int[6] = mode;
					input_int[7] = colorSpace;
					input_int[8] = method_int;
					
					Boolean[] input_bool = new Boolean[4];
					input_bool[0] = bandPassH;
					input_bool[1] = bandPassS;
					input_bool[2] = bandPassB;
					input_bool[3] = darkBackground;
					
					//thresher.setStringValue(method);
					thresher.setIntValues(input_int);
					thresher.setBoolValues(input_bool);
					
					InputDialog inputcolorthresher = new InputDialog("Input", "Please adjust Threshold", "Threshold", imp_crop[i]);
					inputcolorthresher.show();
					
					//color_string_value = thresher.getStringValue();
					color_int_values = thresher.getIntValues();
					color_bool_values = thresher.getBoolValues();
					
					//method = color_string_value;
					method = "DEFAULT";
					minHue = color_int_values[0];
					maxHue = color_int_values[1];
					minSat = color_int_values[2];
					maxSat = color_int_values[3];
					minBri = color_int_values[4];
					maxBri = color_int_values[5];
					mode = color_int_values[6];
					colorSpace = color_int_values[7];
					method_int = color_int_values[8];
					bandPassH = color_bool_values[0];
					bandPassS = color_bool_values[1];
					bandPassB = color_bool_values[2];
					darkBackground = color_bool_values[3];
					
					String added = color_int_values[0] + ", " + color_int_values[1] + ", " + color_bool_values[0] + ", " + color_int_values[2] + ", " + color_int_values[3] + ", " + color_bool_values[1] + ", " + color_int_values[4] + ", " + color_int_values[5] + ", " + color_bool_values[2] + ", " + color_int_values[8] + ", " + color_int_values[6] + ", " + color_int_values[7] + ", " + color_bool_values[3];
					
					System.out.println("Values: " + added);							
					
					PixelMask = thresher.getdrawfillMask();
					
					thresher.drawBinary(PixelMask, imp_crop[i]);
					
					thresher.close();
					
					String name_export = StartFileDirectory + "/Export/" + StartFileName + "_" + export + "_binary.tif";
					
					
					IJ.saveAs(imp_crop[i], "tiff", name_export);
					//imp_crop[i].close();
					//crop_counter++;
					
					//Fläche berechnen
												
					double real_area_complete = 300000;
					
					DecimalFormat df = new DecimalFormat("#.###");
					double measure_counter = 0;
					
					for(int l = 0; l < PixelMask.length; l++){
						if (PixelMask[l]!=0){
							//fill
							measure_counter++;
						}
					}
					
					double RealFraction = (100 * measure_counter) / PixelMask.length;
					
					double real_area = real_area_complete/100*RealFraction;
					
					System.out.println("PixelMask.length: " + PixelMask.length + "\nMeasure_Counter: " + measure_counter + "\nRealFraction: " + RealFraction + "\nArea: " + real_area);
					
					String pixel_aFraction_df = df.format(RealFraction); 
					String real_Fraction_df = df.format(real_area);
					
					String real_Fraction_complete = real_Fraction_df + " cm²";
					
					if(RealFraction == 0) {
						real_Fraction_complete = "Konnte nicht ausgerechnet werden.";
					}
					
					String text = "Prozentuale Fl\u00e4che: " + pixel_aFraction_df + " %" + "\nReale Fl\u00e4che: " + real_Fraction_complete;
					String filename = StartFileName + "_" + export + "_Fl\u00e4chenexport" + ".txt";
					
					summary = summary + "\n" + export + "|" + real_Fraction_df + "|" + pixel_aFraction_df;
					name_export_summary = StartFileName + "_summary.txt";
					
					/*Opener opener_topixel = new Opener();
					ImagePlus imp_a_pixelvalues = opener_topixel.openImage(export_name);
					
					if(imp_a_pixelvalues != null){
						String[] rgb_values = extractPixelValues(imp_a_pixelvalues, PixelMask);
						
						StringBuilder sb_pixel = new StringBuilder();
				
						for(int l = 0; l < rgb_values.length; l++){
							sb_pixel.append(rgb_values[l]);
						}
						export_pixelvalues = sb_pixel.toString();
						filename_pixelvalues = StartFileName + "_" + export + "_Einzelpixel_Export" + ".txt";
					}*/
					
					imp_crop[i].close();
					crop_counter++;
					
					//Hier Text-Dateien speichern
					
					saveData(text, filename);
					//saveData(export_pixelvalues, filename_pixelvalues);
					
					
				}
			}
			
			//imp2.setRoi(new PointRoi(pointsX, pointsY, pointsY.length));
			//imp2.setRoi(new PointRoi(xpoints_flaechen, ypoints_flaechen, ypoints_flaechen.length));
			//IJ.run("Scale to Fit", "");
			
			//imp_export.show();
			
			saveData(summary, name_export_summary);
			
			imp2.close();
			problem = false;
		}
	}
	
	public void loadimagesReferencePoints(int CountImages, int project){
		
		imp = IJ.getImage();
		ImageProcessor originalip = imp.getProcessor();
		
		StartFileDirectory = getDirectory(imp);
		StartFileName = getFileName(imp);
		
		String StartFilePathName = StartFileDirectory + StartFileName;
				
		ImagePlus imp2 = new ImagePlus();
		imp2.setImage(imp);
		imp.close();
		imp2.show();
		
		int height = imp.getHeight();
		int width = imp.getWidth();
		
		IJ.setTool("multipoint");
		
		if(project == AKWHA_I) {
			
			InputDialog inputreferencepoints = new InputDialog("Input", "Please enter Reference-Points for AKWHA I Project\nPoints are movable\nDelete with ALT + Click on Point", "AKWHA", imp2); //Überprüfen ob 12 Punkte eingegeben wurden! Über anderen Input über "Points"
			inputreferencepoints.show();
			
			Point[] points;
			Roi rois;
			
			rois = imp2.getRoi();
			
			if( rois instanceof PointRoi ){
				points = rois.getContainedPoints();
				//System.out.println("Length: " + points.length);
				
				StringBuilder sb = new StringBuilder();
				
				for(int i = 0; i < points.length; i++){
					for(int k = 0; k < points.length; k++) {
						
						int x_0 = points[i].x;
						int y_0 = points[i].y;
						
						int x_1 = points[k].x;
						int y_1 = points[k].y;
						
						Line line = new Line(x_0, y_0, x_1, y_1);
						
						String toappend = String.valueOf(line.getLength()) + ":" + String.valueOf(x_0) + "," + String.valueOf(y_0) + ";" + String.valueOf(x_1) + "," + String.valueOf(y_1) + "-";
						//System.out.println(toappend);
						sb.append(toappend);
						
					}
				}
				
				String[] distances = extractDistances(sb);
				
				Arrays.sort(distances);
				
				int counter = 1;
				
				int a = 0; 
				int b = 1; 
				int c = 2;
				
				for(int i = 12; i < 24; i+=2) {
					
					/*Ablauf:
					- Pixel/Meter ausrechnen
					- Umrechnung a,b,c in Meter zu Pixel (in Abhängigkeit von Counter und eigene Klasse?!)
					- setTriangles mit den Werten aufrufen (Typ und Höhe von ImagePlus in Pixel)*/
					
					String[] split_length = distances[i].split(":");
					
					double length = Double.parseDouble(split_length[0]);

					String[] split_xy = split_length[1].split(";");
					
					String[] split_xy0 = split_xy[0].split(",");
					String[] split_xy1 = split_xy[1].split(",");
					
					int x1 = Integer.parseInt(split_xy0[0]);
					int y1 = Integer.parseInt(split_xy0[1]);
					int x2 = Integer.parseInt(split_xy1[0]);
					int y2 = Integer.parseInt(split_xy1[1]);
					
					double Pixel_pro_Meter = length / getlength(project, counter, c);
					
					double d_a = Pixel_pro_Meter * getlength(project, counter, a);
					double d_b = Pixel_pro_Meter * getlength(project, counter, b);
					double d_c = Pixel_pro_Meter * getlength(project, counter, c);
					
					//System.out.println(Pixel_pro_Meter + "\n" + d_a + "\n" + d_b + "\n" + d_c);
					
					int type = 0;
					
					if(counter == 1 || counter == 4 || counter == 5) {
						type = 1;
					}
					if(counter == 3 || counter == 6) {
						type = 2;
					}
					if(counter == 2) {
						type = 3;
					}
					
					setTriangles(counter, height, d_a, d_b, d_c, x1, y1, x2, y2, type);
					
					counter++;
				}
				
				/*
				- Punkte sortieren
				- Punkte in PolygonRoi speichern
				- PolygonRoi anzeigen mit drawPixels()
				*/
				
				int[] orderedXPoints = reorderXY(xpoints);
				int[] orderedYPoints = reorderXY(ypoints);
				int amount_points = orderedXPoints.length;
				
				//imp2.setRoi(new PolygonRoi(orderedXPoints,orderedYPoints,amount_points,Roi.POLYGON));
				
				//imp2.setRoi(new PointRoi(orderedXPoints, orderedYPoints, amount_points));
				
				/*for(int i = 0; i < amount_points; i++) {
					xpoints_flaechen[i] = orderedXPoints[i];
					ypoints_flaechen[i] = orderedYPoints[i];
					point_counter++;
				}*/
				
				setRectangles(height, project);
				
				int[] pointsX = new int[points_up_counter+points_down_counter];
				int[] pointsY = new int[points_up_counter+points_down_counter];
				
				int[] xpoints_polygon = new int[4];
				int[] ypoints_polygon = new int[4];
				int polygon_counter = 0;
				int polygon_column = 0;
				int export_polygon = 1; //AKHWA I fängt bei 1 an, AKWHA II geht dann mit 65 weiter
				
				ImagePlus imp_export = imp2;
				ImagePlus[] imp_crop = new ImagePlus[ypoints_flaechen.length];
				
				String text = "";
				String filename = "";
				
				String export_pixelvalues = "";
				String filename_pixelvalues = "";
				
				imp2.setRoi(new PointRoi(xpoints_flaechen, ypoints_flaechen, ypoints_flaechen.length));
				IJ.run("Scale to Fit", "");
				
				for(int i = 0; i < ypoints_flaechen.length - 5/*i<15*/; i++) {
					//System.out.println("Punkt:" + i + "," + xpoints_flaechen[i] + "," + ypoints_flaechen[i]);
					System.out.println(xpoints_flaechen[i] + "," + ypoints_flaechen[i]);
					
					imp_crop[i] = new ImagePlus();
					imp_crop[i].setImage(imp2);
					
					/*PolygonRoi mit 4 Punkten:
						- x[i] und y[i], x[i+1] und y[i+1], x[i+5] und y[i+5], x[i+6] und y[i+6]
						- 4 mal und dann eins auslassen 
						*/
					
					polygon_counter++;
					if(polygon_counter < 5) {
						if(polygoncolumn(polygon_column)) {
							
							imp_crop[i].show();
							
							xpoints_polygon[0] = xpoints_flaechen[i];
							ypoints_polygon[0] = ypoints_flaechen[i];
							xpoints_polygon[1] = xpoints_flaechen[i+1];
							ypoints_polygon[1] = ypoints_flaechen[i+1];
							xpoints_polygon[2] = xpoints_flaechen[i+6];
							ypoints_polygon[2] = ypoints_flaechen[i+6];
							xpoints_polygon[3] = xpoints_flaechen[i+5];
							ypoints_polygon[3] = ypoints_flaechen[i+5];	
							
							double x1 = (double)xpoints_polygon[0];
							double y1 = (double)ypoints_polygon[0];
							double x2 = (double)xpoints_polygon[3];
							double y2 = (double)ypoints_polygon[3];
							double x3 = (double)xpoints_polygon[1];
							double y3 = (double)ypoints_polygon[1];
							double x4 = (double)xpoints_polygon[2];
							double y4 = (double)ypoints_polygon[2];
														
							double distance_scale_up = Abstand_PunktPunkt(x1, y1, x2, y2);
							double distance_scale_down = Abstand_PunktPunkt(x3, y3, x4, y4);
							double distance_scale_left = Abstand_PunktPunkt(x2, y2, x4, y4);
							double distance_scale_right = Abstand_PunktPunkt(x3, y3, x1, y1);
														
							double scale_CMPx_up = 600/distance_scale_up;
							double scale_CMPx_down = 600/distance_scale_down;
							double scale_CMPx_left = 1500/distance_scale_left;
							double scale_CMPx_right = 1500/distance_scale_right;
							
							scale_CMPx = (scale_CMPx_up + scale_CMPx_down + scale_CMPx_left + scale_CMPx_right)/4;
							
							System.out.println("Punkte: " + x1 + "," + y1 + "," + x2 + "," + y2);
							
							System.out.println("Distance_scale_up: " + distance_scale_up + "\nDistance_scale_down: " + distance_scale_down + "\ndistance_scale_left: " + distance_scale_left + "\ndistance_scale_right: " + distance_scale_right);
							
							System.out.println("scale_CMPx_up: " + scale_CMPx_up + "\nscale_CMPx_down: " + scale_CMPx_down + "\nscale_CMPx_left: " + scale_CMPx_left + "\nscale_CMPx_right: " + scale_CMPx_right);
														
							Roi polygonroi = new PolygonRoi(xpoints_polygon,ypoints_polygon,ypoints_polygon.length,Roi.POLYGON);
							
							imp_crop[i].setRoi(polygonroi);
							
							IJ.run(imp_crop[i], "Crop", "");
							IJ.setBackgroundColor(0, 0, 0);
							IJ.run(imp_crop[i], "Clear Outside", "");
							
							Roi newmask = imp_crop[i].getRoi();
							
							String name = StartFileDirectory + "/Export/" + StartFileName + "_" + export_polygon;
							
							String export_name = name + ".tif";
							
							IJ.saveAs(imp_crop[i], "tiff", name);
							
							int crop_height = imp_crop[i].getHeight();
							int crop_width = imp_crop[i].getWidth();
							
							ByteProcessor bp_selection = createRoiMask(imp_crop[i]);
							
							byte[] selection = (byte[])bp_selection.getPixels();
							
							int invers = 0;
							
							for(int k = 0; k < selection.length; k++) {
								if(selection[k] == 0) {
									invers++;
								}
							}
							
							System.out.println("selection_length: " + selection.length);
							System.out.println("selection_invers: " + invers);
							
							ColorThresholderExtended thresher = new ColorThresholderExtended(); //Color Threshold Fenster öffnen
							
							Integer[] input_int = new Integer[9];
							input_int[0] = minHue;
							input_int[1] = maxHue;
							input_int[2] = minSat;
							input_int[3] = maxSat;
							input_int[4] = minBri;
							input_int[5] = maxBri;
							input_int[6] = mode;
							input_int[7] = colorSpace;
							input_int[8] = method_int;
							
							Boolean[] input_bool = new Boolean[4];
							input_bool[0] = bandPassH;
							input_bool[1] = bandPassS;
							input_bool[2] = bandPassB;
							input_bool[3] = darkBackground;
							
							//thresher.setStringValue(method);
							thresher.setIntValues(input_int);
							thresher.setBoolValues(input_bool);
							
							InputDialog inputcolorthresher = new InputDialog("Input", "Please adjust Threshold", "Threshold", imp_crop[i]);
							inputcolorthresher.show();
							
							//color_string_value = thresher.getStringValue();
							color_int_values = thresher.getIntValues();
							color_bool_values = thresher.getBoolValues();
							
							//method = color_string_value;
							method = "DEFAULT";
							minHue = color_int_values[0];
							maxHue = color_int_values[1];
							minSat = color_int_values[2];
							maxSat = color_int_values[3];
							minBri = color_int_values[4];
							maxBri = color_int_values[5];
							mode = color_int_values[6];
							colorSpace = color_int_values[7];
							method_int = color_int_values[8];
							bandPassH = color_bool_values[0];
							bandPassS = color_bool_values[1];
							bandPassB = color_bool_values[2];
							darkBackground = color_bool_values[3];
							
							String added = color_int_values[0] + ", " + color_int_values[1] + ", " + color_bool_values[0] + ", " + color_int_values[2] + ", " + color_int_values[3] + ", " + color_bool_values[1] + ", " + color_int_values[4] + ", " + color_int_values[5] + ", " + color_bool_values[2] + ", " + color_int_values[8] + ", " + color_int_values[6] + ", " + color_int_values[7] + ", " + color_bool_values[3];
							
							System.out.println("Values: " + added);							
							
							PixelMask = thresher.getdrawfillMask();
							
							thresher.drawBinary(PixelMask, imp_crop[i]);
							
							thresher.close();
							
							String name_export = StartFileDirectory + "/Export/" + StartFileName + "_" + export_polygon + "_binary.tif";
							
							IJ.saveAs(imp_crop[i], "tiff", name_export);
							
							//Fläche berechnen
														
							double real_area_complete = 0;
							DecimalFormat df = new DecimalFormat("#.#####");
							double measure_counter = 0;
							
							if(scale_CMPx != 0) {
								real_area_complete = (crop_width * scale_CMPx) * (crop_height * scale_CMPx);
							}
							
							for(int k = 0; k < PixelMask.length; k++){
								if (PixelMask[k]!=0){
									//fill
									measure_counter++;
								}
							}
							//measure_counter = thresholded Pixel
							double PixelFraction = (measure_counter * 100) / (PixelMask.length - invers);
							
							double real_area = real_area_complete-(invers*scale_CMPx*scale_CMPx);
							
							double RealFraction = (real_area * PixelFraction) / 100;
							
							System.out.println("PixelMask.length: " + PixelMask.length + "\nMeasure_Counter: " + measure_counter + "\nPixelFraction: " + PixelFraction + "\nArea: " + real_area + "\nInvers: " + invers);
							
							String pixel_aFraction_df = df.format(PixelFraction); 
							String real_Fraction_df = df.format(RealFraction);
							
							String real_Fraction_complete = real_Fraction_df + " cm²";
							
							if(RealFraction == 0) {
								real_Fraction_complete = "Konnte nicht ausgerechnet werden.";
							}
							
							text = "Prozentuale Fl\u00e4che: " + pixel_aFraction_df + " %" + "\nReale Fl\u00e4che: " + real_Fraction_complete;
							filename = StartFileName + "_" + export_polygon + "_Fl\u00e4chenexport" + ".txt";	
							
							Opener opener_topixel = new Opener();
							ImagePlus imp_a_pixelvalues = opener_topixel.openImage(export_name);
							
							if(imp_a_pixelvalues != null){
								String[] rgb_values = extractPixelValues(imp_a_pixelvalues, PixelMask);
								
								StringBuilder sb_pixel = new StringBuilder();
						
								for(int l = 0; l < rgb_values.length; l++){
									sb_pixel.append(rgb_values[l]);
								}
								export_pixelvalues = sb_pixel.toString();
								filename_pixelvalues = StartFileName + "_" + export_polygon + "_Einzelpixel_Export" + ".txt";
							}
							
							export_polygon++;
							
							imp_crop[i].close();
							
						}
						
				    } else {
						polygon_counter = 0;
						polygon_column++;
					}
					
					//Hier Text-Dateien speichern
					
					saveData(text, filename);
					saveData(export_pixelvalues, filename_pixelvalues);
					
				}
				
				//imp2.setRoi(new PointRoi(pointsX, pointsY, pointsY.length));
				//imp2.setRoi(new PointRoi(xpoints_flaechen, ypoints_flaechen, ypoints_flaechen.length));
				//IJ.run("Scale to Fit", "");
				
				//imp_export.show();
				
			}
			imp2.close();
			problem = false;
		}
		
		if(project == AKWHA_II) {
			
			InputDialog inputreferencepoints = new InputDialog("Input", "Please enter Reference-Points for AKWHA II Project\nPoints are movable\nDelete with ALT + Click on Point", "AKWHA", imp2); //Überprüfen ob 12 Punkte eingegeben wurden! Über anderen Input über "Points"
			inputreferencepoints.show();
			
			Point[] points;
			Roi rois;
			
			rois = imp2.getRoi();
			
			if( rois instanceof PointRoi ){
				points = rois.getContainedPoints();
				System.out.println("Length: " + points.length);
				
				StringBuilder sb = new StringBuilder();
				
				for(int i = 0; i < points.length; i++){
					for(int k = 0; k < points.length; k++) {
						
						int x_0 = points[i].x;
						int y_0 = points[i].y;
						
						int x_1 = points[k].x;
						int y_1 = points[k].y;
						
						Line line = new Line(x_0, y_0, x_1, y_1);
						
						String toappend = String.valueOf(line.getLength()) + ":" + String.valueOf(x_0) + "," + String.valueOf(y_0) + ";" + String.valueOf(x_1) + "," + String.valueOf(y_1) + "-";
						System.out.println(toappend);
						sb.append(toappend);
						
					}
				}
				
				String[] distances = extractDistances(sb);
				
				Arrays.sort(distances);
				
				int counter = 1;
				
				int a = 0; 
				int b = 1; 
				int c = 2;
				
				for(int i = 12; i < 24; i+=2) {
					
					/*Ablauf:
					- Pixel/Meter ausrechnen
					- Umrechnung a,b,c in Meter zu Pixel (in Abhängigkeit von Counter und eigene Klasse?!)
					- setTriangles mit den Werten aufrufen (Typ und Höhe von ImagePlus in Pixel)*/
					
					String[] split_length = distances[i].split(":");
					
					double length = Double.parseDouble(split_length[0]);

					String[] split_xy = split_length[1].split(";");
					
					String[] split_xy0 = split_xy[0].split(",");
					String[] split_xy1 = split_xy[1].split(",");
					
					int x1 = Integer.parseInt(split_xy0[0]);
					int y1 = Integer.parseInt(split_xy0[1]);
					int x2 = Integer.parseInt(split_xy1[0]);
					int y2 = Integer.parseInt(split_xy1[1]);
					
					double Pixel_pro_Meter = length / getlength(project, counter, c);
					
					double d_a = Pixel_pro_Meter * getlength(project, counter, a);
					double d_b = Pixel_pro_Meter * getlength(project, counter, b);
					double d_c = Pixel_pro_Meter * getlength(project, counter, c);
					
					System.out.println(Pixel_pro_Meter + "\n" + d_a + "\n" + d_b + "\n" + d_c);
					
					int type = 0;
					
					if(counter == 1 || counter == 3 || counter == 5) {
						type = 1;
					}
					if(counter == 2 || counter == 6) {
						type = 2;
					}
					if(counter == 4) { //hier noch ein anderer Type nötig?
						type = 4;
					}
					
					setTriangles(counter, height, d_a, d_b, d_c, x1, y1, x2, y2, type);
					
					counter++;
				}
				
				/*
				- Punkte sortieren
				- Punkte in PolygonRoi speichern
				- PolygonRoi anzeigen mit drawPixels()
				*/
				
				int[] orderedXPoints = reorderXY(xpoints);
				int[] orderedYPoints = reorderXY(ypoints);
				int amount_points = orderedXPoints.length;
				
				//Gefundenen Ecken anzeigen lassen:
				imp2.setRoi(new PointRoi(orderedXPoints, orderedYPoints, amount_points));
				
				setRectangles(height, project);
				
				int[] pointsX = new int[points_up_counter+points_down_counter];
				int[] pointsY = new int[points_up_counter+points_down_counter];
				
				int[] xpoints_polygon = new int[4];
				int[] ypoints_polygon = new int[4];
				int polygon_counter = 0;
				int polygon_column = 0;
				int export_polygon = 65; //AKHWA II fängt bei 65 an, geht nach AKWHA I weiter
				
				ImagePlus imp_export = imp2;
				ImagePlus[] imp_crop = new ImagePlus[ypoints_flaechen.length];
				
				String text = "";
				String filename = "";
				
				String export_pixelvalues = "";
				String filename_pixelvalues = "";
				
				//Gefundene Flächen anzeigen lassen:
				imp2.setRoi(new PointRoi(xpoints_flaechen, ypoints_flaechen, ypoints_flaechen.length));
				IJ.run("Scale to Fit", "");
				
				for(int i = 0; i < ypoints_flaechen.length - 5/*i<15*/; i++) {
					//System.out.println("Punkt:" + i + "," + xpoints_flaechen[i] + "," + ypoints_flaechen[i]);
					System.out.println(xpoints_flaechen[i] + "," + ypoints_flaechen[i]);
					
					imp_crop[i] = new ImagePlus();
					imp_crop[i].setImage(imp2);	
					
					/*PolygonRoi mit 4 Punkten:
						- x[i] und y[i], x[i+1] und y[i+1], x[i+5] und y[i+5], x[i+6] und y[i+6]
						- 4 mal und dann eins auslassen 
						*/
					
					polygon_counter++;
					if(polygon_counter < 5) {
						if(polygoncolumn(polygon_column)) {
							
							imp_crop[i].show();
							
							xpoints_polygon[0] = xpoints_flaechen[i];
							ypoints_polygon[0] = ypoints_flaechen[i];
							xpoints_polygon[1] = xpoints_flaechen[i+1];
							ypoints_polygon[1] = ypoints_flaechen[i+1];
							xpoints_polygon[2] = xpoints_flaechen[i+6];
							ypoints_polygon[2] = ypoints_flaechen[i+6];
							xpoints_polygon[3] = xpoints_flaechen[i+5];
							ypoints_polygon[3] = ypoints_flaechen[i+5];	
							
							double x1 = (double)xpoints_polygon[0];
							double y1 = (double)ypoints_polygon[0];
							double x2 = (double)xpoints_polygon[3];
							double y2 = (double)ypoints_polygon[3];
							double x3 = (double)xpoints_polygon[1];
							double y3 = (double)ypoints_polygon[1];
							double x4 = (double)xpoints_polygon[2];
							double y4 = (double)ypoints_polygon[2];
														
							double distance_scale_up = Abstand_PunktPunkt(x1, y1, x2, y2);
							double distance_scale_down = Abstand_PunktPunkt(x3, y3, x4, y4);
							double distance_scale_left = Abstand_PunktPunkt(x2, y2, x4, y4);
							double distance_scale_right = Abstand_PunktPunkt(x3, y3, x1, y1);
														
							double scale_CMPx_up = 600/distance_scale_up;
							double scale_CMPx_down = 600/distance_scale_down;
							double scale_CMPx_left = 1500/distance_scale_left;
							double scale_CMPx_right = 1500/distance_scale_right;
							
							scale_CMPx = (scale_CMPx_up + scale_CMPx_down + scale_CMPx_left + scale_CMPx_right)/4;
							
							System.out.println("Punkte: " + x1 + "," + y1 + "," + x2 + "," + y2);
							
							System.out.println("Distance_scale_up: " + distance_scale_up + "\nDistance_scale_down: " + distance_scale_down + "\ndistance_scale_left: " + distance_scale_left + "\ndistance_scale_right: " + distance_scale_right);
							
							System.out.println("scale_CMPx_up: " + scale_CMPx_up + "\nscale_CMPx_down: " + scale_CMPx_down + "\nscale_CMPx_left: " + scale_CMPx_left + "\nscale_CMPx_right: " + scale_CMPx_right);
														
							Roi polygonroi = new PolygonRoi(xpoints_polygon,ypoints_polygon,ypoints_polygon.length,Roi.POLYGON);
							
							imp_crop[i].setRoi(polygonroi);
							
							IJ.run(imp_crop[i], "Crop", "");
							IJ.setBackgroundColor(0, 0, 0);
							IJ.run(imp_crop[i], "Clear Outside", "");
							
							Roi newmask = imp_crop[i].getRoi();
							
							String name = StartFileDirectory + "/Export/" + StartFileName + "_" + export_polygon;
							
							String export_name = name + ".tif";
							
							IJ.saveAs(imp_crop[i], "tiff", name);
							
							int crop_height = imp_crop[i].getHeight();
							int crop_width = imp_crop[i].getWidth();
							
							ByteProcessor bp_selection = createRoiMask(imp_crop[i]);
							
							byte[] selection = (byte[])bp_selection.getPixels();
							
							int invers = 0;
							
							for(int k = 0; k < selection.length; k++) {
								if(selection[k] == 0) {
									invers++;
								}
							}
							
							System.out.println("selection_length: " + selection.length);
							System.out.println("selection_invers: " + invers);
							
							ColorThresholderExtended thresher = new ColorThresholderExtended(); //Color Threshold Fenster öffnen
							
							Integer[] input_int = new Integer[9];
							input_int[0] = minHue;
							input_int[1] = maxHue;
							input_int[2] = minSat;
							input_int[3] = maxSat;
							input_int[4] = minBri;
							input_int[5] = maxBri;
							input_int[6] = mode;
							input_int[7] = colorSpace;
							input_int[8] = method_int;
							
							Boolean[] input_bool = new Boolean[4];
							input_bool[0] = bandPassH;
							input_bool[1] = bandPassS;
							input_bool[2] = bandPassB;
							input_bool[3] = darkBackground;
							
							//thresher.setStringValue(method);
							thresher.setIntValues(input_int);
							thresher.setBoolValues(input_bool);
							
							InputDialog inputcolorthresher = new InputDialog("Input", "Please adjust Threshold", "Threshold", imp_crop[i]);
							inputcolorthresher.show();
							
							//color_string_value = thresher.getStringValue();
							color_int_values = thresher.getIntValues();
							color_bool_values = thresher.getBoolValues();
							
							//method = color_string_value;
							method = "DEFAULT";
							minHue = color_int_values[0];
							maxHue = color_int_values[1];
							minSat = color_int_values[2];
							maxSat = color_int_values[3];
							minBri = color_int_values[4];
							maxBri = color_int_values[5];
							mode = color_int_values[6];
							colorSpace = color_int_values[7];
							method_int = color_int_values[8];
							bandPassH = color_bool_values[0];
							bandPassS = color_bool_values[1];
							bandPassB = color_bool_values[2];
							darkBackground = color_bool_values[3];
							
							String added = color_int_values[0] + ", " + color_int_values[1] + ", " + color_bool_values[0] + ", " + color_int_values[2] + ", " + color_int_values[3] + ", " + color_bool_values[1] + ", " + color_int_values[4] + ", " + color_int_values[5] + ", " + color_bool_values[2] + ", " + color_int_values[8] + ", " + color_int_values[6] + ", " + color_int_values[7] + ", " + color_bool_values[3];
							
							System.out.println("Values: " + added);							
							
							PixelMask = thresher.getdrawfillMask();
							
							thresher.drawBinary(PixelMask, imp_crop[i]);
							
							thresher.close();
							
							String name_export = StartFileDirectory + "/Export/" + StartFileName + "_" + export_polygon + "_binary.tif";
							
							IJ.saveAs(imp_crop[i], "tiff", name_export);
							
							//Fläche berechnen
														
							double real_area_complete = 0;
							DecimalFormat df = new DecimalFormat("#.#####");
							double measure_counter = 0;
							
							if(scale_CMPx != 0) {
								real_area_complete = (crop_width * scale_CMPx) * (crop_height * scale_CMPx);
							}
							
							for(int k = 0; k < PixelMask.length; k++){
								if (PixelMask[k]!=0){
									//fill
									measure_counter++;
								}
							}
							//measure_counter = thresholded Pixel
							double PixelFraction = (measure_counter * 100) / (PixelMask.length - invers);
							
							double real_area = real_area_complete-(invers*scale_CMPx*scale_CMPx);
							
							double RealFraction = (real_area * PixelFraction) / 100;
							
							System.out.println("PixelMask.length: " + PixelMask.length + "\nMeasure_Counter: " + measure_counter + "\nPixelFraction: " + PixelFraction + "\nArea: " + real_area + "\nInvers: " + invers);
							
							String pixel_aFraction_df = df.format(PixelFraction); 
							String real_Fraction_df = df.format(RealFraction);
							
							String real_Fraction_complete = real_Fraction_df + " cm²";
							
							if(RealFraction == 0) {
								real_Fraction_complete = "Konnte nicht ausgerechnet werden.";
							}
							
							text = "Prozentuale Fl\u00e4che: " + pixel_aFraction_df + " %" + "\nReale Fl\u00e4che: " + real_Fraction_complete;
							filename = StartFileName + "_" + export_polygon + "_Fl\u00e4chenexport" + ".txt";	
							
							Opener opener_topixel = new Opener();
							ImagePlus imp_a_pixelvalues = opener_topixel.openImage(export_name);
							
							if(imp_a_pixelvalues != null){
								String[] rgb_values = extractPixelValues(imp_a_pixelvalues, PixelMask);
								
								StringBuilder sb_pixel = new StringBuilder();
						
								for(int l = 0; l < rgb_values.length; l++){
									sb_pixel.append(rgb_values[l]);
								}
								export_pixelvalues = sb_pixel.toString();
								filename_pixelvalues = StartFileName + "_" + export_polygon + "_Einzelpixel_Export" + ".txt";
							}
							
							export_polygon++;
							
							imp_crop[i].close();
							
						}
						
				    } else {
						polygon_counter = 0;
						polygon_column++;
					}
					
					//Hier Text-Dateien speichern
					
					saveData(text, filename);
					saveData(export_pixelvalues, filename_pixelvalues);
					
				}
				
				//imp2.setRoi(new PointRoi(pointsX, pointsY, pointsY.length));
				//imp2.setRoi(new PointRoi(xpoints_flaechen, ypoints_flaechen, ypoints_flaechen.length));
				//IJ.run("Scale to Fit", "");
				
				//imp_export.show();
				
			}
			imp2.close();
			problem = false;
		
		}
	}
	
	public void setTriangles(int counter, int height, double a, double b, double c, int n_x1, int n_y1, int n_x2, int n_y2, int type) {
		/*
		Ablauf:
		- je nach Typ Koordinaten anpassen (3 und 2) 
		- Steigung der Geraden zwischen x1y1 und x2y2
			-> Steigung positiv, negativ oder 0
		- Winkel dieser Steigung
		- Winkel alpha im Punkt A
		- Winkel beta im Punkt B
		- in Abhängigkeit vom Typ Steigungen m_AC und m_BC berechnen
		- Y-Achsenschnittpunkt nA und nB berechnen
		- Schnittpunkt xSyS berechnen
		- je nach Typ yS anpassen in yS_real (3 und 2)
		- Punkte in Arrays speichern*/
		
		double x1 = 0;
		double x2 = 0;
		double y1 = 0;
		double y2 = 0;
		
		double m_temp = 0;
		double m;
		double m_Winkel;
		double alpha;
		double beta;
		
		double m_A;
		double m_B;
		double n_A;
		double n_B;
		
		double Sx;
		double Sy;
		
		double Winkel_A;
		double Winkel_B;
		
		double m_Winkel_Abs;
		
		double Steigungswinkel_A = 0;
		double Steigungswinkel_B = 0;
		
		if(type == 1) {
			//Typ 1
			x1 = n_x1;
			x2 = n_x2;
			y1 = n_y1;
			y2 = n_y2;
		} 
		if(type == 2) {
			//Typ 2
			x1 = n_x1;
			x2 = n_x2;
			y1 = height - n_y1;
			y2 = height - n_y2;
		}
		if(type == 3) {
			//Typ 3
			if(n_x1 == n_x2) {
				m_temp = 0;
			} else {
				m_temp = (n_y2-n_y1)/(n_x2-n_x1);
			}
			
			if(m_temp < 0) {
				x1 = n_x2;
				x2 = n_x1;
				y1 = height - n_y2;
				y2 = height - n_y1;
			}
			if(m_temp >= 0) {
				x1 = n_x1;
				x2 = n_x2;
				y1 = height - n_y1;
				y2 = height - n_y2;
			}
		}
		if(type == 4) {
			//Typ 4
			if(n_x1 == n_x2) {
				m_temp = 0;
			} else {
				m_temp = (n_y2-n_y1)/(n_x2-n_x1);
			}
			
			if(m_temp < 0) {
				x1 = n_x2;
				x2 = n_x1;
				y1 = height - n_y2;
				y2 = height - n_y1;
			}
			if(m_temp >= 0) {
				x1 = n_x1;
				x2 = n_x2;
				y1 = height - n_y1;
				y2 = height - n_y2;
			}
		}
		
		m = (y2-y1)/(x2-x1);
		
		m_Winkel = Math.toDegrees(Math.atan(m));
		
		alpha = Math.toDegrees(Math.acos((b*b+c*c-a*a)/(2*b*c)));
		
		beta = Math.toDegrees(Math.acos((a*a+c*c-b*b)/(2*a*c)));
		
		//System.out.println("\n" + counter);
	
		System.out.println(counter + ": Steigung: " + m + "\nWinkel: " + m_Winkel + "\nalpha: " + alpha + "\nbeta: " + beta);
		System.out.println(x1 + "," + y1 + "," + x2 + "," + y2);
		
		//Steigungen m_AC und m_BC
		
		if(type == 1) {
			if(m > 0) { //Steigung (m) positiv (Typ 1 oder 2)
				Winkel_A = alpha;
				Winkel_B = 180 - beta;
				
				Steigungswinkel_A = 180-(180-m_Winkel-Winkel_A);
				Steigungswinkel_B = 180-(180-m_Winkel-Winkel_B);
			}
			if(m < 0) { //Steigung (m) negativ (Typ 1 oder 2)
				Winkel_A = 180 - alpha;
				Winkel_B = beta;
				
				m_Winkel_Abs = Math.abs(m_Winkel);
				
				Steigungswinkel_A = 180-m_Winkel_Abs-Winkel_A;
				Steigungswinkel_B = 180-m_Winkel_Abs-Winkel_B;
			}
			if(m == 0) { //Steigung (m) 0 (Typ 1 oder 2)
				Steigungswinkel_A = alpha;
				Steigungswinkel_B = 180 - beta;
			}
		}
		if(type == 2) {
			if(m > 0) { //Steigung (m) positiv (Typ 1 oder 2)
				Winkel_A = alpha;
				Winkel_B = 180 - beta;
				
				Steigungswinkel_A = 180-(180-m_Winkel-Winkel_A);
				Steigungswinkel_B = 180-(180-m_Winkel-Winkel_B);
			}
			if(m < 0) { //Steigung (m) negativ (Typ 1 oder 2)
				Winkel_A = 180 - alpha;
				Winkel_B = beta;
				
				m_Winkel_Abs = Math.abs(m_Winkel);
				
				Steigungswinkel_A = 180-m_Winkel_Abs-Winkel_A;
				Steigungswinkel_B = 180-m_Winkel_Abs-Winkel_B;
			}
			if(m == 0) { //Steigung (m) 0 (Typ 1 oder 2)
				Steigungswinkel_A = alpha;
				Steigungswinkel_B = 180 - beta;
			}
		}
		if(type == 3) {
			if(m > 0) { //Steigung (m) positiv (Typ 3)
				Winkel_A = alpha;
				Winkel_B = beta;
				
				m_Winkel_Abs = Math.abs(m_Winkel);
				
				Steigungswinkel_A = 180-(180-m_Winkel_Abs)-Winkel_A;
				Steigungswinkel_B = 180-(180-m_Winkel_Abs-Winkel_B);
			}
			if(m < 0) { //Steigung (m) negativ (Typ 3)
				Winkel_A = alpha;
				Winkel_B = beta;
				
				m_Winkel_Abs = Math.abs(m_Winkel);
				
				Steigungswinkel_A = 180-m_Winkel_Abs-Winkel_A;
				Steigungswinkel_B = 180-(180-Winkel_B-(180-m_Winkel_Abs));
			}
			if(m == 0) { //Steigung (m) 0 (Typ 1 oder 2)
				Steigungswinkel_A = alpha;
				Steigungswinkel_B = 180 - beta;
			}
		}
		if(type == 4) {
			if(m > 0) { //Steigung (m) positiv (Typ 4)
				Winkel_A = alpha;
				Winkel_B = beta;
				
				m_Winkel_Abs = Math.abs(m_Winkel);
				
				Steigungswinkel_A = 180-(180-m_Winkel_Abs-Winkel_B);
				Steigungswinkel_B = 180-(180-m_Winkel_Abs)-Winkel_A;
			}
			if(m < 0) { //Steigung (m) negativ (Typ 4)
				Winkel_A = beta;
				Winkel_B = alpha;
				
				m_Winkel_Abs = Math.abs(m_Winkel);
				
				Steigungswinkel_A = 180-(m_Winkel_Abs-Winkel_A);
				Steigungswinkel_B = 180-m_Winkel_Abs-Winkel_B;
			}
			if(m == 0) { //Steigung (m) 0
				Steigungswinkel_A = alpha;
				Steigungswinkel_B = 180 - beta;
			}
		}
		
		m_A = Math.tan(Math.toRadians(Steigungswinkel_A));
		m_B = Math.tan(Math.toRadians(Steigungswinkel_B));
		
		n_A = y1 - m_A*x1;
		n_B = y2 - m_B*x2;
		
		System.out.println("Steigungswinkel_A: " + Steigungswinkel_A + "\nSteigungswinkel_B: " + Steigungswinkel_B);
		System.out.println("Steigung AC: " + m_A + "\nSteigung BC: " + m_B);
		System.out.println("y-Schnitt AC: " + n_A + "\ny-Schnitt BC: " + n_B);
		
		Sx = Schnittpunkt_X(n_A, n_B, m_A, m_B);
		
		if(type == 1) {
			//Typ 1
			Sy = Schnittpunkt_Y(n_A, n_B, m_A, m_B);
		} else {
			//Typ 2 und 3
			double d_height = (double)height;
			Sy = d_height - Schnittpunkt_Y(n_A, n_B, m_A, m_B);
		}
		
		xpoints[counter-1] = (int)Sx;
		ypoints[counter-1] = (int)Sy;
		
		System.out.println("Sx: " + Sx + "\nSy: " + Sy + "\n");
		
	}	
	
	public void setRectangles(int height, int project) {
		if(project == AKWHA_I) {
			/*
			Ablauf:
			- Steigung und Steigungswinkel
			- Länge der Geraden
			- Abstände der Versuchsparzellen umrechnen in Pixel
				- Gerade zwischen 1 und 5 
					Punkte nach x sortieren und in points_1_5
				- Gerade zwischen 2 und 3
					Punkte nach x sortieren und in points_2_3
				- Gerade zwischen 1 und 2
					Punkte nach y sortieren und in points_1_2
				- Gerade zwischen 3 und 5
					Punkte nach y sortieren und in points_3_5
				- Gerade zwischen 4 und 5
					Punkte nach x sortieren und in points_4_5
				- Gerade zwischen 3 und 6
					Punkte nach x sortieren und in points_3_6
				- Gerade zwischen 4 und 6
					Punkte nach y sortieren und in points_4_6
			*/	
			
			double x1 = 0;
			double x2 = 0;
			double y1 = 0;
			double y2 = 0;
			
			double m = 0;
			double m_Winkel;
			
			double distance;
			double dist_temp = 0;
			double x_temp = 0;
			double y_temp = 0;
			
			//1-5
			points_up[points_up_counter] = new Point();
			points_up[points_up_counter].x = xpoints[0];
			points_up[points_up_counter].y = ypoints[0];
			points_up_counter++;
			
			x1 = xpoints[0];
			y1 = (double)height - ypoints[0];
			x2 = xpoints[4];
			y2 = (double)height - ypoints[4];
			
			if(x1 == x2) {
				m = 0;
			} else {
				m = Steigung(x1, y1, x2, y2);
			}
			
			//System.out.println("1-5:");
			//System.out.println("Punkte: " + x1 + "," + y1 + "," + x2 + "," + y2);
			
			m_Winkel = Math.toDegrees(Math.atan(m));
			
			distance = Abstand_PunktPunkt(x1, y1, x2, y2);
			
			//System.out.println("Winkel: " + m_Winkel);
			//System.out.println("Distance: " + distance);
			
			//Abstände berechnen
			
			for(int i = 0; i < distances_right.length; i++) {
				
				dist_temp = distances_right[i] * distance / distances_right[11];
				
				//System.out.println("Distance_Temp: " + dist_temp);
		
				x_temp = x1 - Math.cos(Math.toRadians(m_Winkel)) * dist_temp;
				y_temp = (double)height - (y1 - Math.sin(Math.toRadians(m_Winkel)) * dist_temp);
				
				//xpoints_flaechen[point_counter] = (int)x_temp;
				//ypoints_flaechen[point_counter] = (int)y_temp;
				
				points_up[points_up_counter] = new Point();
				points_up[points_up_counter].x = (int)x_temp;
				points_up[points_up_counter].y = (int)y_temp;
				points_up_counter++;
				
				//System.out.println("x_temp: " + x_temp);
				//System.out.println("y_temp: " + y_temp);
				
				//point_counter++;
				
			}
				
			//2-3
			
			points_down[points_down_counter] = new Point();
			points_down[points_down_counter].x = xpoints[1];
			points_down[points_down_counter].y = ypoints[1];
			points_down_counter++;
			
			x1 = xpoints[1];
			y1 = (double)height - ypoints[1];
			x2 = xpoints[2];
			y2 = (double)height - ypoints[2];
			
			if(x1 == x2) {
				m = 0;
			} else {
				m = Steigung(x1, y1, x2, y2);
			}
			
			//System.out.println("2-3:");
			//System.out.println("Punkte: " + x1 + "," + y1 + "," + x2 + "," + y2);
			
			m_Winkel = Math.toDegrees(Math.atan(m));
			
			distance = Abstand_PunktPunkt(x1, y1, x2, y2);
			
			//System.out.println("Winkel: " + m_Winkel);
			//System.out.println("Distance: " + distance);
			
			//Abstände berechnen
			
			for(int i = 0; i < distances_right.length; i++) {
				
				dist_temp = distances_right[i] * distance / distances_right[11];
				
				//System.out.println("Distance_Temp: " + dist_temp);
		
				x_temp = x1 - Math.cos(Math.toRadians(m_Winkel)) * dist_temp;
				y_temp = (double)height - (y1 - Math.sin(Math.toRadians(m_Winkel)) * dist_temp);
				
				//xpoints_flaechen[point_counter] = (int)x_temp;
				//ypoints_flaechen[point_counter] = (int)y_temp;
				
				points_down[points_down_counter] = new Point();
				points_down[points_down_counter].x = (int)x_temp;
				points_down[points_down_counter].y = (int)y_temp;
				points_down_counter++;
				
				//System.out.println("x_temp: " + x_temp);
				//System.out.println("y_temp: " + y_temp);
				
				//point_counter++;
			}
			
			//5-4
			
			x1 = xpoints[4];
			y1 = (double)height - ypoints[4];
			x2 = xpoints[3];
			y2 = (double)height - ypoints[3];
			
			if(x1 == x2) {
				m = 0;
			} else {
				m = Steigung(x1, y1, x2, y2);
			}
			
			//System.out.println("5-4:");
			//System.out.println("Punkte: " + x1 + "," + y1 + "," + x2 + "," + y2);
			
			m_Winkel = Math.toDegrees(Math.atan(m));
			
			distance = Abstand_PunktPunkt(x1, y1, x2, y2);
			
			//System.out.println("Winkel: " + m_Winkel);
			//System.out.println("Distance: " + distance);
			
			//Abstände berechnen
			
			for(int i = 0; i < distances_left.length; i++) {
				
				dist_temp = distances_left[i] * distance / distances_left[10];
				
				//System.out.println("Distance_Temp: " + dist_temp);
		
				x_temp = x1 - Math.cos(Math.toRadians(m_Winkel)) * dist_temp;
				y_temp = (double)height - (y1 - Math.sin(Math.toRadians(m_Winkel)) * dist_temp);
				
				//xpoints_flaechen[point_counter] = (int)x_temp;
				//ypoints_flaechen[point_counter] = (int)y_temp;
				
				points_up[points_up_counter] = new Point();
				points_up[points_up_counter].x = (int)x_temp;
				points_up[points_up_counter].y = (int)y_temp;
				points_up_counter++;
				
				//System.out.println("x_temp: " + x_temp);
				//System.out.println("y_temp: " + y_temp);
				
				//point_counter++;
			}
			
			//3-6
			
			x1 = xpoints[2];
			y1 = (double)height - ypoints[2];
			x2 = xpoints[5];
			y2 = (double)height - ypoints[5];
			
			if(x1 == x2) {
				m = 0;
			} else {
				m = Steigung(x1, y1, x2, y2);
			}
			
			//System.out.println("3-6:");
			//System.out.println("Punkte: " + x1 + "," + y1 + "," + x2 + "," + y2);
			
			m_Winkel = Math.toDegrees(Math.atan(m));
			
			distance = Abstand_PunktPunkt(x1, y1, x2, y2);
			
			//System.out.println("Winkel: " + m_Winkel);
			//System.out.println("Distance: " + distance);
			
			//Abstände berechnen
			
			for(int i = 0; i < distances_left.length; i++) {
				
				dist_temp = distances_left[i] * distance / distances_left[10];
				
				//System.out.println("Distance_Temp: " + dist_temp);
		
				x_temp = x1 - Math.cos(Math.toRadians(m_Winkel)) * dist_temp;
				y_temp = (double)height - (y1 - Math.sin(Math.toRadians(m_Winkel)) * dist_temp);
				
				//xpoints_flaechen[point_counter] = (int)x_temp;
				//ypoints_flaechen[point_counter] = (int)y_temp;
				
				points_down[points_down_counter] = new Point();
				points_down[points_down_counter].x = (int)x_temp;
				points_down[points_down_counter].y = (int)y_temp;
				points_down_counter++;
				
				//System.out.println("x_temp: " + x_temp);
				//System.out.println("y_temp: " + y_temp);
				
				//point_counter++;
			}
			
			//6-4
			
			x1 = xpoints[5];
			y1 = (double)height - ypoints[5];
			x2 = xpoints[3];
			y2 = (double)height - ypoints[3];
			
			if(x1 == x2) {
				m = 0;
			} else {
				m = Steigung(x1, y1, x2, y2);
			}
			
			//System.out.println("6-4:");
			//System.out.println("Punkte: " + x1 + "," + y1 + "," + x2 + "," + y2);
			
			m_Winkel = Math.toDegrees(Math.atan(m));
			
			distance = Abstand_PunktPunkt(x1, y1, x2, y2);
			
			//System.out.println("Winkel: " + m_Winkel);
			//System.out.println("Distance: " + distance);
			
			//Abstände berechnen
			
			for(int i = 0; i < distances_y.length; i++) {
				
				dist_temp = distances_y[i] * distance / distances_y[3];
				
				//System.out.println("Distance_Temp: " + dist_temp);
		
				if(x1 < x2) {
					x_temp = x1 + Math.cos(Math.toRadians(m_Winkel)) * dist_temp;
					y_temp = (double)height - (y1 + Math.sin(Math.toRadians(m_Winkel)) * dist_temp);
				}
				if(x1 > x2) {
					x_temp = x1 - Math.cos(Math.toRadians(m_Winkel)) * dist_temp;
					y_temp = (double)height - (y1 - Math.sin(Math.toRadians(m_Winkel)) * dist_temp);
				}
				if(x1 == x2) {
					///???
				}
				
				//xpoints_flaechen[point_counter] = (int)x_temp;
				//ypoints_flaechen[point_counter] = (int)y_temp;
				
				//System.out.println("x_temp: " + x_temp);
				//System.out.println("y_temp: " + y_temp);
				
				//point_counter++;
			}
			
			//3-5
			
			x1 = xpoints[2];
			y1 = (double)height - ypoints[2];
			x2 = xpoints[4];
			y2 = (double)height - ypoints[4];
			
			if(x1 == x2) {
				m = 0;
			} else {
				m = Steigung(x1, y1, x2, y2);
			}
			
			//System.out.println("3-5:");
			//System.out.println("Punkte: " + x1 + "," + y1 + "," + x2 + "," + y2);
			
			m_Winkel = Math.toDegrees(Math.atan(m));
			
			distance = Abstand_PunktPunkt(x1, y1, x2, y2);
			
			//System.out.println("Winkel: " + m_Winkel);
			//System.out.println("Distance: " + distance);
			
			//Abstände berechnen
			
			for(int i = 0; i < distances_y.length; i++) {
				
				dist_temp = distances_y[i] * distance / distances_y[3];
				
				//System.out.println("Distance_Temp: " + dist_temp);
		
				if(x1 < x2) {
					x_temp = x1 + Math.cos(Math.toRadians(m_Winkel)) * dist_temp;
					y_temp = (double)height - (y1 + Math.sin(Math.toRadians(m_Winkel)) * dist_temp);
				}
				if(x1 > x2) {
					x_temp = x1 - Math.cos(Math.toRadians(m_Winkel)) * dist_temp;
					y_temp = (double)height - (y1 - Math.sin(Math.toRadians(m_Winkel)) * dist_temp);
				}
				if(x1 == x2) {
					///???
				} 
				
				//xpoints_flaechen[point_counter] = (int)x_temp;
				//ypoints_flaechen[point_counter] = (int)y_temp;
				
				//System.out.println("x_temp: " + x_temp);
				//System.out.println("y_temp: " + y_temp);
				
				//point_counter++;
			}
			
			//2-1
			
			x1 = xpoints[1];
			y1 = (double)height - ypoints[1];
			x2 = xpoints[0];
			y2 = (double)height - ypoints[0];
			
			if(x1 == x2) {
				m = 0;
			} else {
				m = Steigung(x1, y1, x2, y2);
			}
			
			//System.out.println("2-1:");
			//System.out.println("Punkte: " + x1 + "," + y1 + "," + x2 + "," + y2);
			
			m_Winkel = Math.toDegrees(Math.atan(m));
			
			distance = Abstand_PunktPunkt(x1, y1, x2, y2);
			
			//System.out.println("Winkel: " + m_Winkel);
			//System.out.println("Distance: " + distance);
			
			//Abstände berechnen
			
			for(int i = 0; i < distances_y.length; i++) {
				
				dist_temp = distances_y[i] * distance / distances_y[3];
				
				//System.out.println("Distance_Temp: " + dist_temp);
				
				if(x1 < x2) {
					x_temp = x1 + Math.cos(Math.toRadians(m_Winkel)) * dist_temp;
					y_temp = (double)height - (y1 + Math.sin(Math.toRadians(m_Winkel)) * dist_temp);
				}
				if(x1 > x2) {
					x_temp = x1 - Math.cos(Math.toRadians(m_Winkel)) * dist_temp;
					y_temp = (double)height - (y1 - Math.sin(Math.toRadians(m_Winkel)) * dist_temp);
				}
				if(x1 == x2) {
					///???
				}
				//xpoints_flaechen[point_counter] = (int)x_temp;
				//ypoints_flaechen[point_counter] = (int)y_temp;
				
				//System.out.println("x_temp: " + x_temp);
				//System.out.println("y_temp: " + y_temp);
				
				//point_counter++;
			}
			
			//System.out.println("Points.x: " + points_up[0].x + "\nPoints.y: " + points_up[0].y);
			for(int k = 0; k < points_down_counter; k++) {
				Achsen(height, points_up[k], points_down[k], k);
				//Up nach Down von rechts oben nach links unten, wegen der Zahlen
			}
		}
		if(project == AKWHA_II) {
			/*
			Ablauf:
			- Steigung und Steigungswinkel
			- Länge der Geraden
			- Abstände der Versuchsparzellen umrechnen in Pixel
				- Gerade zwischen 5 und 1 
					Punkte nach x sortieren und in points_5_1
				- Gerade zwischen 6 und 2
					Punkte nach x sortieren und in points_6_2
				- Gerade zwischen 5 und 6
					Punkte nach y sortieren und in points_5_6
				- Gerade zwischen 2 und 1
					Punkte nach y sortieren und in points_2_1
				- Gerade zwischen 3 und 1
					Punkte nach x sortieren und in points_4_5
				- Gerade zwischen 2 und 4
					Punkte nach x sortieren und in points_2_4
				- Gerade zwischen 3 und 4
					Punkte nach y sortieren und in points_3_4
			*/	
			
			double x1 = 0;
			double x2 = 0;
			double y1 = 0;
			double y2 = 0;
			
			double m = 0;
			double m_Winkel;
			
			double distance;
			double dist_temp = 0;
			double x_temp = 0;
			double y_temp = 0;
			
			//5-1
			points_up[points_up_counter] = new Point();
			points_up[points_up_counter].x = xpoints[4];
			points_up[points_up_counter].y = ypoints[4];
			points_up_counter++;
			
			x1 = xpoints[4];
			y1 = (double)height - ypoints[4];
			x2 = xpoints[0];
			y2 = (double)height - ypoints[0];
			
			if(x1 == x2) {
				m = 0;
			} else {
				m = Steigung(x1, y1, x2, y2);
			}
			
			System.out.println("5-1:");
			System.out.println("Punkte: " + x1 + "," + y1 + "," + x2 + "," + y2);
			
			m_Winkel = Math.toDegrees(Math.atan(m));
			
			distance = Abstand_PunktPunkt(x1, y1, x2, y2);
			
			System.out.println("Winkel: " + m_Winkel);
			System.out.println("Distance: " + distance);
			
			//Abstände berechnen
			
			for(int i = 0; i < distances_right_II.length; i++) {
				
				dist_temp = distances_right_II[i] * distance / distances_right_II[10];
				
				//System.out.println("Distance_Temp: " + dist_temp);
		
				x_temp = x1 - Math.cos(Math.toRadians(m_Winkel)) * dist_temp;
				y_temp = (double)height - (y1 - Math.sin(Math.toRadians(m_Winkel)) * dist_temp);
				
				//xpoints_flaechen[point_counter] = (int)x_temp;
				//ypoints_flaechen[point_counter] = (int)y_temp;
				
				points_up[points_up_counter] = new Point();
				points_up[points_up_counter].x = (int)x_temp;
				points_up[points_up_counter].y = (int)y_temp;
				points_up_counter++;
				
				//System.out.println("x_temp: " + x_temp);
				//System.out.println("y_temp: " + y_temp);
				
				//point_counter++;
				
			}
				
			//6-2
			
			points_down[points_down_counter] = new Point();
			points_down[points_down_counter].x = xpoints[5];
			points_down[points_down_counter].y = ypoints[5];
			points_down_counter++;
			
			x1 = xpoints[5];
			y1 = (double)height - ypoints[5];
			x2 = xpoints[1];
			y2 = (double)height - ypoints[1];
			
			if(x1 == x2) {
				m = 0;
			} else {
				m = Steigung(x1, y1, x2, y2);
			}
			
			//System.out.println("2-3:");
			//System.out.println("Punkte: " + x1 + "," + y1 + "," + x2 + "," + y2);
			
			m_Winkel = Math.toDegrees(Math.atan(m));
			
			distance = Abstand_PunktPunkt(x1, y1, x2, y2);
			
			//System.out.println("Winkel: " + m_Winkel);
			//System.out.println("Distance: " + distance);
			
			//Abstände berechnen
			
			for(int i = 0; i < distances_right_II.length; i++) {
				
				dist_temp = distances_right_II[i] * distance / distances_right_II[10];
				
				//System.out.println("Distance_Temp: " + dist_temp);
		
				x_temp = x1 - Math.cos(Math.toRadians(m_Winkel)) * dist_temp;
				y_temp = (double)height - (y1 - Math.sin(Math.toRadians(m_Winkel)) * dist_temp);
				
				//xpoints_flaechen[point_counter] = (int)x_temp;
				//ypoints_flaechen[point_counter] = (int)y_temp;
				
				points_down[points_down_counter] = new Point();
				points_down[points_down_counter].x = (int)x_temp;
				points_down[points_down_counter].y = (int)y_temp;
				points_down_counter++;
				
				//System.out.println("x_temp: " + x_temp);
				//System.out.println("y_temp: " + y_temp);
				
				//point_counter++;
			}
			
			//1-3
			
			x1 = xpoints[0];
			y1 = (double)height - ypoints[0];
			x2 = xpoints[2];
			y2 = (double)height - ypoints[2];
			
			if(x1 == x2) {
				m = 0;
			} else {
				m = Steigung(x1, y1, x2, y2);
			}
			
			//System.out.println("5-4:");
			//System.out.println("Punkte: " + x1 + "," + y1 + "," + x2 + "," + y2);
			
			m_Winkel = Math.toDegrees(Math.atan(m));
			
			distance = Abstand_PunktPunkt(x1, y1, x2, y2);
			
			//System.out.println("Winkel: " + m_Winkel);
			//System.out.println("Distance: " + distance);
			
			//Abstände berechnen
			
			for(int i = 0; i < distances_left_II.length; i++) {
				
				dist_temp = distances_left_II[i] * distance / distances_left_II[11];
				
				//System.out.println("Distance_Temp: " + dist_temp);
		
				x_temp = x1 - Math.cos(Math.toRadians(m_Winkel)) * dist_temp;
				y_temp = (double)height - (y1 - Math.sin(Math.toRadians(m_Winkel)) * dist_temp);
				
				//xpoints_flaechen[point_counter] = (int)x_temp;
				//ypoints_flaechen[point_counter] = (int)y_temp;
				
				points_up[points_up_counter] = new Point();
				points_up[points_up_counter].x = (int)x_temp;
				points_up[points_up_counter].y = (int)y_temp;
				points_up_counter++;
				
				//System.out.println("x_temp: " + x_temp);
				//System.out.println("y_temp: " + y_temp);
				
				//point_counter++;
			}
			
			//2-4
			
			x1 = xpoints[1];
			y1 = (double)height - ypoints[1];
			x2 = xpoints[3];
			y2 = (double)height - ypoints[3];
			
			if(x1 == x2) {
				m = 0;
			} else {
				m = Steigung(x1, y1, x2, y2);
			}
			
			//System.out.println("3-6:");
			//System.out.println("Punkte: " + x1 + "," + y1 + "," + x2 + "," + y2);
			
			m_Winkel = Math.toDegrees(Math.atan(m));
			
			distance = Abstand_PunktPunkt(x1, y1, x2, y2);
			
			//System.out.println("Winkel: " + m_Winkel);
			//System.out.println("Distance: " + distance);
			
			//Abstände berechnen
			
			for(int i = 0; i < distances_left_II.length; i++) {
				
				dist_temp = distances_left_II[i] * distance / distances_left_II[11];
				
				//System.out.println("Distance_Temp: " + dist_temp);
		
				x_temp = x1 - Math.cos(Math.toRadians(m_Winkel)) * dist_temp;
				y_temp = (double)height - (y1 - Math.sin(Math.toRadians(m_Winkel)) * dist_temp);
				
				//xpoints_flaechen[point_counter] = (int)x_temp;
				//ypoints_flaechen[point_counter] = (int)y_temp;
				
				points_down[points_down_counter] = new Point();
				points_down[points_down_counter].x = (int)x_temp;
				points_down[points_down_counter].y = (int)y_temp;
				points_down_counter++;
				
				//System.out.println("x_temp: " + x_temp);
				//System.out.println("y_temp: " + y_temp);
				
				//point_counter++;
			}
			
			//4-3
			
			x1 = xpoints[3];
			y1 = (double)height - ypoints[3];
			x2 = xpoints[2];
			y2 = (double)height - ypoints[2];
			
			if(x1 == x2) {
				m = 0;
			} else {
				m = Steigung(x1, y1, x2, y2);
			}
			
			//System.out.println("6-4:");
			//System.out.println("Punkte: " + x1 + "," + y1 + "," + x2 + "," + y2);
			
			m_Winkel = Math.toDegrees(Math.atan(m));
			
			distance = Abstand_PunktPunkt(x1, y1, x2, y2);
			
			//System.out.println("Winkel: " + m_Winkel);
			//System.out.println("Distance: " + distance);
			
			//Abstände berechnen
			
			for(int i = 0; i < distances_y.length; i++) {
				
				dist_temp = distances_y[i] * distance / distances_y[3];
				
				//System.out.println("Distance_Temp: " + dist_temp);
		
				if(x1 < x2) {
					x_temp = x1 + Math.cos(Math.toRadians(m_Winkel)) * dist_temp;
					y_temp = (double)height - (y1 + Math.sin(Math.toRadians(m_Winkel)) * dist_temp);
				}
				if(x1 > x2) {
					x_temp = x1 - Math.cos(Math.toRadians(m_Winkel)) * dist_temp;
					y_temp = (double)height - (y1 - Math.sin(Math.toRadians(m_Winkel)) * dist_temp);
				}
				if(x1 == x2) {
					///???
				}
				
				//xpoints_flaechen[point_counter] = (int)x_temp;
				//ypoints_flaechen[point_counter] = (int)y_temp;
				
				//System.out.println("x_temp: " + x_temp);
				//System.out.println("y_temp: " + y_temp);
				
				//point_counter++;
			}
			
			//2-1
			
			x1 = xpoints[1];
			y1 = (double)height - ypoints[1];
			x2 = xpoints[0];
			y2 = (double)height - ypoints[0];
			
			if(x1 == x2) {
				m = 0;
			} else {
				m = Steigung(x1, y1, x2, y2);
			}
			
			//System.out.println("3-5:");
			//System.out.println("Punkte: " + x1 + "," + y1 + "," + x2 + "," + y2);
			
			m_Winkel = Math.toDegrees(Math.atan(m));
			
			distance = Abstand_PunktPunkt(x1, y1, x2, y2);
			
			//System.out.println("Winkel: " + m_Winkel);
			//System.out.println("Distance: " + distance);
			
			//Abstände berechnen
			
			for(int i = 0; i < distances_y.length; i++) {
				
				dist_temp = distances_y[i] * distance / distances_y[3];
				
				//System.out.println("Distance_Temp: " + dist_temp);
		
				if(x1 < x2) {
					x_temp = x1 + Math.cos(Math.toRadians(m_Winkel)) * dist_temp;
					y_temp = (double)height - (y1 + Math.sin(Math.toRadians(m_Winkel)) * dist_temp);
				}
				if(x1 > x2) {
					x_temp = x1 - Math.cos(Math.toRadians(m_Winkel)) * dist_temp;
					y_temp = (double)height - (y1 - Math.sin(Math.toRadians(m_Winkel)) * dist_temp);
				}
				if(x1 == x2) {
					///???
				} 
				
				//xpoints_flaechen[point_counter] = (int)x_temp;
				//ypoints_flaechen[point_counter] = (int)y_temp;
				
				//System.out.println("x_temp: " + x_temp);
				//System.out.println("y_temp: " + y_temp);
				
				//point_counter++;
			}
			
			//6-5
			
			x1 = xpoints[5];
			y1 = (double)height - ypoints[5];
			x2 = xpoints[4];
			y2 = (double)height - ypoints[4];
			
			if(x1 == x2) {
				m = 0;
			} else {
				m = Steigung(x1, y1, x2, y2);
			}
			
			//System.out.println("2-1:");
			//System.out.println("Punkte: " + x1 + "," + y1 + "," + x2 + "," + y2);
			
			m_Winkel = Math.toDegrees(Math.atan(m));
			
			distance = Abstand_PunktPunkt(x1, y1, x2, y2);
			
			//System.out.println("Winkel: " + m_Winkel);
			//System.out.println("Distance: " + distance);
			
			//Abstände berechnen
			
			for(int i = 0; i < distances_y.length; i++) {
				
				dist_temp = distances_y[i] * distance / distances_y[3];
				
				//System.out.println("Distance_Temp: " + dist_temp);
				
				if(x1 < x2) {
					x_temp = x1 + Math.cos(Math.toRadians(m_Winkel)) * dist_temp;
					y_temp = (double)height - (y1 + Math.sin(Math.toRadians(m_Winkel)) * dist_temp);
				}
				if(x1 > x2) {
					x_temp = x1 - Math.cos(Math.toRadians(m_Winkel)) * dist_temp;
					y_temp = (double)height - (y1 - Math.sin(Math.toRadians(m_Winkel)) * dist_temp);
				}
				if(x1 == x2) {
					///???
				}
				//xpoints_flaechen[point_counter] = (int)x_temp;
				//ypoints_flaechen[point_counter] = (int)y_temp;
				
				//System.out.println("x_temp: " + x_temp);
				//System.out.println("y_temp: " + y_temp);
				
				//point_counter++;
			}
			
			//System.out.println("Points.x: " + points_up[0].x + "\nPoints.y: " + points_up[0].y);
			for(int k = 0; k < points_down_counter; k++) {
				Achsen(height, points_up[k], points_down[k], k);
				//Up nach Down von rechts oben nach links unten, wegen der Zahlen
			}
		}
	}
	
	public void Achsen(int height, Point up, Point down, int counter) {
		
		double x1 = 0;
		double x2 = 0;
		double y1 = 0;
		double y2 = 0;
		
		double m = 0;
		double m_Winkel;
		
		double distance;
		double dist_temp = 0;
		double x_temp = 0;
		double y_temp = 0;
		
		xpoints_flaechen[point_counter] = up.x;
		ypoints_flaechen[point_counter] = up.y;
		point_counter++;
		
		/*x1 = down.x;
		y1 = (double)height - down.y;
		x2 = up.x;
		y2 = (double)height - up.y;*/
		
		x1 = up.x;
		y1 = (double)height - up.y;
		x2 = down.x;
		y2 = (double)height - down.y;
		
		/*if(x1 == x2) {
			m = 0;
		} else {
			m = Steigung(x1, y1, x2, y2);
		}*/
		
		if(x1 == x2) {
			x1 = x1 + 1;
		}
		
		m = Steigung(x1, y1, x2, y2);
		
		//System.out.println(counter + ":");
		//System.out.println("Punkte: " + x1 + "," + y1 + "," + x2 + "," + y2);
		
		m_Winkel = Math.toDegrees(Math.atan(m));
		
		distance = Abstand_PunktPunkt(x1, y1, x2, y2);
		
		//System.out.println("Winkel: " + m_Winkel);
		//System.out.println("Distance: " + distance);
		
		//Abstände berechnen
		
		for(int i = 0; i < distances_y.length - 1; i++) {
			
			dist_temp = distances_y[i] * distance / distances_y[3];
			
			//System.out.println("Distance_Temp: " + dist_temp);
	
			if(x1 < x2) {
				x_temp = x1 + Math.cos(Math.toRadians(m_Winkel)) * dist_temp;
				y_temp = (double)height - (y1 + Math.sin(Math.toRadians(m_Winkel)) * dist_temp);
			}
			if(x1 > x2) {
				x_temp = x1 - Math.cos(Math.toRadians(m_Winkel)) * dist_temp;
				y_temp = (double)height - (y1 - Math.sin(Math.toRadians(m_Winkel)) * dist_temp);
			}
			if(x1 == x2) { //??? Stimmt das so bereits?
				//x_temp = x1 + Math.cos(Math.toRadians(m_Winkel)) * dist_temp;
				//y_temp = (double)height - (y1 + Math.sin(Math.toRadians(m_Winkel)) * dist_temp);
			}
			
			xpoints_flaechen[point_counter] = (int)x_temp;
			ypoints_flaechen[point_counter] = (int)y_temp;
			
			//System.out.println("x_temp: " + x_temp);
			//System.out.println("y_temp: " + y_temp);
			
			point_counter++;
		}
		xpoints_flaechen[point_counter] = down.x;
		ypoints_flaechen[point_counter] = down.y;
		point_counter++;
	}
	
	double Steigung(double x1, double y1, double x2, double y2) {
		
		double value = (y2-y1)/(x2-x1);
		
		return value;
	}
	
	double Schnittpunkt_X(double n1, double n2, double m1, double m2) {
		
		double value = (n1-n2)/(m2-m1);
		
		return value;
	}
	
	double Schnittpunkt_Y(double n1, double n2, double m1, double m2) {
		
		double value = ((m2*n1)-(m1*n2))/(m2-m1);	
		
		return value;
	}
	
	double Abstand_PunktPunkt(double x1, double y1, double x2, double y2) {
		
		double dx = x2 - x1;
		double dy = y2 - y1;
		
		double value = Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
		
		return value;
	}
	
	double getlength(int project, int number, int site) {
		
		double value = 0;
		
		if(project == AKWHA_I) {
		
			String[] splitarray = AKWHA_I_lengths[number-1].split(";");
			
			value = Double.parseDouble(splitarray[site]);
		
		}
		
		if(project == AKWHA_II) {
			
			String[] splitarray = AKWHA_II_lengths[number-1].split(";");
			
			value = Double.parseDouble(splitarray[site]);
		
		}
		
		return value;
	}
	
	boolean polygoncolumn(int column) {
		
		boolean value = true;
		
		if(column == 2 || column == 5 || column == 8 || column == 11 || column == 14 || column == 17 || column == 20) {
			value = false;
		}
		
		return value;
		
	}
	
	int[] reorderXY(int[] reorderArray) {
		int[] newArray = new int[reorderArray.length];
		
		newArray[0] = reorderArray[0];
		newArray[1] = reorderArray[1];
		newArray[2] = reorderArray[2];
		newArray[3] = reorderArray[5];
		newArray[4] = reorderArray[3];
		newArray[5] = reorderArray[4];
		
		return newArray;
		
	}
		
	String[] extractDistances(StringBuilder sb){
		
		String complete_array = sb.toString();
		
		String splitarray[]= complete_array.split("-");
		
		String[] distances = new String[splitarray.length];
		
		for(int i = 0; i < splitarray.length; i++) {
			
			String splitarray_value[]= splitarray[i].split(":");
			double value = Double.parseDouble(splitarray_value[0]);
			String actual_value = "";
			
			if(value <= 9){
				actual_value = "000" + String.valueOf(value);
			}
			if(value > 9 && value <= 99){
				actual_value = "00" + String.valueOf(value);
			}
			if(value > 99 && value <= 999){
				actual_value = "0" + String.valueOf(value);
			}
			
			distances[i] = actual_value + ":" + splitarray_value[1];
		}
		
		return distances;
	}
	
	public void getScale(ImagePlus imp) {
		
		IJ.setTool("line");
		
		InputDialog input = new InputDialog("Set the Scale", "Please draw Line on known Length\nLine and Points are movable", "Line", imp);
		input.show();	
		
		Roi roi;
		
		roi = imp.getRoi();
		double length = 0;
		double angle = 0;
		
		if(roi.isLine()){
			Line line = (Line)roi;
			length = roi.getLength();
			angle = roi.getAngle(line.x1, line.y1, line.x2, line.y2);
		}
		
		double reference = 0;
		
		while(reference == 0 || reference == 0 || reference == 0) {
			reference = getScaleReference();
		}
		
		scale_inCM = 10;
		
		scale_PxCM = length/reference;
		scale_CMPx = reference/length;
		
		System.out.println("Pixel/cm: " + scale_PxCM + "\ncm/Pixel: " + scale_CMPx);
		
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
		String[] projektArray = {"Baitplates", "Einzelnes Viereck Batch", "Oscar I", "AKHWA I", "AKHWA II", "EcoStack", "Mehrere Vierecke", "Einzelnes Viereck", "Referenzpunkte", "VORAN", "OSCAR"};
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
	
	String enterName() {
		String Name = "";
		
		GenericDialog dlg = new GenericDialog("Eingabe", IJ.getInstance());
		dlg.addMessage("Bitte Name für Plot/Batch eingeben");
		dlg.addStringField( "Name", "", 2);
		
		dlg.showDialog();
		
		Name = (String)dlg.getNextString();
		
		if (dlg.wasCanceled()) {
			Name = "dlg.canceled";
			dlg.dispose();
		}
		
		return Name;
	}
	
	int showMehrereVierecke(){
		
		int choice = 0;
				
		GenericDialog dlg = new GenericDialog("Auswahl", IJ.getInstance());
		dlg.addMessage("Bitte Projekt ausw\u00e4hlen");
		dlg.addNumericField( "Anzahl Vierecke", 2, 0);
		
		dlg.showDialog();
		
		choice = (int)dlg.getNextNumber();
		
		if (dlg.wasCanceled()) {
			choice = -1;
			dlg.dispose();
		}
		
		return choice;
	}
	
	double getScaleReference(){
		
		double reference = 0;
		String[] einheitenarray = {"cm", "m"};
		
				
		GenericDialog dlg = new GenericDialog("Eingabe und Auswahl", IJ.getInstance());
		dlg.addMessage("Bitte L\u00e4nge eingeben und Einheit ausw\u00e4hlen");
		dlg.addStringField("L\u00e4nge","");
		dlg.addChoice( "Einheit", einheitenarray, einheitenarray[0] );
		
		dlg.showDialog();
		
		String reference_string = dlg.getNextString();		
		String einheit = dlg.getNextChoice();
		
		if(reference_string.contains(",")) {
			System.out.println("reference_String contains: " + reference_string);
			String replace_string = reference_string.replace(",",".").trim();
			reference_string = replace_string;
			System.out.println("reference_String contains after trim: " + reference_string);
		}
		
		System.out.println("reference_String Neu: " + reference_string);
		
		boolean convert = false;
		
		try {
			reference = Double.parseDouble(reference_string);
			convert = true;
		} catch(NumberFormatException ex) {
			  // handle exception
		}
		
		if(convert) {
			System.out.println("reference: " + reference);
			
			if(einheit.equals("cm") || einheit.equals("m")) {
				if(einheit.equals("m")){
					reference = reference * 100;
				}
			} else {
				reference = -2;
			}
		} else {
			// Parse nicht geklappt
			System.out.println("Parse: double nicht funktioniert");
			reference = -3;
		}
		
		if (dlg.wasCanceled()) {
			reference = -1;
			dlg.dispose();
		}
		
		return reference;
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
				IJ.error("savedata", ""+e);
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
				//Vergleichen: Wenn array(i) und array(i-1) ungefÃ¤hr gleich sind (innerhalb 5px) dann counter++
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
		problem = false;
	}
	
	public void exportCropImagesevbatch(String FullName, String StartFileName, Roi rois, int index, String batchname) {
		
		int blockcounter = 1;
		int counter = 0;
		int good_counter = 0;
		boolean startsaving = false;
		boolean two_control = false;
		int two_counter = 1;
		int imagecounter = 0;
		int CountImages = 0;
		
		exportImages = new String[1];
		exportCalc = new String[1];
		String export = "Export der Fl\u00e4chenberechnung: \n";
		String export_pixelvalues = "";
		String filename = "";
		String filename_pixelvalues = "";
		
		Opener opener = new Opener();
		ImagePlus impToCrop = opener.openImage(FullName);
		
		impToCrop.setRoi(rois);
		
		//Rectangle rec = rois.getBounds();
		//System.out.println("x,y: " + rec.x + "," + rec.y);
		
		ImagePlus imp3 = impToCrop.crop();
		
		//String name = StartFileDirectory + "/Export/" + String.valueOf(CountImages) + "_" + Maske + "_" + String.valueOf(imagecounter);
		String name = StartFileDirectory + "/Export/" + StartFileName + "_" + batchname + "_" + String.valueOf(index) + "_" + "original";
		
		String export_name = name + ".tif";
		
		//IJ.saveAs(imp3, "jpg", name);
		
		IJ.saveAs(imp3, "tiff", name);
		
		Opener opener_toconvert = new Opener();
		ImagePlus imp_a = opener_toconvert.openImage(export_name);
		
		if(imp_a != null){
			
			imp_a.show();
			//IJ.run(imp_a, "Make Binary", "");
			
			Integer[] input_int = new Integer[9];
			input_int[0] = minHue;
			input_int[1] = maxHue;
			input_int[2] = minSat;
			input_int[3] = maxSat;
			input_int[4] = minBri;
			input_int[5] = maxBri;
			input_int[6] = mode;
			input_int[7] = colorSpace;
			input_int[8] = method_int;
			
			Boolean[] input_bool = new Boolean[4];
			input_bool[0] = bandPassH;
			input_bool[1] = bandPassS;
			input_bool[2] = bandPassB;
			input_bool[3] = darkBackground;
			
			try {	            
                Thread.sleep(200);
	        }
	        catch (Exception e) {
	            System.out.println(e);
	        }
			
			ColorThresholderExtended thresher = new ColorThresholderExtended(); //Color Threshold Fenster Ã¶ffnen
			//thresher.setStringValue(method);
			thresher.setIntValues(input_int);
			thresher.setBoolValues(input_bool);
			
			thresher.updateAfterSetting(imp_a);
			
			if(index == 0) {
			
				InputDialog inputcolorthresher = new InputDialog("Input", "Please adjust Threshold", "Threshold", imp_a);
				inputcolorthresher.show();
				
				//color_string_value = thresher.getStringValue();
				color_int_values = thresher.getIntValues();
				color_bool_values = thresher.getBoolValues();
				
				//method = color_string_value;
				method = "DEFAULT";
				minHue = color_int_values[0];
				maxHue = color_int_values[1];
				minSat = color_int_values[2];
				maxSat = color_int_values[3];
				minBri = color_int_values[4];
				maxBri = color_int_values[5];
				mode = color_int_values[6];
				colorSpace = color_int_values[7];
				method_int = color_int_values[8];
				bandPassH = color_bool_values[0];
				bandPassS = color_bool_values[1];
				bandPassB = color_bool_values[2];
				darkBackground = color_bool_values[3];
				
			}	
			
			System.out.println("Active Windows: " + WindowManager.getWindowCount());
			
			//color_string_value = thresher.getStringValue();
			color_int_values = thresher.getIntValues();
			color_bool_values = thresher.getBoolValues();
			
			String added = color_int_values[0] + ", " + color_int_values[1] + ", " + color_bool_values[0] + ", " + color_int_values[2] + ", " + color_int_values[3] + ", " + color_bool_values[1] + ", " + color_int_values[4] + ", " + color_int_values[5] + ", " + color_bool_values[2] + ", " + color_int_values[8] + ", " + color_int_values[6] + ", " + color_int_values[7] + ", " + color_bool_values[3];
			
			System.out.println("Values: " + added);
			
			try {	            
                Thread.sleep(200);
	        }
	        catch (Exception e) {
	            System.out.println(e);
	        }
			
			//Hier bei Bedarf stattdessen Alterselection einfügen
			PixelMask = thresher.getdrawfillMask();
			
			thresher.drawBinary(PixelMask, imp_a);
			
			thresher.close();
			
			String name_export = StartFileDirectory + "/Export/" + StartFileName + "_" + batchname + "_" + String.valueOf(index) + "_binary.tif";
			
			IJ.saveAs(imp_a, "tiff", name_export);
			
			imp_a.close();
			
			if(PixelMask != null) {
			
				Opener opener_tomeasure = new Opener();
				ImagePlus imp_m = opener_tomeasure.openImage(name_export);
				
				if(imp_m != null){
					ImageProcessor ip_binary = imp_m.getProcessor();
				
					ImageStatistics stats = ImageStatistics.getStatistics(ip_binary, ImageStatistics.AREA_FRACTION, null);
					
					int width = ip_binary.getWidth();
					int height = ip_binary.getHeight();
					
					double real_area = 0;
					
					if(scale_CMPx != 0) {
						real_area = (width * scale_CMPx) * (height * scale_CMPx);
					}
					
					double aFraction = stats.areaFraction;
					double greenFraction = 100-aFraction;
					//double area = (aFraction * 36)/100;
					
					DecimalFormat df = new DecimalFormat("#.#####");
					String aFraction_df = df.format(greenFraction); 
					
					double measure_counter = 0;
					
					for(int i = 0; i < PixelMask.length; i++){
						if (PixelMask[i]!=0){
							//fill
							measure_counter++;
						}
					}
					//Prozent Abdeckung der thresholded Pixel
					double PixelFraction = (measure_counter * 100) / PixelMask.length;
					
					double RealFraction = (real_area * PixelFraction) / 100;
					
					System.out.println("PixelMask.length: " + PixelMask.length + "\nMeasure_Counter: " + measure_counter + "\nPixelFraction: " + PixelFraction + "\nArea: " + real_area);
					
					String pixel_aFraction_df = df.format(PixelFraction); 
					String real_Fraction_df = df.format(RealFraction);
					
					String real_Fraction_complete = real_Fraction_df + " cm²";
					
					if(RealFraction == 0) {
						real_Fraction_complete = "Konnte nicht ausgerechnet werden.";
					}
					
					//String area_df = df.format(area);
					//System.out.println("Flaechenberechnung (" + String.valueOf(i) + "): " + String.valueOf(aFraction));
					//export = export + String.valueOf(CountImages) + "_" + Maske + "_" + correct_numbers[i] + ": " + area_df + "m\u00B2 (" + aFraction_df + "%)\n";
					//exportCalc[0]= "Fl\u00e4che: " + aFraction_df + "%" + "\nFl\u00e4che mit Schleife: " + pixel_aFraction_df + "%";
					exportCalc[0]= "Prozentuale Fl\u00e4che: " + pixel_aFraction_df + " %" + "\nReale Fl\u00e4che: " + real_Fraction_complete;
					filename = StartFileName + "_" + batchname + "_" + String.valueOf(index) + "_Fl\u00e4chenexport" + ".txt";		
					
					summary_batch = summary_batch + "\n" + batchname + "|" + StartFileName + "|" + real_Fraction_df + "|" + pixel_aFraction_df;
				}
				
				Opener opener_topixel = new Opener();
				ImagePlus imp_a_pixelvalues = opener_topixel.openImage(export_name);
				
				if(imp_a_pixelvalues != null){
					String[] rgb_values = extractPixelValues(imp_a_pixelvalues, PixelMask);
					
					StringBuilder sb = new StringBuilder();
			
					for(int i = 0; i < rgb_values.length; i++){
						sb.append(rgb_values[i]);
					}
					export_pixelvalues = sb.toString();
					filename_pixelvalues = StartFileName + "_" + batchname + "_" + String.valueOf(index) + "_Einzelpixel_Export" + ".txt";
				} else {
					summary_batch = summary_batch + "\n" + batchname + "|" + StartFileName + "|0|0";
				}
			}
		}
		
		if(PixelMask != null) {
			Arrays.sort(exportCalc);
			
			for(int i=0; i<exportCalc.length; i++){
				export = export + exportCalc[0] + "\n";
			}
			
			saveData(export, filename);
			saveData(export_pixelvalues, filename_pixelvalues);
			
			problem = false;
		}
		
	}
	
	public void exportCropImagesEinzelViereck(String FullName, String StartFileName, Roi rois, String Index) {
		
		int blockcounter = 1;
		int counter = 0;
		int good_counter = 0;
		boolean startsaving = false;
		boolean two_control = false;
		int two_counter = 1;
		int imagecounter = 0;
		int CountImages = 0;
		
		exportImages = new String[1];
		exportCalc = new String[1];
		String export = "Export der Fl\u00e4chenberechnung: \n";
		String export_pixelvalues = "";
		String filename = "";
		String filename_pixelvalues = "";
		
		Opener opener = new Opener();
		ImagePlus impToCrop = opener.openImage(FullName);
		
		impToCrop.setRoi(rois);
		
		//Rectangle rec = rois.getBounds();
		//System.out.println("x,y: " + rec.x + "," + rec.y);
		
		ImagePlus imp3 = impToCrop.crop();
		
		//String name = StartFileDirectory + "/Export/" + String.valueOf(CountImages) + "_" + Maske + "_" + String.valueOf(imagecounter);
		String name = StartFileDirectory + "/Export/" + StartFileName + "_" + Index + "_" + "original";
		
		String export_name = name + ".tif";
		
		//IJ.saveAs(imp3, "jpg", name);
		
		IJ.saveAs(imp3, "tiff", name);
		
		Opener opener_toconvert = new Opener();
		ImagePlus imp_a = opener_toconvert.openImage(export_name);
		
		if(imp_a != null){
			
			imp_a.show();
			//IJ.run(imp_a, "Make Binary", "");
			
			ColorThresholderExtended thresher = new ColorThresholderExtended(); //Color Threshold Fenster open
		
			Integer[] input_int = new Integer[9];
			input_int[0] = minHue;
			input_int[1] = maxHue;
			input_int[2] = minSat;
			input_int[3] = maxSat;
			input_int[4] = minBri;
			input_int[5] = maxBri;
			input_int[6] = mode;
			input_int[7] = colorSpace;
			input_int[8] = method_int;
			
			Boolean[] input_bool = new Boolean[4];
			input_bool[0] = bandPassH;
			input_bool[1] = bandPassS;
			input_bool[2] = bandPassB;
			input_bool[3] = darkBackground;
			
			//thresher.setStringValue(method);
			thresher.setIntValues(input_int);
			thresher.setBoolValues(input_bool);
			
			InputDialog inputcolorthresher = new InputDialog("Input", "Please adjust Threshold", "Threshold", imp_a);
			inputcolorthresher.show();
			
			System.out.println("Active Windows: " + WindowManager.getWindowCount());
			
			//color_string_value = thresher.getStringValue();
			color_int_values = thresher.getIntValues();
			color_bool_values = thresher.getBoolValues();
			
			//method = color_string_value;
			method = "DEFAULT";
			minHue = color_int_values[0];
			maxHue = color_int_values[1];
			minSat = color_int_values[2];
			maxSat = color_int_values[3];
			minBri = color_int_values[4];
			maxBri = color_int_values[5];
			mode = color_int_values[6];
			colorSpace = color_int_values[7];
			method_int = color_int_values[8];
			bandPassH = color_bool_values[0];
			bandPassS = color_bool_values[1];
			bandPassB = color_bool_values[2];
			darkBackground = color_bool_values[3];
			
			String added = color_int_values[0] + ", " + color_int_values[1] + ", " + color_bool_values[0] + ", " + color_int_values[2] + ", " + color_int_values[3] + ", " + color_bool_values[1] + ", " + color_int_values[4] + ", " + color_int_values[5] + ", " + color_bool_values[2] + ", " + color_int_values[8] + ", " + color_int_values[6] + ", " + color_int_values[7] + ", " + color_bool_values[3];
			
			System.out.println("Values: " + added);
			
			int alterselection = dialolgAlterSelection();
			
			if(alterselection == 1) {
				//Ja Auswahl ändern
				IJ.setTool("freehand");
				
				thresher.createSelection();
				
				InputDialog inputalterselection = new InputDialog("Input", "Please adjust Selection with ALT + Selection", "AlterSelection", imp_a);
				inputalterselection.show();
				
				//Roi roi_newselection = imp_a.getRoi();
				
				ByteProcessor bp_newselection = createRoiMask(imp_a);
				
				byte[] fillMask_newselection = (byte[])bp_newselection.getPixels();
				
				System.out.println("newselection_length: " + fillMask_newselection.length);
				
				PixelMask = fillMask_newselection;
				
			} else {
				//Nein Auswahl nicht ändern
				PixelMask = thresher.getdrawfillMask();
			}
			
			thresher.drawBinary(PixelMask, imp_a);
			
			thresher.close();
			
			String name_export = StartFileDirectory + "/Export/" + StartFileName + "_" + Index + "_binary.tif";
			
			IJ.saveAs(imp_a, "tiff", name_export);
			
			imp_a.close();
			
			Opener opener_tomeasure = new Opener();
			ImagePlus imp_m = opener_tomeasure.openImage(name_export);
			
			if(imp_m != null){
				ImageProcessor ip_binary = imp_m.getProcessor();
			
				ImageStatistics stats = ImageStatistics.getStatistics(ip_binary, ImageStatistics.AREA_FRACTION, null);
				
				int width = ip_binary.getWidth();
				int height = ip_binary.getHeight();
				
				double real_area = 0;
				
				if(scale_CMPx != 0) {
					real_area = (width * scale_CMPx) * (height * scale_CMPx);
				}
				
				double aFraction = stats.areaFraction;
				double greenFraction = 100-aFraction;
				//double area = (aFraction * 36)/100;
				
				DecimalFormat df = new DecimalFormat("#.#####");
				String aFraction_df = df.format(greenFraction); 
				
				double measure_counter = 0;
				
				for(int i = 0; i < PixelMask.length; i++){
					if (PixelMask[i]!=0){
						//fill
						measure_counter++;
					}
				}
				//Prozent Abdeckung der thresholded Pixel
				double PixelFraction = (measure_counter * 100) / PixelMask.length;
				
				double RealFraction = (real_area * PixelFraction) / 100;
				
				System.out.println("PixelMask.length: " + PixelMask.length + "\nMeasure_Counter: " + measure_counter + "\nPixelFraction: " + PixelFraction + "\nArea: " + real_area);
				
				String pixel_aFraction_df = df.format(PixelFraction); 
				String real_Fraction_df = df.format(RealFraction);
				
				String real_Fraction_complete = real_Fraction_df + " cm²";
				
				if(RealFraction == 0) {
					real_Fraction_complete = "Konnte nicht ausgerechnet werden.";
				}
				
				//String area_df = df.format(area);
				//System.out.println("Flaechenberechnung (" + String.valueOf(i) + "): " + String.valueOf(aFraction));
				//export = export + String.valueOf(CountImages) + "_" + Maske + "_" + correct_numbers[i] + ": " + area_df + "m\u00B2 (" + aFraction_df + "%)\n";
				//exportCalc[0]= "Fl\u00e4che: " + aFraction_df + "%" + "\nFl\u00e4che mit Schleife: " + pixel_aFraction_df + "%";
				exportCalc[0]= "Prozentuale Fl\u00e4che: " + pixel_aFraction_df + " %" + "\nReale Fl\u00e4che: " + real_Fraction_complete;
				filename = StartFileName + "_" + Index + "_Fl\u00e4chenexport" + ".txt";				
			}
			
			Opener opener_topixel = new Opener();
			ImagePlus imp_a_pixelvalues = opener_topixel.openImage(export_name);
			
			if(imp_a_pixelvalues != null){
				String[] rgb_values = extractPixelValues(imp_a_pixelvalues, PixelMask);
				
				StringBuilder sb = new StringBuilder();
		
				for(int i = 0; i < rgb_values.length; i++){
					sb.append(rgb_values[i]);
				}
				export_pixelvalues = sb.toString();
				filename_pixelvalues = StartFileName + "_" + Index + "_Einzelpixel_Export" + ".txt";
			}
			
		}
		
		Arrays.sort(exportCalc);
		
		for(int i=0; i<exportCalc.length; i++){
			export = export + exportCalc[0] + "\n";
		}
		
		saveData(export, filename);
		saveData(export_pixelvalues, filename_pixelvalues);
		
		problem = false;
		
	}
	
	public ByteProcessor createRoiMask(ImagePlus imp) {
        Roi roi2 = imp.getRoi();
        Overlay overlay2 = getOverlay();
        if (roi2==null && overlay2==null)
            throw new IllegalArgumentException("ROI or overlay required");
        ByteProcessor mask = new ByteProcessor(imp.getWidth(),imp.getHeight());
        mask.setColor(255);
        if (roi2!=null)
            mask.fill(roi2);
        else if (overlay2!=null) {
            if (overlay2.size()==1 && (overlay2.get(0) instanceof ImageRoi)) {
                ImageRoi iRoi = (ImageRoi)overlay2.get(0);
                ImageProcessor ip = iRoi.getProcessor();
                if (ip.getWidth()!=mask.getWidth() || ip.getHeight()!=mask.getHeight())
                    return mask;
                for (int i=0; i<ip.getPixelCount(); i++) {
                    if (ip.get(i)!=0)
                        mask.set(i, 255);
                }
            } else {
                for (int i=0; i<overlay2.size(); i++)
                    mask.fill(overlay2.get(i));
            }
        }
        mask.setThreshold(255, 255, ImageProcessor.NO_LUT_UPDATE);
        return mask;
    }
	
	int dialolgAlterSelection(){
		
		int choice = 0;
				
		GenericDialog dlg = new GenericDialog("Change selection", IJ.getInstance());
		dlg.addMessage("Remove pixels from selection?");
		
		dlg.showDialog();
		
		choice = 1;
		
		if (dlg.wasCanceled()) {
			choice = -1;
			dlg.dispose();
		}
		
		return choice;
	}
	
	private boolean dialolgStackAction(String title, String message, boolean stackaction, ColorThresholderExtended thresher) {
		// display dialog , return false if cancelled or on error.
		NonBlockingGenericDialog dlg = new NonBlockingGenericDialog(title);
		dlg.hideCancelButton();
		dlg.addMessage(message);
		
		dlg.showDialog();
		if(dlg.invalidNumber()) {
			IJ.showMessage("Error", "Invalid input number");
			return false;
		}
		if(thresher.getStackAction()) {
			return true;
		}
		if(!stackaction) {
			return false;
		}
		
		return true;
	}
	
	String[] extractPixelValues(ImagePlus imp_pixel, byte[] PixelMask){
		
		ImageProcessor ip = imp_pixel.getProcessor();
		
		int width = ip.getWidth();
		int height = ip.getHeight();
		int numPixels = width*height;
		
		int[] pixels = (int[])ip.getPixels();
		int counter = 0;
		String[] rgb_values = new String[numPixels];
		
		for(int y = 0; y < height; y++){
			for(int x = 0; x < width; x++){
				if (PixelMask[counter]!=0){
					//fill
					int c = ip.getPixel(x,y);
					int r = (c&0xff0000)>>16;
					int g = (c&0xff00)>>8;
					int b = c&0xff;
					
					if(x == width-1){
						//rgb_values[counter] = "R:" + String.valueOf(r) + ",G:" + String.valueOf(g) + ",B:" + String.valueOf(b) + "\n";
						rgb_values[counter] = "R:" + r + ",G:" + g + ",B:" + b + "\n";
					} else {
						rgb_values[counter] = "R:" + r + ",G:" + g + ",B:" + b + "|";
					}
				}
				else {
					//keep
					if(x == width-1){
						//rgb_values[counter] = "R:" + String.valueOf(r) + ",G:" + String.valueOf(g) + ",B:" + String.valueOf(b) + "\n";
						rgb_values[counter] = "x\n";
					} else {
						rgb_values[counter]= "x|";
					}
					
					
				}
				counter++;
			}
		}
		//System.out.println("Counter: " + String.valueOf(counter));
		//System.out.println("numPixels: " + String.valueOf(numPixels));
		return rgb_values;
	}
	
	String[] extractPixelValuesNoMask(ImagePlus imp_pixel){
		
		int[] dark_pixel_array_recon = new int[dark_pixel_array.length];
		int[] empty_pixel_array_high_recon = new int[empty_pixel_array_high.length];
		int[] empty_pixel_array_low_recon = new int[empty_pixel_array_low.length];
		int[] circle_pixel_array_recon = new int[circle_pixel_array.length];
		
		int dpar_counter = 0;
		int epahr_counter = 0;
		int epalr_counter = 0;
		
		int cpa_counter = 0;
		
		ImageProcessor ip = imp_pixel.getProcessor();
		
		int width = ip.getWidth();
		int height = ip.getHeight();
		int numPixels = width*height;
		
		int[] pixels = (int[])ip.getPixels();
		int counter = 0;
		String[] rgb_values = new String[numPixels];
		
		for(int y = 0; y < height; y++){
			for(int x = 0; x < width; x++){
				//fill
				int c = ip.getPixel(x,y);
				int r = (c&0xff0000)>>16;
				int g = (c&0xff00)>>8;
				int b = c&0xff;
				
				int mean = (r+g+b)/3;
				
				if(counter >= circle_pixel_array[0] && counter <= circle_pixel_array[circle_pixel_array.length-1]) {
					//circle area pixels
					//System.out.println("Debug: DarkPixel if" + String.valueOf(counter));
					if(circle_pixel_array[cpa_counter] == counter) {
						circle_pixel_array_recon[cpa_counter] = mean;
						cpa_counter++;
					}
				}
				
				if(counter >= dark_pixel_array[0] && counter <= dark_pixel_array[dark_pixel_array.length-1]) {
					//dark pixel (Cat 1)
					//System.out.println("Debug: DarkPixel if" + String.valueOf(counter));
					if(dark_pixel_array[dpar_counter] == counter) {
						dark_pixel_array_recon[dpar_counter] = mean;
						dpar_counter++;
					}
				}
				if(counter >= empty_pixel_array_high[0] && counter <= empty_pixel_array_high[empty_pixel_array_high.length-1]) {
					//empty pixel high (Cat 0)
					//System.out.println("Debug: empty pixel high if" + String.valueOf(counter));
					if(empty_pixel_array_high[epahr_counter] == counter) {
						empty_pixel_array_high_recon[epahr_counter] = mean;
						epahr_counter++;
					}
				}
				if(counter >= empty_pixel_array_low[0] && counter <= empty_pixel_array_low[empty_pixel_array_low.length-1]) {
					//empty pixel low (Cat 0)
					//System.out.println("Debug: empty pixel low if" + String.valueOf(counter));
					if(empty_pixel_array_low[epalr_counter] == counter) {
						empty_pixel_array_low_recon[epalr_counter] = mean;
						epalr_counter++;
					}
				}
				
				if(x == width-1){
					//rgb_values[counter] = "R:" + r + ",G:" + g + ",B:" + b + "\n";
					rgb_values[counter] = mean + "\n";
				} else {
					//rgb_values[counter] = "R:" + r + ",G:" + g + ",B:" + b + "|";
					rgb_values[counter] = mean + "|";
				}
				counter++;
			}
		}
		
		//System.out.println("DarkPixelCounter: " + String.valueOf(dpar_counter));
		//System.out.println("EmptyPixelHighCounter: " + String.valueOf(epahr_counter));
		//System.out.println("EmptyPixelLowCounter: " + String.valueOf(epalr_counter));
		
		int tolerance = 15;
		
		int sum_dpar = 0;
		int sum_epahr = 0;
		int sum_epalr = 0;
		int sum_cpar = 0;
		
		int average_dpar = 0;
		int average_epahr = 0;
		int average_epalr = 0;
		int average_cpar = 0;
		
		int min_dpar = 0;
		int min_epahr = 0;
		int min_epalr = 0;
		int min_cpar = 0;
		
		int max_dpar = 0;
		int max_epahr = 0;
		int max_epalr = 0;
		int max_cpar = 0;
		
		int dpar_check = 0;
		int epahr_check = 0;
		int epalr_check = 0;
		
		int cpar_cat_zero_check = 0;
		int cpar_cat_one_check = 0;
		int cpar_cat_two_check = 0;
		
		//circle area pixel
		for (int i = 0; i < circle_pixel_array_recon.length; i++) {
			sum_cpar += circle_pixel_array_recon[i];
		}
		average_cpar = sum_cpar/circle_pixel_array_recon.length;
		
		
		Arrays.sort(circle_pixel_array_recon);
		min_cpar = circle_pixel_array_recon[0];
		max_cpar = circle_pixel_array_recon[circle_pixel_array_recon.length-1];
		
		//System.out.println("CirclePixelAverage: " + String.valueOf(average_cpar));
		//System.out.println("CirclePixelMin: " + String.valueOf(min_cpar));
		//System.out.println("CirclePixelMax: " + String.valueOf(max_cpar));
		
		//dark pixel
		for (int i = 0; i < dark_pixel_array_recon.length; i++) {
			sum_dpar += dark_pixel_array_recon[i];
		}
		average_dpar = sum_dpar/dark_pixel_array_recon.length;
		
		
		Arrays.sort(dark_pixel_array_recon);
		min_dpar = dark_pixel_array_recon[0];
		max_dpar = dark_pixel_array_recon[dark_pixel_array_recon.length-1];
		
		//System.out.println("DarkPixelAverage: " + String.valueOf(average_dpar));
		//System.out.println("DarkPixelMin: " + String.valueOf(min_dpar));
		//System.out.println("DarkPixelMax: " + String.valueOf(max_dpar));
		
		//empty pixel high
		for (int i = 0; i < empty_pixel_array_high_recon.length; i++) {
			sum_epahr += empty_pixel_array_high_recon[i];
		}
		average_epahr = sum_epahr/empty_pixel_array_high_recon.length;
		
		Arrays.sort(empty_pixel_array_high_recon);
		min_epahr = empty_pixel_array_high_recon[0];
		max_epahr = empty_pixel_array_high_recon[empty_pixel_array_high_recon.length-1];
		
		//System.out.println("EmptyPixelhighAverage: " + String.valueOf(average_epahr));
		//System.out.println("EmptyPixelhighMin: " + String.valueOf(min_epahr));
		//System.out.println("EmptyPixelhighMax: " + String.valueOf(max_epahr));
		
		//empty pixel low
		for (int i = 0; i < empty_pixel_array_low_recon.length; i++) {
			sum_epalr += empty_pixel_array_low_recon[i];
		}
		average_epalr = sum_epalr/empty_pixel_array_low_recon.length;
		
		Arrays.sort(empty_pixel_array_low_recon);
		min_epalr = empty_pixel_array_low_recon[0];
		max_epalr = empty_pixel_array_low_recon[empty_pixel_array_low_recon.length-1];
		
		//System.out.println("EmptyPixellowAverage: " + String.valueOf(average_epalr));
		//System.out.println("EmptyPixellowMin: " + String.valueOf(min_epalr));
		//System.out.println("EmptyPixellowMax: " + String.valueOf(max_epalr));
		
		String entscheidung = "";
		
		//CirclePixel Cat 0
		if(average_cpar >= cpa_zero_mean - tolerance && average_cpar <= cpa_zero_mean + tolerance) {
			cpar_cat_zero_check += 2;
		}
		if(min_cpar >= cpa_zero_min - tolerance && min_cpar <= cpa_zero_mean) {
			cpar_cat_zero_check += 2;
		}
		if(max_cpar >= cpa_zero_mean && max_cpar <= cpa_zero_max + tolerance) {
			cpar_cat_zero_check += 2;
		}
				
		//CirclePixel Cat 1
		if(average_cpar >= cpa_one_mean - tolerance && average_cpar <= cpa_one_mean + tolerance) {
			cpar_cat_one_check += 2;
		}
		if(min_cpar >= cpa_one_min - tolerance && min_cpar <= cpa_zero_mean) {
			cpar_cat_one_check += 2;
		}
		if(max_cpar >= cpa_zero_mean && max_cpar <= cpa_one_max + tolerance) {
			cpar_cat_one_check += 2;
		}
				
		//CirclePixel Cat 2
		if(average_cpar >= cpa_two_mean - tolerance && average_cpar <= cpa_two_mean + tolerance) {
			cpar_cat_two_check += 2;
		}
		if(min_cpar >= cpa_two_min - tolerance && min_cpar <= cpa_zero_mean) {
			cpar_cat_two_check += 2;
		}
		if(max_cpar >= cpa_zero_mean && max_cpar <= cpa_two_max + tolerance) {
			cpar_cat_two_check += 2;
		}
		
		//DarkPixel
		if(average_dpar >= dpa_mean-tolerance && average_dpar <= dpa_mean+tolerance) {
			//DarkPixel Mittelwert im richtigen Bereich 2 Punkte
			dpar_check += 2;
		}
		if(min_dpar >= dpa_min-tolerance && min_dpar <= dpa_mean) {
			//DarkPixel Minimum im richtigen Bereich 2 Punkte
			dpar_check += 2;
		}
		if(max_dpar >= dpa_mean && max_dpar <= dpa_max+tolerance) {
			//DarkPixel Maximum im richtigen Bereich 2 Punkte
			dpar_check += 2;
		}
		
		//EmptyPixel High
		if(average_epahr >= epa_h_mean-tolerance && average_epahr <= epa_h_mean+tolerance) {
			//EmptyPixelHigh Mittelwert im richtigen Bereich 2 Punkte
			epahr_check += 2;
		}
		if(min_epahr >= epa_h_min-tolerance && min_epahr <= epa_h_mean) {
			//EmptyPixelHigh Minimum im richtigen Bereich 2 Punkte
			epahr_check += 2;
		}
		if(max_epahr >= epa_h_mean && max_epahr <= epa_h_max+tolerance) {
			//EmptyPixelHigh Maximum im richtigen Bereich 2 Punkte
			epahr_check += 2;
		}
		
		//EmptyPixel Low
		if(average_epalr >= epa_l_mean-tolerance && average_epalr <= epa_l_mean+tolerance) {
			//EmptyPixelLow Mittelwert im richtigen Bereich 2 Punkte
			epalr_check += 2;
		}
		if(min_epalr >= epa_l_min-tolerance && min_epalr <= epa_l_mean) {
			//EmptyPixelLow Minimum im richtigen Bereich 2 Punkte
			epalr_check += 2;
		}
		if(max_epalr >= epa_l_mean && max_epalr <= epa_l_max+tolerance) {
			//EmptyPixelLow Maximum im richtigen Bereich 2 Punkte
			epalr_check += 2;
		}
		
		//Logische Erkennung
		if(dpar_check > 2) {
			darkpixel = true;	
		}
		if(epahr_check >= 2) {
			emptypixel_high = true;	
		}
		if(epalr_check >= 2) {
			emptypixel_low = true;	
		}
		
		
		String[] pixel_erkennung = new String[1];
		
		pixel_erkennung[0] = cpar_cat_zero_check + "," + cpar_cat_one_check + "," + cpar_cat_two_check + "," + dpar_check + "," + epahr_check + "," + epalr_check + "," + average_cpar;
		
		return pixel_erkennung;
	}
	
	int[] extractPixelValuesNoMaskInt(ImagePlus imp_pixel){
		
		int[] dark_pixel_array_recon = new int[dark_pixel_array.length];
		int[] empty_pixel_array_high_recon = new int[empty_pixel_array_high.length];
		int[] empty_pixel_array_low_recon = new int[empty_pixel_array_low.length];
		
		int dpar_counter = 0;
		int epahr_counter = 0;
		int epalr_counter = 0;
		
		ImageProcessor ip = imp_pixel.getProcessor();
		
		int width = ip.getWidth();
		int height = ip.getHeight();
		int numPixels = width*height;
		
		int[] pixels = (int[])ip.getPixels();
		int counter = 0;
		String[] rgb_values = new String[numPixels];
		int[] pixel_array = new int[numPixels];
		
		for(int y = 0; y < height; y++){
			for(int x = 0; x < width; x++){
				//fill
				int c = ip.getPixel(x,y);
				int r = (c&0xff0000)>>16;
				int g = (c&0xff00)>>8;
				int b = c&0xff;
				
				int mean = (r+g+b)/3;
				
				pixel_array[counter] = r;
				
				counter++;
			}
		}
		return pixel_array;
	}
	
	int[] extractPixelValuesNoMaskIntManual(ImagePlus imp_pixel){
		
		ImageProcessor ip = imp_pixel.getProcessor();
		
		int width = ip.getWidth();
		int height = ip.getHeight();
		int numPixels = width*height;
		
		int[] pixels = (int[])ip.getPixels();
		int counter = 0;
		String[] rgb_values = new String[numPixels];
		int[] pixel_array = new int[numPixels];
		
		for(int y = 0; y < height; y++){
			for(int x = 0; x < width; x++){
				//fill
				int c = ip.getPixel(x,y);
				int r = (c&0xff0000)>>16;
				int g = (c&0xff00)>>8;
				int b = c&0xff;
				
				int mean = (r+g+b)/3;
				
				//if(g <= 85 && b <= 80){
				if(g <= 90 && b <= 85){
					pixel_array[counter] = 1;
				} else {
					pixel_array[counter] = 0;
				}
				
				//pixel_array[counter] = r;
				
				counter++;
			}
		}
		return pixel_array;
	}
	
	int[][] extractPixelValuesRGB(ImagePlus imp_pixel){
		
		ImageProcessor ip = imp_pixel.getProcessor();
		
		int width = ip.getWidth();
		int height = ip.getHeight();
		int numPixels = width*height;
		
		int[] pixels = (int[])ip.getPixels();
		int counter = 0;
		String[] rgb_values = new String[numPixels];
		int[][] pixel_array = new int[numPixels][3];
		
		for(int y = 0; y < height; y++){
			for(int x = 0; x < width; x++){
				//fill
				int c = ip.getPixel(x,y);
				int r = (c&0xff0000)>>16;
				int g = (c&0xff00)>>8;
				int b = c&0xff;
				
				pixel_array[counter][0] = r;
				pixel_array[counter][1] = g;
				pixel_array[counter][2] = b;
				
				//pixel_array[counter] = r;
				
				counter++;
			}
		}
		return pixel_array;
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
	
	String getFileDateandTime(ImagePlus imp) {
		ImageInfo info_image = new ImageInfo();
		String datetime = "";
		String returndatetime = "";
		
		String test_info = info_image.getImageInfo(imp);
		
		String subStr = "Date/Time Original:";
		
		int endposOfSubstr = test_info.indexOf(subStr) + subStr.length();
		if(endposOfSubstr > -1) {
			//Test auf korrektes Format und richtige Zeile in EXIFData:
			datetime = test_info.substring(endposOfSubstr, endposOfSubstr+20);
			if(!isLegalDate(datetime)) {
				datetime = "n/a";
			} else {				
				String[] split = datetime.split(" ");
				returndatetime = split[0].trim() + ":" + split[1];
			}
			
		} else {
		datetime = "n/a";	
		}
		
		return returndatetime;
	}
	
	int getFileDPI(ImagePlus imp) {
		ImageInfo info_image = new ImageInfo();
		String dpi = "";
		int returndpi = 0;
		
		String test_info = info_image.getImageInfo(imp);
		
		String subStr = "X Resolution:";
		
		dpi = "300";
		
		int endposOfSubstr = test_info.indexOf(subStr) + subStr.length();
		if(endposOfSubstr > -1) {
			dpi = test_info.substring(endposOfSubstr, endposOfSubstr+4).trim();
			System.out.println("DPI-Erkannt:" + dpi + "-Ende");
			try {
	            returndpi = Integer.parseInt(dpi);
	            System.out.println(returndpi);
	        }
	        catch (NumberFormatException e){
	            e.printStackTrace();
	        }
			
		} else {
		returndpi = 0;	
		}
		
		return returndpi;
	}
	
	boolean isLegalDate(String s) {
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
	    sdf.setLenient(false);
	    return sdf.parse(s, new ParsePosition(0)) != null;
	}
}