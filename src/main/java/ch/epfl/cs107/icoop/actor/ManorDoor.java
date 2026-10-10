package ch.epfl.cs107.icoop.actor;

import ch.epfl.cs107.icoop.area.ICoopArea;
import ch.epfl.cs107.icoop.handler.ICoopInteractionVisitor;
import ch.epfl.cs107.play.areagame.actor.AreaEntity;
import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.Dialog;
import ch.epfl.cs107.play.engine.actor.RPGSprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.signal.logic.Logic;
import ch.epfl.cs107.play.window.Canvas;

import java.util.Collections;
import java.util.List;

/**
 * Classe ManorDoor.
 * Cette classe représente une porte de manoir utilisée dans le jeu, avec des interactions spécifiques.
 */
public class ManorDoor extends AreaEntity implements Interactable {

    /** Texte du dialogue associé à la porte. */
    private String dialog;

    /**
     * Constructeur de la classe ManorDoor.
     * @param area L'aire où la porte est créée.
     * @param orientation Orientation initiale de la porte.
     * @param position Position initiale de la porte.
     */
    public ManorDoor(ICoopArea area, Orientation orientation, DiscreteCoordinates position) {
        super(area, orientation, position);
        area.registerActor(this);
        this.dialog = "key_required";
    }

    /**
     * Renvoie les cellules actuellement occupées par la porte.
     * @return Liste contenant une seule cellule.
     */
    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates());
    }

    /**
     * Indique si la porte occupe un espace cellulaire.
     * @return Toujours false.
     */
    @Override
    public boolean takeCellSpace() {
        return false;
    }

    /**
     * Vérifie si la porte peut interagir au niveau des cellules.
     * @return Toujours true.
     */
    @Override
    public boolean isCellInteractable() {
        return true;
    }

    /**
     * Vérifie si la porte peut interagir au niveau de la vue.
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

    /**
     * Renvoie le dialogue associé à la porte.
     * @return Le texte du dialogue actuel.
     */
    public String getManorDialog() {
        return dialog;
    }

    /**
     * Définit la porte comme prête pour la victoire.
     * Change le dialogue en "victory".
     */
    public void setReadyToWin() {
        dialog = "victory";
    }
}