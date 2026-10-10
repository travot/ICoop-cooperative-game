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
 * Classe OrbWay.
 * Représente une aire où les joueurs doivent interagir avec des murs et des plaques de pression
 * pour avancer vers la sortie. Cette aire contient des orbes et des coeurs à collecter.
 */
public final class OrbWay extends ICoopArea {

    /** Positions initiales des joueurs ICoopPlayer (Pos0 = Joueur EAU, Pos1 = Joueur FEU). */
    private final DiscreteCoordinates[] orbCoord = {
            new DiscreteCoordinates(1, 5),
            new DiscreteCoordinates(1, 12)
    };

    /** Liste des murs de feu. */
    private List<Wall> wallFeu = new ArrayList<>();

    /** Liste des murs d'eau. */
    private List<Wall> wallEau = new ArrayList<>();

    /** Cellules composant les portes menant au Spawn. */
    private final List<DiscreteCoordinates> cellPositionDoors1 = new ArrayList<>();

    /** Cellules d'arrivée pour les portes menant au Spawn. */
    private final List<DiscreteCoordinates> pathCellDoors1and2 = new ArrayList<>();

    /**
     * Renvoie le titre de l'aire.
     * @return "OrbWay" comme titre de cette aire.
     */
    @Override
    public String getTitle() {
        return "OrbWay";
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

        // Création des orbes
        new Orb(this, Orientation.DOWN, new DiscreteCoordinates(17, 12), "FEU", false);
        new Orb(this, Orientation.DOWN, new DiscreteCoordinates(17, 6), "EAU", false);

        // Création des coeurs
        new Heart(this, Orientation.DOWN, new DiscreteCoordinates(8, 4), false);
        new Heart(this, Orientation.DOWN, new DiscreteCoordinates(10, 6), false);
        new Heart(this, Orientation.DOWN, new DiscreteCoordinates(5, 13), false);
        new Heart(this, Orientation.DOWN, new DiscreteCoordinates(10, 11), false);

        // Création des portes
        new Door("Spawn", Logic.TRUE, pathCellDoors1and2, this, new DiscreteCoordinates(0, 14), cellPositionDoors1);
        new Door("Spawn", Logic.TRUE, pathCellDoors1and2, this, new DiscreteCoordinates(0, 8), cellPositionDoors1);

        // Création des plaques de pression
        new PressurePlate(this, Orientation.DOWN, new DiscreteCoordinates(5, 10), wallEau);
        new PressurePlate(this, Orientation.DOWN, new DiscreteCoordinates(5, 8), wallFeu);
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
            return orbCoord[1];
        }
        if (elementType.equals("EAU")) {
            return orbCoord[0];
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
     * Initialise les coordonnées des portes menant au Spawn.
     */
    private void setDoorCoordinateInit() {
        for (int i = 10; i <= 13; i++) {
            cellPositionDoors1.add(new DiscreteCoordinates(0, i));
            cellPositionDoors1.add(new DiscreteCoordinates(0, i - 6));
        }
        pathCellDoors1and2.add(new DiscreteCoordinates(18, 16));
        pathCellDoors1and2.add(new DiscreteCoordinates(18, 15));
    }

    /**
     * Configure les murs de l'aire.
     */
    private void setupWalls() {
        // Créez 5 murs de feu orientés à gauche en positions (12, 10+i)
        for (int i = 0; i < 5; i++) {
            Wall wallF = new Wall(this, Orientation.LEFT, new DiscreteCoordinates(12, 10 + i), "FEU", Logic.TRUE);
            wallFeu.add(wallF);
        }
        // Créez 5 murs d'eau orientés à gauche en positions (12, 4+i)
        for (int i = 0; i < 5; i++) {
            Wall wallE = new Wall(this, Orientation.LEFT, new DiscreteCoordinates(12, 4 + i), "EAU", Logic.TRUE);
            wallEau.add(wallE);
        }
        // Ajoute un mur d'eau à des fins de test en position (7,12)
        new Wall(this, Orientation.LEFT, new DiscreteCoordinates(7, 12), "EAU", Logic.TRUE);

        // Ajoute un mur de feu à des fins de test en position (7,6)
        new Wall(this, Orientation.LEFT, new DiscreteCoordinates(7, 6), "FEU", Logic.TRUE);
    }
}
