# PixelGameEngine

This is an experimantal prototype-like 2D game engine written in Java.
It currently uses OpenGL (from lwjgl) for rendering. It's still in-dev.
**It is being written by a 14 year old.**

Note: You can't use it unless you get the assets for it. (Unless you manage to make them by hand) (Not included in this repository right now)



**Features** (* Needs to be improved soon):
  - 2D physics (only AA rectangles)\*
  - Tile grid map,
  - Map loading & saving (in JSON)
  - Level editor (very primitive) \*
  - Textures (PNG) with frame-by-frame animation
  - Shaders (GLSL) (Compiled when game starts)
  - Cool image-roll intro
  - Entities & decoratives
  - Simple particle system \*
  
  **Known bugs**:
  - 2D collision can make you stuck in walls
  - The whole editor is just garbage
  
  **Planned improvements** (I can *not* promise anything) (* Priority):
  - Rewrite level editor (with ingame gizmos) \*\*\*
  - Physics will support rotation and konvex polygons \*
  - Performance improvements
  - Basic GUI \*
  - Special decoratives (like a linear mover, or rotator..)
  - Logical I/O for entities \*
  - Materials (tiles) and their properties will be loaded from a file \*
  - Get an actual name for this engine (the current is not original)
