# CircularRingProgressview
有刻度的进度条，分为四分之一圆和整圆两种表现形式

##1.效果图

![rendering1](https://raw.githubusercontent.com/StormBlue/CircularRingProgressview/master/source/rendering_01.png)

##2.可选参数

###1)
```xml
  <com.bluestrom.gao.customview.CircularRingPercentageView
    android:id="@+id/circle1"
    android:layout_width="match_parent"
    android:layout_height="match_parent"

    ArcRing:arc_max="100"                    //进度最大值(默认值 100)
    ArcRing:arc_progress="50"                //进度(默认值 0)
    ArcRing:arc_draw_mode="quarter|complete" //quarter为效果图左侧效果，complete为右侧效果(默认值 quarter)
    ArcRing:arc_stroke_width="2"             //每个刻度的粗细(默认值 2)    
    ArcRing:arc_ring_width="40"              //每个刻度的长度(默认值 40)
    ArcRing:arc_scale_numbers="200"          //整个圆的总刻度数(默认值 200) 注：此值不可过高，会影响绘制效率
    ArcRing:arc_unfinished_color="#6f000000" //未达到的进度颜色(默认值 #6f000000)
    ArcRing:arc_finished_color="#21e474"     //已达到的进度颜色(默认值 #21e474)

    />
```

###2)
每个参数都有对应的set方法，例如：
```java
CircularRingPercentageView arcProgress1 =  (CircularRingPercentageView)findViewById(R.id.circle1);
arcProgress1.setProgress(50);
arcProgress1.setDrawMode(CircularRingPercentageView.DrawMode.COMPLETE);
......
...
```

##3.License
```
Copyright (c) 2015 LingoChamp Inc.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```