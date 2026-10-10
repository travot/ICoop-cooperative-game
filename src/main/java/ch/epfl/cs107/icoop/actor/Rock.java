package ch.epfl.cs107.icoop.actor;

import ch.epfl.cs107.icoop.handler.ICoopInteractionVisitor;
import ch.epfl.cs107.play.areagame.actor.AreaEntity;
import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.Sprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.window.Canvas;

import java.util.Collections;
import java.util.List;

/**
 * Classe Rock.
 * Cette classe représente un rocher dans le jeu, qui peut être détruit par interaction.
 */
public final class Rock extends AreaEntity implements Interactable {

    /** Sprite représentant le rocher. */
    private final Sprite sprite;

    /** Indique si le rocher est détruit. */
    private boolean destroyed;

    /**
     * Constructeur de la classe Rock.
     * @param area L'aire où le rocher est créé.
     * @param orientation Orientation initiale du rocher.
     * @param position Position initiale du rocher.
     * @param destroyed Indique si le rocher est détruit dès la création.
     */
    public Rock(Area area, Orientation orientation, DiscreteCoordinates position, boolean destroyed) {
        super(area, orientation, position);
        sprite = new Sprite("rock.1", 1f, 1f, this);
        this.destroyed = destroyed;
        area.registerActor(this);
    }

    /**
     * Dessine le rocher sur le canvas.
     * Si le rocher est détruit, il est retiré de l'aire.
     * @param canvas Le canvas où dessiner.
     */
    @Override
    public void draw(Canvas canvas) {
        if (!isDestroyed()) {
            sprite.draw(canvas);
        }
        if (destroyed) {
            getOwnerArea().unregisterActor(this);
        }
    }

    /**
     * Renvoie les cellules actuellement occupées par le rocher.
     * @return Liste contenant une seule cellule.
     */
    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates());
    }

    /**
     * Indique si le rocher occupe un espace cellulaire.
     * @return Toujours true.
     */
    @Override
    public boolean takeCellSpace() {
        return true;
    }

    /**
     * Vérifie si le rocher peut interagir au niveau des cellules.
     * @return Toujours true.
     */
    @Override
    public boolean isCellInteractable() {
        return true;
    }

    /**
     * Vérifie si le rocher peut interagir au niveau de la vue.
     * @return Toujours true.
     */
    @Override
    public boolean isViewInteractable() {
        return true;
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
     * Vérifie si le rocher est détruit.
     * @return True si détruit, sinon False.
     */
    public boolean isDestroyed() {
        return destroyed;
    }

    /**
     * Définit l'état de destruction du rocher.
     * @param destroyed True pour marquer le rocher comme détruit, sinon False.
     */
    public void setDestroyed(boolean destroyed) {
        this.destroyed = destroyed;
    }
}