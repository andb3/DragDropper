# DragDropper
Kotlin DSL for RecyclerView drag-and-drop actions

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
