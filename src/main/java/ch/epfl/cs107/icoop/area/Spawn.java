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
 * Classe Spawn.
 * Représente l'aire de départ du jeu où les joueurs commencent leur aventure.
 * Contient des portes vers d'autres zones comme OrbWay et Maze, ainsi que divers acteurs.
 */
public final class Spawn extends ICoopArea {

    /** Positions initiales des joueurs ICoopPlayer (Pos0 = Joueur EAU, Pos1 = Joueur FEU). */
    private final DiscreteCoordinates[] spawnCoord = {
            new DiscreteCoordinates(13, 6),
            new DiscreteCoordinates(14, 6)
    };

    /** Cellules composant la porte vers OrbWay. */
    private final List<DiscreteCoordinates> cellPositionDoor1 = new ArrayList<>();

    /** Cellules composant la porte vers Maze. */
    private final List<DiscreteCoordinates> cellPositionDoor2 = new ArrayList<>();

    /** Cellules d'arrivée de la porte vers OrbWay. */
    private final List<DiscreteCoordinates> pathCellDoor1 = new ArrayList<>();

    /** Cellules d'arrivée de la porte vers Maze. */
    private final List<DiscreteCoordinates> pathCellDoor2 = new ArrayList<>();

    /**
     * Renvoie le titre de l'aire.
     * @return "Spawn" comme titre de cette aire.
     */
    @Override
    public String getTitle() {
        return "Spawn";
    }

    /**
     * Crée et configure les acteurs et le comportement de l'aire.
     */
    @Override
    protected void createArea() {
        registerActor(new Background(this));
        registerActor(new Foreground(this));

        // Configuration des portes
        setDoor1CoordinateInit();
        setDoor2CoordinateInit();

        new Door("OrbWay", Logic.TRUE, pathCellDoor1, this, new DiscreteCoordinates(19, 15), cellPositionDoor1);
        new Door("Maze", Logic.TRUE, pathCellDoor2, this, new DiscreteCoordinates(4, 0), cellPositionDoor2);

        // Ajout des obstacles et autres acteurs
        new Obstacle(this, Orientation.DOWN, new DiscreteCoordinates(10, 10), false);
        new Explosif(this, Orientation.DOWN, new DiscreteCoordinates(11, 10), 5, false);

        new ManorDoor(this, Orientation.DOWN, new DiscreteCoordinates(6, 11));
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
            return spawnCoord[1];
        }
        if (elementType.equals("EAU")) {
            return spawnCoord[0];
        }
        return null;
    }

    /**
     * Renvoie l'orientation initiale des joueurs dans l'aire.
     * @return Toujours Orientation.DOWN.
     */
    @Override
    public Orientation getPlayerOrientation() {
        return Orientation.DOWN;
    }

    /**
     * Initialise les coordonnées de la porte menant à OrbWay.
     */
    private void setDoor1CoordinateInit() {
        cellPositionDoor1.add(new DiscreteCoordinates(19, 16));
        pathCellDoor1.add(new DiscreteCoordinates(1, 12));
        pathCellDoor1.add(new DiscreteCoordinates(1, 5));
    }

    /**
     * Initialise les coordonnées de la porte menant à Maze.
     */
    private void setDoor2CoordinateInit() {
        pathCellDoor2.add(new DiscreteCoordinates(2, 39));
        pathCellDoor2.add(new DiscreteCoordinates(3, 39));
        cellPositionDoor2.add(new DiscreteCoordinates(5, 0));
    }
}
