package com.shark.dynamics.graphics.renderer;

public class DefaultShader {

    public static final String sDefaultVertexShader = "" +
            "#version 320 es\n" +
            "\n" +
            "layout(location=0) in vec3 aPos;\n" +
            "layout(location=1) in vec3 aColor;\n" +
            "layout(location=2) in vec2 aTex;\n" +
            "\n" +
            "uniform mat4 model;\n" +
            "uniform mat4 view;\n" +
            "uniform mat4 projection;\n" +
            "\n" +
            "out vec3 outColor;\n" +
            "out vec2 outTex;\n" +
            "\n" +
            "void main() {\n" +
            "    outColor = aColor;\n" +
            "    outTex = aTex;\n" +
            "    gl_Position = projection * view * model * vec4(aPos, 1);\n" +
            "    gl_PointSize = 9.0;\n" +
            "}";

    public static final String sDefaultFragmentShader = "" +
            "#version 320 es\n" +
            "\n" +
            "precision mediump float;\n" +
            "\n" +
            "in vec3 outColor;\n" +
            "\n" +
            "out vec4 FragColor;\n" +
            "\n" +
            "void main() {\n" +
            "    FragColor = vec4(outColor, 1.0);\n" +
            "}";

    public static final String sDefaultImageFragmentShader = "" +
            "#version 320 es\n" +
            "\n" +
            "precision mediump float;\n" +
            "\n" +
            "in vec2 outTex;\n" +
            "\n" +
            "uniform sampler2D image;\n" +
            "\n" +
            "out vec4 FragColor;\n" +
            "\n" +
            "void main() {\n" +
            "    FragColor = texture(image, outTex);\n" +
            "}\n";
}
