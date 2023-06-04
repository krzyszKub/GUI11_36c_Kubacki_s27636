import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.MouseInfo;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ArrayList;

public class SimpleDrawEditor extends JFrame {
    private JPanel drawingArea;
    private JLabel statusLabel;

    private PreviousPoint previousPoint = null;
    private JPanel mainPanel = new JPanel(new BorderLayout());
    private boolean isModified;
    private JLabel fileStatusLabel;

    private String fileSavedName = null;
    private String title = "Simple Draw";
    private java.util.List<Figure> figures;
    private BufferedImage loadedImage = null;

    private String fileSavedPath = null;

    private Color currentColor = Color.BLACK;

    private String currentTool;

    private Color getRandomColor() {
        int r = (int) (Math.random() * 256);
        int g = (int) (Math.random() * 256);
        int b = (int) (Math.random() * 256);
        return new Color(r, g, b);
    }

    public SimpleDrawEditor() {
        setTitle(title);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        drawingArea = new JPanel(){
            @Override
            protected void paintComponent(Graphics g) {
                //zapewnienie ze narysowane obrazy nie znikna po zmianie narzedzia i nie nzniknie wczytanmy obrazz

                super.paintComponent(g);
                if (loadedImage != null){
                    g.drawImage(loadedImage, 0, 0, null);
                }

                for (Figure figure : figures) {
                    figure.paint(g);
                }
            }
        };;
        drawingArea.setBackground(Color.WHITE);
        drawingArea.setPreferredSize(new Dimension(640, 480));
        mainPanel.add(drawingArea, BorderLayout.CENTER);

        figures = new ArrayList<>();
        // ustawienie paska stanu na dole ramki
        fileStatusLabel = new JLabel("New");
        statusLabel = new JLabel();
        statusLabel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        statusLabel.add(fileStatusLabel);
        statusLabel.setPreferredSize(new Dimension(100, 30));
        mainPanel.add(statusLabel, BorderLayout.SOUTH);

        add(mainPanel);

        // Ustawienie focusa na drwaing area, obsluga przysiku F1 i przeciagniecia myszka
        drawingArea.setFocusable(true);
        drawingArea.requestFocusInWindow();
        drawingArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int x = MouseInfo.getPointerInfo().getLocation().x - drawingArea.getLocationOnScreen().x;
                int y = MouseInfo.getPointerInfo().getLocation().y - drawingArea.getLocationOnScreen().y;
                if (e.getKeyCode() == KeyEvent.VK_F1) {
                    if (statusLabel.getText().equals("Circle")) {
                        Color color = getRandomColor();
                        Circle circle = new Circle(x, y, color, 50); // Promień koła ustawiony na 30 pikseli (wartość stała)
                        figures.add(circle);

                        Graphics g = drawingArea.getGraphics();
                        circle.paint(g);
                    } else if (statusLabel.getText().equals("Square")) {
                        Color color = getRandomColor();
                        Square square = new Square(x, y, color, 50); // Promień kwadratu ustawiony na 50 pikseli (wartość stała)
                        figures.add(square);
                        Graphics g = drawingArea.getGraphics();
                        square.paint(g);
                    }
                    if (!isModified){
                        fileStatusLabel.setText("Modified");
                        isModified = true;
                    }
                }
                if (e.getKeyCode() == KeyEvent.VK_D){
                    for (Figure f : figures) {
                        if (f.isPointInside(x, y)){
                            figures.remove(f);
                            drawingArea.repaint();
                            break;
                        }
                    }
                    if (figures.isEmpty()){
                        fileStatusLabel.setText("New");
                    }
                }
            }
        });
        drawingArea.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (statusLabel.getText().equals("Pen")) {
                    int x = e.getX();
                    int y = e.getY();

                    Color color = currentColor;
                    if (previousPoint != null) {
                        Figure line = new Line(x, y, color, previousPoint, 3);
                        figures.add(line);
                        drawingArea.repaint();
                    }
                    previousPoint =new PreviousPoint(x,y);
                    if (!isModified){
                        fileStatusLabel.setText("Modified");
                        isModified = true;
                    }

                }
            }
        });



        // Tworzenie menu rozwijalnego
        JMenuBar menuBar = new JMenuBar();

        // tworzenie menu File
        JMenu fileMenu = new JMenu("File");
        JMenuItem openMenuItem = new JMenuItem("Open");
        JMenuItem saveMenuItem = new JMenuItem("Save");
        JMenuItem saveAsMenuItem = new JMenuItem("Save As...");
        JMenuItem quitMenuItem = new JMenuItem("Quit");

        fileMenu.add(openMenuItem);
        fileMenu.add(saveMenuItem);
        fileMenu.add(saveAsMenuItem);
        fileMenu.addSeparator();
        fileMenu.add(quitMenuItem);



        // utworzenie draw menu i checkboxes
        JMenu drawMenu = new JMenu("Draw");
        JMenu figureSubMenu = new JMenu("Figure");
        JCheckBoxMenuItem circleMenuItem = new JCheckBoxMenuItem("Circle");
        JCheckBoxMenuItem squareMenuItem = new JCheckBoxMenuItem("Square");
        JCheckBoxMenuItem penMenuItem = new JCheckBoxMenuItem("Pen");

        //draw menu tool bar dodanie elementow
        drawMenu.add(figureSubMenu);
        figureSubMenu.add(circleMenuItem);
        figureSubMenu.add(squareMenuItem);
        figureSubMenu.add(penMenuItem);

        JMenuItem colorMenuItem = new JMenuItem("Color");
        JMenuItem clearMenuItem = new JMenuItem("Clear");

        drawMenu.add(colorMenuItem);
        drawMenu.addSeparator();
        drawMenu.add(clearMenuItem);

        menuBar.add(fileMenu);
        menuBar.add(drawMenu);
        setJMenuBar(menuBar);

        //zapewnienie ze tylko jeden checkBox naraz jest zaznaczony
        circleMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                circleMenuItem.setSelected(true);

                squareMenuItem.setSelected(false);
                penMenuItem.setSelected(false);
            }
        });

        squareMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                squareMenuItem.setSelected(true);


                circleMenuItem.setSelected(false);
                penMenuItem.setSelected(false);
            }
        });
        penMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                penMenuItem.setSelected(true);

                squareMenuItem.setSelected(false);
                circleMenuItem.setSelected(false);
            }
        });


        // Mnemoniki, zmiany statusu, obsluga i akceleracje dla elementow menu
        openMenuItem.setMnemonic(KeyEvent.VK_O);
        openMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
        openMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int option = fileChooser.showOpenDialog(SimpleDrawEditor.this);
                if (option == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    openFile(file);
                }
            }
        });


        saveMenuItem.setMnemonic(KeyEvent.VK_S);
        saveMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
        saveMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (fileSavedName != null) {
                    saveFile();
                } else {
                    saveAs();
                }
            }
        });


        saveAsMenuItem.setMnemonic(KeyEvent.VK_S);
        saveAsMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx() | KeyEvent.SHIFT_DOWN_MASK));
        saveAsMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveAs();
            }
        });

        quitMenuItem.setMnemonic(KeyEvent.VK_Q);
        quitMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
        quitMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                quit();
            }
        });

        circleMenuItem.setMnemonic(KeyEvent.VK_C);
        circleMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
        circleMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                statusLabel.setText("Circle");
                currentTool = "Circle";


                drawingArea.requestFocusInWindow();
                drawingArea.repaint();
            }
        });

        squareMenuItem.setMnemonic(KeyEvent.VK_R);
        squareMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
        squareMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentTool = "Square";
                statusLabel.setText("Square");

                drawingArea.requestFocusInWindow();
                drawingArea.repaint();
            }
        });

        penMenuItem.setMnemonic(KeyEvent.VK_E);
        penMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
        penMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentTool = "Pen";
                statusLabel.setText("Pen");

                drawingArea.requestFocusInWindow();
            }
        });

        colorMenuItem.setMnemonic(KeyEvent.VK_C);
        colorMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx() | KeyEvent.SHIFT_DOWN_MASK));
        colorMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentColor = JColorChooser.showDialog(SimpleDrawEditor.this, "Choose Color", Color.BLACK);
            }
        });

        clearMenuItem.setMnemonic(KeyEvent.VK_N);
        clearMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx() | KeyEvent.SHIFT_DOWN_MASK));
        clearMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentTool = "Clear";
                fileStatusLabel.setText("New");
                clearDrawingArea();
            }
        });

        pack();
    }

    private void openFile(File file) {
        try {
            loadedImage = ImageIO.read(file);

            Graphics2D g = (Graphics2D) drawingArea.getGraphics();
            g.drawImage(loadedImage, 0, 0, null);

            fileStatusLabel.setText("New");
            isModified = false;
            currentTool = "Open";

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveFile() {
            try {
                File file = new File(fileSavedPath);
                BufferedImage image = new BufferedImage(drawingArea.getWidth(), drawingArea.getHeight(), BufferedImage.TYPE_BYTE_INDEXED);
                Graphics2D g = image.createGraphics();
                drawingArea.printAll(g);
                ImageIO.write(image, "png", new File(fileSavedPath+".png"));
                setTitle(title+": "+file.getName()+".png");
                fileStatusLabel.setText("Saved");
            } catch (IOException e) {
                e.printStackTrace();
            }

        isModified = false;
    }

    private void saveAs() {
        JFileChooser fileChooser = new JFileChooser();
        int option = fileChooser.showSaveDialog(SimpleDrawEditor.this);
        if (option == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            fileSavedPath = file.getPath();
            try {
                BufferedImage image = new BufferedImage(drawingArea.getWidth(), drawingArea.getHeight(), BufferedImage.TYPE_BYTE_INDEXED);
                Graphics2D g = image.createGraphics();
                drawingArea.printAll(g);
                ImageIO.write(image, "png", new File(fileSavedPath+".png"));
                fileSavedName = file.getName();
                setTitle(title+": "+file.getName()+".png");
                fileStatusLabel.setText("Saved");
                isModified = false;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void clearDrawingArea() {
        //zapewnia ze nie czysci obrazu pobranego przez open
        if (!currentTool.equals("Open")) {
            figures.clear();
            isModified = false;
            fileStatusLabel.setText("New");

            drawingArea.repaint();
        }
    }

    private void quit() {
        System.exit(0);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                SimpleDrawEditor editor = new SimpleDrawEditor();
                editor.setVisible(true);
            }
        });
    }
}

