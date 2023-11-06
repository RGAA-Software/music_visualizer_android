package com.shark.dynamics.graphics.renderer.r2d.font;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.shark.dynamics.graphics.renderer.texture.Texture;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

public class FontLoader {

    private static final String TAG = "Font";

    private Context mContext;

    public FontLoader(Context context) {
        mContext = context;
    }

    public Font loadFonts(String fontPath, String name) {
        Font font = new Font();
        font.mapCharacters = new HashMap<>();

        InputStream fis = null;
        try {
            fis = mContext.getAssets().open(fontPath+"/"+name+".fnt");
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            String line = null;
            int idx = 0;
            while ( (line = br.readLine()) != null) {
                if (idx < 4) {
                    idx++;
                    continue;
                }

                if (!line.startsWith("char")) {
                    idx++;
                    continue;
                }

                Character ch = parseCharacter(line);
                font.mapCharacters.put((int)ch.id, ch);

                idx++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        String texPath = fontPath+"/"+name+".png";
        font.texture = new Texture(texPath);

        return font;
    }

    private Character parseCharacter(String line) {
        Character ch = new Character();
        String[] splits = line.split(" ");
        for (String item : splits) {
            if (TextUtils.isEmpty(item)) {
                continue;
            }

            String[] info = item.split("=");
            if (info.length != 2) {
                continue;
            }

            if (TextUtils.equals("id", info[0])) {
                ch.id = Integer.parseInt(info[1]);

            } else if (TextUtils.equals("x", info[0])) {
                ch.x = Integer.parseInt(info[1]);

            } else if (TextUtils.equals("y", info[0])) {
                ch.y = Integer.parseInt(info[1]);

            } else if (TextUtils.equals("width", info[0])) {
                ch.width = Integer.parseInt(info[1]);

            } else if (TextUtils.equals("height", info[0])) {
                ch.height = Integer.parseInt(info[1]);

            } else if (TextUtils.equals("xoffset", info[0])) {
                ch.xOffset = Integer.parseInt(info[1]);

            } else if (TextUtils.equals("yoffset", info[0])) {
                ch.yOffset = Integer.parseInt(info[1]);

            } else if (TextUtils.equals("xadvance", info[0])) {
                ch.xAdvance = Integer.parseInt(info[1]);
            }
        }
        return ch;
    }

}
