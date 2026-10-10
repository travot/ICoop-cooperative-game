package ch.epfl.cs107.icoop.actor;

import ch.epfl.cs107.play.areagame.actor.AreaEntity;
import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.actor.Interactor;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.engine.actor.RPGSprite;
import ch.epfl.cs107.play.engine.actor.Sprite;
import ch.epfl.cs107.play.math.*;
import ch.epfl.cs107.play.signal.logic.Logic;
import ch.epfl.cs107.play.window.Canvas;

import static java.awt.ComponentOrientation.getOrientation;

/**
 * Classe ElementalWall.
 * Cette classe représente les murs avec un type d'élément et un état de signal.
 */
public abstract class ElementalWall extends AreaEntity implements Interactable, Interactor, Logic{

    private final Sprite[] wallSprites;   // Sprites du mur

    /**
     * Attribut activationSignal.
     * Description de l'attribut.
     */
    private Logic activationSignal;
    private final String spriteName;

    public ElementalWall(Area area, Orientation orientation, DiscreteCoordinates position, String element, Logic activationSignal) {
        super(area, orientation, position);
        this.activationSignal = activationSignal;
        if (element.equals("FEU")) spriteName="fire_wall";
        else spriteName="water_wall";
        wallSprites = RPGSprite.extractSprites(spriteName, 4, 1, 1, this, Vector.ZERO, 256, 256);
    }

    /**
     * Méthode setActivationSignal.
     * Description de la méthode.
     * @return (décrire le retour si applicable)
     */
    public void setActivationSignal(Logic signal) {
        this.activationSignal = signal;
    }

    @Override
/**
 * Méthode isOn.
 * Description de la méthode.
 * @return (décrire le retour si applicable)
 */
    public boolean isOn() {
        return activationSignal.isOn();
    }

    @Override
/**
 * Méthode isOff.
 * Description de la méthode.
 * @return (décrire le retour si applicable)
 */
    public boolean isOff() {
        return activationSignal.isOff();
    }

    @Override
/**
 * Méthode draw.
 * Description de la méthode.
 * @return (décrire le retour si applicable)
 */
    public void draw(Canvas canvas) {
        if (activationSignal.isOn()) {
            wallSprites[getOrientation().ordinal()].draw(canvas);
        }
    }

    @Override
/**
 * Méthode takeCellSpace.
 * Description de la méthode.
 * @return (décrire le retour si applicable)
 */
    public boolean takeCellSpace() {
        return false;
    }

    @Override
/**
 * Méthode isCellInteractable.
 * Description de la méthode.
 * @return (décrire le retour si applicable)
 */
    public boolean isCellInteractable() {
        return true;
    }

    @Override
/**
 * Méthode isViewInteractable.
 * Description de la méthode.
 * @return (décrire le retour si applicable)
 */
    public boolean isViewInteractable() {
        return true;
    }
}


