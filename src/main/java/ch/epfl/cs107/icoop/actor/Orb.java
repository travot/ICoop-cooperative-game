package ch.epfl.cs107.icoop.actor;

import ch.epfl.cs107.icoop.ICoopBehavior;
import ch.epfl.cs107.icoop.handler.ICoopInteractionVisitor;
import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.Animation;
import ch.epfl.cs107.play.engine.actor.RPGSprite;
import ch.epfl.cs107.play.engine.actor.Sprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.window.Canvas;

/**
 * Classe Orb.
 * Cette classe représente un orbe élémentaire utilisé dans le jeu.
 * Les orbes peuvent être collectés et sont associés à des éléments tels que "FEU" ou "EAU".
 */
public class Orb extends ElementalItem implements Interactable {

    /** Indique si l'orbe a été collecté. */
    private boolean isCollected;

    /** Nombre de frames dans l'animation de l'orbe. */
    private final int ANIMATION_FRAMES = 6;

    /** Durée totale de l'animation en frames. */
    private final int ANIMATION_DURATION = 24;

    /** Décalage vertical du sprite en fonction de l'élément. */
    private int spriteYDelta;

    /** Animation associée à l'orbe. */
    private Animation animation;

    /** Élément associé à l'orbe ("FEU", "EAU", etc.). */
    private String element;

    /**
     * Constructeur de la classe Orb.
     * @param area L'aire où l'orbe est créé.
     * @param orientation Orientation initiale de l'orbe.
     * @param position Position initiale de l'orbe.
     * @param element L'élément associé à l'orbe.
     * @param isCollected Indique si l'orbe est collecté dès la création.
     */
    public Orb(Area area, Orientation orientation, DiscreteCoordinates position, String element, boolean isCollected) {
        super(area, orientation, position, element, isCollected);
        this.element = element;
        this.isCollected = isCollected;
        spriteYDelta = OrbType.getSpriteYDeltaByElement(element);
        final Sprite[] sprites = new Sprite[ANIMATION_FRAMES];
        for (int i = 0; i < ANIMATION_FRAMES; i++) {
            sprites[i] = new RPGSprite("icoop/orb", 1, 1, this, new RegionOfInterest(i * 32, spriteYDelta, 32, 32));
        }
        this.animation = new Animation(ANIMATION_DURATION / ANIMATION_FRAMES, sprites);
        area.registerActor(this);
    }

    /**
     * Enumération des types d'orbes.
     */
    public enum OrbType {
        FEU("FEU", 64, "orb_fire_msg"),
        EAU("EAU", 0, "orb_water_msg");

        final String element;
        final int spriteYDelta;
        final String dialog;

        OrbType(String element, int spriteYDelta, String dialog) {
            this.element = element;
            this.spriteYDelta = spriteYDelta;
            this.dialog = dialog;
        }

        /**
         * Renvoie le décalage vertical du sprite en fonction de l'élément.
         * @param element L'élément de l'orbe.
         * @return Le décalage vertical du sprite.
         */
        public static int getSpriteYDeltaByElement(String element) {
            for (OrbType orb : OrbType.values()) {
                if (orb.element.equals(element)) {
                    return orb.spriteYDelta;
                }
            }
            return 0;
        }

        /**
         * Renvoie le dialogue associé à un élément.
         * @param element L'élément de l'orbe.
         * @return Le texte du dialogue associé.
         */
        public static String getDialogByElement(String element) {
            for (OrbType orb : OrbType.values()) {
                if (orb.element.equals(element)) {
                    return orb.dialog;
                }
            }
            return null;
        }
    }

    /**
     * Dessine l'orbe sur le canvas.
     * @param canvas Le canvas où dessiner.
     */
    @Override
    public void draw(Canvas canvas) {
        animation.draw(canvas);
    }

    /**
     * Met à jour l'animation de l'orbe.
     * @param deltaTime Temps écoulé depuis la dernière mise à jour.
     */
    @Override
    public void update(float deltaTime) {
        animation.update(deltaTime);
    }

    /**
     * Collecte l'orbe.
     * Si l'orbe n'est pas encore collecté, il est marqué comme collecté et retiré de l'aire.
     */
    @Override
    public void collect() {
        if (!isCollected) {
            isCollected = true;
            getOwnerArea().unregisterActor(this);
        }
    }

    /**
     * Vérifie si l'orbe a été collecté.
     * @return True si collecté, sinon False.
     */
    @Override
    public boolean isCollected() {
        return isCollected;
    }

    /**
     * Renvoie le dialogue associé à l'orbe en fonction de son élément.
     * @return Le texte du dialogue.
     */
    public String getOrbDialog() {
        return OrbType.getDialogByElement(element);
    }

    /**
     * Vérifie si l'orbe peut interagir au niveau des cellules.
     * @return Toujours true.
     */
    @Override
    public boolean isCellInteractable() {
        return true;
    }

    /**
     * Vérifie si l'orbe peut interagir au niveau de la vue.
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
}