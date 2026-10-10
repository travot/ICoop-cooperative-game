package ch.epfl.cs107.icoop.actor;

import ch.epfl.cs107.icoop.ICoopCollectable;
import ch.epfl.cs107.icoop.handler.ICoopInteractionVisitor;
import ch.epfl.cs107.play.areagame.actor.AreaEntity;
import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.actor.Interactor;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.Animation;
import ch.epfl.cs107.play.engine.actor.OrientedAnimation;
import ch.epfl.cs107.play.engine.actor.RPGSprite;
import ch.epfl.cs107.play.engine.actor.Sprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

/**
 * Classe Staff.
 * Cette classe représente un bâton élémentaire dans le jeu.
 * Les bâtons peuvent être collectés et sont associés à des éléments tels que "EAU" ou "FEU".
 */
public class Staff extends ElementalItem implements Interactable {

    /** Animation visuelle du bâton. */
    private final Animation animation;

    /** Élément associé au bâton ("EAU", "FEU", etc.). */
    private final String element;

    /** Indique si le bâton a été collecté. */
    private boolean isCollected;

    /** Durée totale de l'animation en frames. */
    private static final int ANIMATION_DURATION = 32;

    /** Nombre de frames dans l'animation du bâton. */
    private final int ANIMATION_FRAMES = 4;

    /**
     * Constructeur de la classe Staff.
     * @param area L'aire où le bâton est créé.
     * @param orientation Orientation initiale du bâton.
     * @param position Position initiale du bâton.
     * @param element L'élément associé au bâton.
     * @param isCollected Indique si le bâton est collecté dès la création.
     */
    public Staff(Area area, Orientation orientation, DiscreteCoordinates position, String element, boolean isCollected) {
        super(area, orientation, position, element, isCollected);
        this.element = element;
        this.isCollected = isCollected;

        String elementSpriteName = element.equals("EAU") ? "water" : "fire";
        String spriteSheetPath = "icoop/staff_" + elementSpriteName;
        this.animation = new Animation(spriteSheetPath, 4, 2, 2, this, 32, 32, new Vector(-0.5f, 0), ANIMATION_DURATION / 4, true);
        area.registerActor(this);
    }

    /**
     * Dessine l'animation du bâton sur le canvas.
     * @param canvas Le canvas où dessiner.
     */
    @Override
    public void draw(Canvas canvas) {
        animation.draw(canvas);
    }

    /**
     * Met à jour l'animation du bâton.
     * @param deltaTime Temps écoulé depuis la dernière mise à jour.
     */
    @Override
    public void update(float deltaTime) {
        animation.update(deltaTime);
    }

    /**
     * Collecte le bâton.
     * Si le bâton n'est pas encore collecté, il est marqué comme collecté et retiré de l'aire.
     */
    @Override
    public void collect() {
        if (!isCollected) {
            isCollected = true;
            getOwnerArea().unregisterActor(this);
        }
    }

    /**
     * Vérifie si le bâton a été collecté.
     * @return True si collecté, sinon False.
     */
    @Override
    public boolean isCollected() {
        return isCollected;
    }

    /**
     * Vérifie si le bâton peut interagir au niveau des cellules.
     * @return Toujours true.
     */
    @Override
    public boolean isCellInteractable() {
        return true;
    }

    /**
     * Vérifie si le bâton peut interagir au niveau de la vue.
     * @return Toujours false.
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