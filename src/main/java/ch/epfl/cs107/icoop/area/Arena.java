package ch.epfl.cs107.icoop.area;

import ch.epfl.cs107.icoop.ICoopBehavior;
import ch.epfl.cs107.icoop.actor.*;
import ch.epfl.cs107.play.areagame.area.AreaBehavior;
import ch.epfl.cs107.play.engine.actor.Background;
import ch.epfl.cs107.play.engine.actor.Foreground;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.signal.logic.Logic;
import ch.epfl.cs107.play.window.Window;

import java.util.ArrayList;
import java.util.List;

import static ch.epfl.cs107.play.io.ResourcePath.getBehavior;

/**
 * Classe Arena.
 * Dernière aire avant la fin du jeu, composée principalement de rochers (ROCK) et d'obstacles (OBSTACLE).
 */
public class Arena extends ICoopArea {

    /** Liste des positions initiales des joueurs ICoopPlayer (Pos0 = Joueur FEU, Pos1 = Joueur EAU). */
    private final DiscreteCoordinates[] ArenaCoord = {
            new DiscreteCoordinates(4, 5),
            new DiscreteCoordinates(14, 15)
    };

    /** Fenêtre utilisée pour le rendu et l'interaction. */
    private Window window;

    /**
     * Constructeur de la classe Arena prenant une fenêtre comme paramètre.
     * @param window La fenêtre utilisée pour le rendu et les interactions. Ne doit pas être null.
     */
    public Arena(Window window) {
        this.window = window;
    }

    /**
     * Renvoie le titre de l'aire.
     * @return "Arena" comme titre de cette aire.
     */
    @Override
    public String getTitle() {
        return "Arena";
    }

    /**
     * Crée et configure les acteurs et le comportement de l'aire.
     */
    @Override
    protected void createArea() {
        registerActor(new Background(this));
        registerActor(new Foreground(this));

        ICoopBehavior areaBehavior = new ICoopBehavior(window, "Arena");

        // Création des portails et des clés associées
        Portal portal = new Portal("Spawn", Orientation.DOWN, Logic.FALSE, this, new DiscreteCoordinates(10, 11));

        new Key(this, Orientation.DOWN, new DiscreteCoordinates(9, 16), "RED", false, portal);
        new Key(this, Orientation.DOWN, new DiscreteCoordinates(9, 4), "BLUE", false, portal);

        areaBehavior.registerActors(this);
    }

    /**
     * Renvoie la position initiale du joueur en fonction de son élément.
     * @param player Le joueur dont on veut connaître la position initiale.
     * @return Les coordonnées initiales du joueur, ou null si non défini.
     */
    @Override
    public DiscreteCoordinates getPlayerPosition(ICoopPlayer player) {
        String elementType = player.getElementType();
        if (elementType.equals("FEU")) {
            return ArenaCoord[0]; // Position du joueur Feu
        }
        if (elementType.equals("EAU")) {
            return ArenaCoord[1]; // Position du joueur Eau
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
}
