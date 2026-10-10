package ch.epfl.cs107.icoop.actor;

import ch.epfl.cs107.icoop.handler.ICoopInteractionVisitor;
import ch.epfl.cs107.play.areagame.actor.AreaEntity;
import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.actor.Interactor;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.OrientedAnimation;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.math.Transform;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.math.random.RandomGenerator;
import ch.epfl.cs107.play.window.Canvas;

import java.util.*;

/**
 * Classe HellSkull.
 * Cette classe représente une entité ennemie invoquant des projectiles de type "Feu" et possédant des points de vie limités.
 */
public class HellSkull extends AreaEntity implements Interactable, Interactor {

    /** Points de vie de l'entité. */
    private final Health health;

    /** Nombre maximum de points de vie. */
    private final int MAX_LIFE = 1;

    /** Dégâts infligés par l'entité. */
    private final int DAMAGE = 5;

    /** Liste des types de dégâts auxquels l'entité est invulnérable. */
    private final List<ICoopPlayer.DamageType> invulnerability;

    /** Animation associée à l'entité. */
    private OrientedAnimation animation;

    /** Durée de l'animation. */
    private final int ANIMATION_DURATION = 12;

    /** Temps aléatoire pour l'invocation de projectiles. */
    private float randomFloat = 1.0f;

    /** Orientation actuelle de l'entité. */
    private Orientation orientation;

    /** Gestionnaire d'interactions pour l'entité HellSkull. */
    private final ICoopHellSkullInteractionHandler handler = new ICoopHellSkullInteractionHandler();

    /**
     * Constructeur de la classe HellSkull.
     *
     * @param area        La zone où l'entité est située.
     * @param orientation L'orientation initiale de l'entité.
     * @param position    La position initiale de l'entité.
     */
    public HellSkull(Area area, Orientation orientation, DiscreteCoordinates position) {
        super(area, orientation, position);
        this.health = new Health(this, null, MAX_LIFE, false);
        this.invulnerability = new ArrayList<>();
        this.orientation = orientation;
        invulnerability.add(ICoopPlayer.DamageType.FIRE);
        invulnerability.add(ICoopPlayer.DamageType.EXPLOSION);

        Orientation[] orders = new Orientation[]{Orientation.UP, Orientation.LEFT, Orientation.DOWN, Orientation.RIGHT};
        animation = new OrientedAnimation("icoop/flameskull", ANIMATION_DURATION / 3, this, new Vector(-0.5f, -0.5f), orders, 3, 2, 2, 32, 32, true);

        area.registerActor(this);
    }

    /**
     * Dessine l'entité et son animation.
     *
     * @param canvas Le canvas sur lequel dessiner.
     */
    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        animation.draw(canvas);
    }

    /**
     * Met à jour l'état de l'entité chaque frame.
     *
     * @param deltaTime Le temps écoulé depuis la dernière mise à jour.
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        animation.update(deltaTime);
        if (isDead()) {
            getOwnerArea().unregisterActor(this);
        } else {
            randomFloat -= deltaTime;
            if (randomFloat < 0f) {
                randomFloat = RandomGenerator.getInstance().nextFloat(1.5f, 4.f);
                Vector orientationVector = getOrientation().toVector();
                DiscreteCoordinates frontCell = getCurrentMainCellCoordinates().jump(orientationVector);
                getOwnerArea().registerActor(new Fire(getOwnerArea(), orientation, frontCell, "icoop/fire", 1, 250, ICoopPlayer.DamageType.FIRE, true));
            }
        }
    }

    /**
     * Renvoie les cellules actuellement occupées par l'entité.
     *
     * @return Une liste contenant les coordonnées des cellules occupées.
     */
    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates());
    }

    /**
     * Renvoie les cellules dans le champ de vision de l'entité.
     *
     * @return Une liste des coordonnées des cellules adjacentes.
     */
    @Override
    public List<DiscreteCoordinates> getFieldOfViewCells() {
        DiscreteCoordinates mainCell = getCurrentMainCellCoordinates();
        return Arrays.asList(
                mainCell.jump(new Vector(1, 0)),  // Droite
                mainCell.jump(new Vector(-1, 0)), // Gauche
                mainCell.jump(new Vector(0, 1)),  // Haut
                mainCell.jump(new Vector(0, -1))  // Bas
        );
    }

    /**
     * Indique si l'entité veut interagir via les cellules.
     *
     * @return true car HellSkull veut interagir via les cellules.
     */
    @Override
    public boolean wantsCellInteraction() {
        return true;
    }

    /**
     * Indique si l'entité veut interagir via la vue.
     *
     * @return false car HellSkull n'interagit pas via la vue.
     */
    @Override
    public boolean wantsViewInteraction() {
        return false;
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
     * Indique si l'entité est invulnérable à un type de dégâts donné.
     *
     * @param damageType Le type de dégâts.
     * @return true si l'entité est invulnérable, false sinon.
     */
    public boolean isInvulnerable(ICoopPlayer.DamageType damageType) {
        return invulnerability.contains(damageType);
    }

    /**
     * Indique si l'entité est morte.
     *
     * @return true si les points de vie sont à 0, false sinon.
     */
    public boolean isDead() {
        return health.isOff();
    }

    /**
     * Applique des dégâts à l'entité.
     *
     * @param damageType Le type de dégâts.
     * @param amount     La quantité de dégâts.
     */
    public void applyDamage(ICoopPlayer.DamageType damageType, int amount) {
        if (isInvulnerable(damageType)) {
            System.out.println("GOD MODE pour " + damageType);
        } else {
            health.decrease(amount);
        }
    }

    /**
     * Soigne l'entité en augmentant ses points de vie.
     *
     * @param amount Quantité de points de vie à ajouter.
     */
    public void heal(int amount) {
        health.increase(amount);
    }

    /**
     * Réinitialise les points de vie de l'entité à leur maximum.
     */
    public void resetHealth() {
        health.resetHealth();
    }

    /**
     * Indique si l'entité occupe l'espace d'une cellule.
     *
     * @return false car l'entité ne bloque pas les cellules.
     */
    @Override
    public boolean takeCellSpace() {
        return false;
    }

    /**
     * Indique si l'entité est interactable au niveau des cellules.
     *
     * @return true car HellSkull est interactable via les cellules.
     */
    @Override
    public boolean isCellInteractable() {
        return true;
    }

    /**
     * Indique si l'entité est interactable au niveau de la vue.
     *
     * @return true car HellSkull est interactable via la vue.
     */
    @Override
    public boolean isViewInteractable() {
        return true;
    }

    /**
     * Accepte une interaction d'un visiteur d'interaction.
     *
     * @param v                 Le visiteur interagissant avec HellSkull.
     * @param isCellInteraction Indique si l'interaction est basée sur les cellules.
     */
    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        ((ICoopInteractionVisitor) v).interactWith(this, isCellInteraction);
    }

    /**
     * Gestionnaire d'interactions pour HellSkull.
     */
    private class ICoopHellSkullInteractionHandler implements ICoopInteractionVisitor {

        /**
         * Interagit avec un joueur.
         *
         * @param player            Le joueur interagissant avec HellSkull.
         * @param isCellInteraction Indique si l'interaction est basée sur les cellules.
         */
        @Override
        public void interactWith(ICoopPlayer player, boolean isCellInteraction) {
            if (wantsCellInteraction() && isCellInteraction) {
                player.applyDamage(ICoopPlayer.DamageType.FIRE, DAMAGE);
            }
        }
    }
}