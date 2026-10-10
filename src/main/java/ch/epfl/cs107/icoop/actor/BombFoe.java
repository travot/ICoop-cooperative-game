package ch.epfl.cs107.icoop.actor;

import ch.epfl.cs107.icoop.handler.ICoopInteractionVisitor;
import ch.epfl.cs107.play.areagame.actor.AreaEntity;
import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.actor.Interactor;
import ch.epfl.cs107.play.areagame.actor.MovableAreaEntity;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.OrientedAnimation;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.math.random.RandomGenerator;
import ch.epfl.cs107.play.window.Canvas;

import java.util.*;

import static java.nio.file.Files.move;

/**
 * Classe BombFoe.
 * Cette classe représente les ennemis présents dans le labyrinthe (Maze).
 * Les BombFoes sont des entités mobiles qui peuvent attaquer un joueur, entrer dans un mode protégé, ou
 * rester inactifs selon leur état actuel. Ils possèdent une barre de vie et des vulnérabilités à certains types
 * de dommages.
 */
public class BombFoe extends MovableAreaEntity implements Interactable, Interactor {

    /**
     * Barre de vie de l'entité BombFoe.
     */
    private final Health health;

    /**
     * Points de vie maximum du BombFoe.
     */
    private final int MAX_LIFE = 2;

    /**
     * Dégâts infligés par le BombFoe lors d'une attaque.
     */
    private final int DAMAGE = 5;

    /**
     * Liste des types de dommages auxquels le BombFoe est invulnérable.
     */
    private final List<ICoopPlayer.DamageType> invulnerability;

    /**
     * Animation orientée du BombFoe.
     */
    private OrientedAnimation animation;

    /**
     * Durée de l'animation.
     */
    private final int ANIMATION_DURATION = 24;

    /**
     * Point d'ancrage de l'animation.
     */
    private final Vector anchor = new Vector(-0.5f, 0);

    /**
     * Ordre des orientations disponibles pour le déplacement.
     */
    private final Orientation[] orders = {Orientation.DOWN, Orientation.RIGHT, Orientation.UP, Orientation.LEFT};

    /**
     * Temps restant d'invulnérabilité.
     */
    private float invulnerabilityTimers;

    /**
     * Temps maximum d'inactivité entre les mouvements.
     */
    private final int MAX_STEP_INACTIVE = 10;

    /**
     * Temps courant d'inactivité.
     */
    private int currentStepInactive = 0;

    /**
     * Indique si le BombFoe est en mouvement.
     */
    private boolean isMoving;

    /**
     * Durée minimale de mouvement pour éviter des bugs.
     */
    private float movingTime = 1.0f;

    /**
     * Orientation actuelle du BombFoe.
     */
    private Orientation orientation;

    /**
     * Temps de protection (mode protégé).
     */
    private int protectTime;

    /**
     * Cible actuelle du BombFoe.
     */
    private ICoopPlayer target;

    /**
     * Temps avant que le BombFoe puisse attaquer à nouveau.
     */
    private float isReady = 0.1f;

    /**
     * État actuel du BombFoe (IDLE, en attaque ou protégé).
     */
    private State state = State.IDLE;

    /**
     * Gestionnaire d'interactions pour le BombFoe.
     */
    private final ICoopBombFoeInteractionHandler handler = new ICoopBombFoeInteractionHandler();

    /**
     * Constructeur de la classe BombFoe.
     *
     * @param area        L'aire dans laquelle le BombFoe existe.
     * @param orientation L'orientation initiale du BombFoe.
     * @param position    La position initiale du BombFoe.
     */
    public BombFoe(Area area, Orientation orientation, DiscreteCoordinates position) {
        super(area, Orientation.DOWN, position);
        this.health = new Health(this, null, MAX_LIFE, false);
        this.invulnerability = new ArrayList<>();
        this.orientation = orientation;
        invulnerability.add(ICoopPlayer.DamageType.WATER);
        invulnerability.add(ICoopPlayer.DamageType.EXPLOSION);
        defaultAnimation();
        area.registerActor(this);
    }

    /**
     * Définit une cible et passe le BombFoe en mode attaque.
     *
     * @param player Le joueur ciblé.
     */
    private void setAttack(ICoopPlayer player) {
        this.target = player;
        state = State.ISCURRENTATTACK;
        defaultAnimation();
    }

