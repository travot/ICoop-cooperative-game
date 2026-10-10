package ch.epfl.cs107.icoop;

import ch.epfl.cs107.icoop.actor.Obstacle;
import ch.epfl.cs107.icoop.actor.Projectile;
import ch.epfl.cs107.icoop.actor.Rock;
import ch.epfl.cs107.icoop.area.ICoopArea;
import ch.epfl.cs107.icoop.handler.ICoopInteractionVisitor;
import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.area.AreaBehavior;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.window.Window;

/**
 * Classe ICoopBehavior.
 * Représente le comportement d'une aire de jeu coopérative.
 * Initialise et gère les cellules et les acteurs en fonction des données graphiques fournies.
 */
public class ICoopBehavior extends AreaBehavior {

    /** Couleur de la cellule par rapport au comportement. */
    private ICoopCellType color;

    /**
     * Constructeur de ICoopBehavior.
     * Initialise une grille de cellules basée sur les données graphiques fournies.
     *
     * @param window (Window) : le contexte graphique, non null.
     * @param name (String) : le nom du fichier d'image de comportement.
     */
    public ICoopBehavior(Window window, String name) {
        super(window, name);
        int height = getHeight();
        int width = getWidth();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                color = ICoopCellType.toType(getRGB(height - 1 - y, x));
                setCell(x, y, new ICoopCell(x, y, color));
            }
        }
    }

    /**
     * Enregistre les acteurs dans l'aire en fonction du type de cellule.
     * @param area (ICoopArea) : l'aire à laquelle enregistrer les acteurs.
     */
    public void registerActors(ICoopArea area) {
        for (int x = 0; x < getWidth(); x++) {
            for (int y = 0; y < getHeight(); y++) {
                ICoopCell cell = (ICoopCell) getCell(x, y);
                ICoopCellType cellType = cell.getType();

                // Enregistrement des acteurs Rock et Obstacle
                if (cellType == ICoopCellType.ROCK) {
                    area.registerActor(new Rock(area, Orientation.DOWN, new DiscreteCoordinates(x, y), false));
                } else if (cellType == ICoopCellType.OBSTACLE) {
                    area.registerActor(new Obstacle(area, Orientation.DOWN, new DiscreteCoordinates(x, y), false));
                }
            }
        }
    }

    /**
     * Enumération ICoopCellType.
     * Représente les différents types de cellules disponibles dans une aire coopérative.
     */
    public enum ICoopCellType {
        /**
         * Types de cellules disponibles :
         * - NULL : Cellule indéfinie ou par défaut.
         * - WALL : Mur ou limite infranchissable.
         * - IMPASSABLE : Cellule infranchissable à pied mais accessible par vol.
         * - INTERACT : Cellule interactive.
         * - DOOR : Cellule représentant une porte.
         * - WALKABLE : Terrain praticable.
         * - ROCK : Cellule représentant un rocher.
         * - OBSTACLE : Cellule représentant un obstacle.
         */
        NULL(0, false, false),
        WALL(-16777216, false, false),
        IMPASSABLE(-8750470, false, true),
        INTERACT(-256, true, true),
        DOOR(-195580, true, true),
        WALKABLE(-1, true, true),
        ROCK(-16777204, true, true),
        OBSTACLE(-16723187, true, true);

        final int type;
        final boolean isWalkable;
        final boolean isFlyable;

        ICoopCellType(int type, boolean isWalkable, boolean isFlyable) {
            this.type = type;
            this.isWalkable = isWalkable;
            this.isFlyable = isFlyable;
        }

        /**
         * Convertit une valeur entière en un type de cellule ICoopCellType correspondant.
         *
         * @param type La valeur représentant un type de cellule.
         * @return Le type de cellule correspondant ou NULL si aucun match n'est trouvé.
         */
        public static ICoopCellType toType(int type) {
            for (ICoopCellType ict : ICoopCellType.values()) {
                if (ict.type == type)
                    return ict;
            }
            return NULL;
        }
    }

    /**
     * Classe ICoopCell.
     * Représente les cellules composant le comportement de l'aire.
     */
    public class ICoopCell extends Cell {

        /** Type de la cellule. */
        private final ICoopCellType type;

        /**
         * Constructeur de la cellule.
         *
         * @param x (int) : coordonnée x de la cellule.
         * @param y (int) : coordonnée y de la cellule.
         * @param type (ICoopCellType) : type de la cellule, non null.
         */
        public ICoopCell(int x, int y, ICoopCellType type) {
            super(x, y);
            this.type = type;
        }

        /**
         * Renvoie le type de la cellule.
         * @return Le type de la cellule.
         */
        public ICoopCellType getType() {
            return type;
        }

        @Override
        protected boolean canLeave(Interactable entity) {
            return true;
        }

        @Override
        protected boolean canEnter(Interactable entity) {
            if (entity instanceof Projectile projectile) {
                if (projectile.isUnstoppable()) {
                    return true;
                }
            }
            if (!type.isWalkable) {
                return false;
            }
            for (Interactable interactable : entities) {
                if (interactable.takeCellSpace()) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public boolean isCellInteractable() {
            return true;
        }

        @Override
        public boolean isViewInteractable() {
            return false;
        }

        @Override
        public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
            ((ICoopInteractionVisitor) v).interactWith(this, isCellInteraction);
        }
    }
}
