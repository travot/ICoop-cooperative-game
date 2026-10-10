package ch.epfl.cs107.icoop;

import ch.epfl.cs107.play.areagame.actor.CollectableAreaEntity;
import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;

import java.util.Collections;
import java.util.List;

/**
 * Classe abstraite ICoopCollectable.
 * Représente une entité collectable dans le jeu ICoop.
 */
public abstract class ICoopCollectable extends CollectableAreaEntity implements Interactable {

    /**
     * Constructeur de la classe ICoopCollectable.
     *
     * @param area (Area) : L'aire à laquelle appartient l'entité collectable.
     * @param orientation (Orientation) : L'orientation de l'entité collectable dans l'aire.
     * @param position (DiscreteCoordinates) : Les coordonnées discrètes représentant la position de l'entité collectable.
     * @param isCollected (boolean) : Indique si l'entité collectable a déjà été collectée.
     */
    public ICoopCollectable(Area area, Orientation orientation, DiscreteCoordinates position, boolean isCollected) {
        super(area, orientation, position, isCollected);
    }

    /**
     * Renvoie la liste des cellules actuelles occupées par l'entité collectable.
     * @return Une liste contenant les coordonnées principales de la cellule actuelle.
     */
    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates());
    }

    /**
     * Indique si l'entité prend de l'espace dans la cellule.
     * @return False, car les entités collectables ne bloquent pas l'espace.
     */
    @Override
    public boolean takeCellSpace() {
        return false;
    }

    /**
     * Indique si la cellule contenant l'entité est interactive.
     * @return True, car les entités collectables peuvent être interactives.
     */
    @Override
    public boolean isCellInteractable() {
        return true;
    }

    /**
     * Indique si l'entité est interactable par la vue.
     * @return False, car les entités collectables ne sont pas interactives par la vue.
     */
    @Override
    public boolean isViewInteractable() {
        return false;
    }
}

