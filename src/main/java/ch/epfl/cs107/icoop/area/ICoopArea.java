package ch.epfl.cs107.icoop.area;

import ch.epfl.cs107.icoop.ICoopBehavior;
import ch.epfl.cs107.icoop.actor.ICoopPlayer;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.io.FileSystem;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.window.Window;

/**
 * Classe abstraite ICoopArea.
 * Représente les zones du jeu ICoop avec leurs spécificités.
 */
public abstract class ICoopArea extends Area {

    /** Facteur d'échelle par défaut pour la caméra. */
    public final static float DEFAULT_SCALE_FACTOR = 13.f;

    /** Facteur d'échelle actuel pour la caméra. */
    private float cameraScaleFactor = DEFAULT_SCALE_FACTOR;

    /**
     * Crée tous les acteurs et autres composants de cette zone.
     * Cette méthode doit être implémentée par les sous-classes.
     */
    protected abstract void createArea();

    /**
     * Initialise une instance de la zone.
     *
     * @param window (Window) : Contexte d'affichage, non null.
     * @param fileSystem (FileSystem) : Système de fichiers donné, non null.
     * @return True si la zone est correctement initialisée, sinon False.
     */
    @Override
    public boolean begin(Window window, FileSystem fileSystem) {
        if (super.begin(window, fileSystem)) {
            setBehavior(new ICoopBehavior(window, getTitle()));
            createArea();
            return true;
        }
        return false;
    }

    /**
     * Renvoie le facteur d'échelle actuel de la caméra.
     * @return Facteur d'échelle de la caméra.
     */
    @Override
    public final float getCameraScaleFactor() {
        return cameraScaleFactor;
    }

    /**
     * Indique si la vue est centrée.
     * @return True, car la vue est toujours centrée.
     */
    @Override
    public boolean isViewCentered() {
        return true;
    }

    /**
     * Modifie le facteur d'échelle de la caméra.
     * @param scaleFactor (float) : Nouveau facteur d'échelle pour la caméra.
     */
    public void setCameraScaleFactor(float scaleFactor) {
        this.cameraScaleFactor = scaleFactor;
    }

    /**
     * Renvoie les coordonnées par défaut du joueur ICoopPlayer dans cette zone.
     * Cette méthode doit être implémentée par les sous-classes.
     *
     * @param player (ICoopPlayer) : Le joueur dont on veut les coordonnées.
     * @return Les coordonnées par défaut du joueur dans la zone.
     */
    public abstract DiscreteCoordinates getPlayerPosition(ICoopPlayer player);

    /**
     * Renvoie l'orientation par défaut dans la zone actuelle des joueurs ICoopPlayers.
     * Cette méthode doit être implémentée par les sous-classes.
     *
     * @return Orientation par défaut des joueurs.
     */
    public abstract Orientation getPlayerOrientation();
}