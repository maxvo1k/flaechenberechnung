import utility.HtmlReport;
import utility.NucleiSobel;
import ij.*;
import ij.process.*;
import ij.gui.*;
import java.awt.*;
import ij.plugin.filter.*;

/**
 *
 * Plugin che estrae i bordi con il filtro di Sobel
 *
 * @author Tommaso Testa, Salvatore Adriano Zappala', Salvo Scalia
 */
public class Sobel_Plugin implements PlugInFilter{
    ImagePlus imp;
    HtmlReport report;
    String directory;
    String nomeImg;
    
    public int setup(String arg, ImagePlus imp) {
        this.imp=imp;
        directory = imp.getOriginalFileInfo().directory;
        nomeImg = imp.getTitle();
        report = new HtmlReport( directory+nomeImg );
        return DOES_RGB+DOES_8G;
    }

    public void run(ImageProcessor ip) {        
        boolean orizz;
        boolean verti;
        String[] items = {"3", "5", "7"};
        String apertura;
        do{
            // mostra la finestra per l'inserimento dei parametri
            GenericDialog window = new GenericDialog("Sobel");
            window.addCheckbox("Orizzontale", true);
            window.addCheckbox("Verticale", false);
            window.addChoice("Apertura", items, null);
            window.showDialog();
            
            if(window.wasCanceled())
                return;
            
            orizz = window.getNextBoolean();
            verti = window.getNextBoolean();
            if((orizz ^ verti)==false)
                IJ.error("Errore", "Devi selezionare una delle due direzioni");
            
            apertura=window.getNextChoice();
        }while((orizz ^ verti)==false); // EFFETTUA L'XOR
        
        String direzione;
        if(orizz)
            direzione = "Orizzontale";
        else
            direzione = "Verticale";
        
        float[] kernel;
        if(orizz){
            kernel = NucleiSobel.getInstance().getSobel(Integer.parseInt(apertura), "Orizzontale");
        }
        else{
            kernel = NucleiSobel.getInstance().getSobel(Integer.parseInt(apertura), "Verticale");
        }
        // effettua la convoluzione
        ip.convolve(kernel, Integer.parseInt(apertura), Integer.parseInt(apertura));
        // aggiorna l'immagine
        imp.updateAndDraw();
        // genera il report
        createReport(direzione, apertura);
    }
    
    private void createReport(String direzione, String apertura){
        report.setOperation("Sobel Plugin");
        report.setSummary("Estrae i bordi con il filtro di Sobel."); 
        report.setDetails("\"Sobel\" calcola la derivata dell'immagine effettuando la "
                + "convoluzione dell'immagine con l'appropriato nucleo di Sobel.");
        report.addParameter("Direzione", "Direzione princiaple dei bordi da estrarre.", direzione);
        report.addParameter("Apertura", "Dimensione del nucleo di Sobel.", apertura);
        report.addReference("Anil. K. Jain, Fundamentals of Digital Image Processing, Prentice Hall, pp. 347-357, 1989."); 
        
        report.generateReport("Report_Sobel.html");
    }
}
