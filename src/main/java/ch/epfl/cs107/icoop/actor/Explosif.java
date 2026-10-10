package ch.epfl.cs107.icoop.actor;

import ch.epfl.cs107.icoop.ICoopCollectable;
import ch.epfl.cs107.icoop.handler.ICoopInteractionVisitor;
import ch.epfl.cs107.icoop.handler.ICoopItem;
import ch.epfl.cs107.play.areagame.actor.AreaEntity;
import ch.epfl.cs107.play.areagame.actor.CollectableAreaEntity;
import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.actor.Interactor;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.Animation;
import ch.epfl.cs107.play.engine.actor.Sprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Classe Explosif.
 * Représente une entité collectable explosive pouvant interagir avec d'autres entités et changer d'état lorsqu'elle explose.
 */
public final class Explosif extends CollectableAreaEntity implements Interactor {

    /** Durée d'animation par défaut. */
    private final static int ANIMATION_DURATION = 24;

    /** Indique si l'explosif est actif. */
    private boolean active;

    /** Indique si l'explosif a explosé. */
    private boolean exploded;

    /** Temps restant avant explosion. */
    private float time;

    /** Indique si l'explosif a été collecté. */
    private boolean isCollected;

    /** Animation associée à l'explosif. */
    private Animation animation;

    /** Sprite associé à l'explosif. */
    private Sprite sprite;

    /** Gestionnaire d'interactions pour l'explosif. */
    private final ICoopExplosifInteractionHandler handler = new ICoopExplosifInteractionHandler();

    /**
     * Constructeur de la classe Explosif.
     *
     * @param area        La zone où l'explosif est placé.
     * @param orientation L'orientation initiale de l'explosif.
     * @param position    La position initiale de l'explosif.
     * @param time        Le temps avant explosion.
     * @param isCollected Indique si l'explosif est déjà collecté.
     */
    public Explosif(Area area, Orientation orientation, DiscreteCoordinates position, int time, boolean isCollected) {
        super(area, orientation, position);
        this.active = false;
        this.exploded = false;
        this.time = (float) time;
        this.isCollected = isCollected;
        this.animation = new Animation("icoop/explosive", 2, 1, 1, this, 16, 16, ANIMATION_DURATION / 2, true);
        area.registerActor(this);
    }

    /**
     * Dessine l'explosif sur le canvas.
     *
     * @param canvas Le canvas sur lequel dessiner.
     */
    @Override
    public void draw(Canvas canvas) {
        animation.draw(canvas);
    }

    /**
     * Met à jour l'état de l'explosif chaque frame.
     *
     * @param deltaTime Le temps écoulé depuis la dernière mise à jour.
     */
    @Override
    public void update(float deltaTime) {
        if (active && !getExploded()) {
            animation.update(deltaTime);
            time -= deltaTime;
            if (time <= 0) {
                setExploded(true);
            }
        }
        if (!active && getExploded()) {
            animation.update(deltaTime);
            if (time <= -0.5) getOwnerArea().unregisterActor(this);
            time -= deltaTime;
        }
        if (isCollected) {
            getOwnerArea().unregisterActor(this);
        }
    }

    /**
     * Indique si l'explosif a explosé.
     *
     * @return true si l'explosif a explosé, false sinon.
     */
    public boolean getExploded() {
        return exploded;
    }

    /**
     * Définit l'état d'explosion de l'explosif et change son animation.
     *
     * @param exploded true si l'explosif doit être en état explosé, false sinon.
     */
    public void setExploded(boolean exploded) {
        this.exploded = exploded;
        this.active = !exploded;
        this.animation = new Animation("icoop/explosion", 7, 1, 1, this, 32, 32, ANIMATION_DURATION / 7, false);
    }

    /**
     * Active ou désactive l'explosif.
     *
     * @param active true pour activer, false pour désactiver.
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Indique si l'explosif est actif.
     *
     * @return true si l'explosif est actif, false sinon.
     */
    public boolean getActive() {
        return active;
    }

