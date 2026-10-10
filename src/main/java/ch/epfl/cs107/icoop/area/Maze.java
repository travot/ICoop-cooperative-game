package ch.epfl.cs107.icoop.area;

import ch.epfl.cs107.icoop.actor.*;
import ch.epfl.cs107.play.engine.actor.Background;
import ch.epfl.cs107.play.engine.actor.Foreground;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.signal.logic.Logic;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe Maze.
 * Représente un labyrinthe dans le jeu, composé principalement de murs et d'obstacles,
 * avec des interactions complexes entre les joueurs, les plaques de pression, et les portes.
 */
public final class Maze extends ICoopArea {

    /** Positions initiales des joueurs ICoopPlayer (Pos0 = Joueur FEU, Pos1 = Joueur EAU). */
    private final DiscreteCoordinates[] mazeCoord = {
            new DiscreteCoordinates(2, 39),
            new DiscreteCoordinates(3, 39)
    };

    /** Cellules composant la porte menant à l'Arena. */
    private final List<DiscreteCoordinates> cellPositionDoors1 = new ArrayList<>();

    /** Cellules d'arrivée pour la porte menant à l'Arena. */
    private final List<DiscreteCoordinates> pathCellDoors1 = new ArrayList<>();

    /** Liste des murs contrôlés par la plaque de pression Plate4 (mur de feu). */
    private final List<Wall> wallFeuPlate4 = new ArrayList<>();

    /** Liste des murs contrôlés par la plaque de pression Plate5 (mur de feu). */
    private final List<Wall> wallFeuPlate5 = new ArrayList<>();

    /**
     * Renvoie le titre de l'aire.
     * @return "Maze" comme titre de cette aire.
     */
    @Override
    public String getTitle() {
        return "Maze";
    }

    /**
     * Crée et configure les acteurs et le comportement de l'aire.
     */
    @Override
    protected void createArea() {
        registerActor(new Background(this));
        registerActor(new Foreground(this));

        // Configuration des murs et des coordonnées des portes
        setupWalls();
        setDoorCoordinateInit();

        // Création des acteurs spécifiques
        new Door("Arena", Logic.TRUE, pathCellDoors1, this, new DiscreteCoordinates(19, 6), cellPositionDoors1);

        // Ajout des coeurs
        new Heart(this, Orientation.DOWN, new DiscreteCoordinates(15, 18), false);
        new Heart(this, Orientation.DOWN, new DiscreteCoordinates(16, 19), false);
        new Heart(this, Orientation.DOWN, new DiscreteCoordinates(14, 19), false);
        new Heart(this, Orientation.DOWN, new DiscreteCoordinates(14, 17), false);
        new Heart(this, Orientation.DOWN, new DiscreteCoordinates(16, 17), false);

        // Ajout des ennemis BombFoe
        new BombFoe(this, Orientation.DOWN, new DiscreteCoordinates(5, 15));
        new BombFoe(this, Orientation.DOWN, new DiscreteCoordinates(6, 17));
        new BombFoe(this, Orientation.DOWN, new DiscreteCoordinates(10, 17));
        new BombFoe(this, Orientation.DOWN, new DiscreteCoordinates(5, 14));

        // Ajout des ennemis HellSkull
        new HellSkull(this, Orientation.RIGHT, new DiscreteCoordinates(12, 33));
        new HellSkull(this, Orientation.RIGHT, new DiscreteCoordinates(12, 31));
        new HellSkull(this, Orientation.RIGHT, new DiscreteCoordinates(12, 29));
        new HellSkull(this, Orientation.RIGHT, new DiscreteCoordinates(12, 27));
        new HellSkull(this, Orientation.RIGHT, new DiscreteCoordinates(12, 25));
        new HellSkull(this, Orientation.RIGHT, new DiscreteCoordinates(10, 33));
        new HellSkull(this, Orientation.RIGHT, new DiscreteCoordinates(10, 32));
        new HellSkull(this, Orientation.RIGHT, new DiscreteCoordinates(10, 30));
        new HellSkull(this, Orientation.RIGHT, new DiscreteCoordinates(10, 28));
        new HellSkull(this, Orientation.RIGHT, new DiscreteCoordinates(10, 26));

        // Ajout des plaques de pression
        new PressurePlate(this, Orientation.DOWN, new DiscreteCoordinates(6, 33), wallFeuPlate4);
        new PressurePlate(this, Orientation.DOWN, new DiscreteCoordinates(9, 25), wallFeuPlate5);

        // Ajout des explosifs
        new Explosif(this, Orientation.DOWN, new DiscreteCoordinates(6, 25), 5, false);

        // Ajout des bâtons élémentaires
        new Staff(this, Orientation.RIGHT, new DiscreteCoordinates(13, 3), "FEU", false);
        new Staff(this, Orientation.RIGHT, new DiscreteCoordinates(8, 3), "EAU", false);
    }

