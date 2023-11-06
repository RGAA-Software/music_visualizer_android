package com.shark.dynamics.graphics.renderer.r2d.font;

import com.shark.dynamics.graphics.renderer.texture.Texture;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Font {

    public Texture texture;
    public Map<Integer, Character> mapCharacters;

    public List<Character> getCharacters(String text) {
        List<Character> cs = new ArrayList<>();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            Character ch = findCharacter(c);
            if (ch == null) {
                continue;
            }
            cs.add(ch);
        }
        return cs;
    }

    private Character findCharacter(char c) {
        return mapCharacters.get((int)c);
    }

    public int getTexWidth() {
        return texture.getWidth();
    }

    public int getTexHeight() {
        return texture.getHeight();
    }
}
