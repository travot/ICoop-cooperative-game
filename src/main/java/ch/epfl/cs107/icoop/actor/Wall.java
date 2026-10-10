package ch.epfl.cs107.icoop.actor;

import ch.epfl.cs107.icoop.handler.ICoopInteractionVisitor;
import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.signal.logic.Logic;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static ch.epfl.cs107.icoop.actor.Orb.OrbType.FEU;

/**
 * Classe Wall.
 * Cette classe représente un mur élémentaire dans le jeu.
 * Les murs peuvent être activés ou désactivés, et ils interagissent avec les joueurs selon leur élément.
 */
public class Wall extends ElementalWall {

    /** Indique si le mur peut être traversé. */
    private boolean canPass;

    /** Élément associé au mur ("FEU", "EAU", etc.). */
    private final String element;

    /** Signal d'activation contrôlant l'état du mur. */
    private Logic activationSignal;

    /** Gestionnaire d'interactions spécifiques au mur. */
    private final ICoopWallInteractionHandler handler = new ICoopWallInteractionHandler();

    /**
     * Constructeur de la classe Wall.
     * @param area L'aire où le mur est créé.
     * @param orientation Orientation initiale du mur.
     * @param position Position initiale du mur.
     * @param element L'élément associé au mur.
     * @param activationSignal Signal logique initial contrôlant l'état du mur.
     */
    public Wall(Area area, Orientation orientation, DiscreteCoordinates position, String element, Logic activationSignal) {
        super(area, orientation, position, element, activationSignal);
        canPass = false;
        this.element = element;
        this.activationSignal = activationSignal;
        area.registerActor(this);
    }

    /**
     * Vérifie si le mur est activé.
     * @return True si le mur est activé, sinon False.
     */
    public boolean isOn() {
        return super.isOn();
    }

    /**
     * Vérifie si le mur est désactivé.
     * @return True si le mur est désactivé, sinon False.
     */
    public boolean isOff() {
        return super.isOff();
    }

    /**
     * Active le mur.
     */
    public void setOn() {
        setActivationSignal(Logic.TRUE);
    }

    /**
     * Désactive le mur.
     */
    public void setOff() {
        setActivationSignal(Logic.FALSE);
    }

    /**
     * Renvoie les cellules actuellement occupées par le mur.
     * @return Liste contenant une seule cellule.
     */
    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates());
    }

    /**
     * Renvoie les cellules adjacentes visibles depuis le mur.
     * @return Liste des coordonnées des cellules adjacentes.
     */
    @Override
    public List<DiscreteCoordinates> getFieldOfViewCells() {
        DiscreteCoordinates mainCell = getCurrentMainCellCoordinates();
        List<DiscreteCoordinates> adjacentCells = Arrays.asList(
                mainCell.jump(new Vector(1, 0)),  // Droite
                mainCell.jump(new Vector(-1, 0)), // Gauche
                mainCell.jump(new Vector(0, 1)),  // Haut
                mainCell.jump(new Vector(0, -1)) // Bas
        );
        return adjacentCells;
    }

    /**
     * Vérifie si le mur souhaite interagir au niveau des cellules.
     * @return Toujours true.
     */
    @Override
    public boolean wantsCellInteraction() {
        return true;
    }

    /**
     * Vérifie si le mur souhaite interagir au niveau de la vue.
     * @return Toujours true.
     */
    @Override
    public boolean wantsViewInteraction() {
        return true;
    }

    /**
     * Vérifie si le mur occupe un espace cellulaire.
     * @return True si le mur ne peut pas être traversé, sinon False.
     */
    @Override
    public boolean takeCellSpace() {
        return !canPass;
    }

    /**
     * Gère les interactions avec d'autres entités.
     * @param other L'entité interagissant avec le mur.
     * @param isCellInteraction True si l'interaction est cellulaire.
     */
    @Override
    public void interactWith(Interactable other, boolean isCellInteraction) {
        other.acceptInteraction(handler, isCellInteraction);
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
     * Gestionnaire interne pour les interactions avec le mur.
     */
    private class ICoopWallInteractionHandler implements ICoopInteractionVisitor {

        /**
         * Gère l'interaction avec un joueur.
         * @param player Le joueur interagissant avec le mur.
         * @param isCellInteraction True si l'interaction est cellulaire.
         */
        @Override
        public void interactWith(ICoopPlayer player, boolean isCellInteraction) {
            if (wantsCellInteraction() && isCellInteraction && isOn()) {
                if (element.equals("FEU")) player.applyDamage(ICoopPlayer.DamageType.FIRE, 5);
                else if (element.equals("EAU")) player.applyDamage(ICoopPlayer.DamageType.WATER, 5);
            }
            if (wantsViewInteraction() && !isCellInteraction) {
                if (player.getElementType().equals(element) || isOff()) {
                    canPass = true;
                } else {
                    canPass = false;
                }
            }
        }
    }
}
