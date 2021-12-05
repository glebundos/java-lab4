import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.LinkedList;
import javax.swing.*;



@SuppressWarnings("serial")

public class GraphicsDisplay extends JPanel {

    class GraphPoint {
        double xd;
        double yd;
        int x;
        int y;
        int n;
    }

    private Double[][] graphicsData;

    private boolean showAxis = true;
    private boolean showMarkers = true;
    private boolean transform = false;
    private boolean showGrid = true;
    private double minX;
    private double maxX;
    private double minY;
    private double maxY;
    private double scale;

    private DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance();
    private BasicStroke graphicsStroke;
    private BasicStroke axisStroke;
    private boolean turnGraph = false;
    private BasicStroke markerStroke;
    private Font axisFont;
    private Font captionFont;
    private Font smallfont;
    private GraphPoint SMP;
    public GraphicsDisplay() {


        setBackground(Color.WHITE);



        graphicsStroke = new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 10.0f, new float[] {10, 10, 10, 10, 10, 10, 30 , 30, 30,30,30,30}, 0.0f);


        axisStroke = new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, null, 0.0f);


        markerStroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, null, 0.0f);



        axisFont = new Font("Serif", Font.BOLD, 36);
    }
    
    

    public void showGraphics(Double[][] graphicsData) {

        this.graphicsData = graphicsData;

        repaint();
    }

    

    public void setShowAxis(boolean showAxis) {
        this.showAxis = showAxis;
        repaint();

    }

    public void setTransform(boolean transform) {
        this.transform = transform;
        repaint();
    }

    public void setShowGrid(boolean showGrid) {
        this.showGrid = showGrid;
        repaint();
    }

    public int getDataLenght() {
        return graphicsData.length;
    }

    public double getValue(int i, int j) {
        return graphicsData[i][j];
    }
    public void setShowMarkers(boolean showMarkers) {
        this.showMarkers = showMarkers;
        repaint();
    }
    

    @Override
    public void paintComponent (Graphics g){
        
        
        
        
        super.paintComponent(g);

        if (graphicsData == null || graphicsData.length == 0) return;



        minX = graphicsData[0][0];
        maxX = graphicsData[graphicsData.length - 1][0];
        minY = graphicsData[0][1];
        maxY = minY;

        for (int i = 1; i < graphicsData.length; i++) {
            if (graphicsData[i][1] < minY) {
                minY = graphicsData[i][1];
            }
            if (graphicsData[i][1] > maxY) {
                maxY = graphicsData[i][1];
            }
        }

        if (!turnGraph) {
            double scaleX = getSize().getWidth() / (maxX - minX);
            double scaleY = getSize().getHeight() / (maxY - minY);


            scale = Math.min(scaleX, scaleY);

            if (scale == scaleX) {

                double yIncrement = (getSize().getHeight() / scale - (maxY - minY)) / 2;
                maxY += yIncrement;
                minY -= yIncrement;
            }
            if (scale == scaleY) {

                double xIncrement = (getSize().getWidth() / scale - (maxX - minX)) / 4;
                maxX += xIncrement;
                minX -= xIncrement;
            }
        } else {
            double scaleX = getSize().getHeight() / (maxX - minX);
            double scaleY = getSize().getWidth() / (maxY - minY);
            scale = Math.min(scaleX, scaleY);
            if (scale == scaleY) {
                double xIncrement = (getSize().getHeight() / scale - (maxX - minX)) / 2;
                maxX += xIncrement;
                minX -= xIncrement;
            }
            if (scale == scaleX) {
                double yIncrement = (getSize().getWidth() / scale - (maxY - minY)) / 2;
                maxY += yIncrement;
                minY -= yIncrement;
            }
        }

        Graphics2D canvas = (Graphics2D) g;
        Stroke oldStroke = canvas.getStroke();
        Color oldColor = canvas.getColor();
        Paint oldPaint = canvas.getPaint();
        Font oldFont = canvas.getFont();



        if (turnGraph) {
            rotatePanel(canvas);
        }
        if (showAxis) paintAxis(canvas);

        paintGraphics(canvas);

        if (showMarkers) paintMarkers(canvas);

        canvas.setFont(oldFont);
        canvas.setPaint(oldPaint);
        canvas.setColor(oldColor);
        canvas.setStroke(oldStroke);
    }

    protected void paintHint(Graphics2D canvas) {
        Color oldColor = canvas.getColor();
        canvas.setColor(Color.MAGENTA);
        StringBuffer label = new StringBuffer();
        label.append("X=");
        label.append(formatter.format((SMP.xd)));
        label.append(", Y=");
        label.append(formatter.format((SMP.yd)));
        FontRenderContext context = canvas.getFontRenderContext();
        Rectangle2D bounds = captionFont.getStringBounds(label.toString(), context);
        if (!transform) {
            int dy = -10;
            int dx = +7;
            if (SMP.y < bounds.getHeight())
                dy = +13;
            if (getWidth() < bounds.getWidth() + SMP.x + 20)
                dx = -(int) bounds.getWidth() - 15;
            canvas.drawString(label.toString(), SMP.x + dx, SMP.y + dy);
        } else {
            int dy = 10;
            int dx = -7;
            if (SMP.x < 10)
                dx = +13;
            if (SMP.y < bounds.getWidth() + 20)
                dy = -(int) bounds.getWidth() - 15;
            canvas.drawString(label.toString(), getHeight() - SMP.y + dy, SMP.x + dx);
        }
        canvas.setColor(oldColor);
    }

    
    protected void paintGraphics(Graphics2D canvas) {
        canvas.setStroke(graphicsStroke);
        canvas.setColor(Color.magenta);
        GeneralPath graphics = new GeneralPath();
        for (int i = 0; i < graphicsData.length; i++) {
            Point2D.Double point = xyToPoint(graphicsData[i][0], graphicsData[i][1]);
            if (i > 0) {
                graphics.lineTo(point.getX(), point.getY());
            } else {
                graphics.moveTo(point.getX(), point.getY());
            }
        }

        canvas.draw(graphics);
    }

    protected void rotatePanel(Graphics2D canvas){
        canvas.translate(0, getHeight());
        canvas.rotate(-Math.PI/2);
    }
    protected void paintMarkers(Graphics2D canvas) {

        canvas.setStroke(markerStroke);

        canvas.setColor(Color.BLUE);

        canvas.setPaint(Color.BLUE);

        for (Double[] point : graphicsData) {

            int size = 5;
            Ellipse2D.Double marker = new Ellipse2D.Double();
            Point2D.Double center = xyToPoint(point[0], point[1]);
            

            Point2D.Double corner = shiftPoint(center, size, size);

            marker.setFrameFromCenter(center, corner);


            Line2D.Double line = new Line2D.Double(shiftPoint(center, -size, 0), shiftPoint(center, size, 0));
            Boolean highervalue = true;
            DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance();
            formatter.setMaximumFractionDigits(2);
            DecimalFormatSymbols dottedDouble =
                    formatter.getDecimalFormatSymbols();
            dottedDouble.setDecimalSeparator('.');
            formatter.setDecimalFormatSymbols(dottedDouble);
            String temp = formatter.format(Math.abs(point[1]));
            temp = temp.replace(".", "");
            
            for (int i = 0; i < temp.length() - 1; i++) {

                if (temp.charAt(i) != 46 && (int) temp.charAt(i) > (int) temp.charAt(i + 1)) {
                    highervalue = false;
                    break;
                }
            }
            if (highervalue) {
                canvas.setColor(Color.BLACK);
            }
            canvas.draw(line);
            line.setLine(shiftPoint(center, 0, -size), shiftPoint(center, 0, size));
            canvas.draw(line);
            canvas.draw(marker); 
            canvas.setColor(Color.BLUE);

        }
    }



    
    protected void paintAxis(Graphics2D canvas) {

        canvas.setStroke(axisStroke);

        canvas.setColor(Color.BLACK);

        canvas.setPaint(Color.BLACK);

        canvas.setFont(axisFont);


        FontRenderContext context = canvas.getFontRenderContext();

        if (minX<=0.0 && maxX>=0.0) {




            canvas.draw(new Line2D.Double(xyToPoint(0, maxY),

                    xyToPoint(0, minY)));


            GeneralPath arrow = new GeneralPath();


            Point2D.Double lineEnd = xyToPoint(0, maxY);
            arrow.moveTo(lineEnd.getX(), lineEnd.getY());


            arrow.lineTo(arrow.getCurrentPoint().getX()+5,

                    arrow.getCurrentPoint().getY()+20);



            arrow.lineTo(arrow.getCurrentPoint().getX()-10,

                    arrow.getCurrentPoint().getY());


            arrow.closePath();
            canvas.draw(arrow); 
            canvas.fill(arrow); 


            Rectangle2D bounds = axisFont.getStringBounds("y", context);
            Point2D.Double labelPos = xyToPoint(0, maxY);

            canvas.drawString("y", (float)labelPos.getX() + 10,

                    (float)(labelPos.getY() - bounds.getY()));

            Rectangle2D centerBounds = axisFont.getStringBounds("0", context);
            Point2D.Double centerLabelPos = xyToPoint(0, 0);
            canvas.drawString("0", (float)centerLabelPos.getX() + 10,
                    (float)(centerLabelPos.getY() - centerBounds.getY()));
        }

        if (minY<=0.0 && maxY>=0.0) {



            canvas.draw(new Line2D.Double(xyToPoint(minX, 0),

                    xyToPoint(maxX, 0)));


            GeneralPath arrow = new GeneralPath();


            Point2D.Double lineEnd = xyToPoint(maxX, 0);
            arrow.moveTo(lineEnd.getX(), lineEnd.getY());


            arrow.lineTo(arrow.getCurrentPoint().getX()-20,

                    arrow.getCurrentPoint().getY()-5);



            arrow.lineTo(arrow.getCurrentPoint().getX(),

                    arrow.getCurrentPoint().getY()+10);


            arrow.closePath();
            canvas.draw(arrow); 
            canvas.fill(arrow); 


            Rectangle2D bounds = axisFont.getStringBounds("x", context);
            Point2D.Double labelPos = xyToPoint(maxX, 0);

            canvas.drawString("x", (float)(labelPos.getX() -
                    bounds.getWidth() - 10), (float)(labelPos.getY() + bounds.getY()));

        }
    }
    protected Point2D.Double xyToPoint(double x, double y) {

        double deltaX = x - minX;

        double deltaY = maxY - y;
        return new Point2D.Double(deltaX*scale, deltaY*scale);
    }
    
    
    
    
    protected Point2D.Double shiftPoint(Point2D.Double src, double deltaX,
                                        double deltaY) {


        Point2D.Double dest = new Point2D.Double();


        dest.setLocation(src.getX() + deltaX, src.getY() + deltaY);
        return dest;
    }
    public void setTurnGrid(boolean turnGraph) {
        this.turnGraph = turnGraph;
        System.out.println(turnGraph);
        repaint();
    }
}