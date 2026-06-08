Usage:
Create your content_view.xml (example) or construct a View programatically.
Set the content view (for example, using setContentView in your activity).
Create your menu.xml (example) or construct a View programatically.
Now you need to inject the menu in your onCreate. You can specify transformations of a content view or use the default ones.
new SlidingRootNavBuilder(this)
  .withMenuLayout(R.layout.menu_left_drawer)
  .inject();

  API
Transformations
You can specify root transformations using SlidingRootNavBuilder.

new SlidingRootNavBuilder(this)
  .withDragDistance(140) //Horizontal translation of a view. Default == 180dp
  .withRootViewScale(0.7f) //Content view's scale will be interpolated between 1f and 0.7f. Default == 0.65f;
  .withRootViewElevation(10) //Content view's elevation will be interpolated between 0 and 10dp. Default == 8.
  .withRootViewYTranslation(4) //Content view's translationY will be interpolated between 0 and 4. Default == 0
  .addRootTransformation(customTransformation)
  .inject();
customTransformation in the above example is a user-created class that implements RootTransformation interface. For an example, refer to the default transformations.

Menu behavior
new SlidingRootNavBuilder(this)
  .withMenuOpened(true) //Initial menu opened/closed state. Default == false
  .withMenuLocked(false) //If true, a user can't open or close the menu. Default == false.
  .withGravity(SlideGravity.LEFT) //If LEFT you can swipe a menu from left to right, if RIGHT - the direction is opposite. 
  .withSavedState(savedInstanceState) //If you call the method, layout will restore its opened/closed state
  .withContentClickableWhenMenuOpened(isClickable) //Pretty self-descriptive. Builder Default == true
Controling the layout
A call to inject() returns you an interface for controlling the layout.

public interface SlidingRootNav {
    boolean isMenuClosed();
    boolean isMenuOpened();
    boolean isMenuLocked();
    void closeMenu();
    void closeMenu(boolean animated);
    void openMenu();
    void openMenu(boolean animated);
    void setMenuLocked(boolean locked);
    SlidingRootNavLayout getLayout(); //If for some reason you need to work directly with layout - you can
}
Callbacks
Drag progress:
builder.addDragListener(listener);

public interface DragListener {
  void onDrag(float progress); //Float between 0 and 1, where 1 is a fully visible menu
}
Drag state changes:
builder.addDragStateListener(listener);

public interface DragStateListener {
  void onDragStart();
  void onDragEnd(boolean isMenuOpened);
}
Compatibility with DrawerLayout.DrawerListener:
DrawerListenerAdapter adapter = new DrawerListenerAdapter(yourDrawerListener, viewToPassAsDrawer);
builder.addDragListener(listenerAdapter).addDragStateListener(listenerAdapter);
