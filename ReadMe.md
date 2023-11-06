### OpenGL Music visualizer

#### 音频可视化，可用来学习Android & OpenGL。
![](docs/learn_it.jpeg)

#### Now, let's go.

#### 1.前置声明
#### 1.1 OpenGL ES版本为OpenGL ES 3.2，Android 10，版本兼容未作太多处理，如需要兼容，需自行处理。主要在于OpenGL ES的兼容上，具体来说有以下几点：
###### 1.1.1 相对于2.0来说，每个可绘制对象都使用了VAO（Vertex Array Object）.
###### 1.1.2 shader中的关键字有所改变，虽然varying，attribute这些仍然可用，但本项目用的是in，out，location这些。
###### 1.1.3 版本之间的差异并没有很复杂，只是局部修改。注意如果是OpenGL的Core模式的话，必须用VAO。

#### 1.2. OpenGL 与 OpenGL ES的不同
###### 1.2.1 OpenGL ES 是 OpenGL的子集
###### 1.2.2 有一些具体的差异，经常遇到的如下：
> OpenGL ES 的fragment shader中，必须声明精度，即：`precision mediump float;`  
> OpenGL ES 缺少一些API，比如在FrameBuffer中常用的，将color attachment设置为空的 `glDrawBuffer(GL_NONE);`   
> OpenGL ES fragment shader默认没有gl_FragColor输出,需要自己声明一个vec4的变量。
> 

#### 1.3. 关于使用的库
###### 1.3.1 像在C++中使用时，常用glm数学库，当然Android中也有矩阵操作的API。这里用的是joml这个数学库。如果用lwjgl开发java项目的话，joml几乎是必选的。  
###### 1.3.2 为了方便操作图片，引入了OpenCV，但目前用到的并不多，仅限于java用了点模糊，镜像等简单操作。但后期若做其他的图片操作，直接用即可，很方便。
###### 1.3.3 如果是自己解码，用PCM(音频裸流)播放的，可以使用FFTW这个库做FFT。


#### 2.第一步，可渲染对象，封装API
###### 2.1 OpenGL只是绘制，绘制什么东西需要自己指定，每次都塞一堆数据太麻烦，所以封装一下。简单封装了点2D下的图形，看下面图片和视频：

![](docs/2d_preview_intro.png)

#### [视频演示](docs/2d_preview.mp4)

###### 2.2 当然3D空间也做了一点封装,只展示一部分，其他的部分见具体效果。
![](docs/3d_preview.png)

#### 3.第二步，开始绘制效果
- 涉及到频谱绘制的，基本都是使用draw instance 的方式绘制，这能大量减少draw call次数，包括粒子系统也是如此，大量的粒子不可能每个都要绘制一下。但是也有几个并没有使用，比如GLMatrix这个效果，如果有需要，自行修改。
- 同理，大量相同模型的亦然。
##### 3.1 竖向的直线效果,白点为粒子系统产生雪花效果
![](docs/2d_vert_bars.jpg)

##### 3.2 圆形布局的直线效果，黄色的为火花效果,此火花沿圆周运动
![](docs/2d_circle_bars.jpg)

##### 3.3 区域填充绘制，并且带有局部实时动态模糊
![](docs/2d_blur_region.jpg)

##### 3.4 纹理代替竖线绘制，粒子系统也替换纹理
![](docs/2d_tex_bars.jpg)

##### 3.5 圆形布局，绘制纹理
![](docs/2d_circle_tex_bars.jpg)

##### 3.6 纹理重叠，BLEND
![](docs/2d_tex_bars_1.jpg)

##### 3.7 调整数据，让其看起来更圆滑。蝴蝶为帧动画，自身沿贝塞尔曲线前进。
![](docs/2d_peeling_bars.jpg)

##### 3.8 绘制圆点，线。三角形是粒子系统产生的。
![](docs/2d_dancing_dots.jpg)

##### 3.9 仿网易云的孤独星球
![](docs/2d_lonely_planet.jpg)

##### 3.10 多段 2 阶贝塞尔曲线组合
![](docs/2d_flower.jpg)

##### 3.11 一段 n 阶贝塞尔曲线，点是控制点
![](docs/2d_n_order_bezier.jpg)

##### 3.12 两个圆与直线，气泡为粒子系统产生
![](docs/2d_double_line.jpg)

##### 3.13 仿网易 宇宙尘埃
![](docs/2d_universe_ash.jpg)

##### 3.14 黑白双煞
![](docs/2d_circle_region.jpg)

##### 3.15 雷电法王杨永信
![](docs/2d_thunder.jpg)

##### 3.16 分割直线，粒子效果加重力
##### [参考B站视频](https://www.bilibili.com/video/BV1my4y1p7Gv)
![](docs/2d_vertical_split.jpg)

#### 4.进入3D世界

##### 4.1 将粒子图片在3D空间中排成m行n列
##### [参考B站视频](https://www.bilibili.com/video/BV1gU4y1b7Sj/)
![](docs/3d_dot_matrix.jpg)

##### 4.2 长方体在XOZ平面按圆周排列，加粒子效果
![](docs/3d_bars.jpg)

##### 4.3 仿黑客帝国的效果(此效果draw call较多，需自己用draw instance修改)
![](docs/3d_matrix.jpg)

##### 4.4 粒子由远及近
![](docs/3d_explore_particle.jpg)

##### 4.5 加载OBJ模型，并添加光照，光源位置在变化，注意小鹿的明暗。镜面反射光关了，要打开去shader中干掉注释。
![](docs/3d_deer.jpg)

##### 4.6 震撼地球, 加载模型并贴图，同时绘制N个小行星。
#### [B站视频参考，PC端实现](https://www.bilibili.com/video/BV1zQ4y1R7KB)
![](docs/3d_earth.jpg)

##### 4.7 加载模型，添加光照并且添加平行光阴影
![](docs/3d_shadow.jpg)

##### 4.8 随机同时绘制N个模型，添加光照并且添加 雾 ，远处的景物都隐匿于雾中。
![](docs/3d_model_instance.jpg)

#### 5.未竟事宜
> 没做完的事还是有很多的，列举几个，仅供参考

##### 5.1 直接用片段着色器绘制图形图像，效率上有点点不妥，但学习是没问题的，比如：
```glsl
	vec2 st = fragCoord/iResolution.xy;
    float aspect = iResolution.x/iResolution.y;

    float pcf = 0.0;
    vec3 color = vec3(0.0);
    float pct = 0.0;

    vec2 center = vec2(0.5, 0.5);
    center = vec2(abs(sin(iTime)), 0.5);
    vec2 dist = st - center;

    dist.x *= aspect;
    float toCenterLength = length(dist);

    pct = step(0.2, toCenterLength);
    color = vec3(1.0-pct);
    fragColor = vec4(color, 1.0);

    color = vec3(0.1, 0.1, 0.1);

    pct = smoothstep(0.0, abs(sin(iTime))*0.2, toCenterLength);
    color += vec3(1.0-pct) * vec3(1.0, 0.5, 0.2);
    fragColor = vec4(color, 1.0);

    pct = smoothstep(0.0, abs(cos(iTime))*0.2, toCenterLength);
    color += vec3(1.0-pct) * vec3(0.0, 0.5, 0.8);
    fragColor += vec4(color, 1.0);
```
##### 这段可直接拿到shadertoy中去，把main方法里都给替换掉就能看到效果。
##### 可在此Renderer的基础上完善，修改ShaderIt类，传入的参数参考shadertoy的几个uniform，编写，替换不同的fragment shader即可。
![](docs/shader_it.jpg)

### Finally
