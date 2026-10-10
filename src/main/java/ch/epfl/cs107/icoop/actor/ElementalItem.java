package ch.epfl.cs107.icoop.actor;

import ch.epfl.cs107.icoop.ICoopCollectable;
import ch.epfl.cs107.icoop.handler.ICoopInteractionVisitor;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.Sprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.signal.logic.Logic;
import ch.epfl.cs107.play.window.Canvas;

import java.util.Collections;
import java.util.List;

/**
 * Classe ElementalItem.
 * Cette classe représente un objet collectable avec un type d'élément et un état de signal.
 */
public class ElementalItem extends ICoopCollectable implements Logic, ElementalEntity {

    /**
     * Indique si l'objet a été collecté.
     */
    private boolean isCollected;

    /**
     * Type d'élément associé à l'objet.
     */
    private final String element;

    /**
     * Signal associé à l'état de l'objet.
     */
    private Logic signal;

    /**
     * Constructeur de la classe ElementalItem.
     *
     * @param area        La zone où l'objet existe.
     * @param orientation L'orientation initiale de l'objet.
     * @param position    La position initiale de l'objet.
     * @param element     Le type d'élément de l'objet.
     * @param isCollected Indique si l'objet est initialement collecté.
     */
    public ElementalItem(Area area, Orientation orientation, DiscreteCoordinates position, String element, boolean isCollected) {
        super(area, orientation, position, isCollected);
        this.isCollected = isCollected;
        this.element = element;
        signal = Logic.TRUE;
    }

    /**
     * Renvoie le type d'élément de l'objet.
     *
     * @return Le type d'élément sous forme de chaîne de caractères.
     */
    @Override
    public String element() {
        return element;
    }

    /**
     * Indique si le signal est activé.
     *
     * @return true si le signal est activé, false sinon.
     */
    @Override
    public boolean isOn() {
        return signal.isOn();
    }

    /**
     * Indique si le signal est désactivé.
     *
     * @return true si le signal est désactivé, false sinon.
     */
    @Override
    public boolean isOff() {
        return signal.isOff();
    }

    /**
     * Collecte l'objet. Change son état en collecté et met à jour le signal.
     */
    @Override
    public void collect() {
        if (!isCollected) {
            isCollected = true;
            signal = Logic.FALSE;
            getOwnerArea().unregisterActor(this);
        }
    }

    /**
     * Indique si l'objet a déjà été collecté.
     *
     * @return true si l'objet est collecté, false sinon.
     */
    @Override
    public boolean isCollected() {
        return isCollected;
    }

    /**
     * Accepte une interaction d'un visiteur d'interaction.
     *
     * @param v                 Le visiteur interagissant avec l'objet.
     * @param isCellInteraction Indique si l'interaction est basée sur les cellules.
     */
    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        ((ICoopInteractionVisitor) v).interactWith(this, isCellInteraction);
    }
}