package Game.GameStates;


import Display.DisplayScreen;
import Display.UI.UIStringButton;
import Game.World.MapBuilder;
import Input.KeyManager;
import Input.MouseManager;
import Main.Handler;
import Resources.Images;
import Display.UI.UIImageButton;
import Display.UI.UIManager;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by AlexVR on 7/1/2018.
 */
public class MenuState extends State {

    private UIManager uiManager;
    private int background;
    private String mode= "Menu";


    private DisplayScreen display;

    private BufferStrategy bs;
    private Graphics g;
    private boolean creatingMap=false;
    public int GridWidthPixelCount,GridHeightPixelCount,DiplayHeight,DisplayWidth;
    public int GridPixelsize;
    int colorSelected = MapBuilder.boundBlock;
    Color[][] blocks;
    //Input
    private KeyManager keyManager;
    private MouseManager mouseManager;
    private boolean clicked = true;



    public MenuState(Handler handler) {
        super(handler);
        uiManager = new UIManager(handler);
        handler.getMouseManager().setUimanager(uiManager);
        background = new Random().nextInt(9);


        DisplayWidth=(handler.getWidth())+(handler.getWidth()/2);
        DiplayHeight = handler.getHeight();
        GridPixelsize = 30;
        GridHeightPixelCount = DiplayHeight/GridPixelsize;
        GridWidthPixelCount = DisplayWidth/GridPixelsize;
        blocks = new Color[GridWidthPixelCount][GridHeightPixelCount];
        keyManager = handler.getGame().keyManager;
        mouseManager = new MouseManager();


        uiManager.addObjects(new UIImageButton(handler.getWidth()/2-64, handler.getHeight()/2+(handler.getHeight()/8), 128, 64, Images.butstart, () -> {
            mode = "Select";
        }));
    }

    @Override
    public void tick() {
        if(!creatingMap) {
            handler.getMouseManager().setUimanager(uiManager);
            uiManager.tick();
            if (mode.equals("Select")) {
                mode = "Selecting";
                uiManager = new UIManager(handler);
                handler.getMouseManager().setUimanager(uiManager);

                //New Map
                uiManager.addObjects(new UIStringButton(handler.getWidth() / 2 - 64, (handler.getHeight() / 2) + (handler.getHeight() / 10) - (64), 128, 64, "New Map", () -> {
                    mode = "Menu";
                    initNew("New Map Creator", handler);

                }, handler));


                //testMap1
                uiManager.addObjects(new UIStringButton(handler.getWidth() / 2 - 64, handler.getHeight() / 2 + (handler.getHeight() / 10), 128, 64, "Map 1", () -> {
                    mode = "Menu";
                    handler.setMap(MapBuilder.createMap(Images.testMap, handler));
                    State.setState(handler.getGame().gameState);

                }, handler));

                //testmap2
                uiManager.addObjects(new UIStringButton(handler.getWidth() / 2 - 64, (handler.getHeight() / 2) + (handler.getHeight() / 10) + (64), 128, 64, "Map 2", () -> {
                    mode = "Menu";
                    handler.setMap(MapBuilder.createMap(Images.testMaptwo, handler));
                    State.setState(handler.getGame().gameState);

                }, handler));

                //other
                uiManager.addObjects(new UIStringButton(handler.getWidth() / 2 - 64, (handler.getHeight() / 2) + (handler.getHeight() / 10) + (128), 128, 64, "Other", () -> {
                    mode = "Menu";
                    JFileChooser chooser = new JFileChooser("/maps");
                    FileNameExtensionFilter filter = new FileNameExtensionFilter(
                            "JPG, & PNG Images", "jpg", "png");
                    chooser.setFileFilter(filter);
                    int returnVal = chooser.showOpenDialog(null);
                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        System.out.println("You chose to open this file: " + chooser.getSelectedFile().getAbsolutePath());
                        try {
                            handler.setMap(MapBuilder.createMap(ImageIO.read(chooser.getSelectedFile()), handler));
                            State.setState(handler.getGame().gameState);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }


                }, handler));
            }
            if (mode.equals("Selecting") && handler.getKeyManager().keyJustPressed(KeyEvent.VK_ESCAPE)) {
                mode = "Menu";
                uiManager = new UIManager(handler);
                handler.getMouseManager().setUimanager(uiManager);
                uiManager.addObjects(new UIImageButton(handler.getWidth() / 2 - 64, handler.getHeight() / 2 + (handler.getHeight() / 8), 128, 64, Images.butstart, () -> {
                    mode = "Select";
                }));
            }
        }else{
            handler.getGame().mouseManager=null;
            tickNewScreen();
            if(clicked){
                clicked = mouseManager.isLeftPressed();
            }
        }
    }