    /**
     * Renvoie les cellules actuellement occupées par l'explosif.
     *
     * @return Une liste contenant les coordonnées des cellules occupées.
     */
    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates());
    }

    /**
     * Renvoie les cellules autour de l'explosif.
     *
     * @return Une liste des coordonnées des cellules adjacentes.
     */
    @Override
    public List<DiscreteCoordinates> getFieldOfViewCells() {
        DiscreteCoordinates mainCell = getCurrentMainCellCoordinates();
        List<DiscreteCoordinates> adjacentCells = Arrays.asList(
                mainCell.jump(new Vector(1, 0)),  // Droite
                mainCell.jump(new Vector(-1, 0)), // Gauche
                mainCell.jump(new Vector(0, 1)),  // Haut
                mainCell.jump(new Vector(0, -1)), // Bas
                mainCell                                // Milieu
        );
        return adjacentCells;
    }

    /**
     * Indique si l'explosif veut interagir via les cellules.
     *
     * @return true si l'explosif veut interagir, false sinon.
     */
    @Override
    public boolean wantsCellInteraction() {
        return exploded;
    }

    /**
     * Indique si l'explosif veut interagir via la vue.
     *
     * @return true si l'explosif veut interagir, false sinon.
     */
    @Override
    public boolean wantsViewInteraction() {
        return exploded;
    }

    /**
     * Interagit avec un autre objet interactif.
     *
     * @param other             L'autre objet interactif.
     * @param isCellInteraction Indique si l'interaction est basée sur les cellules.
     */
    @Override
    public void interactWith(Interactable other, boolean isCellInteraction) {
        other.acceptInteraction(handler, isCellInteraction);
    }

    /**
     * Indique si l'explosif occupe une cellule.
     *
     * @return false car l'explosif ne bloque pas les cellules.
     */
    @Override
    public boolean takeCellSpace() {
        return false;
    }

    /**
     * Indique si l'explosif est interactable au niveau des cellules.
     *
     * @return true si l'explosif est interactable, false sinon.
     */
    @Override
    public boolean isCellInteractable() {
        return !exploded;
    }

    /**
     * Indique si l'explosif est interactable au niveau de la vue.
     *
     * @return true si l'explosif est interactable, false sinon.
     */
    @Override
    public boolean isViewInteractable() {
        return !exploded;
    }

    /**
     * Accepte une interaction d'un visiteur d'interaction.
     *
     * @param v                 Le visiteur interagissant avec l'explosif.
     * @param isCellInteraction Indique si l'interaction est basée sur les cellules.
     */
    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        ((ICoopInteractionVisitor) v).interactWith(this, isCellInteraction);
    }

    /**
     * Collecte l'explosif. Change son état en collecté.
     */
    @Override
    public void collect() {
        if (!isCollected) {
            isCollected = true;
        }
    }

    /**
     * Indique si l'explosif a été collecté.
     *
     * @return true si l'explosif est collecté, false sinon.
     */
    @Override
    public boolean isCollected() {
        return isCollected;
    }

    /**
     * Gestionnaire d'interactions pour l'explosif.
     */
    private class ICoopExplosifInteractionHandler implements ICoopInteractionVisitor {

        @Override
        public void interactWith(Obstacle obstacle, boolean isCellInteraction) {
            if (wantsViewInteraction()) {
                obstacle.setDestroyed(true);
            }
        }

        @Override
        public void interactWith(Rock rock, boolean isCellInteraction) {
            if (wantsViewInteraction()) {
                rock.setDestroyed(true);
            }
        }

        @Override
        public void interactWith(ICoopPlayer player, boolean isCellInteraction) {
            if (wantsViewInteraction() && !isCellInteraction) {
                player.applyDamage(ICoopPlayer.DamageType.EXPLOSION, 10);
            }
        }

        @Override
        public void interactWith(Explosif explosif, boolean isCellInteraction) {
            if (wantsViewInteraction()) {
                explosif.setExploded(true);
            }
        }

        @Override
        public void interactWith(Wall wall, boolean isCellInteraction) {
            if (wantsViewInteraction()) {
                wall.setOff();
            }
        }
    }
}
