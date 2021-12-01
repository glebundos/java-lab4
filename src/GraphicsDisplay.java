import javax.swing.*;

public class GraphicsDisplay extends JPanel {
    private double[][] graphicsData;
    private int[][] graphicsDataI;


    public void showGraphics(double[][] graphicsData) {
        this.graphicsData = graphicsData;
        graphicsDataI = new int[graphicsData.length][2];
        repaint();
    }
}
