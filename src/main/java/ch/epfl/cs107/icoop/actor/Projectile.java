package ch.epfl.cs107.icoop.actor;

import ch.epfl.cs107.play.areagame.actor.AreaEntity;
import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.actor.Interactor;
import ch.epfl.cs107.play.areagame.actor.MovableAreaEntity;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.Animation;
import ch.epfl.cs107.play.engine.actor.RPGSprite;
import ch.epfl.cs107.play.engine.actor.Sprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Classe abstraite Projectile.
 * Cette classe représente un projectile dans le jeu qui peut se déplacer dans une aire et interagir avec d'autres entités.
 */
public abstract class Projectile extends MovableAreaEntity implements Interactable, Interactor {

    /** Durée du mouvement du projectile, en frames. */
    protected final int MOVE_DURATION = 30;

    /**
     * Constructeur de la classe Projectile.
     * @param area L'aire où le projectile est créé.
     * @param orientation Orientation initiale du projectile.
     * @param position Position initiale du projectile.
     * @param range Portée du projectile.
     */
    public Projectile(Area area, Orientation orientation, DiscreteCoordinates position, int range) {
        super(area, orientation, position);
    }

    /**
     * Vérifie si le projectile a atteint la fin de sa course.
     * @return True si le projectile a terminé sa course (range <= 0), sinon False.
     */
    public abstract boolean isOver();

    /**
     * Marque le projectile comme étant en fin de course.
     * Le projectile est désenregistré de l'aire.
     */
    public abstract void setOver();

    /**
     * Vérifie si le projectile souhaite interagir avec les cellules.
     * @return True si le projectile n'a pas terminé sa course, sinon False.
     */
    @Override
    public boolean wantsCellInteraction() {
        return !isOver();
    }

    /**
     * Vérifie si le projectile souhaite interagir avec la vue.
     * @return True si le projectile n'a pas terminé sa course, sinon False.
     */
    @Override
    public boolean wantsViewInteraction() {
        return !isOver();
    }

    /**
     * Vérifie si le projectile occupe un espace cellulaire.
     * @return Toujours False.
     */
    @Override
    public boolean takeCellSpace() {
        return false;
    }

    /**
     * Vérifie si le projectile peut interagir au niveau des cellules.
     * @return Toujours False.
     */
    @Override
    public boolean isCellInteractable() {
        return false;
    }

    /**
     * Vérifie si le projectile peut interagir au niveau de la vue.
     * @return Toujours False.
     */
    @Override
    public boolean isViewInteractable() {
        return false;
    }

    /**
     * Détermine si le projectile est inarrêtable (peut traverser toutes les cellules).
     * @return True si le projectile est inarrêtable, sinon False.
     */
    public abstract boolean isUnstoppable();
}