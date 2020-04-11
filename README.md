# DragDropper
[![](https://jitpack.io/v/andb3/DragDropper.svg)](https://jitpack.io/#andb3/DragDropper)
A Kotlin DSL for RecyclerView drag-and-drop actions.

# Usage
#### Example
```kotlin
recyclerView.dragDropWith {
    onDropped { oldPos, newPos ->
        val moved: String = itemsList.removeAt(oldPos)
        itemsList.add(newPos, moved)
    }
    constrainDrag { vh ->
        val pos = vh.adapterPosition
        return@constrainDrag (pos - 3)..(pos + 3)
    }
    canDrag = { vh ->
        vh.itemView.listText.text != "Banana"
    }
    elevateBy = 4.dp //default is 8
}
```

#### Notes
- The library handles updating the adapter, but the underlying data does need to be updated at the end for the changes to stay in effect.
- This is a pre-release, so the API is subject to change

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
    implementation 'com.github.andb3:dragdropper:0.3.0'
}
```