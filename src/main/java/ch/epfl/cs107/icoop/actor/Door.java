package ch.epfl.cs107.icoop.actor;

import ch.epfl.cs107.icoop.area.ICoopArea;
import ch.epfl.cs107.icoop.handler.ICoopInteractionVisitor;
import ch.epfl.cs107.play.areagame.actor.AreaEntity;
import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.signal.logic.Logic;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe Door.
 * Cette classe représente les portes utilisées pour faire transiter les joueurs ICoop d'une zone à une autre.
 */
public class Door extends AreaEntity implements Interactable {

    /** Liste des positions des cellules occupées par la porte. */
    private List<DiscreteCoordinates> cellposition;

    /**
     * Position de la porte dans la zone actuelle.
     */
    private DiscreteCoordinates position;

    /**
     * Liste des positions des cellules dans la zone de destination.
     */
    private List<DiscreteCoordinates> positionPathArea;

    /**
     * Signal de la porte : TRUE = activée, FALSE = désactivée.
     */
    private Logic signal;

    /**
     * Nom de la zone de destination.
     */
    private String pathArea;

    /**
     * Constructeur principal de la classe Door.
     *
     * @param pathArea (String) : Nom de la zone de destination.
     * @param signal (Logic) : Signal contrôlant le comportement de la porte.
     * @param positionPathArea (List<DiscreteCoordinates>) : Liste des positions dans la zone de destination.
     * @param area (Area) : La zone actuelle où la porte est située.
     * @param position (DiscreteCoordinates) : Position de la porte dans la zone actuelle.
     * @param cellposition (List<DiscreteCoordinates>) : Liste des cellules occupées par la porte.
     */
    public Door(String pathArea, Logic signal, List<DiscreteCoordinates> positionPathArea, Area area, DiscreteCoordinates position, List<DiscreteCoordinates> cellposition) {
        super(area, null, position);
        this.cellposition = cellposition;
        this.positionPathArea = positionPathArea;
        this.position = position;
        this.signal = signal;
        this.pathArea = pathArea;
        area.registerActor(this);
    }

    /**
     * Renvoie la liste des cellules occupées par la porte.
     *
     * @return Liste des coordonnées des cellules occupées.
     */
    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        List<DiscreteCoordinates> currentCells = new ArrayList<>();
        currentCells.add(position);
        if (cellposition != null) currentCells.addAll(cellposition);
        return currentCells;
    }

    /**
     * Indique si la porte occupe l'espace d'une cellule.
     *
     * @return false car la porte ne bloque pas l'espace des cellules.
     */
    @Override
    public boolean takeCellSpace() {
        return false;
    }

    /**
     * Indique si la porte peut être directement interactable via une cellule.
     *
     * @return true car la porte est interactable au niveau des cellules.
     */
    @Override
    public boolean isCellInteractable() {
        return true;
    }

    /**
     * Indique si la porte peut être directement interactable via une vue.
     *
     * @return false car la porte n'est pas interactable via une vue.
     */
    @Override
    public boolean isViewInteractable() {
        return false;
    }

    /**
     * Accepte une interaction d'un visiteur d'interaction.
     *
     * @param v Le visiteur interagissant avec la porte.
     * @param isCellInteraction Indique si l'interaction est basée sur les cellules.
     */
    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        ((ICoopInteractionVisitor) v).interactWith(this, isCellInteraction);
    }

    /**
     * Indique si la porte est activée.
     *
     * @return true si le signal est activé, false sinon.
     */
    public boolean isOn() {
        return signal.isOn();
    }

    /**
     * Indique si la porte est désactivée.
     *
     * @return true si le signal est désactivé, false sinon.
     */
    public boolean isOff() {
        return signal.isOff();
    }

    /**
     * Renvoie le nom de la zone de destination.
     *
     * @return Nom de la zone de destination.
     */
    public String getPathArea() {
        return pathArea;
    }

    /**
     * Renvoie la liste des coordonnées de destination dans la zone de destination.
     *
     * @return Liste des coordonnées de destination.
     */
    public List<DiscreteCoordinates> getPositionPathArea() {
        return positionPathArea;
    }
}