    @Override
    public void render(Graphics g) {
        if(!creatingMap) {
            g.setColor(Color.GREEN);
            g.drawImage(Images.backgrounds[background], 0, 0, handler.getWidth(), handler.getHeight(), null);
            g.drawImage(Images.title, 0, 0, handler.getWidth(), handler.getHeight(), null);
            uiManager.Render(g);
        }else{

            renderNewScreen();

        }


    }

    private void initNew(String title,Handler handler){
        display = new DisplayScreen(title + "              (H for Mapping)", DisplayWidth, DiplayHeight);
        display.getFrame().addKeyListener(keyManager);
        display.getFrame().addMouseListener(mouseManager);
        display.getFrame().addMouseMotionListener(mouseManager);
        display.getCanvas().addMouseListener(mouseManager);
        display.getCanvas().addMouseMotionListener(mouseManager);
        creatingMap = true;
        Cursor c = Toolkit.getDefaultToolkit().createCustomCursor(Images.tint(Images.Cursor,0,0,0), new Point(0, 0), "cursor1");

        display.getCanvas().setCursor(c);

    }

    private void tickNewScreen(){
        if(handler.getKeyManager().keyJustPressed(KeyEvent.VK_0)){
            Cursor c = Toolkit.getDefaultToolkit().createCustomCursor(Images.tint(Images.Cursor,1,1,1), new Point(0, 0), "cursor1");
            display.getCanvas().setCursor(c);
            colorSelected = Color.WHITE.getRGB();
        }
        if(handler.getKeyManager().keyJustPressed(KeyEvent.VK_1)){
            Cursor c = Toolkit.getDefaultToolkit().createCustomCursor(Images.tint(Images.Cursor,1,0,0), new Point(0, 0), "cursor1");
            display.getCanvas().setCursor(c);
            colorSelected = MapBuilder.mario;
        }
        if(handler.getKeyManager().keyJustPressed(KeyEvent.VK_2)){
            Cursor c = Toolkit.getDefaultToolkit().createCustomCursor(Images.tint(Images.Cursor,0,0,1), new Point(0, 0), "cursor1");
            display.getCanvas().setCursor(c);
            colorSelected = MapBuilder.breakBlock;
        }
        if(handler.getKeyManager().keyJustPressed(KeyEvent.VK_3)){
            Cursor c = Toolkit.getDefaultToolkit().createCustomCursor(Images.tint(Images.Cursor,1,1,0), new Point(0, 0), "cursor1");
            display.getCanvas().setCursor(c);
            colorSelected = MapBuilder.misteryBlock;
        }
        if(handler.getKeyManager().keyJustPressed(KeyEvent.VK_4)){
            Cursor c = Toolkit.getDefaultToolkit().createCustomCursor(Images.tint(Images.Cursor,1,0.5f,0), new Point(0, 0), "cursor1");
            display.getCanvas().setCursor(c);
            colorSelected = MapBuilder.surfaceBlock;
        }
        if(handler.getKeyManager().keyJustPressed(KeyEvent.VK_5)){
            Cursor c = Toolkit.getDefaultToolkit().createCustomCursor(Images.tint(Images.Cursor,0,0,0), new Point(0, 0), "cursor1");
            display.getCanvas().setCursor(c);
            colorSelected = MapBuilder.boundBlock;
        }


        if(mouseManager.isLeftPressed() && !clicked){
            int posX =mouseManager.getMouseX()/GridPixelsize;
            int posY =mouseManager.getMouseY()/GridPixelsize;
            System.out.println(posX + " , " +posY);
            blocks[posX][posY]=new Color(colorSelected);
            clicked=true;
        }

        if(handler.getKeyManager().keyJustPressed(KeyEvent.VK_ENTER)){

            handler.setMap(MapBuilder.createMap(createImage(GridWidthPixelCount,GridHeightPixelCount,blocks,JOptionPane.showInputDialog("Enter file name: ","Mario Heaven")), handler));
            State.setState(handler.getGame().gameState);
            creatingMap=false;
            display.getFrame().setVisible(false);
            display.getFrame().dispose();
            handler.getGame().mouseManager=new MouseManager();

        }

            if(handler.getKeyManager().keyJustPressed(KeyEvent.VK_H)){
                JOptionPane.showMessageDialog(display.getFrame(), "Number key <-> Color Mapping: \n" +
                        "0 -> Erase \n" +
                        "1 -> Mario (Red)\n" +
                        "2 -> Break Block (Blue)\n" +
                        "3 -> Mystery Block (Yellow)\n" +
                        "4 -> Surface Block (Orange)\n" +
                        "5 -> Bounds Block (Black)\n");
            }




        }



