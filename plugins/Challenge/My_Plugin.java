import ij.*;
import ij.process.*;
import ij.gui.*;
import java.awt.*;
import ij.plugin.*;

public class My_Plugin implements PlugIn {

	public void run(String arg) {
		ImagePlus imp = IJ.getImage();
		IJ.run(imp, "Histogram", "bla");
	}

}
