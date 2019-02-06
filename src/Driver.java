import Core.ApplicationWindow;
import Utilities.SpriteSheet;

public class Driver {
    public static void main(String[] args)
    {
        SpriteSheet.Initialize();

        ApplicationWindow app = new ApplicationWindow();
        app.setVisible(true);
    }
}
