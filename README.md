# Shape背景的xml化

### 注意：和shape一样都是设置background，所以和background属性会相互覆盖

### 提示：所有属性和shape xml保持一致，只是前面加了个shape，如shapeSolidColor

示例：

```
<com.foundation.widget.shape.ShapeTextView或ShapeFrameLayout或ShapeView
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:shape="oval"//必须
    app:shapeSolidColor="#00f"//其他以shape开头的属性
     />

    tv.buildShape()
        .apply {
            setShape(GradientDrawable.RECTANGLE)
            setCornersRadius(100)
        }
```

### 待完善

useLevel属性：几乎没啥用，暂时不做

gradientRadius属性：原xml支持百分比，但受反射限制并且高api没有对应方法，暂时只支持固定大小

### 补充shape相关知识

1.自己搜索shape教程，一大堆

2.想直接看源码解释：xml打出”android:shape="oval"，点进去，所有以“GradientDrawable”开头的styleable均是
