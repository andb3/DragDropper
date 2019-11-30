# DragDropper
Kotlin DSL for RecyclerView drag-and-drop actions

[![](https://jitpack.io/v/andb3/DragDropper.svg)](https://jitpack.io/#andb3/DragDropper)

Works with any RecyclerView implementation that supports ItemTouchHelper (should be all)

#### Example
```kotlin
recyclerView.dragDropWith {
    onMoved { oldPos, newPos ->
        val moved: String = itemsList.removeAt(oldPos)
        itemsList.add(newPos, moved)
    }
}
```

# Download

```groovy
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

```groovy
dependencies {
    implementation 'com.github.andb3:dragdropper:0.2.0'
}
```