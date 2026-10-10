package ch.epfl.cs107.icoop.actor;

import ch.epfl.cs107.icoop.ICoopCollectable;
import ch.epfl.cs107.icoop.handler.ICoopInteractionVisitor;
import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.Animation;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.window.Canvas;

import java.util.Collections;
import java.util.List;

/**
 * Classe Heart.
 * Cette classe représente un objet collectable en forme de cœur qui peut interagir avec d'autres entités.
 */
public class Heart extends ICoopCollectable implements Interactable {

    /**
     * Indique si l'objet a été collecté.
     */
    private boolean isCollected;

    /**
     * Animation associée à l'objet Heart.
     */
    private Animation animation;

    /**
     * Durée d'une animation.
     */
    private final int ANIMATION_DURATION = 24;

    /**
     * Constructeur de la classe Heart.
     *
     * @param area        La zone où le cœur est situé.
     * @param orientation L'orientation initiale du cœur.
     * @param position    La position initiale du cœur.
     * @param isCollected Indique si le cœur est déjà collecté.
     */
    public Heart(Area area, Orientation orientation, DiscreteCoordinates position, boolean isCollected) {
        super(area, orientation, position, isCollected);
        this.isCollected = isCollected;
        this.animation = new Animation("icoop/heart", 4, 1, 1, this, 16, 16, ANIMATION_DURATION / 4, true);
        area.registerActor(this);
    }

    /**
     * Dessine l'animation de l'objet Heart sur le canvas.
     *
     * @param canvas Le canvas sur lequel dessiner.
     */
    @Override
    public void draw(Canvas canvas) {
        animation.draw(canvas);
    }

    /**
     * Met à jour l'animation de l'objet Heart.
     *
     * @param deltaTime Le temps écoulé depuis la dernière mise à jour.
     */
    @Override
    public void update(float deltaTime) {
        animation.update(deltaTime);
    }

    /**
     * Collecte l'objet Heart et le retire de la zone.
     */
    @Override
    public void collect() {
        if (!isCollected) {
            isCollected = true;
            getOwnerArea().unregisterActor(this);
        }
    }

    /**
     * Indique si l'objet Heart a été collecté.
     *
     * @return true si le cœur est collecté, false sinon.
     */
    @Override
    public boolean isCollected() {
        return isCollected;
    }

    /**
     * Indique si l'objet Heart est interactable au niveau des cellules.
     *
     * @return true car le cœur est interactable via les cellules.
     */
    @Override
    public boolean isCellInteractable() {
        return true;
    }

    /**
     * Indique si l'objet Heart est interactable au niveau de la vue.
     *
     * @return false car le cœur n'est pas interactable via la vue.
     */
    @Override
    public boolean isViewInteractable() {
        return false;
    }

    /**
     * Accepte une interaction d'un visiteur d'interaction.
     *
     * @param v                 Le visiteur interagissant avec l'objet Heart.
     * @param isCellInteraction Indique si l'interaction est basée sur les cellules.
     */
    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        ((ICoopInteractionVisitor) v).interactWith(this, isCellInteraction);
    }
}
