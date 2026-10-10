package ch.epfl.cs107.icoop.handler;

import ch.epfl.cs107.icoop.actor.ICoopPlayer;
import ch.epfl.cs107.play.engine.actor.Graphics;
import ch.epfl.cs107.play.engine.actor.ImageGraphics;
import ch.epfl.cs107.play.io.ResourcePath;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;


/**
 * Classe ICoopPlayerStatusGUI.
 * Cette classe représente le GUI représente l'inventaire des ICoopPlayers
 */
public class ICoopPlayerStatusGUI implements Graphics {

    private final static int DEPTH = 2000;

    private final ICoopPlayer player;

    private final boolean flipped;

    /**
     * Constructor for ICoopPlayerStatusGUI.
     * This constructor initializes a graphical interface object representing
     * the inventory of an ICoopPlayer with the specified player and orientation
     * configuration.
     *
     * @param player the ICoopPlayer whose status the GUI represents
     * @param flipped a boolean indicating whether the GUI display should be flipped
     */
    public ICoopPlayerStatusGUI(ICoopPlayer player, boolean flipped) {
        this.player = player;
        this.flipped = flipped;
    }

    @Override
    public void draw(Canvas canvas) {
        // Compute width, height and anchor
        float width = canvas.getTransform().getX().getX();
        float height = canvas.getTransform().getY().getY();

        float ratio = canvas.getWidth() / (float) canvas.getHeight();
        if (ratio > 1)
            height = width / ratio;
        else
            width = height * ratio;

        Vector anchor = canvas.getTransform().getOrigin().sub(new Vector(flipped ? (-width / 2 + 2) : width / 2, height / 2));

        //Draw selected gear
        ImageGraphics gearDisplay = new ImageGraphics(ResourcePath.getSprite("icoop/gearDisplay"), 1.5f, 1.5f, new RegionOfInterest(0, 0, 32, 32), anchor.add(new Vector(0, height - 1.75f)), 1, DEPTH);
        gearDisplay.draw(canvas);
        if(player.getInventory().getCurrentItem() != null) {
            ImageGraphics itemDisplay = new ImageGraphics(ResourcePath.getSprite(player.getInventory().getCurrentItem().getSpritename()), 0.5f, 0.5f, new RegionOfInterest(0, 0, 16, 16), anchor.add(new Vector (0.5f, height - 1.25f)), 1, DEPTH);
            itemDisplay.draw(canvas);
        }
    }
}