    /**
     * Active le mode protégé du BombFoe.
     */
    private void setProtect() {
        state = State.ISCURRENTPROTECT;
        animation = new OrientedAnimation("icoop/bombFoe.protecting", ANIMATION_DURATION / 3, this, anchor, orders, 4, 2, 2, 32, 32, false);
    }

    /**
     * Restaure l'état IDLE par défaut.
     */
    private void setIdle() {
        state = State.IDLE;
        defaultAnimation();
    }

    /**
     * Génère un temps d'inactivité aléatoire.
     */
    private void randomCurrentStepInactive() {
        currentStepInactive = RandomGenerator.getInstance().nextInt(MAX_STEP_INACTIVE);
    }

    /**
     * Définit l'animation par défaut du BombFoe.
     */
    private void defaultAnimation() {
        animation = new OrientedAnimation("icoop/bombFoe", ANIMATION_DURATION / 3, this, anchor, orders, 4, 2, 2, 32, 32, true);
    }

    /**
     * Effectue un déplacement aléatoire.
     *
     * @param MOVE_DURATION Durée du mouvement.
     */
    private void randomMove(int MOVE_DURATION) {
        if (!isDisplacementOccurs()) {
            double randDouble = RandomGenerator.getInstance().nextDouble();

            if (randDouble <= 0.4) {
                this.orientation = orders[RandomGenerator.getInstance().nextInt(orders.length)];
            }
            orientate(orientation);
            move(MOVE_DURATION);
            isMoving = true;
            movingTime = (float) MOVE_DURATION / 5;
        }
    }

    /**
     * Génère un temps de protection aléatoire.
     */
    private void randomTime() {
        protectTime = RandomGenerator.getInstance().nextInt(120 - 72 + 1) + 72;
    }

    /**
     * Déplace le BombFoe vers sa cible.
     */
    private void moveToTarget() {
        int deltaX = target.getCurrentMainCellCoordinates().x - getCurrentMainCellCoordinates().x;
        int deltaY = target.getCurrentMainCellCoordinates().y - getCurrentMainCellCoordinates().y;

        DiscreteCoordinates direction;
        if (Math.abs(deltaX) > Math.abs(deltaY)) {
            direction = new DiscreteCoordinates(Integer.signum(deltaX), 0);
        } else {
            direction = new DiscreteCoordinates(0, Integer.signum(deltaY));
        }

        Vector directionVector = direction.toVector();

        Orientation nouvelleOrientation = Orientation.fromVector(directionVector);
        orientate(nouvelleOrientation);
        move(3);
        isMoving = true;
        movingTime = 0.5f;
    }

    /**
     * Calcule la distance entre le BombFoe et sa cible.
     *
     * @return La distance jusqu'à la cible.
     */
    private int rangeOfTarget() {
        int deltaX = Math.abs(getCurrentMainCellCoordinates().x - target.getCurrentMainCellCoordinates().x);
        int deltaY = Math.abs(getCurrentMainCellCoordinates().y - target.getCurrentMainCellCoordinates().y);
        return Math.max(deltaX, deltaY);
    }

    /**
     * Indique si le BombFoe est prêt à attaquer.
     *
     * @return true si prêt, false sinon.
     */
    private boolean isReady() {
        return (isReady <= 0);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        animation.draw(canvas);
    }

