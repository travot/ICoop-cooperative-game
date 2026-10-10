package ch.epfl.cs107.icoop.actor;

import ch.epfl.cs107.icoop.ICoopCollectable;
import ch.epfl.cs107.icoop.handler.ICoopInteractionVisitor;
import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.Animation;
import ch.epfl.cs107.play.engine.actor.RPGSprite;
import ch.epfl.cs107.play.engine.actor.Sprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.signal.logic.Logic;
import ch.epfl.cs107.play.window.Canvas;

/**
 * Classe Key.
 * Cette classe représente une clé utilisée dans le jeu pour déverrouiller des portails.
 */
public class Key extends ICoopCollectable implements Interactable {

    /** Indique si la clé a été collectée. */
    private boolean isCollected;

    /** Sprite représentant l'animation de la clé. */
    private Sprite animation;

    /** Couleur de la clé ("BLUE", "RED", etc.). */
    private String color;

    /** Logique associée à la clé (TRUE si collectée, FALSE sinon). */
    private Logic logic;

    /** Portail associé à cette clé. */
    private Portal portal;

    /**
     * Constructeur de la classe Key.
     * @param area L'aire où la clé est créée.
     * @param orientation L'orientation initiale de la clé.
     * @param position Les coordonnées de la clé.
     * @param color La couleur de la clé.
     * @param isCollected Indique si la clé est collectée dès la création.
     * @param portal Le portail associé à cette clé.
     */
    public Key(Area area, Orientation orientation, DiscreteCoordinates position, String color, boolean isCollected, Portal portal) {
        super(area, orientation, position, isCollected);
        this.isCollected = isCollected;
        this.logic = isCollected ? Logic.TRUE : Logic.FALSE;
        this.color = color;
        this.portal = portal;
        this.animation = new Sprite("icoop/key_" + color, 0.6f, 0.6f, this);
        area.registerActor(this);
    }

    /**
     * Renvoie l'élément associé à la clé.
     * @return "EAU" si la clé est bleue, "FEU" si rouge, sinon null.
     */
    public String getElement() {
        if (color.equals("BLUE")) return "EAU";
        if (color.equals("RED")) return "FEU";
        return null;
    }

    /**
     * Renvoie la logique associée à la clé.
     * @return La logique actuelle (TRUE ou FALSE).
     */
    public Logic getLogic() {
        return logic;
    }

    /**
     * Dessine l'animation de la clé sur le canvas.
     * @param canvas Le canvas où dessiner.
     */
    @Override
    public void draw(Canvas canvas) {
        animation.draw(canvas);
    }

    /**
     * Met à jour l'état de la clé.
     * Si la clé est collectée, elle est retirée de l'aire.
     * @param deltaTime Temps écoulé depuis la dernière mise à jour.
     */
    @Override
    public void update(float deltaTime) {
        if (isCollected) {
            getOwnerArea().unregisterActor(this);
        }
    }

    /**
     * Collecte la clé.
     * Si la clé n'est pas encore collectée, elle devient collectée,
     * le portail associé est activé, et la clé est retirée de l'aire.
     */
    @Override
    public void collect() {
        if (!isCollected) {
            isCollected = true;
            portal.setKey();
            getOwnerArea().unregisterActor(this);
        }
    }

    /**
     * Vérifie si la clé a été collectée.
     * @return True si collectée, sinon False.
     */
    @Override
    public boolean isCollected() {
        return isCollected;
    }

    /**
     * Vérifie si la clé peut interagir au niveau des cellules.
     * @return Toujours True.
     */
    @Override
    public boolean isCellInteractable() {
        return true;
    }

    /**
     * Vérifie si la clé peut interagir au niveau de la vue.
     * @return Toujours False.
     */
    @Override
    public boolean isViewInteractable() {
        return false;
    }

    /**
     * Accepte une interaction d'un visiteur.
     * @param v Le visiteur d'interaction de zone.
     * @param isCellInteraction True si l'interaction est cellulaire.
     */
    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        ((ICoopInteractionVisitor) v).interactWith(this, isCellInteraction);
    }
}