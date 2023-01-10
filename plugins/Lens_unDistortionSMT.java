import ij.*;
import ij.process.*;
import ij.gui.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.TextEvent;
import java.awt.event.TextListener;

import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import ij.plugin.frame.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

@SuppressWarnings("serial")
public class Lens_unDistortionSMT extends PlugInFrame implements ActionListener,
		ImageListener, ChangeListener {

	int larghezza;
	int altezza;
	Object firstpixel;
	private ImagePlus imp;
	private ImageProcessor current;
	private Button preview;
	private TextField k1Field;
	private TextField k2Field;
	private JPanel panelpb;
	private Double k1;
	private Double k2;
	private Label progress;
	private Label progress2;
	private Label th;
	private Button export;
	private byte[] firstpixelB;
	private Font monoFont = new Font("Monospaced", Font.PLAIN, 12);
	private Double divisore;
	private JFileChooser fc;
	private int[] resRGB;
	private byte[] res8Bit;
	private Thread[] threads;;
	private int centerX;
	private int centerY;
	private int nthread;
	private JSlider slider;
	private JSlider slider2;

	public Lens_unDistortionSMT() {
		super("Lens Distortion Correction");
		setResizable(false);

	}

	public static String now(String dateFormat) {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		return sdf.format(cal.getTime());

	}

	public void apply() {
		try {

			slider
					.setValue((int) (Double.parseDouble(k1Field.getText()) * 10.0));
			slider2
					.setValue((int) (Double.parseDouble(k2Field.getText()) * 100000.0));
			divisore = 1000000.0;
			k1 = (Double.valueOf((k1Field.getText()))) / divisore;
			k2 = (Double.valueOf((k2Field.getText()))) / divisore;

			prepareThreads(isRGB());
			long startTime = System.currentTimeMillis();
			progress.setText("Processing...");
			startAndJoin(threads);
			long estimatedTime = System.currentTimeMillis() - startTime;
			progress.setText("Time elapsed:");
			progress2.setText(Long.toString(estimatedTime) + "ms");
			th.setText("Threads used: " + nthread);
			if (isRGB())
				current.setPixels(resRGB);
			else
				current.setPixels(res8Bit);

			imp.show();
			imp.updateAndDraw();

		} catch (Exception e1) {
			IJ.showMessage("Invalid k1 or k2 input.");
		}
	}

	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if (source == preview) {
			apply();
		} else if (source == export) {

			if (k1 != null && k2 != null) {
				fc = new JFileChooser();
				int returnVal = fc.showSaveDialog(Lens_unDistortionSMT.this);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = fc.getSelectedFile();
					writeExport(file);
				}
			}

		}
	}

	public boolean isRGB() {

		int type = imp.getType();

		if ((type == ImagePlus.GRAY8) || (type == ImagePlus.GRAY16)
				|| (type == ImagePlus.GRAY32) || (type == ImagePlus.COLOR_256))
			return false;
		else if (type == ImagePlus.COLOR_RGB) {
			return true;
		}
		return false;

	}

	public void prepareThreads(final boolean rgb) {

		threads = newThreadArray();

		altezza = current.getHeight();
		larghezza = current.getWidth();

		centerX = (int) (larghezza / 2);
		centerY = (int) (altezza / 2);

		if (firstpixel != null) {
			current.setPixels(firstpixel);
		}

		if (firstpixelB != null) {
			current.setPixels(firstpixelB);
		}

		if (rgb)
			resRGB = new int[altezza * larghezza];
		else
			res8Bit = new byte[altezza * larghezza];

		final int nthread = threads.length;
		this.nthread = nthread;

		final int step = (int) (altezza / nthread);

		for (int ithread = 0, y = 0, rest = (altezza % nthread); ithread < threads.length; ithread++, y += step) {

			final int gg = y;
			final int i = ithread;
			final int resto = rest;
			final int index = ithread;

			threads[ithread] = new Thread() {

				{
					setPriority(Thread.NORM_PRIORITY);
				}

				public void run() {
					if (rgb) {
						if (index == (nthread - 1)) {
							executeRGB(gg, (step * (i + 1)) + resto);
						} else {
							executeRGB(gg, (step * (i + 1)));
						}

					} else {
						if (index == (nthread - 1)) {
							execute8bit(gg, (step * (i + 1)) + resto);
						} else {
							execute8bit(gg, (step * (i + 1)));
						}
					}
				}
			};

		}
	}

	public void writeExport(File f) {

		String html = " <title>Report Lens Undistortion v1.0</title></head>";

		html += "<body><p><strong><em>Lens Undistortion v1.0</em></strong></p>";

		html += "<p><em>Lens Undistortion v1.0 plugin is based on lens undistortion algorithm (simplified version of Brown’s model):</em></p><p><em> Xu =  Xd + ( k1 * r^2 + k2 * r^4) * (Xd - Xc)<br>Yu =  Yd + ( k1 * r^2 + k2 * r^4) * (Yd - Yc)<br><br>";

		html += "Where:</em></p><p><em> (Xd,Yd) are the distorted image position</em></p><p><em> (Xu,Yu) are the undistorted correct position. </em></p><p><em> (k1,k2)Values that depends on the camera.</em></p><p><em> (Xc, Yc) is the center of image.</em></p> <p><em> r is the radius.</em></p>";

		html += "<p>Report ----------------------------------------------------------------------------------------</p>";

		html += " <p><strong>" + Lens_unDistortionSMT.now("yyyy.MMMMM.dd GGG hh:mm aaa")
				+ "</strong></p>";
		
		html += " <p><strong>Values used:</strong></p>";
		NumberFormat nm = NumberFormat.getInstance();
		nm.setMaximumFractionDigits(25);
		html += " <p>k1: " + nm.format(k1 * divisore) + " (internal k1: "
				+ nm.format(k1) + ")" + "</p>";
		html += " <p>k2: " + nm.format(k2 * divisore) + " (internal k2: "
				+ nm.format(k2) + ")" + "</p>";
		html += " <p>&nbsp;</p>";
		html += " </body>";
		html += " </html> ";

		try {
			f = new File(f.getPath() + ".html");
			FileWriter fw = new FileWriter(f);
			BufferedWriter out = new BufferedWriter(fw);
			out.write(html);
			out.close();
			Desktop.getDesktop().open(f);
		} catch (IOException e) {
		}

	}

	@SuppressWarnings("deprecation")
	public void run(String arg) {

		ImagePlus.addImageListener(this);
		imp = WindowManager.getCurrentImage();

		if (imp == null) {
			IJ.beep();
			IJ.showStatus("No image");
			IJ.noImage();
			return;
		} else {
			int type = imp.getType();
			current = imp.getProcessor();

			if ((type == ImagePlus.GRAY8) || (type == ImagePlus.GRAY16)
					|| (type == ImagePlus.GRAY32)
					|| (type == ImagePlus.COLOR_256))
				firstpixelB = (byte[]) current.getPixels();

			else if (type == ImagePlus.COLOR_RGB) {
				firstpixel = (int[]) current.getPixels();
			}
		}

		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		setLayout(gridbag);

		// Panel title
		Panel paneltitle = new Panel();
		Label title = new Label("Lens Distortion Correction", Label.CENTER);
		title.setFont(monoFont);
		paneltitle.add(title);
		c.gridx = 0;
		int y = 0;
		c.gridy = y++;
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.CENTER;
		c.insets = new Insets(5, 10, 0, 10);// top,left,bottom,right
		gridbag.setConstraints(paneltitle, c);
		add(paneltitle);

		// Slider k1
		JPanel panelk1 = new JPanel();
		Label tmp = new Label("K1", Label.CENTER);
		tmp.setFont(monoFont);
		panelk1.add(tmp);
		k1Field = new TextField("0", 10);
		panelk1.add(k1Field);
		slider = new JSlider(0, -1000, 1000, 0);
		slider.setValue(255);
		slider.setEnabled(true);
		panelk1.add(slider);

		// Slider k2
		JPanel panelk2 = new JPanel();
		Label tmp2 = new Label("K2", Label.CENTER);
		tmp2.setFont(monoFont);
		panelk2.add(tmp2);
		k2Field = new TextField("0", 10);
		panelk2.add(k2Field);
		slider2 = new JSlider(0, -100, 100, 0);
		slider2.setValue(255);
		slider2.setEnabled(true);
		panelk2.add(slider2);

		// Progress
		panelpb = new JPanel();
		progress = new Label("   Progress: ", Label.CENTER);
		progress.setFont(monoFont);
		panelpb.add(progress);

		// Progress2
		JPanel panelpb2 = new JPanel();
		progress2 = new Label("                ", Label.CENTER);
		progress2.setFont(monoFont);
		panelpb2.add(progress2);

		// Infos
		JPanel info = new JPanel();
		th = new Label("                ", Label.CENTER);
		th.setFont(monoFont);
		info.add(th);

		// panel Preview
		Panel panelPreview = new Panel();
		preview = new Button("Apply");
		preview.addActionListener(this);
		panelPreview.add(preview);

		// panel Export html
		Panel exportPanel = new Panel();
		export = new Button("Html repot");
		export.addActionListener(this);
		exportPanel.add(export);

		// panel container
		Panel container = new Panel();
		container.setLayout(new GridLayout(7, 3));
		c.gridy = y++;
		c.insets = new Insets(10, 10, 10, 10);
		gridbag.setConstraints(container, c);

		container.add(panelk1);
		container.add(panelk2);
		container.add(panelPreview);
		container.add(exportPanel);
		container.add(panelpb);
		container.add(panelpb2);

		container.add(info);

		add(container);

		slider.setValue(0);
		slider2.setValue(0);
		slider.addChangeListener(this);
		slider2.addChangeListener(this);

		// - - - - - - - - - - - - - - - - - -
		pack();
		GUI.center(this);
		show();

	}

	public void execute8bit(int startA, int endA) {

		for (int y = startA; y < endA; y++) {
			for (int x = 0; x < larghezza; x++) {
				// Editing pixel at x,y position
				res8Bit[y * larghezza + x] = core8bit(x, y);
			}
		}

	}

	public void executeRGB(int startA, int endA) {

		for (int y = startA; y < endA; y++) {
			for (int x = 0; x < larghezza; x++) {
				// Editing pixel at x,y position
				resRGB[y * larghezza + x] = coreRGB(x, y);
			}
		}

	}

	private Byte core8bit(int Xd, int Yd) {

		double r = distanzaPixel(Xd, Yd, centerX, centerY);
		int Xu = (int) (Xd + ((k1 * Math.pow(r, 2) + (k2 * Math.pow(r, 4))))
				* (Xd - centerX));
		int Yu = (int) (Yd + ((k1 * Math.pow(r, 2) + (k2 * Math.pow(r, 4))))
				* (Yd - centerY));

		return (byte) current.getPixel(Xu, Yu);

	}

	private int coreRGB(int Xd, int Yd) {
		double r = distanzaPixel(Xd, Yd, centerX, centerY);

		int Xu = (int) (Xd + ((k1 * Math.pow(r, 2) + (k2 * Math.pow(r, 4))))
				* (Xd - centerX));
		int Yu = (int) (Yd + ((k1 * Math.pow(r, 2) + (k2 * Math.pow(r, 4))))
				* (Yd - centerY));

		return (int) current.getPixel(Xu, Yu);
	}

	private double distanzaPixel(int x1, int y1, int x2, int y2) {
		return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
	}

	@Override
	public void imageClosed(ImagePlus imp) {
		// Memory clean
		current = null;
		firstpixel = null;
		firstpixelB = null;
		this.close();

	}

	@Override
	public void imageOpened(ImagePlus imp) {
	}

	@Override
	public void imageUpdated(ImagePlus imp) {
	}

	private Thread[] newThreadArray() {
		int n_cpus = Runtime.getRuntime().availableProcessors();
		return new Thread[n_cpus];
	}

	public static void startAndJoin(Thread[] threads) {
		for (int ithread = 0; ithread < threads.length; ++ithread) {
			threads[ithread].setPriority(Thread.NORM_PRIORITY);
			threads[ithread].start();
		}

		try {
			for (int ithread = 0; ithread < threads.length; ++ithread)
				threads[ithread].join();
		} catch (InterruptedException ie) {
			throw new RuntimeException(ie);
		}
	}

	@Override
	public void stateChanged(ChangeEvent c) {
		double value = slider.getValue() / 10.0;
		double value2 = (slider2.getValue() * (1 / 100000.0));
		k1Field.setText("" + value);
		NumberFormat nm = NumberFormat.getInstance();
		nm.setMaximumFractionDigits(25);
		k2Field.setText("" + nm.format(value2).replace(",", "."));
		apply();

	}

}
