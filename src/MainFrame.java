import javax.swing.*;
import java.awt.*;
import java.io.*;

public class MainFrame extends JFrame {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private boolean fileLoaded = false;
    private JFileChooser fileChooser = null;
    private GraphicsDisplay display = new GraphicsDisplay();

    public static void main(String[] args) {
        MainFrame frame = new MainFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    public MainFrame() {
        super("График");
        File init_ = new File("init");
        openGraphics(init_);
        setSize(WIDTH, HEIGHT);
        Dimension ss = new Dimension();
        ss.height = 60;
        ss.width = 40;
        Toolkit kit = Toolkit.getDefaultToolkit();
        setLocation((kit.getScreenSize().width - WIDTH) / 2, (kit.getScreenSize().height - HEIGHT) / 2);
    }

    protected void openGraphics(File selectedFile) {
        try {
            DataInputStream in = new DataInputStream(new FileInputStream(selectedFile));
            double[][] graphicsData = new double[in.available() / (Double.SIZE / 8) / 2][];
            int i = 0;
            while (in.available() > 0) {
                Double x = in.readDouble();
                Double y = in.readDouble();
                graphicsData[i++] = new double[]{x, y};
            }
            if (graphicsData != null && graphicsData.length > 0) {
                fileLoaded = true;
                display.showGraphics(graphicsData);
                display.repaint();
            }
            in.close();
        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(MainFrame.this, "Указанный файл не найден", "Ошибка загрузки данных", JOptionPane.WARNING_MESSAGE);
            return;
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(MainFrame.this, "Ошибка чтения координат точек из файла", "Ошибка загрузки данных", JOptionPane.WARNING_MESSAGE);
            return;
        }
    }
}