    /**
     * Met à jour le BombFoe en fonction de son état et des conditions environnementales.
     * Gère le mouvement, les transitions d'état, l'invulnérabilité et les interactions
     * avec d'autres objets durant le cycle de mise à jour.
     *
     * @param deltaTime le temps écoulé depuis la dernière mise à jour, utilisé pour synchroniser
     * les mouvements et les animations
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        animation.update(deltaTime);
        if (!(invulnerabilityTimers <= 0)) invulnerabilityTimers -= deltaTime;
        if(currentStepInactive > 0) currentStepInactive -= deltaTime;

        if(invulnerabilityTimers<=0) {
            if(currentStepInactive <= 0 && !isMoving) {
                if (state == State.IDLE) {
                    randomMove(8);
                    randomCurrentStepInactive();
                }
                if (state == State.ISCURRENTATTACK) {
                    if(target != null && isReady() && (rangeOfTarget() <=2)) {
                        isReady = 0.1f;
                        Vector orientationVector = getOrientation().toVector();
                        DiscreteCoordinates fieldOfViewCell = getCurrentMainCellCoordinates().jump(orientationVector);
                        if(getOwnerArea().canEnterAreaCells(this, getFieldOfViewCells())) {
                            Explosif bomb = new Explosif(getOwnerArea(), Orientation.DOWN, fieldOfViewCell, 5, false);
                            getOwnerArea().registerActor(bomb);
                            bomb.setActive(true);
                            setProtect();
                        }
                    } else {
                        isReady-= deltaTime;
                        moveToTarget();
                    }
                }
                if (state == State.ISCURRENTPROTECT) {
                    randomMove(15);
                    //target = null;
                    if(protectTime<= 0) randomTime();
                }
            }
        } else {
            setIdle();
            currentStepInactive = 0;
        }
        if(protectTime <= 0 && state == State.ISCURRENTPROTECT) setIdle();
        else protectTime -= deltaTime;
        if(isMoving) {
            movingTime-= deltaTime;
            if(movingTime <= 0) isMoving = false;
        }
    }

    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates());
    }

    @Override
    public List<DiscreteCoordinates> getFieldOfViewCells() {
        DiscreteCoordinates mainCell = getCurrentMainCellCoordinates();
        if(state == State.ISCURRENTATTACK) {
            Vector orientationVector = getOrientation().toVector();
            return Arrays.asList(mainCell.jump(orientationVector));
        } else {
            List<DiscreteCoordinates> adjacentCells = Arrays.asList(
                    mainCell.jump(new Vector(1, 0)),  // Droite
                    mainCell.jump(new Vector(-1, 0)), // Gauche
                    mainCell.jump(new Vector(0, 1)),  // Haut
                    mainCell.jump(new Vector(0, -1)) // Bas
            );
            return adjacentCells;
        }
    }

    @Override
    public boolean wantsCellInteraction() {
        return false;
    }

    @Override
    public boolean wantsViewInteraction() {
        if(state != State.ISCURRENTPROTECT) return true;
        return false;
    }

    @Override
    public void interactWith(Interactable other, boolean isCellInteraction) {
        other.acceptInteraction(handler, isCellInteraction);
    }

    ///Enumération des etats du BombFoe
    private enum State {
        IDLE,
        ISCURRENTATTACK,
        ISCURRENTPROTECT;
    }

    /**
     * Méthode isInvulnerable.
     * @return True si le BombFoe est Invulnérable au DamageType donné
     */
    public boolean isInvulnerable(ICoopPlayer.DamageType damageType) {
        return invulnerability.contains(damageType);
    }

    /**
     * Vérifie si le BombFoe est mort.
     * @return True si les points de vie sont épuisés, false sinon.
     */
    public boolean isDead() {
        return health.isOff();
    }

    /**
     * Applique un certain nombre de dégâts au BombFoe si le type de dégâts n'est pas dans sa liste d'invulnérabilités.
     *
     * @param damageType Le type de dégâts à appliquer.
     * @param amount     La quantité de dégâts.
     */
    public void applyDamage(ICoopPlayer.DamageType damageType, int amount) {
        if (!isInvulnerable(damageType)) {
            health.decrease(amount);
            if (isDead()) {
                getOwnerArea().unregisterActor(this);
            }
            invulnerabilityTimers = 5.0f;
        }
    }

    /**
     * Soigne le BombFoe d'un certain montant.
     *
     * @param amount La quantité de points de vie à restaurer.
     */
    public void heal(int amount) {
        health.increase(amount);
    }

    /**
     * Remet les points de vie du BombFoe à leur maximum.
     */
    public void resetHealth() {
        health.resetHealth();
    }

    @Override
    public boolean takeCellSpace() {
        return true;
    }

    @Override
    public boolean isCellInteractable() {
        return false;
    }

    @Override
    public boolean isViewInteractable() {
        return true;
    }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        ((ICoopInteractionVisitor) v).interactWith(this, isCellInteraction);
    }

    /**
     * Représente le gestionnaire d'interactions pour le BombFoe.
     * Implémente l'interface ICoopInteractionVisitor pour définir les comportements
     * spécifiques lors des interactions avec d'autres entités.
     */
    private class ICoopBombFoeInteractionHandler implements ICoopInteractionVisitor {

        @Override
        public void interactWith(ICoopPlayer player, boolean isCellInteraction) {
            if (wantsViewInteraction()) {
                setAttack(player);
            }
        }

        @Override
        public void interactWith(Explosif explosif, boolean isCellInteraction) {
            if (state != State.ISCURRENTPROTECT) {
                explosif.setExploded(true);
                applyDamage(ICoopPlayer.DamageType.EXPLOSION, DAMAGE);
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