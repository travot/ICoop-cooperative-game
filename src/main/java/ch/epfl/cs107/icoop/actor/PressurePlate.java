package ch.epfl.cs107.icoop.actor;

import ch.epfl.cs107.icoop.ICoopCollectable;
import ch.epfl.cs107.icoop.handler.ICoopInteractionVisitor;
import ch.epfl.cs107.play.areagame.actor.AreaEntity;
import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.actor.Interactor;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.Animation;
import ch.epfl.cs107.play.engine.actor.RPGSprite;
import ch.epfl.cs107.play.engine.actor.Sprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.signal.logic.Logic;
import ch.epfl.cs107.play.window.Canvas;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Classe PressurePlate.
 * Cette classe représente une plaque de pression dans le jeu, utilisée pour activer ou désactiver des murs.
 */
public class PressurePlate extends AreaEntity implements Interactable, Logic {

    /** Liste des murs contrôlés par la plaque de pression. */
    private final List<Wall> wall;

    /** Logique associée à l'état de la plaque de pression. */
    private Logic logic;

    /** Sprite visuel de la plaque de pression. */
    private final RPGSprite sprite;

    /** Interacteur actuellement en contact avec la plaque de pression. */
    private Interactor interactor;

    /**
     * Constructeur de la classe PressurePlate.
     * @param area L'aire où la plaque est créée.
     * @param orientation Orientation initiale de la plaque.
     * @param position Position initiale de la plaque.
     * @param wall Liste des murs contrôlés par la plaque.
     */
    public PressurePlate(Area area, Orientation orientation, DiscreteCoordinates position, List<Wall> wall) {
        super(area, orientation, position);
        this.wall = wall;
        this.sprite = new RPGSprite("GroundPlateOff", 1, 1, this);
        logic = Logic.FALSE;
        getOwnerArea().registerActor(this);
    }

    /**
     * Active les murs associés à la plaque de pression.
     */
    private void wallOn() {
        for (Wall wall : wall) {
            wall.setOn();
        }
    }

    /**
     * Désactive les murs associés à la plaque de pression.
     */
    private void wallOff() {
        for (Wall wall : wall) {
            wall.setOff();
        }
    }

    /**
     * Dessine la plaque de pression sur le canvas.
     * @param canvas Le canvas où dessiner.
     */
    @Override
    public void draw(Canvas canvas) {
        sprite.draw(canvas);
    }

    /**
     * Met à jour l'état de la plaque de pression.
     * @param deltaTime Temps écoulé depuis la dernière mise à jour.
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        if (interactor != null && !interactor.getCurrentCells().equals(getCurrentCells())) {
            wallOn();
            logic = Logic.FALSE;
        }
    }

    /**
     * Renvoie les cellules actuellement occupées par la plaque de pression.
     * @return Liste contenant une seule cellule.
     */
    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates());
    }

    /**
     * Indique si la plaque de pression occupe un espace cellulaire.
     * @return Toujours false.
     */
    @Override
    public boolean takeCellSpace() {
        return false;
    }

    /**
     * Vérifie si la plaque de pression peut interagir au niveau des cellules.
     * @return Toujours true.
     */
    @Override
    public boolean isCellInteractable() {
        return true;
    }

    /**
     * Vérifie si la plaque de pression peut interagir au niveau de la vue.
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
     * Vérifie si la plaque de pression est activée.
     * @return True si activée, sinon False.
     */
    @Override
    public boolean isOn() {
        return logic.isOn();
    }

    /**
     * Vérifie si la plaque de pression est désactivée.
     * @return True si désactivée, sinon False.
     */
    @Override
    public boolean isOff() {
        return logic.isOff();
    }

    /**
     * Désactive la plaque de pression et les murs associés.
     * @param interactor L'interacteur qui interagit avec la plaque.
     */
    public void setOff(Interactor interactor) {
        this.interactor = interactor;
        logic = Logic.TRUE;
        wallOff();
    }

    /**
     * Active la plaque de pression et les murs associés.
     * @param interactor L'interacteur qui interagit avec la plaque.
     */
    public void setOn(Interactor interactor) {
        this.interactor = interactor;
        wallOn();
        logic = Logic.FALSE;
    }
}