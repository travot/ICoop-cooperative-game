package ch.epfl.cs107.icoop.actor;

import ch.epfl.cs107.icoop.ICoopBehavior;
import ch.epfl.cs107.icoop.area.ICoopArea;
import ch.epfl.cs107.icoop.handler.ICoopInteractionVisitor;
import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.actor.Interactor;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.Actor;
import ch.epfl.cs107.play.engine.actor.Animation;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

import java.util.Collections;
import java.util.List;

/**
 * Classe Fire.
 * Représente un projectile de type "Feu" qui peut interagir avec les autres entités et se déplacer dans une zone.
 */
public class Fire extends Projectile implements Interactable, Interactor {

    /** Type de dégâts infligés par le projectile. */
    private final ICoopPlayer.DamageType damageType;

    /** Indique si le projectile est imparable (ignore les collisions). */
    private final boolean isUnstoppable;

    /** Animation associée au projectile. */
    private final Animation animation;

    /** Vitesse du projectile. */
    private final int speed;

    /** Portée restante du projectile. */
    private int range;

    /** Gestionnaire d'interactions pour le projectile Fire. */
    private final ICoopFireInteractionHandler handler = new ICoopFireInteractionHandler();

    /**
     * Constructeur de la classe Fire.
     *
     * @param area         La zone où le projectile existe.
     * @param orientation  L'orientation initiale du projectile.
     * @param position     La position initiale du projectile.
     * @param spriteName   Le nom du sprite associé au projectile.
     * @param speed        La vitesse du projectile.
     * @param range        La portée maximale du projectile.
     * @param damageType   Le type de dégâts infligés par le projectile.
     * @param isUnstoppable Indique si le projectile est imparable.
     */
    public Fire(Area area, Orientation orientation, DiscreteCoordinates position, String spriteName, int speed, int range, ICoopPlayer.DamageType damageType, boolean isUnstoppable) {
        super(area, orientation, position, range);
        this.damageType = damageType;
        this.isUnstoppable = isUnstoppable;
        this.speed = speed;
        this.range = range;
        orientate(orientation);
        this.animation = new Animation(spriteName, 7, 1, 1, this, 16, 16, 4, true);
        area.registerActor(this);
    }

    /**
     * Vérifie si la portée du projectile est terminée.
     *
     * @return true si la portée est épuisée, false sinon.
     */
    @Override
    public boolean isOver() {
        return range <= 0;
    }

    /**
     * Marque le projectile comme terminé et le retire de la zone.
     */
    @Override
    public void setOver() {
        range = 0;
        getOwnerArea().unregisterActor(this);
    }

    /**
     * Indique si le projectile est imparable.
     *
     * @return true si le projectile est imparable, false sinon.
     */
    @Override
    public boolean isUnstoppable() {
        return isUnstoppable;
    }

    /**
     * Dessine le projectile sur le canvas.
     *
     * @param canvas Le canvas sur lequel dessiner.
     */
    @Override
    public void draw(Canvas canvas) {
        animation.draw(canvas);
    }

    /**
     * Met à jour l'état du projectile chaque frame.
     *
     * @param deltaTime Le temps écoulé depuis la dernière mise à jour.
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        animation.update(deltaTime);
        if (!isOver()) {
            if (!isDisplacementOccurs()) {
                if (!move(MOVE_DURATION / speed)) {
                    setOver();
                }
            }
        } else {
            setOver();
        }
        range -= 1;
    }

    /**
     * Renvoie les cellules actuellement occupées par le projectile.
     *
     * @return Une liste contenant les coordonnées des cellules occupées.
     */
    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates());
    }

    /**
     * Renvoie les cellules dans le champ de vision du projectile.
     *
     * @return Une liste contenant les coordonnées dans le champ de vision.
     */
    @Override
    public List<DiscreteCoordinates> getFieldOfViewCells() {
        DiscreteCoordinates mainCell = getCurrentMainCellCoordinates();
        Vector orientationVector = getOrientation().toVector();
        DiscreteCoordinates fieldOfViewCell = mainCell.jump(orientationVector);
        return Collections.singletonList(fieldOfViewCell);
    }

    /**
     * Accepte une interaction d'un visiteur d'interaction.
     *
     * @param v                 Le visiteur interagissant avec le projectile.
     * @param isCellInteraction Indique si l'interaction est basée sur les cellules.
     */
    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        ((ICoopInteractionVisitor) v).interactWith(this, isCellInteraction);
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
     * Définit une nouvelle portée pour le projectile.
     *
     * @param range La nouvelle portée du projectile.
     */
    public void setRange(int range) {
        this.range = range;
    }

    /**
     * Gestionnaire d'interactions pour le projectile Fire.
     */
    private class ICoopFireInteractionHandler implements ICoopInteractionVisitor {

        @Override
        public void interactWith(ICoopPlayer player, boolean isCellInteraction) {
            if (wantsCellInteraction() && isCellInteraction) {
                player.applyDamage(damageType, 5);
            }
        }

        @Override
        public void interactWith(Explosif explosif, boolean isCellInteraction) {
            if (wantsCellInteraction()) {
                explosif.setExploded(true);
            }
        }

        @Override
        public void interactWith(Obstacle obstacle, boolean isCellInteraction) {
            if (wantsCellInteraction()) {
                obstacle.setDestroyed(true);
            }
        }
    }
}