    /**
     * Renvoie la position initiale d'un joueur en fonction de son élément.
     * @param player Le joueur dont on veut connaître la position initiale.
     * @return Les coordonnées initiales du joueur, ou null si non défini.
     */
    @Override
    public DiscreteCoordinates getPlayerPosition(ICoopPlayer player) {
        String elementType = player.getElementType();
        if (elementType.equals("FEU")) {
            return mazeCoord[0]; // Position du joueur Feu à (2, 39)
        }
        if (elementType.equals("EAU")) {
            return mazeCoord[1]; // Position du joueur Eau à (3, 39)
        }
        return null;
    }

    /**
     * Renvoie l'orientation initiale des joueurs dans l'aire.
     * @return Toujours Orientation.RIGHT.
     */
    @Override
    public Orientation getPlayerOrientation() {
        return Orientation.RIGHT;
    }

    /**
     * Initialise les coordonnées de la porte menant à l'Arena.
     */
    private void setDoorCoordinateInit() {
        cellPositionDoors1.add(new DiscreteCoordinates(19, 7));
        pathCellDoors1.add(new DiscreteCoordinates(4, 5));
        pathCellDoors1.add(new DiscreteCoordinates(14, 15));
    }

    /**
     * Initialise les murs et les regroupe dans des listes pour un meilleur contrôle.
     */
    private void setupWalls() {
        // Murs d'eau orientés à gauche en positions (4, 35) et (4, 36)
        new Wall(this, Orientation.LEFT, new DiscreteCoordinates(4, 35), "EAU", Logic.TRUE);
        new Wall(this, Orientation.LEFT, new DiscreteCoordinates(4, 36), "EAU", Logic.TRUE);

        // Murs de feu orientés à gauche, désactivés lorsque la plaque de plate4 est active
        wallFeuPlate4.add(new Wall(this, Orientation.LEFT, new DiscreteCoordinates(6, 35), "FEU", Logic.TRUE));
        wallFeuPlate4.add(new Wall(this, Orientation.LEFT, new DiscreteCoordinates(6, 36), "FEU", Logic.TRUE));

        // Murs de feu orientés vers le bas, toujours actifs
        new Wall(this, Orientation.DOWN, new DiscreteCoordinates(2, 34), "FEU", Logic.TRUE);
        new Wall(this, Orientation.DOWN, new DiscreteCoordinates(3, 34), "FEU", Logic.TRUE);

        // Murs d'eau orientés vers le bas, toujours actifs
        new Wall(this, Orientation.DOWN, new DiscreteCoordinates(5, 24), "EAU", Logic.TRUE);
        new Wall(this, Orientation.DOWN, new DiscreteCoordinates(6, 24), "EAU", Logic.TRUE);

        // Mur de feu orienté vers le bas, désactivé lorsque la plaque de plate5 est active
        wallFeuPlate5.add(new Wall(this, Orientation.DOWN, new DiscreteCoordinates(8, 21), "FEU", Logic.TRUE));

        // Murs d'eau et de feu orientés vers le bas, toujours actifs
        new Wall(this, Orientation.DOWN, new DiscreteCoordinates(8, 4), "EAU", Logic.TRUE);
        new Wall(this, Orientation.DOWN, new DiscreteCoordinates(13, 4), "FEU", Logic.TRUE);
    }
}
