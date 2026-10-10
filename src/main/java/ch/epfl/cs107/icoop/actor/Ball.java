package ch.epfl.cs107.icoop.actor;

import ch.epfl.cs107.icoop.handler.ICoopInteractionVisitor;
import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.actor.Interactor;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.Animation;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

import java.util.Collections;
import java.util.List;

/**
 * Représente un projectile de type "Ball" dans le jeu, capable d'interagir avec d'autres acteurs
 * et de se déplacer dans une zone.
 */
public class Ball extends Projectile implements Interactor, Interactable {

    /** Le type de dégâts infligés par la balle. */
    private ICoopPlayer.DamageType damageType;

    /** Indique si la balle est imparable (ignore les collisions). */
    private final boolean isUnstoppable;

    /** L'animation associée à la balle. */
    private final Animation animation;

    /** Durée de chaque image de l'animation. */
    private final int ANIMATION_DURATION = 12;

    /** La vitesse de la balle. */
    private final int speed;

    /** La portée restante de la balle. */
    private int range;

    /** Gestionnaire d'interactions pour gérer les interactions avec d'autres acteurs. */
    private final ICoopBallInteractionHandler handler = new ICoopBallInteractionHandler();

    /**
     * Construit un objet Ball.
     *
     * @param area         La zone où la balle existe.
     * @param orientation  L'orientation de la balle.
     * @param position     La position initiale de la balle.
     * @param element      Le type d'élément de la balle (par exemple, "EAU", "FEU").
     * @param speed        La vitesse de la balle.
     * @param range        La portée maximale que la balle peut parcourir.
     * @param isUnstoppable Indique si la balle est imparable.
     */
    public Ball(Area area, Orientation orientation, DiscreteCoordinates position, String element, int speed, int range, boolean isUnstoppable) {
        super(area, orientation, position, range);
        this.isUnstoppable = isUnstoppable;
        this.speed = speed;
        this.range = range;

        orientate(orientation);

        String spriteName = "";
        if (element.equals("EAU")) {
            spriteName = "icoop/magicWaterProjectile";
            this.damageType = ICoopPlayer.DamageType.WATER;
        } else if (element.equals("FEU")) {
            spriteName = "icoop/magicFireProjectile";
            this.damageType = ICoopPlayer.DamageType.FIRE;
        }

        this.animation = new Animation(spriteName, 4, 1, 1, this, 32, 32, ANIMATION_DURATION / 4, true);
        area.registerActor(this);
    }

    /**
     * Indique si la balle est imparable.
     *
     * @return true si la balle est imparable ; false sinon.
     */
    @Override
    public boolean isUnstoppable() {
        return isUnstoppable;
    }

    /**
     * Vérifie si la portée de la balle est terminée.
     *
     * @return true si la portée est inférieure ou égale à 0 ; false sinon.
     */
    @Override
    public boolean isOver() {
        return range <= 0;
    }

    /**
     * Marque la balle comme terminée et la désengage de la zone.
     */
    @Override
    public void setOver() {
        range = 0;
        getOwnerArea().unregisterActor(this);
    }

    /**
     * Dessine la balle sur le canvas donné.
     *
     * @param canvas Le canvas sur lequel dessiner.
     */
    @Override
    public void draw(Canvas canvas) {
        animation.draw(canvas);
    }

    /**
     * Met à jour l'état de la balle à chaque frame.
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
     * Renvoie les cellules actuellement occupées par la balle.
     *
     * @return Une liste des coordonnées des cellules occupées.
     */
    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates());
    }

    /**
     * Renvoie les cellules dans le champ de vision de la balle.
     *
     * @return Une liste des coordonnées dans le champ de vision.
     */
    @Override
    public List<DiscreteCoordinates> getFieldOfViewCells() {
        DiscreteCoordinates mainCell = getCurrentMainCellCoordinates();
        Vector orientationVector = getOrientation().toVector();
        DiscreteCoordinates fieldOfViewCell = mainCell.jump(orientationVector);
        return Collections.singletonList(fieldOfViewCell);
    }

    /**
     * Accepte les interactions d'un visiteur d'interaction.
     *
     * @param v                 Le visiteur interagissant avec la balle.
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
     * Gère les interactions spécifiques à l'objet Ball.
     */
    private class ICoopBallInteractionHandler implements ICoopInteractionVisitor {

        /** Une référence à la balle elle-même. */
        private final Ball ball = Ball.this;

        @Override
        public void interactWith(ICoopPlayer player, boolean isCellInteraction) {
            if (wantsCellInteraction() && !player.isInvulnerable(damageType)) {
                player.applyDamage(damageType, 5);
            }
        }

        @Override
        public void interactWith(BombFoe foe, boolean isCellInteraction) {
            if (wantsCellInteraction()) {
                foe.applyDamage(damageType, 1);
                getOwnerArea().unregisterActor(ball);
            }
        }

        @Override
        public void interactWith(HellSkull skull, boolean isCellInteraction) {
            if (wantsCellInteraction()) {
                skull.applyDamage(damageType, 1);
                getOwnerArea().unregisterActor(ball);
            }
        }

        @Override
        public void interactWith(Explosif explosif, boolean isCellInteraction) {
            if (wantsCellInteraction()) {
                explosif.setExploded(true);
                getOwnerArea().unregisterActor(ball);
            }
        }

        @Override
        public void interactWith(Rock rock, boolean isCellInteraction) {
            if (wantsViewInteraction()) {
                rock.setDestroyed(true);
            }
        }
    }
}
