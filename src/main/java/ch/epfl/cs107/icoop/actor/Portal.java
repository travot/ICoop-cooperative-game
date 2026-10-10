package ch.epfl.cs107.icoop.actor;

import ch.epfl.cs107.icoop.area.ICoopArea;
import ch.epfl.cs107.icoop.handler.ICoopInteractionVisitor;
import ch.epfl.cs107.play.areagame.actor.AreaEntity;
import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.RPGSprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.signal.Signal;
import ch.epfl.cs107.play.signal.logic.Logic;
import ch.epfl.cs107.play.window.Canvas;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Classe Portal.
 * Cette classe représente un portail dans le jeu qui peut être activé ou désactivé selon un signal logique.
 */
public class Portal extends AreaEntity implements Interactable {

    /** Signal logique contrôlant l'état du portail. */
    private Logic signal;

    /** Animation visuelle associée au portail. */
    private RPGSprite animation;

    /** Chemin de l'aire associée au portail. */
    private final String pathArea;

    /** Nombre de clés collectées pour activer le portail. */
    private int setKey = 0;

    /**
     * Constructeur de la classe Portal.
     * @param pathArea Le chemin de l'aire associée.
     * @param orientation L'orientation initiale du portail.
     * @param signal Le signal logique contrôlant l'état du portail.
     * @param area L'aire où le portail est créé.
     * @param position La position initiale du portail.
     */
    public Portal(String pathArea, Orientation orientation, Logic signal, ICoopArea area, DiscreteCoordinates position) {
        super(area, orientation, position);
        this.signal = signal;
        this.pathArea = pathArea;
        animation = new RPGSprite("shadow", 1, 1, this, new RegionOfInterest(0, 0, 32, 32));
        area.registerActor(this);
    }

    /**
     * Définit un nouveau signal logique pour le portail.
     * @param signal Le nouveau signal logique.
     */
    public void setLogic(Logic signal) {
        this.signal = signal;
    }

    /**
     * Dessine l'animation du portail si celui-ci est activé.
     * @param canvas Le canvas où dessiner.
     */
    @Override
    public void draw(Canvas canvas) {
        if (this.isOn()) {
            animation.draw(canvas);
        }
    }

    /**
     * Ajoute une clé pour activer le portail.
     * Lorsque deux clés sont ajoutées, le portail est activé.
     */
    public void setKey() {
        this.setKey += 1;
        if (setKey >= 2) {
            signal = Logic.TRUE;
        }
    }

    /**
     * Renvoie le chemin de l'aire associée au portail.
     * @return Le chemin de l'aire.
     */
    public String getPathArea() {
        return pathArea;
    }

    /**
     * Renvoie les cellules actuellement occupées par le portail.
     * @return Liste contenant une seule cellule.
     */
    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates());
    }

    /**
     * Indique si le portail occupe un espace cellulaire.
     * @return Toujours false.
     */
    @Override
    public boolean takeCellSpace() {
        return false;
    }

    /**
     * Vérifie si le portail peut interagir au niveau des cellules.
     * @return Toujours true.
     */
    @Override
    public boolean isCellInteractable() {
        return true;
    }

    /**
     * Vérifie si le portail peut interagir au niveau de la vue.
     * @return Toujours false.
     */
    @Override
    public boolean isViewInteractable() {
        return false;
    }

    /**
     * Vérifie si le portail est activé.
     * @return True si le portail est activé, sinon false.
     */
    public boolean isOn() {
        return signal.isOn();
    }

    /**
     * Vérifie si le portail est désactivé.
     * @return True si le portail est désactivé, sinon false.
     */
    public boolean isOff() {
        return signal.isOff();
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
