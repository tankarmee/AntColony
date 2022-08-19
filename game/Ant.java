package game;

import core.entity.Dot;

public class Ant extends Dot {

    public enum finding { FINDING, MOVETOEAT, MOVETOHOUSE};

    public finding state;

    public int target_id;

    public Ant(char s, int x, int y) {
        super(s, x, y);
    }

}