    private void renderNewScreen(){
        bs = display.getCanvas().getBufferStrategy();
        if(bs == null){
            display.getCanvas().createBufferStrategy(3);
            return;
        }
        g = bs.getDrawGraphics();
        //Clear Screen
        g.clearRect(0, 0,  handler.getWidth()+handler.getWidth()/2, handler.getHeight());

        //Draw Here!
        Graphics2D g2 = (Graphics2D) g.create();

        g.setColor(Color.white);
        g.fillRect(0,0,handler.getWidth()+(handler.getWidth()/2),handler.getHeight());

        for (int i = 0; i <= DisplayWidth; i = i + GridPixelsize) {
            g.setColor(Color.BLACK);
            g.drawLine(i,0,i,DiplayHeight);
        }
        for (int i = 0; i <= DiplayHeight; i = i + GridPixelsize) {
            g.setColor(Color.BLACK);
            g.drawLine(0, i, DisplayWidth , i);
        }
        for (int i = 0; i < GridWidthPixelCount; i++) {
            for (int j = 0; j < GridHeightPixelCount; j++) {
                if(blocks[i][j]!=null && !blocks[i][j].equals(Color.WHITE)){
                    g.setColor((blocks[i][j]));
                    g.fillRect((i*GridPixelsize),(j*GridPixelsize),GridPixelsize,GridPixelsize);
                }
            }
        }





        //End Drawing!
        bs.show();
        g.dispose();
    }

    public BufferedImage createImage(int width,int height,Color[][] blocks,String name){

        // Create buffered image object
        BufferedImage img = null;
        img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        // file object
        File f = null;

        // create random values pixel by pixel
        for (int y = 0; y < height; y++)
        {
            for (int x = 0; x < width; x++)
            {
                if(blocks[x][y]!=null) {
                    img.setRGB(x, y, blocks[x][y].getRGB());
                }else{
                    img.setRGB(x, y, Color.WHITE.getRGB());

                }
            }
        }

        // write image
        try
        {
            f = new File(System.getProperty("user.home") + "/Desktop/"+name+".png");
            ImageIO.write(img, "png", f);
        }
        catch(IOException e)
        {
            System.out.println("Error: " + e);
        }
        return img;
    }

}
