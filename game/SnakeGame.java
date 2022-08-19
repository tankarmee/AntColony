package game;

import core.entity.Dot;
import core.keyboard.Keyboard;
import core.keyboard.Keycode;
import core.math.SymbolVector;
import core.scene.Scene;
import core.Game;
import core.MainWindow;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Random;

public class SnakeGame extends Keyboard implements Game {

    private MainWindow mainWindow;

    Screens screens = Screens.MAINMENU;

    private final int XLEN = 100;
    private final int YLEN = 50;

    private int clock = 30;

    private final Random random = new Random();

    private final Scene scene = new Scene(XLEN,YLEN);
    private final Scene mainScene = new Scene(XLEN,YLEN);
    private final Scene pauseScene = new Scene(XLEN,YLEN);

    private int born = 4;

    private int collestedFruits = 0;

    private final Dot house = new Dot('o', 11,10);

    private final ArrayList<Ant> ants = new ArrayList<>();
    private final ArrayList<Fruit> eats = new ArrayList<>();

    @Override
    public void start() {
        mainWindow = new MainWindow(700,700);
        keyboardAdd(mainWindow);
        scene.fill('.');
        mainScene.fill('~');
        pauseScene.fill('_');
        bornAnt();
        bornEat();
    }

    @Override
    public void update() throws InterruptedException {
        while (true){
            if(screens == Screens.MAINMENU){
                mainScene.setCell('P', 10,10);
                mainScene.setCell('r', 11,10);
                mainScene.setCell('e', 12,10);
                mainScene.setCell('s', 13,10);
                mainScene.setCell('s', 14,10);
                mainScene.setCell('P', 16,10);
                mainScene.setCell('t', 18,10);
                mainScene.setCell('o', 19,10);
                mainScene.setCell('p', 21,10);
                mainScene.setCell('l', 22,10);
                mainScene.setCell('a', 23,10);
                mainScene.setCell('y', 24,10);

                mainWindow.drawScene(mainScene);

            }
            if(screens == Screens.PAUSE){

                pauseScene.setCell('P', 10,10);
                pauseScene.setCell('a', 11,10);
                pauseScene.setCell('u', 12,10);
                pauseScene.setCell('s', 13,10);
                pauseScene.setCell('e', 14,10);

                mainWindow.drawScene(pauseScene);
            }
            if(screens == Screens.GAME){
                scene.clear();

                for(Dot eat : eats){
                    scene.draw(eat);
                }
                for(Ant ant : ants) {
                    scene.draw(ant);
                    if(ant.state == Ant.finding.FINDING) {
                        setTarget(ant);
                        ant.state = Ant.finding.MOVETOEAT;
                    }
                    else if(ant.state == Ant.finding.MOVETOEAT){
                        try {
                            ant.moveTo(eats.get(ant.target_id).getCorX(), eats.get(ant.target_id).getCorY());
                            if (ant.getCorX() == eats.get(ant.target_id).getCorX() && ant.getCorY() == eats.get(ant.target_id).getCorY()) {
                                eats.remove(ant.target_id);
                                ant.state = Ant.finding.MOVETOHOUSE;
                            }
                        } catch (IndexOutOfBoundsException e){
                            ant.target_id--;
                        }

                    }
                    else if(ant.state == Ant.finding.MOVETOHOUSE){
                        ant.moveTo(house.getCorX(), house.getCorY());
                        if(ant.getCorX() == house.getCorX() && ant.getCorY() == house.getCorY()){
                            collestedFruits++;
                            mainWindow.setWindowTitle("Collectead fruits: " + collestedFruits);
                            ant.state = Ant.finding.FINDING;
                        }
                    }
                    else {
                        ant.state = Ant.finding.FINDING;
                    }
                }

//                if(ant.finding){
//                    if(ant.target != null){
//                        ant.moveTo(ant.target.getCorX(), ant.target.getCorY());
//                        if(ant.getCorX() == ant.target.getCorX() && ant.getCorY() == ant.target.getCorY()) {
//                            ant.finding = false;
//                            eats.removeIf((n) -> n.getCorX() == ant.target.getCorX() && n.getCorY() == ant.target.getCorY());
//                            if(eats.size() > 0){
//                                int tempid = random.nextInt(eats.size());
//                                if(!eats.get(tempid).closed){
//                                    ant.target = eats.get(tempid);
//                                }
//                                born--;
//                            }
//                        }
//                    }else {
//                        int tempid = random.nextInt(eats.size());
//                        if(!eats.get(tempid).closed){
//                            ant.target = eats.get(tempid);
//                        }
//                    }
//                }
//                else{
//                    if(ant.getCorX() == 11 && ant.getCorY() == 10){
//                        if(eats.size() > 0){
//                            ant.finding = true;
//                            int tempid = random.nextInt(eats.size());
//                            if(!eats.get(tempid).closed){
//                                ant.target = eats.get(tempid);
//                            }
//                        }
//                    }
//                    else {
//                        ant.moveTo(11,10);
//
//                    }
//                }
//            }

                scene.draw(house);

                if(collestedFruits == born){
                    born += 5;
                    bornAnt();
                }

                if(clock == 0){
                    bornEat();
                    clock = 30;
                }


                System.out.println("eats: " + eats.size());
                mainWindow.drawScene(scene);


                clock--;
                Thread.sleep(100);

            }
        }
    }

    private void bornAnt(){
        ants.add(new Ant('@', 11, 10));
    }
    private void bornEat(){
        eats.add(new Fruit('%', random.nextInt(XLEN), random.nextInt(YLEN)));
    }

    void setTarget(Ant ant){
        if(eats.size() > 0){
            int tempid = random.nextInt(eats.size());
            if(eats.get(tempid).closed){
                try {setTarget(ant);} catch (StackOverflowError e){
                    ant.state = Ant.finding.MOVETOHOUSE;
                }
            }else {
                ant.target_id = tempid;
                eats.get(tempid).closed = true;
            }
        } else{
            ant.state = Ant.finding.FINDING;
        }

    }

    @Override
    public void keyPressed(KeyEvent e) {
        super.keyPressed(e);
        if(lastKey == Keycode.P){
            switch (screens){
                case MAINMENU, PAUSE -> screens = Screens.GAME;
                case GAME -> screens = Screens.PAUSE;
            }

        }
    }
}